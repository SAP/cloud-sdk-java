/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import static com.sap.cloud.sdk.datamodel.odata.utility.EdmxValidator.Version.V2;
import static com.sap.cloud.sdk.datamodel.odata.utility.EdmxValidator.Version.V4;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import lombok.RequiredArgsConstructor;

class EdmxValidatorTest
{
    private static final boolean QUALIFIED = true;
    private static final boolean NOT_QUALIFIED = false;

    @RequiredArgsConstructor
    enum TestCase
    {
        ODataV2with1(
            V2,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"1\"></edmx:DataServices></edmx:Edmx>"),

        ODataV2with2(
            V2,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"2\"></edmx:DataServices></edmx:Edmx>"),

        ODataV2with1dot0(
            V2,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"1.0\"></edmx:DataServices></edmx:Edmx>"),

        ODataV2with2dot0(
            V2,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"2.0\"></edmx:DataServices></edmx:Edmx>"),

        ODataV2with2dot0_INCOMPLETE(
            V2,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"2.0\">"),

        ODataV2with10(
            V2,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"10\"></edmx:DataServices></edmx:Edmx>"),

        ODataV2with20(
            V2,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"20\"></edmx:DataServices></edmx:Edmx>"),

        ODataV2with99(
            V2,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"99\"></edmx:DataServices></edmx:Edmx>"),

        ODataV2without(
            V2,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices></edmx:DataServices></edmx:Edmx>"),

        ODataV4with1(
            V4,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4with2(
            V4,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"2\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4with3(
            V4,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"3\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4with4(
            V4,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"4\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4with4dot0(
            V4,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4with4dot01(
            V4,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"4.01\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4with4dot01_INCOMPLETE(
            V4,
            QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"4.01\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"),

        ODataV4with40(
            V4,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"40\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4with99(
            V4,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"99\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>"),

        ODataV4without(
            V4,
            NOT_QUALIFIED,
            "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\"></edmx:Edmx>");

        private final EdmxValidator.Version version;
        private final boolean expectedQualified;
        private final String givenEdmx;
    }

    @ParameterizedTest
    @EnumSource( TestCase.class )
    void testIsQualified( final TestCase testCase, @TempDir final Path path )
        throws IOException
    {
        final Path file = path.resolve("test_" + testCase.name() + ".edmx");
        Files.write(file, testCase.givenEdmx.getBytes(StandardCharsets.UTF_8));

        final boolean qualified = EdmxValidator.isQualified(file.toFile(), testCase.version);
        assertThat(qualified).isEqualTo(testCase.expectedQualified);
    }
}
