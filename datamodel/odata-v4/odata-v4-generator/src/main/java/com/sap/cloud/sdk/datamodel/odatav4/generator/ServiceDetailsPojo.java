package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.util.List;

import io.vavr.control.Option;
import lombok.Data;

@Data
class ServiceDetailsPojo implements ServiceDetails
{
    private String serviceUrl;
    private Info info;
    private ExternalDocs externalDocs;
    private String minErpVersion;
    private List<ExternalOverview> extOverview;
    private StateInfo stateInfo;
    private boolean isDeprecated;

    @Override
    public Option<StateInfo> getStateInfo()
    {
        return Option.of(stateInfo);
    }
}
