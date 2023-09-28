package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;
import com.sap.conn.jco.JCoRepository;

@Deprecated
public class RemoteFunctionCacheTest
{
    @Test
    public void testClearCacheForHttpDestination()
        throws RemoteFunctionException
    {
        final HttpDestination httpDestination = DefaultHttpDestination.builder("foo").build();

        RemoteFunctionCache.clearCache(httpDestination);

        //nothing happens, hence no test assertion
    }

    @Test
    public void testClearCacheForRfcDestination()
        throws RemoteFunctionException
    {
        final JCoRepository jCoRepository = mock(JCoRepository.class);

        RemoteFunctionCache
            .clearCacheInternal(DefaultDestination.builder().name("name").build().asRfc(), jCoRepository, null);

        Mockito.verify(jCoRepository).clear();
    }

    @Test
    public void testClearCacheForHttpDestinationWithFunctionName()
        throws RemoteFunctionException
    {
        final HttpDestination httpDestination = DefaultHttpDestination.builder("foo").build();

        final String functionName = "BAPI_FOO";

        RemoteFunctionCache.clearCache(httpDestination, functionName);

        //nothing happens, hence no test assertion
    }

    @Test
    public void testClearCacheForRfcDestinationWithFunctionName()
        throws RemoteFunctionException
    {
        final String functionName = "BAPI_FOO";

        final JCoRepository jCoRepository = mock(JCoRepository.class);

        RemoteFunctionCache
            .clearCacheInternal(DefaultDestination.builder().name("name").build().asRfc(), jCoRepository, functionName);

        Mockito.verify(jCoRepository).removeFunctionTemplateFromCache(ArgumentMatchers.eq(functionName));
    }

    @Test
    public void testClearCacheForDefaultDestination()
        throws RemoteFunctionException
    {
        RemoteFunctionCache.clearCache(mock(Destination.class));

        //nothing happens, hence no test assertion
    }
}
