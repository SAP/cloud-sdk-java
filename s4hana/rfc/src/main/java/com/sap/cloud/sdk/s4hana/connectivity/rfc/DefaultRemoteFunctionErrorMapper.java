/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.HashMap;
import java.util.Map;

import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.AccessDeniedExceptionFactory;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.InvalidParameterExceptionFactory;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.MissingErpConfigurationExceptionFactory;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.MissingParameterFactory;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.NotImplementedExceptionFactory;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.ParameterNotFoundFactory;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionExceptionFactory;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.WrongCustomizingExceptionFactory;

class DefaultRemoteFunctionErrorMapper implements RemoteFunctionErrorMapper
{
    @Deprecated
    private final Map<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError, RemoteFunctionExceptionFactory<?>> errorToException =
        new HashMap<>();
    @Deprecated
    private final Map<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError, RemoteFunctionExceptionPriority> errorToPriority =
        new HashMap<>();

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    public
        Map<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError, RemoteFunctionExceptionFactory<?>>
        getMapping()
    {
        return errorToException;
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    public
        Map<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError, RemoteFunctionExceptionPriority>
        getPriorities()
    {
        return errorToPriority;
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public DefaultRemoteFunctionErrorMapper()
    {
        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KI,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("280"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("account"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BK,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("031"),
            new RemoteFunctionExceptionPriority(3),
            new MissingParameterFactory(null));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KI,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("222"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("cost center"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KI,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("235"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("CO object"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BK,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("048"),
            new RemoteFunctionExceptionPriority(3),
            new MissingParameterFactory(null));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BK,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("057"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("activity prices"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.F5,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("814"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("document type"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.SG,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("105"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("rate"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KI,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("154"),
            new RemoteFunctionExceptionPriority(2),
            new MissingParameterFactory("cost center"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BM,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("312"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("unit"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BM,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("311"),
            new RemoteFunctionExceptionPriority(2),
            new InvalidParameterExceptionFactory("unit"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KI,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("101"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("controlling area"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KI,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("152"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("activity type"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BK,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("025"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("quantity"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KW,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("407"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("activity prices"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.FAGL_LEDGER_CUST,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("025"),
            new RemoteFunctionExceptionPriority(2),
            new ParameterNotFoundFactory("ledger group"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BAPI4499,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("005"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("bank statement date"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BAPI4499,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("005"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("bank statement date"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.B1,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("537"),
            new RemoteFunctionExceptionPriority(1),
            new MissingParameterFactory("bank statement currency"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("040"),
            new RemoteFunctionExceptionPriority(1),
            new NotImplementedExceptionFactory("this feature is not enabled for FIN Cloud API 1702"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("072"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("073"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("074"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("075"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.F5,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("083"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("882"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("883"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("888"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("company code"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.AA,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("001"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("asset"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BAPI1022,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("021"),
            new RemoteFunctionExceptionPriority(1),
            new InvalidParameterExceptionFactory("selection field"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BAPI1022,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("003"),
            new RemoteFunctionExceptionPriority(1),
            new InvalidParameterExceptionFactory("master data field"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BAPI1022,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("004"),
            new RemoteFunctionExceptionPriority(1),
            new InvalidParameterExceptionFactory("depreciation area field"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.BAPI1022,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("005"),
            new RemoteFunctionExceptionPriority(1),
            new ParameterNotFoundFactory("depreciation area"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.FDC_POSTING_001,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("128"),
            new RemoteFunctionExceptionPriority(1),
            new MissingErpConfigurationExceptionFactory("workflow not enabled"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KI,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("591"),
            new RemoteFunctionExceptionPriority(1),
            new AccessDeniedExceptionFactory("controlling area"));

        addMapping(
            com.sap.cloud.sdk.s4hana.serialization.MessageClass.KE,
            new com.sap.cloud.sdk.s4hana.serialization.MessageNumber("081"),
            new RemoteFunctionExceptionPriority(1),
            new WrongCustomizingExceptionFactory("operating concern"));
    }

    @Deprecated
    private void addMapping(
        final com.sap.cloud.sdk.s4hana.serialization.MessageClass messageClass,
        final com.sap.cloud.sdk.s4hana.serialization.MessageNumber messageNumber,
        final RemoteFunctionExceptionPriority priority,
        final RemoteFunctionExceptionFactory<?> exceptionFactory )
    {
        final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError error =
            new com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError(messageClass, messageNumber);

        errorToException.put(error, exceptionFactory);
        errorToPriority.put(error, priority);
    }
}
