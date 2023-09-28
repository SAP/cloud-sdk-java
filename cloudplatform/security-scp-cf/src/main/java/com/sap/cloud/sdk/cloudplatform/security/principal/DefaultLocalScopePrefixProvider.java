package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.CloudPlatform;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;

import io.vavr.control.Try;

/**
 * This is the default strategy for determining the local scope prefix used by the SDK. It tries to get the xsappname of
 * the bound XSUAA service instance whose clientid matches the client_id of the current request's JWT.
 *
 * @deprecated To be removed without replacement. Please refer to release notes for more information.
 */
@Deprecated
public class DefaultLocalScopePrefixProvider implements LocalScopePrefixProvider
{
    private ScpCfCloudPlatform getCloudPlatform()
    {
        final CloudPlatform cloudPlatform = CloudPlatformAccessor.getCloudPlatform();

        if( !(cloudPlatform instanceof ScpCfCloudPlatform) ) {
            throw new ShouldNotHappenException(
                "The current Cloud platform is not an instance of "
                    + ScpCfCloudPlatform.class.getSimpleName()
                    + ". Please make sure to specify a dependency to com.sap.cloud.sdk.cloudplatform:cloudplatform-core-scp-cf.");
        }

        return (ScpCfCloudPlatform) cloudPlatform;
    }

    @Nonnull
    @Override
    public Try<String> getLocalScopePrefix()
    {
        return AuthTokenAccessor
            .tryGetCurrentToken()
            .map(
                authToken -> getCloudPlatform()
                    .getXsuaaServiceCredentials(authToken.getJwt())
                    .get("xsappname")
                    .getAsString()
                    + ".");
    }
}
