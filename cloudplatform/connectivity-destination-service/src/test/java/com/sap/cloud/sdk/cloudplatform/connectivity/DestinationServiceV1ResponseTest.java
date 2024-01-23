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
        final String payload = """
            {
              "owner": {
                "SubaccountId": "1234",
                "InstanceId": null
              },
              "destinationConfiguration": {
                "Name": "DummySAPAssertionSSODestination",
                "Type": "HTTP",
                "URL": "https://some.system.com",
                "SystemUser": "SomeUser",
                "Authentication": "SAPAssertionSSO"
              },
              "authTokens": [
                {
                  "type": "MYSAPSSO2",
                  "value": "someString",
                  "http_header": {
                    "key": "Cookie",
                    "value": "MYSAPSSO2=someString"
                  }
                },
                { "error":"Simulated failure." }
              ]
            }
            """;

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
