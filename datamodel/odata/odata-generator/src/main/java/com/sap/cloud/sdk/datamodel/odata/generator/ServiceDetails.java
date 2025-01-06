/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.List;

import io.vavr.control.Option;

interface ServiceDetails
{
    String getServiceUrl();

    Info getInfo();

    ExternalDocs getExternalDocs();

    String getMinErpVersion();

    List<? extends ExternalOverview> getExtOverview();

    void setServiceUrl( final String serviceUrl );

    Option<? extends StateInfo> getStateInfo();

    boolean isDeprecated();

    enum State
    {
        Deprecated
    }

    interface Info
    {
        String getTitle();

        String getDescription();

        String getVersion();
    }

    interface ExternalDocs
    {
        String getDescription();

        String getUrl();
    }

    interface ExternalOverview
    {
        String getName();

        List<String> getValues();
    }

    interface StateInfo
    {
        State getState();

        String getDeprecationRelease();

        String getSuccessorApi();

        String getDeprecationDate();
    }
}
