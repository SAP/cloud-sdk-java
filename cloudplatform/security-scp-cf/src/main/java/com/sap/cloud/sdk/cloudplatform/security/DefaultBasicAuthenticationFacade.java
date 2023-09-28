package com.sap.cloud.sdk.cloudplatform.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;

import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.security.exception.BasicAuthenticationAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

/**
 * Default implementation of the {@link BasicAuthenticationAccessor} reading the username and password from the current
 * {@link ThreadContext} or, after that, from the currently incoming request and returning it as a
 * {@link BasicCredentials} object.
 */
public class DefaultBasicAuthenticationFacade implements BasicAuthenticationFacade
{
    private static final String PROPERTY = BasicAuthenticationThreadContextListener.PROPERTY_BASIC_AUTH_HEADER;

    /**
     * Regex pattern to match the basic authentication header value.
     * <p>
     * Noteworthy points:
     * <ul>
     * <li>the "Basic" prefix is case insensitive</li>
     * <li>the base64 encoded actual value can be wrapped in arbitrarily many whitespaces</li>
     * <li>the actual value will be stored in the first (and only) matching group of the regex</li>
     * </ul>
     */
    private static final Pattern CASE_INSENSITIVE_BASIC_PREFIX_MATCH =
        Pattern.compile("^(?i)basic(?-i) \\s*([A-Za-z0-9+/=]+)\\s*$");

    @Nonnull
    @Override
    public Try<BasicCredentials> tryGetBasicCredentials()
    {
        @Nullable
        final ThreadContext context = ThreadContextAccessor.getCurrentContextOrNull();
        if( context != null && context.containsProperty(PROPERTY) ) {
            return context.getPropertyValue(PROPERTY);
        }

        return Try.of(this::extractBasicCredentialsFromHeaderOrThrow);
    }

    private BasicCredentials extractBasicCredentialsFromHeaderOrThrow()
    {
        final RequestHeaderContainer headers =
            RequestHeaderAccessor
                .tryGetHeaderContainer()
                .getOrElseThrow(
                    e -> new BasicAuthenticationAccessException(
                        "Unable to resolve basic credentials: Unable to read request headers.",
                        e));
        final String value = selectBasicAuthenticationHeaderOrThrow(headers.getHeaderValues(HttpHeaders.AUTHORIZATION));
        final String valueBase64 = extractBasicHeaderValueOrThrow(value);
        return decodeBasicCredentialsOrThrow(valueBase64);
    }

    private String selectBasicAuthenticationHeaderOrThrow( final List<String> allAuthenticationHeader )
    {
        if( allAuthenticationHeader.isEmpty() ) {
            throw new BasicAuthenticationAccessException(
                "Unable to resolve basic credentials: Received an '"
                    + HttpHeaders.AUTHORIZATION
                    + "' header without a value.");
        }

        if( allAuthenticationHeader.size() > 1 ) {
            throw new BasicAuthenticationAccessException(
                "Unable to resolve basic credentials: Received multiple '"
                    + HttpHeaders.AUTHORIZATION
                    + "' headers with the request, but the specification allows at most one.");
        }
        return allAuthenticationHeader.get(0);
    }

    private String extractBasicHeaderValueOrThrow( final CharSequence completeHeader )
    {
        final Matcher match = CASE_INSENSITIVE_BASIC_PREFIX_MATCH.matcher(completeHeader);
        if( !match.matches() ) {
            throw new BasicAuthenticationAccessException(
                "Unable to resolve basic credentials: The '"
                    + HttpHeaders.AUTHORIZATION
                    + "' header did not contain a Basic Authentication header field.");
        }
        return match.group(1);
    }

    private BasicCredentials decodeBasicCredentialsOrThrow( final String base64Credentials )
    {
        try {
            final String[] credentials =
                new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8).split(":");
            return new BasicCredentials(credentials[0], credentials[1]);
        }
        catch( IllegalArgumentException | ArrayIndexOutOfBoundsException e ) {
            throw new BasicAuthenticationAccessException("Unable to resolve basic credentials: Invalid format.", e);
        }
    }
}
