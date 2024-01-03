/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import org.assertj.core.api.AutoCloseableSoftAssertions;

class CustomSoftAssertions extends AutoCloseableSoftAssertions
{
    public NamingContextAssert assertThat( final NamingContext actual )
    {
        return proxy(NamingContextAssert.class, NamingContext.class, actual);
    }
}
