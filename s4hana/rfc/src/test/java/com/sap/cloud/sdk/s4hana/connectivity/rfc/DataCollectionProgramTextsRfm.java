/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

@Deprecated
public class DataCollectionProgramTextsRfm
{
    private static final String RFC_NAME = "FCXL_GET_PROGRAM_TEXTS";

    public RfmRequestResult execute( final Destination destination )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final RfmRequest rfcRequest =
            new RfmRequest(RFC_NAME, false)
                .withExporting("ED_LANGU", "SYLANGU")
                .withImporting("EXCEPTION", "FCXL_MESSG-MESSAGE")
                .withTable("E_PROGRAM_TEXTS", "FCXL_PTEXT")
                .end();
        return rfcRequest.execute(destination);
    }
}
