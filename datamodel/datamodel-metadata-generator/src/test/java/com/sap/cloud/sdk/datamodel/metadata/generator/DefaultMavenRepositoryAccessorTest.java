/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okXml;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.io.Resources;

import io.vavr.control.Try;

public class DefaultMavenRepositoryAccessorTest
{
    private static final String RELATIVE_PATH = "/com/sap/cloud/sdk/datamodel/odata-generator/maven-metadata.xml";
    private static final MavenCoordinate MAVEN_COORDINATE =
        MavenCoordinate.builder().groupId("com.sap.cloud.sdk.datamodel").artifactId("odata-generator").build();

    @Test
    void testGetLatestModuleVersionSucceeds()
    {
        invokeTest(server -> {
            final String mavenMetadataXml =
                readResourceFile(DefaultMavenRepositoryAccessorTest.class, "correct-maven-metadata.xml");

            server.stubFor(get(urlEqualTo(RELATIVE_PATH)).willReturn(okXml(mavenMetadataXml)));

            final DefaultMavenRepositoryAccessor mavenRepositoryAccessor = new DefaultMavenRepositoryAccessor();
            mavenRepositoryAccessor.setMavenRepositoryBaseUrl(server.baseUrl());

            final Try<String> latestPublicVersionTry = mavenRepositoryAccessor.getLatestModuleVersion(MAVEN_COORDINATE);

            assertThat(latestPublicVersionTry.get()).isEqualTo("3.50.0");

            server.verify(getRequestedFor(urlEqualTo(RELATIVE_PATH)));
        });
    }

    @Test
    void testGetLatestModuleVersionFailsWithHttpError()
    {
        invokeTest(server -> {
            server.stubFor(get(urlEqualTo(RELATIVE_PATH)).willReturn(serverError()));

            final DefaultMavenRepositoryAccessor mavenRepositoryAccessor = new DefaultMavenRepositoryAccessor();
            mavenRepositoryAccessor.setMavenRepositoryBaseUrl(server.baseUrl());

            final Try<String> latestPublicVersionTry = mavenRepositoryAccessor.getLatestModuleVersion(MAVEN_COORDINATE);

            assertThat(latestPublicVersionTry.isFailure()).isTrue();
            assertThat(latestPublicVersionTry.getCause()).isInstanceOf(MetadataGenerationException.class);

            server.verify(getRequestedFor(urlEqualTo(RELATIVE_PATH)));
        });
    }

    @Test
    void testGetLatestModuleVersionFailsDueToTruncatedXml()
    {
        invokeTest(server -> {
            final String mavenMetadataXml =
                readResourceFile(DefaultMavenRepositoryAccessorTest.class, "truncated-maven-metadata.xml");

            server.stubFor(get(urlEqualTo(RELATIVE_PATH)).willReturn(okXml(mavenMetadataXml)));

            final DefaultMavenRepositoryAccessor mavenRepositoryAccessor = new DefaultMavenRepositoryAccessor();
            mavenRepositoryAccessor.setMavenRepositoryBaseUrl(server.baseUrl());

            final Try<String> latestPublicVersionTry = mavenRepositoryAccessor.getLatestModuleVersion(MAVEN_COORDINATE);

            assertThat(latestPublicVersionTry.isFailure()).isTrue();
            assertThat(latestPublicVersionTry.getCause()).isInstanceOf(MetadataGenerationException.class);
            assertThat(latestPublicVersionTry.getCause()).hasCauseInstanceOf(JsonParseException.class);

            server.verify(getRequestedFor(urlEqualTo(RELATIVE_PATH)));
        });
    }

    @Test
    void testGetLatestModuleVersionFailsDueToWrongXmlSchema()
    {
        invokeTest(server -> {
            final String mavenMetadataXml =
                readResourceFile(DefaultMavenRepositoryAccessorTest.class, "wrong-schema-maven-metadata.xml");

            server.stubFor(get(urlEqualTo(RELATIVE_PATH)).willReturn(okXml(mavenMetadataXml)));

            final DefaultMavenRepositoryAccessor mavenRepositoryAccessor = new DefaultMavenRepositoryAccessor();
            mavenRepositoryAccessor.setMavenRepositoryBaseUrl(server.baseUrl());

            final Try<String> latestPublicVersionTry = mavenRepositoryAccessor.getLatestModuleVersion(MAVEN_COORDINATE);

            assertThat(latestPublicVersionTry.isFailure()).isTrue();
            assertThat(latestPublicVersionTry.getCause()).isInstanceOf(MetadataGenerationException.class);

            server.verify(getRequestedFor(urlEqualTo(RELATIVE_PATH)));
        });
    }

    @Test
    void testSubsequentCallsCachePreviousResult()
    {
        invokeTest(server -> {
            final String mavenMetadataXml =
                readResourceFile(DefaultMavenRepositoryAccessorTest.class, "correct-maven-metadata.xml");

            server.stubFor(get(urlEqualTo(RELATIVE_PATH)).willReturn(okXml(mavenMetadataXml)));

            final DefaultMavenRepositoryAccessor mavenRepositoryAccessor = new DefaultMavenRepositoryAccessor();
            mavenRepositoryAccessor.setMavenRepositoryBaseUrl(server.baseUrl());

            final Try<String> firstTry = mavenRepositoryAccessor.getLatestModuleVersion(MAVEN_COORDINATE);

            assertThat(firstTry.get()).isEqualTo("3.50.0");

            final Try<String> secondTry = mavenRepositoryAccessor.getLatestModuleVersion(MAVEN_COORDINATE);

            assertThat(secondTry.get()).isEqualTo("3.50.0");

            server.verify(1, getRequestedFor(urlEqualTo(RELATIVE_PATH)));
        });
    }

    private void invokeTest( Consumer<WireMockServer> testLogic )
    {
        final WireMockServer server = new WireMockServer(new WireMockConfiguration().dynamicPort());
        try {
            server.start();

            testLogic.accept(server);
        }
        finally {
            server.stop();
        }
    }

    private static URL getResourceUrl( final Class<?> cls, final String resourceFileName )
    {
        final URL resourceUrl = cls.getClassLoader().getResource(cls.getSimpleName() + "/" + resourceFileName);

        if( resourceUrl == null ) {
            throw new IllegalStateException("Cannot find resource file with name \"" + resourceFileName + "\".");
        }

        return resourceUrl;
    }

    private static String readResourceFile( final Class<?> cls, final String resourceFileName )
    {
        try {
            final URL resourceUrl = getResourceUrl(cls, resourceFileName);

            return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        }
        catch( final IOException e ) {
            throw new IllegalStateException(e);
        }
    }
}
