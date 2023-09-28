package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.Header;

@Deprecated
public class BapiRequestTest
{
    private static final String FUNCTION_NAME = "BAPI_SOMETHING";

    @Test( expected = IllegalArgumentException.class )
    public void testInvalidFunctionName()
    {
        new BapiRequest("A_RFC");
    }

    @Test( expected = IllegalArgumentException.class )
    public void testInvalidFunctionNameWithCommittingConstructor()
    {
        new BapiRequest("A_RFC", false);
    }

    @Test
    public void testHeaderParameters()
    {
        final BapiRequest request = new BapiRequest(FUNCTION_NAME, false).withHeader("key", "value");

        assertThat(request.getCustomHttpHeaders()).contains(new Header("key", "value"));
    }

    @Test
    public void testHeaderParametersCopy()
    {
        final BapiRequest firstRequest = new BapiRequest(FUNCTION_NAME, false).withHeader("someHeader", "someValue");

        final BapiRequest secondRequest =
            new BapiRequest(FUNCTION_NAME, false).withSameCustomHttpHeadersAs(firstRequest);

        assertThat(secondRequest.getCustomHttpHeaders()).contains(new Header("someHeader", "someValue"));
    }
}
