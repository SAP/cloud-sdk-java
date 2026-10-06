package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

public class TripPinUtility
{
    public static HttpDestination getDestination()
        throws IOException
    {
        final HttpGet request = new HttpGet("https://services.odata.org/TripPinRESTierService/");
        final CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        final String serviceUrl = client.executeOpen(null, request, null).getLastHeader("Location").getValue();
        final Matcher tokenLookup = Pattern.compile("\\(S\\((.*?)\\)\\)").matcher(serviceUrl);

        assertThat(tokenLookup.find()).isTrue();

        return DefaultHttpDestination
            .builder("https://services.odata.org/TripPinRESTierService/(S(" + tokenLookup.group(1) + "))")
            .build();
    }

    public static HttpDestination getDestinationRW()
        throws IOException
    {
        final HttpGet request = new HttpGet("https://services.odata.org/V4/TripPinServiceRW/");
        final CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        final String serviceUrl = client.executeOpen(null, request, null).getLastHeader("Location").getValue();
        final Matcher tokenLookup = Pattern.compile("\\(S\\((.*?)\\)\\)").matcher(serviceUrl);

        assertThat(tokenLookup.find()).isTrue();

        return DefaultHttpDestination
            .builder("https://services.odata.org/V4/(S(" + tokenLookup.group(1) + "))")
            .build();
    }
}
