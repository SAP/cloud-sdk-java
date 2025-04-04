package com.sap.cloud.sdk.datamodel.odatav4.generator;

import org.assertj.core.api.AutoCloseableSoftAssertions;

class CustomSoftAssertions extends AutoCloseableSoftAssertions
{
    public NamingContextAssert assertThat( final NamingContext actual )
    {
        return proxy(NamingContextAssert.class, NamingContext.class, actual);
    }
}
