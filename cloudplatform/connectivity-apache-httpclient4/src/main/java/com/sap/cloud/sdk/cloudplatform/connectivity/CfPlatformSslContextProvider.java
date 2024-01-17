/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class CfPlatformSslContextProvider extends AbstractX509SslContextProvider
{
    private static final String SECURITY_PROVIDER_CLASS_NAME =
        "org.cloudfoundry.security.CloudFoundryContainerProvider";

    // 5 minutes is well below the 1 hour of buffer time we have
    // See https://docs.cloudfoundry.org/devguide/deploy-apps/instance-identity.html for reference on these values
    private static final Long CACHE_DURATION_IN_MILLIS = Duration.ofMinutes(5).toMillis();
    @Nonnull
    private static Function<String, String> environmentVariableReader = System::getenv;
    static final String CERT_ENVIRONMENT_VARIABLE = "CF_INSTANCE_CERT";
    static final String KEY_ENVIRONMENT_VARIABLE = "CF_INSTANCE_KEY";
    @Getter( AccessLevel.PACKAGE )
    private final Cache cache = new Cache();

    @Setter
    private boolean securityProviderAvailable = Try.of(() -> Class.forName(SECURITY_PROVIDER_CLASS_NAME)).isSuccess();

    @Override
    @Nonnull
    public Try<SSLContext> tryGetContext()
    {
        final Option<SSLContext> maybeCachedContext = cache.get();
        if( maybeCachedContext.isDefined() ) {
            return maybeCachedContext.toTry();
        }

        final Try<SSLContext> result;
        if( securityProviderAvailable ) {
            log
                .trace(
                    "Using security provider from buildpack to establish SSL context with platform provided identity.");
            result = Try.of(SSLContext::getDefault);
        } else {
            result = tryLoadInstanceIdentity();
        }

        result.onSuccess(cache::set);
        return result;
    }

    static void setEnvironmentVariableReader( @Nonnull final Function<String, String> environmentVariableReader )
    {
        CfPlatformSslContextProvider.environmentVariableReader = environmentVariableReader;
    }

    private static Option<String> getEnvironmentVariable( final String name )
    {
        return Option.of(environmentVariableReader.apply(name));
    }

    /**
     * Attempt to load the certificate + private key from the file system according to the locations defined in the
     * environment variables {@value #CERT_ENVIRONMENT_VARIABLE} and {@value #KEY_ENVIRONMENT_VARIABLE}. If these files
     * have been processed previously and remain unchanged a result is returned from cache.
     *
     * In case any of the two are not defined the loading will default to {@link SSLContext#getDefault()} with a
     * warning.
     *
     * @return Either a successfully loaded SSLContext or a failure in case of parsing or I/O errors.
     */
    @Nonnull
    Try<SSLContext> tryLoadInstanceIdentity()
    {
        final Option<String> maybeCertPath = getEnvironmentVariable(CERT_ENVIRONMENT_VARIABLE);
        final Option<String> maybeKeyPath = getEnvironmentVariable(KEY_ENVIRONMENT_VARIABLE);

        if( maybeCertPath.isEmpty() || maybeKeyPath.isEmpty() ) {
            log
                .warn(
                    """
                        Unable to create SSL context from environment: Environment variables {} and/or {} are not defined.
                        Proceeding without platform provided identity certificate. mTLS connections to other systems may not be possible.\
                        """,
                    CERT_ENVIRONMENT_VARIABLE,
                    KEY_ENVIRONMENT_VARIABLE);
            return Try.of(SSLContext::getDefault);
        }
        final String preparedErrorMessage = "Failed to read the %s file declared in %s: File does not exist.";

        final Try<File> maybeCertFile =
            maybeCertPath
                .toTry()
                .map(File::new)
                .filter(
                    File::exists,
                    () -> new CloudPlatformException(
                        String.format(preparedErrorMessage, "certificate", CERT_ENVIRONMENT_VARIABLE)));

        final Try<File> maybeKeyFile =
            maybeKeyPath
                .toTry()
                .map(File::new)
                .filter(
                    File::exists,
                    () -> new CloudPlatformException(
                        String.format(preparedErrorMessage, "key", KEY_ENVIRONMENT_VARIABLE)));

        if( maybeCertFile.isFailure() ) {
            return Try.failure(maybeCertFile.getCause());
        }
        if( maybeKeyFile.isFailure() ) {
            return Try.failure(maybeKeyFile.getCause());
        }

        final File certFile = maybeCertFile.get();
        if( cache.getLastModified() != null && certFile.lastModified() == cache.getLastModified() ) {
            log.trace("Certificate file is unchanged, using cached SSL context.");
            return Try.success(cache.getLastCachedValue());
        }
        cache.setLastModified(certFile.lastModified());

        try(
            Reader certReader = Files.newBufferedReader(certFile.toPath(), StandardCharsets.UTF_8);
            Reader keyReader = Files.newBufferedReader(maybeKeyFile.get().toPath(), StandardCharsets.UTF_8) ) {
            return tryGetContext(certReader, keyReader);
        }
        catch( final IOException e ) {
            return Try
                .failure(
                    new CloudPlatformException(
                        "Should not happen: Instance identity certificate files were removed while reading.",
                        e));
        }
    }

    void setCacheDuration( @Nonnull final Duration duration )
    {
        cache.setCacheDuration(duration.toMillis());
    }

    static final class Cache
    {
        @Setter
        private long cacheDuration = CACHE_DURATION_IN_MILLIS;
        private SSLContext context = null;
        private Long cachedAt = null;
        @Getter
        @Setter
        private Long lastModified = null;

        @Nonnull
        Option<SSLContext> get()
        {
            if( context != null && cacheDuration > 0L && cachedAt + cacheDuration >= System.currentTimeMillis() ) {
                return Option.some(context);
            }
            return Option.none();
        }

        synchronized void set( final SSLContext context )
        {
            this.context = context;
            cachedAt = System.currentTimeMillis();
        }

        @Nullable
        SSLContext getLastCachedValue()
        {
            return context;
        }
    }
}
