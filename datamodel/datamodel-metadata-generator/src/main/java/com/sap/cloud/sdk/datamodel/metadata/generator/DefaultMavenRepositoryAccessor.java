package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultMavenRepositoryAccessor implements MavenRepositoryAccessor
{
    private static final String MAVEN_CENTRAL_BASE_URL = "https://repo1.maven.org/maven2";
    @Setter( AccessLevel.PACKAGE )
    private String mavenRepositoryBaseUrl = MAVEN_CENTRAL_BASE_URL;
    private static final int HTTP_CODE_OK = 200;

    private final Map<MavenCoordinate, String> resultCache = new ConcurrentHashMap<>();

    /**
     * Reads the latest version of a Maven module from the default Maven repository (Maven Central).
     * <p>
     * Uses a thread-safe cache for subsequent calls of the same {@link MavenCoordinate}.
     *
     * @param mavenCoordinate
     *            The coordinate of the Maven module
     * @return A {@link Try} wrapping the latest version of the related module
     */
    @Override
    @Nonnull
    public Try<String> getLatestModuleVersion( @Nonnull final MavenCoordinate mavenCoordinate )
    {
        return Try.of(() -> resultCache.computeIfAbsent(mavenCoordinate, this::obtainLatestVersionViaHttp));
    }

    private String obtainLatestVersionViaHttp( final MavenCoordinate mavenCoordinate )
    {
        final String relativePath =
            mavenCoordinate.getGroupId().replace(".", "/")
                + "/"
                + mavenCoordinate.getArtifactId()
                + "/"
                + "maven-metadata.xml";
        final URI targetUri = URI.create(mavenRepositoryBaseUrl + "/" + relativePath);

        log.debug("Accessing Maven repository with URI {}.", targetUri);

        final HttpResponse response;
        try {
            response = HttpClientAccessor.getHttpClient().execute(new HttpGet(targetUri));
        }
        catch( final IOException e ) {
            throw new MetadataGenerationException(
                "Failed to determine latest module version for " + mavenCoordinate + ".",
                e);
        }
        if( response.getStatusLine().getStatusCode() != HTTP_CODE_OK ) {
            final int statusCode = response.getStatusLine().getStatusCode();
            log
                .debug(
                    "Received HTTP response with status code {} from Maven repository with URI {}.",
                    statusCode,
                    targetUri);
            throw new MetadataGenerationException(
                "Received HTTP status code "
                    + statusCode
                    + " while determining the latest module version for "
                    + mavenCoordinate
                    + ".");
        }
        final String latestVersion;
        try {
            final InputStream stream = response.getEntity().getContent();
            final JsonNode tree = new XmlMapper().readTree(stream);
            latestVersion = tree.path("versioning").path("latest").asText();
        }
        catch( final IOException e ) {
            throw new MetadataGenerationException(
                "Failed to determine latest module version for " + mavenCoordinate + ".",
                e);
        }

        if( StringUtils.isBlank(latestVersion) ) {
            throw new MetadataGenerationException(
                "Could not find expected XML field (versioning -> latest) while determining the latest module version for "
                    + mavenCoordinate
                    + ".");
        }
        return latestVersion;
    }
}
