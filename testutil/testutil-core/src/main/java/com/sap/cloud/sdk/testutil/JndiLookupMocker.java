package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;

interface JndiLookupMocker
{
    /**
     * Mocks a JNDI lookup.
     *
     * @param obj
     *            Object to be returned by the lookup.
     * @param name
     *            Name that is used for the lookup.
     */
    void mockJndiLookup( @Nonnull final Object obj, @Nonnull final String name );

    /**
     * Mocks a JNDI lookup.
     *
     * @param cls
     *            Class to be mocked.
     * @param name
     *            Name that is used for the lookup.
     *
     * @return Instance of the class to be mocked.
     */
    @Nonnull
    <T> T mockJndiLookup( @Nonnull final Class<T> cls, @Nonnull final String name );
}
