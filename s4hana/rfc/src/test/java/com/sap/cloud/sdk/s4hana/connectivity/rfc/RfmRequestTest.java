package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.Header;

@Deprecated
class RfmRequestTest
{
    private static final String FUNCTION_NAME = "A";

    @Test
    void testValidFunctionNameWithCommittingConstructor()
    {
        assertThatThrownBy(() -> new RfmRequest("BAPI_A")).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testValidFunctionName()
    {
        assertThatThrownBy(() -> new RfmRequest("BAPI_A", false)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testHeaderParameters()
    {
        final RfmRequest request = new RfmRequest(FUNCTION_NAME, false).withHeader("key", "value");

        assertThat(request.getCustomHttpHeaders()).contains(new Header("key", "value"));
    }

    @Test
    void testHeaderParametersCopy()
    {
        final RfmRequest firstRequest = new RfmRequest(FUNCTION_NAME, false).withHeader("someHeader", "someValue");

        final RfmRequest secondRequest = new RfmRequest(FUNCTION_NAME, false).withSameCustomHttpHeadersAs(firstRequest);

        assertThat(secondRequest.getCustomHttpHeaders()).contains(new Header("someHeader", "someValue"));
    }
}
