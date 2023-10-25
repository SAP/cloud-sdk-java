/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sap.cloud.environment.servicebinding.SapServiceOperatorLayeredServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.SapServiceOperatorServiceBindingIoAccessor;
import com.sap.cloud.environment.servicebinding.SapVcapServicesServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding;
import com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBindingAccessor;
import com.sap.cloud.sdk.cloudplatform.exception.MultipleServiceBindingsException;
import com.sap.cloud.sdk.cloudplatform.exception.NoServiceBindingException;

import io.vavr.control.Option;

public class ScpCfCloudPlatformTest
{
    private static final String vcapServices =
        "{\n"
            + "  \"xsuaa\": [\n"
            + "   {\n"
            + "    \"binding_name\": null,\n"
            + "    \"credentials\": {\n"
            + "     \"clientid\": \"sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247\",\n"
            + "     \"clientsecret\": \"SS9U5TeRAJrg86S9Il6txqXt9fc=\",\n"
            + "     \"identityzone\": \"d123456\",\n"
            + "     \"identityzoneid\": \"a61ed66e-3bc1-4013-ab96-477bd8bc83df\",\n"
            + "     \"sburl\": \"https://internal-xsuaa.authentication.sap.hana.ondemand.com\",\n"
            + "     \"tenantid\": \"a61ed66e-3bc1-4013-ab96-477bd8bc83df\",\n"
            + "     \"tenantmode\": \"dedicated\",\n"
            + "     \"trustedclientidsuffix\": \"|na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247\",\n"
            + "     \"uaadomain\": \"authentication.sap.hana.ondemand.com\",\n"
            + "     \"url\": \"https://d123456.authentication.sap.hana.ondemand.com\",\n"
            + "     \"verificationkey\": \"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx/jN5v1mp/TVn9nTQoYVIUfCsUDHa3Upr5tDZC7mzlTrN2PnwruzyS7w1Jd+StqwW4/vn87ua2YlZzU8Ob0jR4lbOPCKaHIi0kyNtJXQvQ7LZPG8epQLbx0IIP/WLVVVtB8bL5OWuHma3pUnibbmATtbOh5LksQ2zLMngEjUF52JQyzTpjoQkahp0BNe/drlAqO253keiY63FL6belKjJGmSqdnotSXxB2ym+HQ0ShaNvTFLEvi2+ObkyjGWgFpQaoCcGq0KX0y0mPzOvdFsNT+rBFdkHiK+Jl638Sbim1z9fItFbH9hiVwY37R9rLtH1YKi3PuATMjf/DJ7mUluDQIDAQAB-----END PUBLIC KEY-----\",\n"
            + "     \"xsappname\": \"na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247\"\n"
            + "    },\n"
            + "    \"instance_name\": \"xsuaa-broker\",\n"
            + "    \"label\": \"xsuaa\",\n"
            + "    \"name\": \"xsuaa-broker\",\n"
            + "    \"plan\": \"broker\",\n"
            + "    \"provider\": null,\n"
            + "    \"syslog_drain_url\": null,\n"
            + "    \"tags\": [\n"
            + "     \"xsuaa\"\n"
            + "    ],\n"
            + "    \"volume_mounts\": []\n"
            + "   },\n"
            + "   {\n"
            + "    \"binding_name\": null,\n"
            + "    \"credentials\": {\n"
            + "     \"clientid\": \"sb-jwt-app-d123456!t3247\",\n"
            + "     \"clientsecret\": \"vBzoCFelOR3RKVzAnErQwIpMNCs=\",\n"
            + "     \"identityzone\": \"d123456\",\n"
            + "     \"identityzoneid\": \"a61ed66e-3bc1-4013-ab96-477bd8bc83df\",\n"
            + "     \"sburl\": \"https://internal-xsuaa.authentication.sap.hana.ondemand.com\",\n"
            + "     \"tenantid\": \"a61ed66e-3bc1-4013-ab96-477bd8bc83df\",\n"
            + "     \"tenantmode\": \"shared\",\n"
            + "     \"uaadomain\": \"authentication.sap.hana.ondemand.com\",\n"
            + "     \"url\": \"https://d123456.authentication.sap.hana.ondemand.com\",\n"
            + "     \"verificationkey\": \"-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx/jN5v1mp/TVn9nTQoYVIUfCsUDHa3Upr5tDZC7mzlTrN2PnwruzyS7w1Jd+StqwW4/vn87ua2YlZzU8Ob0jR4lbOPCKaHIi0kyNtJXQvQ7LZPG8epQLbx0IIP/WLVVVtB8bL5OWuHma3pUnibbmATtbOh5LksQ2zLMngEjUF52JQyzTpjoQkahp0BNe/drlAqO253keiY63FL6belKjJGmSqdnotSXxB2ym+HQ0ShaNvTFLEvi2+ObkyjGWgFpQaoCcGq0KX0y0mPzOvdFsNT+rBFdkHiK+Jl638Sbim1z9fItFbH9hiVwY37R9rLtH1YKi3PuATMjf/DJ7mUluDQIDAQAB-----END PUBLIC KEY-----\",\n"
            + "     \"xsappname\": \"jwt-app-d123456!t3247\"\n"
            + "    },\n"
            + "    \"instance_name\": \"my-xsuaa\",\n"
            + "    \"label\": \"xsuaa\",\n"
            + "    \"name\": \"my-xsuaa\",\n"
            + "    \"plan\": \"application\",\n"
            + "    \"provider\": null,\n"
            + "    \"syslog_drain_url\": null,\n"
            + "    \"tags\": [\n"
            + "     \"xsuaa\"\n"
            + "    ],\n"
            + "    \"volume_mounts\": []\n"
            + "   }\n"
            + "  ]\n"
            + " }";

    private static final String vcapServicesMultiXsuaaSamePlan =
        "{\n"
            + "  \"xsuaa\": [\n"
            + "   {\n"
            + "    \"credentials\": {\n"
            + "     \"clientid\": \"sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247\",\n"
            + "     \"xsappname\": \"na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247\"\n"
            + "    },\n"
            + "    \"plan\": \"application\"\n"
            + "   },\n"
            + "   {\n"
            + "    \"credentials\": {\n"
            + "     \"clientid\": \"sb-jwt-app-d123456!t3247\",\n"
            + "     \"xsappname\": \"jwt-app-d123456!t3247\"\n"
            + "    },\n"
            + "    \"plan\": \"application\"\n"
            + "   }\n"
            + "  ]\n"
            + " }";

    private static final String vcapServicesMultiXsuaaUnifiedBroker =
        "{\n"
            + "  \"xsuaa\": [\n"
            + "   {\n"
            + "    \"credentials\": {\n"
            + "     \"clientid\": \"sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247\",\n"
            + "     \"xsappname\": \"na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247\"\n"
            + "    },\n"
            + "    \"plan\": \"application\"\n"
            + "   },\n"
            + "   {\n"
            + "    \"credentials\": {\n"
            + "     \"clientid\": \"sb-jwt-app-d123456!t3247\",\n"
            + "     \"xsappname\": \"jwt-app-d123456!t3247\"\n"
            + "    },\n"
            + "    \"plan\": \"application\"\n"
            + "   },\n"
            + "   {\n"
            + "    \"credentials\": {\n"
            + "     \"clientid\": \"sb-jwt-app-d123456!t3247\",\n"
            + "     \"xsappname\": \"unified-broker-d123456!t3247\"\n"
            + "    },\n"
            + "    \"plan\": \"broker\"\n"
            + "   }\n"
            + "  ]\n"
            + " }";

    private static final String vcapApplication =
        "{\n"
            + "		\"cf_api\": \"https://api.cf.eu10.hana.ondemand.com\",\n"
            + "		\"limits\": {\n"
            + "			\"fds\": 16384,\n"
            + "			\"mem\": 2048,\n"
            + "			\"disk\": 1024\n"
            + "		},\n"
            + "		\"application_name\": \"sample_application\",\n"
            + "		\"application_uris\": [\n"
            + "			\"sample_application.cfapps.eu10.hana.ondemand.com\"\n"
            + "		],\n"
            + "		\"name\": \"sample_application\",\n"
            + "		\"space_name\": \"Platform-Dev\",\n"
            + "		\"space_id\": \"ecb2bd77-84b0-4b04-9bc6-451c13d8f24f\",\n"
            + "		\"organization_id\": \"dc77d9e3-c67e-4c4e-9336-a930a3e9d3b1\",\n"
            + "		\"organization_name\": \"organization\",\n"
            + "		\"uris\": [\n"
            + "			\"sample_application.cfapps.eu10.hana.ondemand.com\"\n"
            + "		],\n"
            + "		\"users\": james,\n"
            + "		\"process_id\": \"bc150846-972c-4cff-a3db-abb3f9a9d66d\",\n"
            + "		\"process_type\": \"web\",\n"
            + "		\"application_id\": \"bc150846-972c-4cff-a3db-abb3f9a9d66d\",\n"
            + "		\"version\": \"0dff8d5e-39c2-443b-95d4-5f855394c09b\",\n"
            + "		\"application_version\": \"0dff8d5e-39c2-443b-95d4-5f855394c09b\"\n"
            + "	}";

    private ServiceBindingAccessor oldFallbackAccessor;

    @Before
    public void before()
    {
        ScpCfCloudPlatform.invalidateCaches();
        oldFallbackAccessor = DefaultServiceBindingAccessor.getInstance();
    }

    @After
    public void resetServiceBindingAccessor()
    {
        DefaultServiceBindingAccessor.setInstance(oldFallbackAccessor);
    }

    @Test
    public void getInstanceReturnsInstance()
    {
        assertThat(ScpCfCloudPlatform.getInstanceOrThrow()).isInstanceOf(ScpCfCloudPlatform.class);
    }

    @Test
    public void getApplicationProperties()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(new ScpCfCloudPlatform());
        scpCfCloudPlatform
            .setEnvironmentVariableReader(Collections.singletonMap("VCAP_APPLICATION", vcapApplication)::get);

        final String applicationName = scpCfCloudPlatform.getApplicationName();
        final String applicationUrl = scpCfCloudPlatform.getApplicationUrl();
        final String applicationProcessId = scpCfCloudPlatform.getApplicationProcessId();

        assertThat(applicationName).isEqualTo("sample_application");
        assertThat(applicationUrl).isEqualTo("sample_application.cfapps.eu10.hana.ondemand.com");
        assertThat(applicationProcessId).isEqualTo("bc150846-972c-4cff-a3db-abb3f9a9d66d");
    }

    @Test
    public void getXsuaaServiceCredentialsThrowsOnMultipleBindingsSamePlan()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        assertThatThrownBy(scpCfCloudPlatform::getXsuaaServiceCredentials)
            .isInstanceOf(MultipleServiceBindingsException.class);
    }

    @Test
    public void getXsuaaServiceCredentialsWithMultipleBindingsAndUnifiedBroker()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaUnifiedBroker)::get);

        final JsonObject xsuaaCredentials = scpCfCloudPlatform.getXsuaaServiceCredentials();

        assertThat(xsuaaCredentials.get("xsappname").getAsString()).isEqualTo("unified-broker-d123456!t3247");
        assertThat(xsuaaCredentials.get("clientid").getAsString()).isEqualTo("sb-jwt-app-d123456!t3247");
    }

    @Test
    public void getXsuaaServiceCredentialsDoesNotThrowSingleBindingPerPlan()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setServiceBindingAccessor(
                new SapVcapServicesServiceBindingAccessor(
                    Collections.singletonMap("VCAP_SERVICES", vcapServices)::get));

        assertThatCode(() -> scpCfCloudPlatform.getXsuaaServiceCredentials("application")).doesNotThrowAnyException();
    }

    @Test
    public void getXsuaaServiceCredentialsThrowsOnMultipleBindingsPerPlan()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        final String modifiedVcapServices = vcapServices.replace("\"plan\": \"broker\"", "\"plan\": \"application\"");
        scpCfCloudPlatform
            .setServiceBindingAccessor(
                new SapVcapServicesServiceBindingAccessor(
                    Collections.singletonMap("VCAP_SERVICES", modifiedVcapServices)::get));

        assertThatThrownBy(() -> scpCfCloudPlatform.getXsuaaServiceCredentials("application"))
            .isInstanceOf(MultipleServiceBindingsException.class);
    }

    @Test
    public void getXsuaaServiceCredentialsListDoesNotThrow()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setServiceBindingAccessor(
                new SapVcapServicesServiceBindingAccessor(
                    Collections.singletonMap("VCAP_SERVICES", vcapServices)::get));

        final List<JsonObject> xsuaaCreds = scpCfCloudPlatform.getXsuaaServiceCredentialsList();

        final List<String> expected = new ArrayList<>();
        expected.add("sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247");
        expected.add("sb-jwt-app-d123456!t3247");

        assertThat(xsuaaCreds).extracting(cred -> cred.get("clientid").getAsString()).isEqualTo(expected);
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtMatchingAudience()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        final String encodedJwt =
            JWT
                .create()
                .withAudience("na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247")
                .withClaim("client_id", "dummy")
                .sign(Algorithm.none());

        final JsonObject xsuaaCredentials = scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt));

        assertThat(xsuaaCredentials.get("clientid").getAsString())
            .isEqualTo("sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247");
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtMatchingAudienceContainingDotDueToXsuaaLogic()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        final String encodedJwt =
            JWT
                .create()
                .withAudience("na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247.restOfScopeThatContainedDot")
                .withClaim("client_id", "dummy")
                .sign(Algorithm.none());

        final JsonObject xsuaaCredentials = scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt));

        assertThat(xsuaaCredentials.get("clientid").getAsString())
            .isEqualTo("sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247");
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtMatchingAudienceFromScope()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        final String encodedJwt =
            JWT
                .create()
                .withArrayClaim("scope", new String[] { "na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247.my.scope.name" })
                .withClaim("client_id", "dummy")
                .sign(Algorithm.none());

        final JsonObject xsuaaCredentials = scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt));

        assertThat(xsuaaCredentials.get("clientid").getAsString())
            .isEqualTo("sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247");
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtMatchingClientId()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        final String encodedJwt =
            JWT
                .create()
                .withAudience("dummy")
                .withClaim("client_id", "sb-jwt-app-d123456!t3247")
                .sign(Algorithm.none());

        final JsonObject xsuaaCredentials = scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt));

        assertThat(xsuaaCredentials.get("clientid").getAsString()).isEqualTo("sb-jwt-app-d123456!t3247");
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtMatchingAudienceAndClientId()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        final String encodedJwt =
            JWT
                .create()
                .withAudience("jwt-app-d123456!t3247")
                .withClaim("client_id", "sb-jwt-app-d123456!t3247")
                .sign(Algorithm.none());

        final JsonObject xsuaaCredentials = scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt));

        assertThat(xsuaaCredentials.get("clientid").getAsString()).isEqualTo("sb-jwt-app-d123456!t3247");
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtMultipleMatches()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        final String encodedJwt =
            JWT
                .create()
                .withAudience("jwt-app-d123456!t3247")
                .withClaim("client_id", "sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247")
                .sign(Algorithm.none());

        assertThatThrownBy(() -> scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt)))
            .isExactlyInstanceOf(MultipleServiceBindingsException.class);
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtUnifiedBroker()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaUnifiedBroker)::get);

        final String encodedJwt =
            JWT
                .create()
                .withAudience("jwt-app-d123456!t3247", "unified-broker-d123456!t3247")
                .withClaim("client_id", "sb-na-1fb5571c-5667-4c87-b08c-c538d19dcb9f!b3247")
                .sign(Algorithm.none());

        final JsonObject xsuaaCredentials = scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt));

        assertThat(xsuaaCredentials.get("xsappname").getAsString()).isEqualTo("unified-broker-d123456!t3247");
        assertThat(xsuaaCredentials.get("clientid").getAsString()).isEqualTo("sb-jwt-app-d123456!t3247");
    }

    @Test
    public void getXsuaaServiceCredentialsWithJwtNoMatches()
    {
        final ScpCfCloudPlatform scpCfCloudPlatform = spy(ScpCfCloudPlatform.class);
        scpCfCloudPlatform
            .setEnvironmentVariableReader(
                Collections.singletonMap("VCAP_SERVICES", vcapServicesMultiXsuaaSamePlan)::get);

        final String encodedJwt =
            JWT.create().withAudience("dummy").withClaim("client_id", "dummy").sign(Algorithm.none());

        assertThatThrownBy(() -> scpCfCloudPlatform.getXsuaaServiceCredentials(JWT.decode(encodedJwt)))
            .isExactlyInstanceOf(NoServiceBindingException.class);
    }

    @Test
    public void getEnvironmentVariableTest()
    {
        final ImmutableMap<String, String> customEnvironmentVar = ImmutableMap.of("foo", "bar");

        final ScpCfCloudPlatformFacade facade = new ScpCfCloudPlatformFacade();
        final CloudPlatform platform1 = facade.tryGetCloudPlatform().get();
        platform1.setEnvironmentVariableReader(customEnvironmentVar::get);

        final ScpCfCloudPlatform platform2 = (ScpCfCloudPlatform) facade.tryGetCloudPlatform().get();

        assertThat(platform1).isEqualTo(platform2);
        assertThat(platform1 == platform2).isTrue();
        assertThat(platform2.getEnvironmentVariable("foo")).isEqualTo(Option.of("bar"));
    }

    @Test
    public void defaultServiceBindingAccessorsOnClassPath()
    {
        final List<Class<?>> defaultAccessorClasses =
            ServiceBindingAccessor
                .getInstancesViaServiceLoader()
                .stream()
                .map(Object::getClass)
                .collect(Collectors.toList());

        assertThat(defaultAccessorClasses)
            .containsExactlyInAnyOrder(
                SapVcapServicesServiceBindingAccessor.class,
                MegacliteServiceBindingAccessor.class, // Is present because of the dwcBindingDoesNotThrow test
                SapServiceOperatorServiceBindingIoAccessor.class,
                SapServiceOperatorLayeredServiceBindingAccessor.class);
    }

    @Test
    public void getServiceBindingAccessorFallsBackToDefaultAccessor()
    {
        final ServiceBindingAccessor fallbackAccessor = mock(ServiceBindingAccessor.class);
        DefaultServiceBindingAccessor.setInstance(fallbackAccessor);

        final ScpCfCloudPlatform sut = spy(ScpCfCloudPlatform.class);

        assertThat(sut.getServiceBindingAccessor()).isSameAs(fallbackAccessor);
        verify(sut, times(0)).newServiceBindingAccessorWithCustomEnvironmentVariableReader();
    }

    @Test
    public void getServiceBindingAccessorAfterSetServiceBindingAccessor()
    {
        final ServiceBindingAccessor specificAccessor = mock(ServiceBindingAccessor.class);

        final ScpCfCloudPlatform sut = spy(ScpCfCloudPlatform.class);
        sut.setServiceBindingAccessor(specificAccessor);

        assertThat(sut.getServiceBindingAccessor()).isSameAs(specificAccessor);
        verify(sut, times(0)).newServiceBindingAccessorWithCustomEnvironmentVariableReader();
    }

    @Test
    public void getServiceBindingAccessorAfterSetEnvironmentVariableReader()
    {
        final ServiceBindingAccessor defaultAccessor = mock(ServiceBindingAccessor.class);

        final ScpCfCloudPlatform sut = spy(ScpCfCloudPlatform.class);
        when(sut.newServiceBindingAccessorWithCustomEnvironmentVariableReader()).thenReturn(defaultAccessor);

        sut.setEnvironmentVariableReader(Collections.singletonMap("VCAP_SERVICES", "")::get);

        assertThat(sut.getServiceBindingAccessor()).isSameAs(defaultAccessor);
        verify(sut, times(1)).newServiceBindingAccessorWithCustomEnvironmentVariableReader();
    }

    @Test
    public void getServiceBindingAccessorAfterMultipleOverwrites()
    {
        final ServiceBindingAccessor fallbackAccessor = mock(ServiceBindingAccessor.class);
        final ServiceBindingAccessor specificAccessor = mock(ServiceBindingAccessor.class);
        final ServiceBindingAccessor defaultAccessor = mock(ServiceBindingAccessor.class);

        DefaultServiceBindingAccessor.setInstance(fallbackAccessor);

        final ScpCfCloudPlatform sut = spy(ScpCfCloudPlatform.class);
        when(sut.newServiceBindingAccessorWithCustomEnvironmentVariableReader()).thenReturn(defaultAccessor);

        // default: use the fallback accessor
        assertThat(sut.getServiceBindingAccessor()).isSameAs(fallbackAccessor);

        // setServiceBindingAccessor: use the specified accessor
        sut.setServiceBindingAccessor(specificAccessor);
        assertThat(sut.getServiceBindingAccessor()).isSameAs(specificAccessor);

        // setEnvironmentVariableReader: create a new default accessor
        sut.setEnvironmentVariableReader(Collections.singletonMap("VCAP_SERVICES", "")::get);
        assertThat(sut.getServiceBindingAccessor()).isSameAs(defaultAccessor);
        verify(sut, times(1)).newServiceBindingAccessorWithCustomEnvironmentVariableReader();

        // setServiceBindingAccessor: use the specified accessor again
        sut.setServiceBindingAccessor(specificAccessor);
        assertThat(sut.getServiceBindingAccessor()).isSameAs(specificAccessor);

        // setServiceBindingAccessor(null): use the fallback accessor again
        sut.setServiceBindingAccessor(null);
        assertThat(sut.getServiceBindingAccessor()).isSameAs(fallbackAccessor);
    }

    @Test
    public void serviceBindingsContainAllProperties()
    {
        final Map<String, Object> underlyingProperties = new HashMap<>();
        underlyingProperties.put("tags", Arrays.asList("tag-1", "tag-2"));

        final Map<String, Object> credentials = new HashMap<>();
        credentials.put("username", "user");
        credentials.put("password", "pass");

        final ServiceBinding serviceBinding =
            DefaultServiceBinding
                .builder()
                .copy(underlyingProperties)
                .withCredentials(credentials)
                .withName("binding-name")
                .withServiceName("service-name")
                .withServicePlan("service-plan")
                .withTagsKey("tags")
                .build();

        // sanity check
        assertThat(serviceBinding.getName()).hasValue("binding-name");
        assertThat(serviceBinding.getServiceName()).hasValue("service-name");
        assertThat(serviceBinding.getServicePlan()).hasValue("service-plan");
        assertThat(serviceBinding.getTags()).containsExactly("tag-1", "tag-2");
        assertThat(serviceBinding.getCredentials()).containsExactlyInAnyOrderEntriesOf(credentials);
        assertThat(serviceBinding.getKeys()).containsExactly("tags");

        // setup subject under test
        final ScpCfCloudPlatform sut = new ScpCfCloudPlatform();
        sut.setServiceBindingAccessor(() -> Collections.singletonList(serviceBinding));

        final Map<String, JsonArray> parsedVcapServices = sut.getVcapServices();

        // assert
        assertThat(parsedVcapServices).containsOnlyKeys("service-name");

        final JsonArray parsedBindings = parsedVcapServices.get("service-name");
        assertThat(parsedBindings).isNotNull();
        assertThat(parsedBindings.size()).isEqualTo(1);

        final JsonObject parsedBinding = parsedBindings.get(0).getAsJsonObject();
        assertThat(parsedBinding.keySet()).containsExactlyInAnyOrder("name", "label", "plan", "tags", "credentials");

        assertThat(parsedBinding.get("name").getAsString()).isEqualTo("binding-name");
        assertThat(parsedBinding.get("label").getAsString()).isEqualTo("service-name");
        assertThat(parsedBinding.get("plan").getAsString()).isEqualTo("service-plan");
        assertThat(parsedBinding.get("tags").getAsJsonArray().size()).isEqualTo(2);
        assertThat(parsedBinding.get("tags").getAsJsonArray().get(0).getAsString()).isEqualTo("tag-1");
        assertThat(parsedBinding.get("tags").getAsJsonArray().get(1).getAsString()).isEqualTo("tag-2");

        assertThat(parsedBinding.get("credentials").getAsJsonObject().keySet())
            .containsExactlyInAnyOrder("username", "password");
        assertThat(parsedBinding.get("credentials").getAsJsonObject().get("username").getAsString()).isEqualTo("user");
        assertThat(parsedBinding.get("credentials").getAsJsonObject().get("password").getAsString()).isEqualTo("pass");
    }

    @Test
    public void underlyingPropertiesWillBeOverwrittenInServiceBinding()
    {
        final Map<String, Object> underlyingProperties = new HashMap<>();
        underlyingProperties.put("name", "underlying-name");

        final ServiceBinding serviceBinding =
            DefaultServiceBinding
                .builder()
                .copy(underlyingProperties)
                .withName("constant-name")
                .withServiceName("service-name")
                .build();

        // sanity check
        assertThat(serviceBinding.getName()).hasValue("constant-name");
        assertThat(serviceBinding.get("name")).hasValue("underlying-name");
        assertThat(serviceBinding.getServiceName()).hasValue("service-name");

        // setup subject under test
        final ScpCfCloudPlatform sut = new ScpCfCloudPlatform();
        sut.setServiceBindingAccessor(() -> Collections.singletonList(serviceBinding));

        final Map<String, JsonArray> parsedVcapServices = sut.getVcapServices();

        // assert
        assertThat(parsedVcapServices).containsOnlyKeys("service-name");

        final JsonArray parsedBindings = parsedVcapServices.get("service-name");
        assertThat(parsedBindings).isNotNull();
        assertThat(parsedBindings.size()).isEqualTo(1);

        final JsonObject parsedBinding = parsedBindings.get(0).getAsJsonObject();
        assertThat(parsedBinding.keySet()).containsExactlyInAnyOrder("name", "label", "tags", "credentials");

        assertThat(parsedBinding.get("name").getAsString()).isEqualTo("constant-name");
        assertThat(parsedBinding.get("label").getAsString()).isEqualTo("service-name");
        assertThat(parsedBinding.get("tags").getAsJsonArray()).isEmpty();
        assertThat(parsedBinding.get("credentials").getAsJsonObject().asMap()).isEmpty();
    }

    @Test
    public void dwcBindingDoesNotThrow()
    {
        final MegacliteServiceBinding serviceBinding =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.DESTINATION)
                .providerConfiguration()
                .name("destination-paas")
                .version("v1")
                .build();

        // sanity check
        assertThat(serviceBinding.getName()).isEmpty();
        assertThat(serviceBinding.getServiceName()).hasValue(ServiceIdentifier.DESTINATION.toString());
        assertThat(serviceBinding.getServicePlan()).isEmpty();
        assertThat(serviceBinding.getTags()).isEmpty();
        // The dwcConfiguration cannot find the DWC_APPLICATION env var, so the credentials are empty
        assertThat(serviceBinding.getCredentials()).isEmpty();
        assertThat(serviceBinding.getKeys()).isEmpty();

        // setup subject under test
        final ScpCfCloudPlatform sut = new ScpCfCloudPlatform();
        sut.setServiceBindingAccessor(() -> Collections.singletonList(serviceBinding));

        // does not throw
        final Map<String, JsonArray> parsedVcapServices = sut.getVcapServices();

        // assert
        assertThat(parsedVcapServices).containsOnlyKeys(ServiceIdentifier.DESTINATION.toString());
    }
}
