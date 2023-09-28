package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import lombok.Data;
import lombok.Getter;

/**
 * Class that represents an authorization of a user.
 *
 * <ul>
 * <li>On SAP Business Technology Platform Cloud Foundry, authorizations correspond to scopes.</li>
 * </ul>
 *
 * @deprecated This class is deprecated and should no longer be used. It will remain for compatibility reasons but will
 *             no longer be supported. Please see release notes for more information.
 */
@Deprecated
@Data
public class Authorization
{
    /**
     * The name of this authorization.
     */
    @Getter
    @Nonnull
    private final String name;

    @Override
    @Nonnull
    public String toString()
    {
        return name;
    }
}
