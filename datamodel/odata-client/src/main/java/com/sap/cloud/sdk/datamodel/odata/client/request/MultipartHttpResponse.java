/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper class to construct an HttpResponse object on behalf of serialized HTTP protocol content.
 */
@Slf4j
class MultipartHttpResponse extends BasicHttpResponse
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Pattern PATTERN_STATUS_LINE = Pattern.compile("^HTTP/(\\d).(\\d) (\\d+) (.*)");
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("\\R");

    @Nullable
    @Getter
    private final Integer contentId;

    private MultipartHttpResponse(
        @Nonnull final StatusLine statusLine,
        @Nonnull final List<Header> headers,
        @Nonnull final HttpEntity entity,
        @Nullable final Integer contentId )
    {
        super(statusLine);
        headers.forEach(this::addHeader);
        setEntity(entity);
        this.contentId = contentId;
    }

    /**
     * Factory method to construct an {@link MultipartHttpResponse} on behalf of serialized HTTP protocol content: First
     * line is the status line, the following lines are headers, the optional body is introduced with an empty line.
     *
     * @param entry
     *            The HTTP protocol content, consisting of status line, headers, (emptyline) and payload.
     * @return A new HTTP response instance.
     */
    @Nonnull
    public static MultipartHttpResponse ofHttpContent( @Nonnull final MultipartParser.Entry entry )
    {
        final Matcher contentIdMatcher =
            Pattern
                .compile("^Content-ID:\\s*(\\d+)\\s*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
                .matcher(entry.getMeta());
        final Integer contentId = contentIdMatcher.find() ? Integer.parseInt(contentIdMatcher.group(1)) : null;
        final String[] lines = PATTERN_NEW_LINE.split(entry.getPayload());

        final StatusLine statusLine = getStatusLine(lines[0]);

        final StringBuilder payload = new StringBuilder();
        final StringBuilder header = new StringBuilder();

        boolean isHeaders = true;
        for( int i = 1; i < lines.length; i++ ) {
            if( isHeaders ) {
                if( lines[i].isEmpty() ) {
                    isHeaders = false;
                } else {
                    header.append(lines[i]).append('\n');
                }
            } else {
                payload.append(lines[i]).append('\n');
            }
        }

        final List<Header> headers = getHeadersFromString(header.toString());
        final ContentType contentType = getContentType(headers).orElse(ContentType.APPLICATION_JSON);
        final ContentType contentTypeCharset = withFallbackCharset(contentType, DEFAULT_CHARSET);
        final StringEntity httpEntity = new StringEntity(payload.toString(), contentTypeCharset);
        return new MultipartHttpResponse(statusLine, headers, httpEntity, contentId);
    }

    @Nonnull
    static List<Header> getHeadersFromString( @Nonnull final String headerString )
    {
        final List<Header> result = new ArrayList<>();
        for( final String headerLine : PATTERN_NEW_LINE.split(headerString.trim()) ) {
            final String[] split = headerLine.split(":", 2);
            result.add(new BasicHeader(split[0].trim(), split.length > 1 ? split[1].trim() : ""));
        }
        return result;
    }

    @Nonnull
    static Optional<ContentType> getContentType( @Nonnull final List<Header> headers )
    {
        return headers
            .stream()
            .filter(h -> HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(h.getName()))
            .map(NameValuePair::getValue)
            .map(contentType -> Try.of(() -> ContentType.parse(contentType)).getOrNull())
            .filter(Objects::nonNull)
            .findFirst();
    }

    @Nonnull
    private static
        ContentType
        withFallbackCharset( @Nonnull final ContentType contentType, @Nonnull final Charset fallbackCharset )
    {
        if( contentType.getCharset() != null ) {
            return contentType;
        }
        return contentType.withParameters(new BasicNameValuePair("charset", fallbackCharset.name()));
    }

    @Nonnull
    private static StatusLine getStatusLine( @Nonnull final String firstLine )
    {
        final Matcher m = PATTERN_STATUS_LINE.matcher(firstLine);
        if( m.find() ) {
            final int major = Integer.parseInt(m.group(1));
            final int minor = Integer.parseInt(m.group(2));
            final int code = Integer.parseInt(m.group(3));
            final String reason = m.group(4);
            return new BasicStatusLine(new HttpVersion(major, minor), code, reason);
        }
        log.error("Failed to construct status line for HTTP protocol response: {}", firstLine);
        return new BasicStatusLine(HttpVersion.HTTP_1_1, 0, "Unknown");
    }
}
