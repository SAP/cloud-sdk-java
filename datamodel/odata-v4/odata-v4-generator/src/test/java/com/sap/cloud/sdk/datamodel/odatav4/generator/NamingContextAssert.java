/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import org.assertj.core.api.AbstractAssert;

class NamingContextAssert extends AbstractAssert<NamingContextAssert, NamingContext>
{
    // needs to be visible, otherwise the CustomSoftAssertions cannot create this class
    NamingContextAssert( final NamingContext actual )
    {
        super(actual, NamingContextAssert.class);
    }

    public static NamingContextAssert assertThat( final NamingContext actual )
    {
        return new NamingContextAssert(actual);
    }

    NamingContextAssert doesNotContain( final String proposedName )
    {
        isNotNull();

        if( actual.alreadyUses(proposedName) ) {
            failWithMessage("The naming context already contains the name <%s> but was expected not to.", proposedName);
        }
        return this;
    }

    NamingContextAssert contains( final String proposedName )
    {
        isNotNull();

        if( !actual.alreadyUses(proposedName) ) {
            failWithMessage("The naming context does not contain the name <%s> but was expected to.", proposedName);
        }
        return this;
    }
}
