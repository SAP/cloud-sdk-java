package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to parse an {@link InputStream} to an {@link Iterable} multi-part response. One part can have multiple
 * segments (e.g. changeset). For that reason the API exposes {@code Iterable<Iterable<T>>}.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class MultipartParser
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String MULTIPART_MIXED_MIME_TYPE = "multipart/mixed";
    private static final String MULTIPART_MIXED_BOUNDARY = "boundary";

    @Nonnull
    private final BufferedReader reader;

    @Nonnull
    private final String delimiter;

    /**
     * Factory method to instantiate an {@link MultipartParser} object.
     *
     * @param httpResponse
     *            The HTTP response to read from: content-type, input-stream and charset.
     * @return A new instance of {@link MultipartParser}.
     */
    public static MultipartParser ofHttpResponse( @Nonnull final HttpResponse httpResponse )
    {
        final HttpEntity entity = httpResponse.getEntity();
        if( entity == null ) {
            throw new IllegalStateException("HTTP response does not contain a content.");
        }

        final InputStream inputStream =
            Try
                .of(entity::getContent)
                .getOrElseThrow(e -> new IllegalStateException("Unable to read HTTP content.", e));

        final String delimiter =
            getDelimiterFromHttpResponse(httpResponse)
                .orElseThrow(() -> new IllegalStateException("No delimiter found in HTTP header."));

        final Charset charsetValue =
            Try
                .of(() -> ContentType.get(entity))
                .onFailure(e -> log.debug("Unable to detect charset, using to default charset {}", DEFAULT_CHARSET))
                .toOption()
                .map(ContentType::getCharset)
                .filter(Objects::nonNull)
                .peek(c -> log.debug("Using detected charset {}", c))
                .getOrElse(DEFAULT_CHARSET);

        return ofInputStream(inputStream, charsetValue, delimiter);
    }

    /**
     * Factory method to instantiate an {@link MultipartParser} object.
     *
     * @param contentStream
     *            The {@link InputStream} which is read from.
     * @param contentCharset
     *            The charset of the content.
     * @param delimiter
     *            The delimiter, usually derived from content type.
     * @return A new instance of {@link MultipartParser}.
     */
    @Nonnull
    public static MultipartParser ofInputStream(
        @Nonnull final InputStream contentStream,
        @Nonnull final Charset contentCharset,
        @Nonnull final String delimiter )
    {
        final BufferedReader reader =
            Try
                .of(() -> new InputStreamReader(contentStream, contentCharset))
                .map(BufferedReader::new)
                .getOrElseThrow(e -> new IllegalStateException("Unable to initialize multi-part parsing.", e));

        return new MultipartParser(reader, delimiter);
    }

    /**
     * Get the iterable, raw multi-part response. This method can be invoked once. It internally caches the objects, so
     * the returned List can be iterated multiple times. The {@link List} objects are eagerly evaluated.
     *
     * @return An iterable multi-part response.
     */
    @Nonnull
    public List<List<String>> toList()
    {
        return toList(Function.identity());
    }

    /**
     * Get the iterable multi-part response, optionally parsed with a custom {@link Function}. This method can be
     * invoked once. It internally caches the objects, so the returned List can be iterated multiple times. The
     * {@link List} objects are eagerly evaluated.
     *
     * @param <T>
     *            Generic item type.
     * @param transformation
     *            Generic item transition logic.
     *
     * @return An iterable multi-part response, with custom deserialization.
     */
    @Nonnull
    public <T> List<List<T>> toList( @Nonnull final Function<String, T> transformation )
    {
        return toStream()
            .map(segments -> segments.map(transformation).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    /**
     * Get the iterable, raw multi-part response. This method only works as long as the underlying {@link InputStream}
     * of {@link MultipartParserReader} is not depleted. Hence for proper results, this method can usually only be
     * invoked once. The resulting {@link Iterable} objects are lazily evaluated and remain uncached.
     *
     * @return An iterable multi-part response.
     */
    @Nonnull
    public Stream<Stream<String>> toStream()
    {
        // Create a stream from a spliterator but only retrieve the spliterator upon terminal operation of stream.
        final Stream<Spliterator<String>> result =
            StreamSupport.stream(this::createSpliterator, MultipartSpliterator.CHARACTERISTICS, false);

        // Translate stream of spliterators to stream of stream. Make sure all previous spliterators were fully consumed.
        final AtomicReference<Spliterator<String>> previous = new AtomicReference<>(Spliterators.emptySpliterator());

        return result.map(currentSpliterator -> {
            // if previous spliterator was not yet fully consumed, do so with the remaining elements
            previous.getAndSet(currentSpliterator).forEachRemaining(item -> log.trace("Skipping element {}", item));

            // return Stream of current spliterator
            return StreamSupport.stream(currentSpliterator, false);
        });
    }

    private Spliterator<Spliterator<String>> createSpliterator()
    {
        final MultipartParserReader batchRead = new MultipartParserReader(reader, delimiter);

        // position reader after first batch delimiter
        batchRead.untilDelimiter();

        return new MultipartSpliterator<>(() -> {
            if( batchRead.isFinished() ) {
                Try.run(reader::close).onFailure(e -> log.debug("Failed to close reader.", e));
                return null;
            }
            final String segmentHead = batchRead.untilPayload();
            final Optional<String> maybeChangesetBoundary = getDelimiterFromString(segmentHead);

            if( maybeChangesetBoundary.isPresent() ) { // multiple responses in changeset
                return getChangeset(maybeChangesetBoundary.get(), batchRead);
            } else { // single response
                final String content = batchRead.untilDelimiter();
                return Collections.singleton(content).spliterator();
            }
        });
    }

    @Nonnull
    private
        Spliterator<String>
        getChangeset( @Nonnull final String changesetDelimiter, final MultipartParserReader batchRead )
    {
        final MultipartParserReader changesetRead = new MultipartParserReader(reader, changesetDelimiter);

        // position reader after first sub-segment delimiter
        changesetRead.untilDelimiter();

        return new MultipartSpliterator<>(() -> {
            if( changesetRead.isFinished() ) {
                return null;
            }
            final String subSegmentHead = changesetRead.untilPayload();
            log.trace("Iterating Batch changeset segment with header {}", subSegmentHead);

            final String content = changesetRead.untilDelimiter();
            if( changesetRead.isFinished() ) {
                batchRead.untilDelimiter(); // position reader to next batch delimiter
            }
            return content;
        });
    }

    @Nonnull
    private static Optional<String> getDelimiterFromString( @Nonnull final String segmentHead )
    {
        final List<Header> segmentHeaders = MultipartHttpResponse.getHeadersFromString(segmentHead);
        final Optional<ContentType> segmentContentType = MultipartHttpResponse.getContentType(segmentHeaders);
        return segmentContentType.flatMap(MultipartParser::getDelimiterFromContentType);
    }

    @Nonnull
    private static Optional<String> getDelimiterFromContentType( @Nonnull final ContentType contentType )
    {
        final String boundary = contentType.getParameter(MULTIPART_MIXED_BOUNDARY);
        return boundary == null ? Optional.empty() : Optional.of("--" + boundary);
    }

    @Nonnull
    private static Optional<String> getDelimiterFromHttpResponse( @Nonnull final HttpResponse httpResponse )
    {
        final List<Header> headers = Arrays.asList(httpResponse.getAllHeaders());
        final ContentType contentType = MultipartHttpResponse.getContentType(headers).orElse(null);
        if( contentType == null ) {
            return Optional.empty();
        }
        if( !MULTIPART_MIXED_MIME_TYPE.equalsIgnoreCase(contentType.getMimeType()) ) {
            log.debug("Unexpected value in HTTP header \"Content-Type\" of OData batch response: {}", contentType);
        }
        return getDelimiterFromContentType(contentType);
    }
}
