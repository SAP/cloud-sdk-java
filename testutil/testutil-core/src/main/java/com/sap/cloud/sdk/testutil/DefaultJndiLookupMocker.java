package com.sap.cloud.sdk.testutil;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.naming.JndiLookupFacade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultJndiLookupMocker implements JndiLookupMocker
{
    private final Supplier<JndiLookupFacade> resetJndiLookupFacade;

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, Object> objectsByName = new HashMap<>();

    @Override
    public void mockJndiLookup( @Nonnull final Object obj, @Nonnull final String name )
    {
        resetJndiLookupFacade.get();
        objectsByName.put(name, obj);
    }

    @Nonnull
    @Override
    public <T> T mockJndiLookup( @Nonnull final Class<T> cls, @Nonnull final String name )
    {
        final T mocked = mock(cls);
        mockJndiLookup(mocked, name);
        return mocked;
    }
}
