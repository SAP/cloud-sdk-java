package com.sap.cloud.sdk.cloudplatform.connectivity;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

final class ServiceBindingLibWorkarounds
{
    static final ServiceIdentifier IAS_IDENTIFIER = ServiceIdentifier.of("identity");

    private ServiceBindingLibWorkarounds()
    {
        throw new IllegalStateException("This utility class must not be instantiated.");
    }
}
