/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

class ODataQueryTest
{
    private static final String SERVICE_PATH = "/service/path";
    private static final String ENTITY_NAME = "EntityName";
    private static final String ENTITY_PATH = "/EntityName(123)";
    private static final ODataEntityKey ENTITY_KEY = new ODataEntityKey(ODataProtocol.V4).addKeyProperty("key", 123);

    @Test
    void testReadAll()
    {
        final ODataRequestRead read = new ODataRequestRead(SERVICE_PATH, ENTITY_NAME, "foo=bar", ODataProtocol.V4);
        assertThat(read.getResourcePath()).hasToString("/" + ENTITY_NAME);
        assertThat(read.getServicePath()).isEqualTo(SERVICE_PATH);
        assertThat(read.getRelativeUri()).hasToString("/service/path/EntityName?foo=bar");
        assertThat(read.getRequestQuery()).isEqualTo("foo=bar");
        assertThat(read.getQueryString()).isEqualTo("foo=bar");
        assertThat(read.toString()).isNotNull();
        assertThat(read).isEqualTo(new ODataRequestRead(SERVICE_PATH, ENTITY_NAME, "foo=bar", ODataProtocol.V4));

    }

    @Test
    void testDelete()
    {
        final ODataRequestDelete delete =
            new ODataRequestDelete(SERVICE_PATH, ENTITY_NAME, ENTITY_KEY, "", ODataProtocol.V4);

        assertThat(delete.getResourcePath()).hasToString(ENTITY_PATH);
        assertThat(delete.getServicePath()).isEqualTo(SERVICE_PATH);
        assertThat(delete.toString()).isNotNull();
        assertThat(delete)
            .isEqualTo(new ODataRequestDelete(SERVICE_PATH, ENTITY_NAME, ENTITY_KEY, "", ODataProtocol.V4));
        assertThat(delete.getHeaders()).contains(entry("Accept", Collections.singletonList("application/json")));
    }

    @Test
    void testByKey()
    {
        final ODataRequestReadByKey byKey =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_NAME, ENTITY_KEY, "foo=bar", ODataProtocol.V4);

        assertThat(byKey.getResourcePath()).hasToString(ENTITY_PATH);
        assertThat(byKey.getServicePath()).isEqualTo(SERVICE_PATH);
        assertThat(byKey.getRequestQuery()).isEqualTo("foo=bar");
        assertThat(byKey.getRelativeUri()).hasToString("/service/path/EntityName(123)?foo=bar");
        assertThat(byKey.getQueryString()).isEqualTo("foo=bar");
        assertThat(byKey.toString()).isNotNull();
        assertThat(byKey)
            .isEqualTo(new ODataRequestReadByKey(SERVICE_PATH, ENTITY_NAME, ENTITY_KEY, "foo=bar", ODataProtocol.V4));
    }

    @Test
    void testUpdate()
    {
        final ODataRequestUpdate update =
            new ODataRequestUpdate(
                SERVICE_PATH,
                ENTITY_NAME,
                ENTITY_KEY,
                "{\"foo\": \"bar\"}",
                UpdateStrategy.MODIFY_WITH_PATCH,
                "",
                ODataProtocol.V4);

        assertThat(update.getResourcePath()).hasToString(ENTITY_PATH);
        assertThat(update.getServicePath()).isEqualTo(SERVICE_PATH);
        assertThat(update.getSerializedEntity()).isEqualTo("{\"foo\": \"bar\"}");
        assertThat(update.getUpdateStrategy()).isEqualTo(UpdateStrategy.MODIFY_WITH_PATCH);
        assertThat(update.toString()).isNotNull();
        assertThat(update)
            .isEqualTo(
                new ODataRequestUpdate(
                    SERVICE_PATH,
                    ENTITY_NAME,
                    ENTITY_KEY,
                    "{\"foo\": \"bar\"}",
                    UpdateStrategy.MODIFY_WITH_PATCH,
                    "",
                    ODataProtocol.V4));
        assertThat(update)
            .isNotEqualTo(
                new ODataRequestUpdate(
                    SERVICE_PATH,
                    ENTITY_NAME,
                    ENTITY_KEY,
                    "{\"foo\": \"bar\"}",
                    UpdateStrategy.REPLACE_WITH_PUT,
                    "",
                    ODataProtocol.V4));

        update.setUpdateStrategy(UpdateStrategy.REPLACE_WITH_PUT);
        assertThat(update.getUpdateStrategy()).isEqualTo(UpdateStrategy.REPLACE_WITH_PUT);
        assertThat(update.getHeaders()).contains(entry("Accept", Collections.singletonList("application/json")));
    }

    @Test
    void testCreate()
    {
        final ODataRequestCreate byKey =
            new ODataRequestCreate(SERVICE_PATH, ENTITY_NAME, "{\"foo\": \"bar\"}", ODataProtocol.V4);

        assertThat(byKey.getResourcePath()).hasToString("/" + ENTITY_NAME);
        assertThat(byKey.getServicePath()).isEqualTo(SERVICE_PATH);
        assertThat(byKey.getRequestQuery()).isEmpty();
        assertThat(byKey.getSerializedEntity()).isEqualTo("{\"foo\": \"bar\"}");
        assertThat(byKey.toString()).isNotNull();

        final ODataRequestCreate compare =
            new ODataRequestCreate(SERVICE_PATH, ENTITY_NAME, "{\"foo\": \"bar\"}", ODataProtocol.V4);
        assertThat(byKey).isEqualTo(compare);
        assertThat(compare.getHeaders()).contains(entry("Accept", Collections.singletonList("application/json")));
    }

    @Test
    void testRequestsWithNullKey()
    {
        final ODataEntityKey nullEntityKey = new ODataEntityKey(ODataProtocol.V4).addKeyProperty("key", null);

        final ODataRequestReadByKey readByKey =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_NAME, nullEntityKey, "foo=bar", ODataProtocol.V4);

        final ODataRequestDelete deleteRequest =
            new ODataRequestDelete(SERVICE_PATH, ENTITY_NAME, nullEntityKey, "", ODataProtocol.V4);

        final ODataRequestUpdate updateRequest =
            new ODataRequestUpdate(
                SERVICE_PATH,
                ENTITY_NAME,
                nullEntityKey,
                "{\"foo\": \"bar\"}",
                UpdateStrategy.MODIFY_WITH_PATCH,
                "",
                ODataProtocol.V4);

        assertThat(readByKey.getRelativeUri()).hasToString("/service/path/EntityName(null)?foo=bar");
        assertThat(deleteRequest.getRelativeUri()).hasToString("/service/path/EntityName(null)");
        assertThat(updateRequest.getRelativeUri()).hasToString("/service/path/EntityName(null)");
    }
}
