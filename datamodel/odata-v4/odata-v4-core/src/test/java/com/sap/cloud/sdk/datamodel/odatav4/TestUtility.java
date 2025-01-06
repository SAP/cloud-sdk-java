/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Resources;
import com.sap.cloud.sdk.testutil.TestConfigurationError;

public class TestUtility
{
    public static String readResourceFile( final Class<?> cls, final String resourceFileName )
    {
        try {
            final URL resourceUrl = getResourceUrl(cls, resourceFileName);

            return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        }
        catch( final IOException e ) {
            throw new TestConfigurationError(e);
        }
    }

    public static String readResourceFileCrlf( final Class<?> cls, final String resourceFileName )
    {
        return readResourceFile(cls, resourceFileName).replaceAll("(?<!\\r)\\n", "" + ((char) 13) + (char) 10);
    }

    static URL getResourceUrl( final Class<?> cls, final String resourceFileName )
    {
        final URL resourceUrl = cls.getClassLoader().getResource(cls.getSimpleName() + "/" + resourceFileName);

        if( resourceUrl == null ) {
            throw new TestConfigurationError("Cannot find resource file with name \"" + resourceFileName + "\".");
        }

        return resourceUrl;
    }
}
