package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.cloudplatform.security.BearerCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.NoCredentials;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ComplexDestinationPropertyFactory
{
    private static final String HEADER_PROPERTY = "URL.headers.";
    private static final String BEARER_CREDENTIALS_PREFIX = "bearer "; // casing will be ignored
    private static final String BASIC_CREDENTIALS_PREFIX = "basic "; // casing will be ignored

    @Nonnull
    Option<ProxyConfiguration> getProxyConfiguration( @Nonnull final DestinationProperties baseProperties )
    {
        final Option<URI> proxyUri = baseProperties.get(DestinationProperty.PROXY_URI);
        final Option<String> proxyHost = baseProperties.get(DestinationProperty.PROXY_HOST);
        final Option<Integer> proxyPort = baseProperties.get(DestinationProperty.PROXY_PORT);
        final Credentials proxyAuth = getProxyCredentials(baseProperties);

        if( proxyUri.isDefined() ) {
            try {
                final String proxyUriWithScheme = addHttpSchemeIfNoSchemeIsPresent(proxyUri.get().toString());
                return Option.of(new ProxyConfiguration(new URI(proxyUriWithScheme), proxyAuth));
            }
            catch( final URISyntaxException e ) {
                throw new DestinationAccessException("Invalid proxy URI.", e);
            }
        }

        if( proxyHost.isDefined() && proxyPort.isDefined() ) {
            try {
                return Option
                    .of(
                        new ProxyConfiguration(
                            new URI("http", null, proxyHost.get(), proxyPort.get(), null, null, null),
                            proxyAuth));
            }
            catch( final URISyntaxException e ) {
                throw new DestinationAccessException("Invalid proxy host or port.", e);
            }
        } else {
            log.debug("No proxy URI or host and port specified. Continuing without proxy configuration.");
        }

        return Option.none();
    }

    @Nullable
    private Credentials getProxyCredentials( @Nonnull final DestinationProperties baseProperties )
    {
        final Option<String> maybeProxyAuth = baseProperties.get(DestinationProperty.PROXY_AUTH);
        if( maybeProxyAuth.isEmpty() ) {
            return null;
        }

        final String proxyAuth = maybeProxyAuth.get();
        if( proxyAuth.isEmpty() ) {
            return new NoCredentials();
        }

        if( StringUtils.startsWithIgnoreCase(proxyAuth, BASIC_CREDENTIALS_PREFIX) ) {
            final String encodedCredentials = proxyAuth.substring(BASIC_CREDENTIALS_PREFIX.length());
            final String decodedCredentials =
                new String(Base64.getDecoder().decode(encodedCredentials), StandardCharsets.UTF_8);
            final String[] credentials = decodedCredentials.split(":");
            if( credentials.length != 2 ) {
                throw new DestinationAccessException(
                    "Invalid proxy basic credentials. Correct format should be username:password encoded in base64.");
            }
            return new BasicCredentials(credentials[0], credentials[1]);
        }

        if( StringUtils.startsWithIgnoreCase(proxyAuth, BEARER_CREDENTIALS_PREFIX) ) {
            final String token = proxyAuth.substring(BEARER_CREDENTIALS_PREFIX.length());
            return new BearerCredentials(token);
        }

        if( log.isErrorEnabled() ) {
            final String maskedCredentials =
                proxyAuth.substring(0, Math.max(0, Math.min(4, proxyAuth.length() - 4))) + "****";
            final String msg =
                "Unsupported proxy credentials: {}. The only supported credential types are 'Bearer' and 'Basic'.";
            log.error(msg, maskedCredentials);
        }

        return null;
    }

    @Nonnull
    Collection<Header> getProxyAuthorizationHeaders( @Nonnull final Option<ProxyConfiguration> proxyConfiguration )
    {
        if( proxyConfiguration.isEmpty() ) {
            return Collections.emptyList();
        }

        final Option<Credentials> maybeCredentials = proxyConfiguration.get().getCredentials();
        if( maybeCredentials.isEmpty() ) {
            return Collections.emptyList();
        }

        final Credentials credentials = maybeCredentials.get();
        if( credentials instanceof NoCredentials ) {
            return Collections.emptyList();
        }

        if( credentials instanceof BasicCredentials ) {
            return Collections
                .singletonList(
                    new Header(HttpHeaders.PROXY_AUTHORIZATION, ((BasicCredentials) credentials).getHttpHeaderValue()));
        }

        if( credentials instanceof BearerCredentials ) {
            return Collections
                .singletonList(
                    new Header(
                        HttpHeaders.PROXY_AUTHORIZATION,
                        ((BearerCredentials) credentials).getHttpHeaderValue()));
        }

        return Collections.emptyList();
    }

    @Nonnull
    Option<ProxyType> getProxyType( @Nonnull final DestinationProperties baseProperties )
    {
        final Option<ProxyType> proxyType = baseProperties.get(DestinationProperty.PROXY_TYPE);

        if( proxyType.isEmpty() ) {
            final String msg = "No valid JSON primitive '{}' defined. Falling back to {}.";
            log.debug(msg, DestinationProperty.PROXY_TYPE, ProxyType.INTERNET);
            return Option.of(ProxyType.INTERNET);
        }

        return proxyType;
    }

    @Nonnull
    Option<BasicCredentials> getBasicCredentials( @Nonnull final DestinationProperties baseProperties )
    {
        final Option<String> username =
            baseProperties
                .get(DestinationProperty.BASIC_AUTH_USERNAME)
                .orElse(() -> baseProperties.get(DestinationProperty.BASIC_AUTH_USERNAME_FALLBACK));
        final Option<String> password = baseProperties.get(DestinationProperty.BASIC_AUTH_PASSWORD);

        if( username.isDefined() && password.isDefined() ) {
            return Option.of(new BasicCredentials(username.get(), password.get()));
        }

        if( log.isDebugEnabled() ) {
            final String msg = "No valid JSON primitives '{}' and '{}' defined. Continuing without basic credentials.";
            log.debug(msg, DestinationProperty.BASIC_AUTH_USERNAME, DestinationProperty.BASIC_AUTH_PASSWORD);
        }
        return Option.none();
    }

    @Nonnull
    AuthenticationType getAuthenticationType(
        @Nonnull final DestinationProperties baseProperties,
        @Nonnull final Option<BasicCredentials> basicCredentials )
    {
        final AuthenticationType authType =
            baseProperties
                .get(DestinationProperty.AUTH_TYPE)
                .orElse(() -> baseProperties.get(DestinationProperty.AUTH_TYPE_FALLBACK))
                .onEmpty(
                    () -> log
                        .debug(
                            "No valid JSON primitive '{}' or '{}' defined. Falling back to {}.",
                            DestinationProperty.AUTH_TYPE,
                            DestinationProperty.AUTH_TYPE_FALLBACK,
                            AuthenticationType.NO_AUTHENTICATION))
                .getOrElse(AuthenticationType.NO_AUTHENTICATION);

        if( authType == AuthenticationType.NO_AUTHENTICATION && basicCredentials.isDefined() ) {
            log
                .debug(
                    "Found Authentication Type {}, but basic credentials are provided. Changing Auth Type to {}.",
                    AuthenticationType.NO_AUTHENTICATION,
                    AuthenticationType.BASIC_AUTHENTICATION);
            return AuthenticationType.BASIC_AUTHENTICATION;
        }

        final boolean forwardAuthToken =
            baseProperties
                .get(DestinationProperty.FORWARD_AUTH_TOKEN)
                .orElse(() -> baseProperties.get(DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN))
                .getOrElse(false);

        if( authType == AuthenticationType.NO_AUTHENTICATION && forwardAuthToken ) {
            log
                .debug(
                    "Found Authentication Type {}, but either {} or {} destination properties are provided. Changing Auth Type to {}.",
                    AuthenticationType.NO_AUTHENTICATION,
                    DestinationProperty.FORWARD_AUTH_TOKEN,
                    DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN,
                    AuthenticationType.TOKEN_FORWARDING);
            return AuthenticationType.TOKEN_FORWARDING;
        }
        return authType;
    }

    @Nonnull
    Collection<Header> getHeadersFromProperties( @Nonnull final DestinationProperties baseProperties )
    {
        final Iterable<String> propertyKeys = baseProperties.getPropertyNames();
        final Collection<Header> result = new LinkedList<>();
        for( final String propertyKey : propertyKeys ) {
            if( StringUtils.startsWithIgnoreCase(propertyKey, HEADER_PROPERTY) ) {
                final Option<String> propertyValue = baseProperties.get(propertyKey, String.class);
                if( propertyValue.isEmpty() ) {
                    log.debug("Cannot find header value for {}. Skipping the header.", propertyKey);
                    continue;
                }
                final Header header = new Header(propertyKey.substring(HEADER_PROPERTY.length()), propertyValue.get());
                result.add(header);
            }
        }
        return result;
    }

    private String addHttpSchemeIfNoSchemeIsPresent( final String proxyUri )
    {
        final String proxyUriWithScheme;
        if( proxyUri.contains("://") ) {
            proxyUriWithScheme = proxyUri;
        } else {
            proxyUriWithScheme = "http://" + proxyUri;

            log.info("Proxy URI {} lacks required URI scheme. Scheme 'http' was added as default value.", proxyUri);
        }
        return proxyUriWithScheme;
    }
}
