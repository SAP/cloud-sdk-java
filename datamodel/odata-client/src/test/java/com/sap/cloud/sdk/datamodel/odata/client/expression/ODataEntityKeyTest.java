/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;

class ODataEntityKeyTest
{
    ODataEntityKey keyV2;
    ODataEntityKey keyV4;

    @BeforeEach
    void setupKey()
    {
        keyV2 = new ODataEntityKey(ODataProtocol.V2);
        keyV4 = new ODataEntityKey(ODataProtocol.V4);
    }

    @Test
    void testEncoding()
    {
        keyV2.addKeyProperty("key", "/? #&value%$");
        keyV4.addKeyProperty("key", "/? #&value%$");

        // $ and & don't have to be encoded because they are not part of the query here
        // Instead they are part of the path. That's why they are not interpreted as query parameter delimiters
        assertThat(keyV2.toEncodedString()).isEqualTo("('%2F%3F%20%23&value%25$')");
        assertThat(keyV4.toEncodedString()).isEqualTo("('%2F%3F%20%23&value%25$')");
    }

    @Test
    void testEncodingForComplexKeys()
    {
        keyV2.addKeyProperty("key1#&%$", "/? #&value%$");
        keyV2.addKeyProperty("key2", "value");
        keyV4.addKeyProperty("key1#&%$", "/? #&value%$");
        keyV4.addKeyProperty("key2", "value");

        assertThat(keyV2.toEncodedString()).isEqualTo("(key1#&%$='%2F%3F%20%23&value%25$',key2='value')");
        assertThat(keyV4.toEncodedString()).isEqualTo("(key1#&%$='%2F%3F%20%23&value%25$',key2='value')");
    }

    @Test
    void testNoEncodingForSafeChars()
    {
        keyV2.addKeyProperty("key1", "-._~!$'(");
        keyV4.addKeyProperty("key1", "-._~!$'(");
        keyV2.addKeyProperty("key2", ")*,;&=@:");
        keyV4.addKeyProperty("key2", ")*,;&=@:");

        assertThat(keyV2.toEncodedString()).isEqualTo("(key1='-._~!$''(',key2=')*,;&=@:')");
        assertThat(keyV4.toEncodedString()).isEqualTo("(key1='-._~!$''(',key2=')*,;&=@:')");
    }

    @Test
    void testQuoteEscaping()
    {
        keyV2.addKeyProperty("key", "valu'e");
        keyV4.addKeyProperty("key", "valu'e");

        assertThat(keyV2.toEncodedString()).isEqualTo("('valu''e')");
        assertThat(keyV4.toEncodedString()).isEqualTo("('valu''e')");
    }

    @Test
    void testEmptyKey()
    {
        assertThat(keyV2).hasToString("()");
        assertThat(keyV2.toEncodedString()).isEqualTo("()");

        assertThat(keyV4).hasToString("()");
        assertThat(keyV4.toEncodedString()).isEqualTo("()");
    }

    @Test
    void testNullKey()
    {
        keyV2.addKeyProperty("key", null);
        assertThat(keyV2.toEncodedString()).isEqualTo("(null)");
        keyV4.addKeyProperty("key", null);
        assertThat(keyV4.toEncodedString()).isEqualTo("(null)");
    }

    @Test
    void testDataTypeSerialisationV4()
    {

        final String expected =
            "("
                + "stringParameter='test',"
                + "booleanParameter=true,"
                + "integerParameter=9000,"
                + "decimalParameter=3.14,"
                + "durationParameter=duration'PT8H',"
                + "dateTimeParameter=2019-12-25T08:00:00Z"
                + ")";

        keyV4.addKeyProperty("stringParameter", "test");
        keyV4.addKeyProperty("booleanParameter", true);
        keyV4.addKeyProperty("integerParameter", 9000);
        keyV4.addKeyProperty("decimalParameter", 3.14);
        keyV4.addKeyProperty("durationParameter", Duration.ofHours(8));
        keyV4.addKeyProperty("dateTimeParameter", LocalDateTime.of(2019, 12, 25, 8, 0, 0));

        assertThat(keyV4).hasToString(expected);
    }
}
