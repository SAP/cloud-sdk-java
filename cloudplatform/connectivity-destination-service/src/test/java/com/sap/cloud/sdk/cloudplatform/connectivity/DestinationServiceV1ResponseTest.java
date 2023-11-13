package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceV1Response.DestinationAuthToken;

class DestinationServiceV1ResponseTest
{
    private static final Gson gson = new Gson();

    @Test
    void testParsing()
    {
        final String payload =
            "{\n"
                + "  \"owner\": {\n"
                + "    \"SubaccountId\": \"1234\",\n"
                + "    \"InstanceId\": null\n"
                + "  },\n"
                + "  \"destinationConfiguration\": {\n"
                + "    \"Name\": \"DummySAPAssertionSSODestination\",\n"
                + "    \"Type\": \"HTTP\",\n"
                + "    \"URL\": \"https://some.system.com\",\n"
                + "    \"SystemUser\": \"SomeUser\",\n"
                + "    \"Authentication\": \"SAPAssertionSSO\"\n"
                + "  },\n"
                + "  \"authTokens\": [\n"
                + "    {\n"
                + "      \"type\": \"MYSAPSSO2\",\n"
                + "      \"value\": \"someString\",\n"
                + "      \"http_header\": {\n"
                + "        \"key\": \"Cookie\",\n"
                + "        \"value\": \"MYSAPSSO2=someString\"\n"
                + "      }\n"
                + "    },\n"
                + "    { \"error\":\"Simulated failure.\" }\n"
                + "  ]\n"
                + "}";

        final DestinationServiceV1Response sut = gson.fromJson(payload, DestinationServiceV1Response.class);

        final Map<String, String> expectedConfig = new HashMap<>();
        expectedConfig.put("Name", "DummySAPAssertionSSODestination");
        expectedConfig.put("Type", "HTTP");
        expectedConfig.put("URL", "https://some.system.com");
        expectedConfig.put("SystemUser", "SomeUser");
        expectedConfig.put("Authentication", "SAPAssertionSSO");

        assertThat(sut.getDestinationConfiguration()).isEqualTo(expectedConfig);

        final DestinationAuthToken expectedSuccessfulToken = new DestinationAuthToken();
        expectedSuccessfulToken.setType("MYSAPSSO2");
        expectedSuccessfulToken.setValue("someString");
        expectedSuccessfulToken.setHttpHeaderSuggestion(new Header("Cookie", "MYSAPSSO2=someString"));

        final DestinationAuthToken expectedFailureToken = new DestinationAuthToken();
        expectedFailureToken.setError("Simulated failure.");

        // Note that this implicitly also asserts that DestinationAuthToken implements equals/hashCode
        // this is important for the HttpClientCache to work correctly
        assertThat(sut.getAuthTokens()).containsExactly(expectedSuccessfulToken, expectedFailureToken);
    }
}
