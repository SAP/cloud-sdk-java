package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.Header;

@Deprecated
public class RfmRequestTest
{
    private static final String FUNCTION_NAME = "A";

    @Test( expected = IllegalArgumentException.class )
    public void testValidFunctionNameWithCommittingConstructor()
    {
        new RfmRequest("BAPI_A");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testValidFunctionName()
    {
        new RfmRequest("BAPI_A", false);
    }

    @Test
    public void testHeaderParameters()
    {
        final RfmRequest request = new RfmRequest(FUNCTION_NAME, false).withHeader("key", "value");

        assertThat(request.getCustomHttpHeaders()).contains(new Header("key", "value"));
    }

    @Test
    public void testHeaderParametersCopy()
    {
        final RfmRequest firstRequest = new RfmRequest(FUNCTION_NAME, false).withHeader("someHeader", "someValue");

        final RfmRequest secondRequest = new RfmRequest(FUNCTION_NAME, false).withSameCustomHttpHeadersAs(firstRequest);

        assertThat(secondRequest.getCustomHttpHeaders()).contains(new Header("someHeader", "someValue"));
    }
}
