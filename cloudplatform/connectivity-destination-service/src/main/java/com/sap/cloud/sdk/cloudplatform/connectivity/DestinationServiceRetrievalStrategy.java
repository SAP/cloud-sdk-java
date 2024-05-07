/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import lombok.Getter;

/**
 * Enumeration which represents the strategies for loading destinations in a multi-tenant application on SCP Cloud
 * Foundry. There can be destinations defined on the tenant hosting the application (provider), or on the tenant(s) that
 * have subscribed to the application (subscriber). Platform default behaviour is represented by null values.
 */
public enum DestinationServiceRetrievalStrategy
{

    /**
     * Only load destination from the provider's sub-account, regardless if subscribers have a destination of the same
     * name. There is no fallback if the destination is not found on the provider's sub-account.
     */
    ALWAYS_PROVIDER("AlwaysProvider"),

    /**
     * Only load destination from the current tenant's sub-account, regardless if the provider has a destination of the
     * same name. There is no fallback if the destination is not found on the subscriber's sub-account.
     * <p>
     * <b>Note:</b> This strategy assumes that the tenant returned by {@link TenantAccessor#getCurrentTenant()} is the
     * subscriber to get the destination for. This means if the {@code TenantAccessor} returns the provider tenant, the
     * destination will be searched for in the provider sub-account.
     */
    CURRENT_TENANT("CurrentTenant"),

    /**
     * Only load destination from the subscriber's sub-account, checking that the current tenant is an actual subscriber
     * and not the provider. There is no fallback if the destination is not found on the subscriber's sub-account.
     */
    ONLY_SUBSCRIBER("OnlySubscriber");

    @Getter
    @Nonnull
    private final String identifier;

    DestinationServiceRetrievalStrategy( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }

    @Override
    public String toString()
    {
        return identifier;
    }

    /**
     * Get the strategy by its identifier.
     *
     * @param identifier
     *            The string representation of a strategy.
     * @return The strategy enum value.
     * @since 5.0.0
     */
    @Nullable
    public static DestinationServiceRetrievalStrategy ofIdentifier( @Nullable final String identifier )
    {
        return Stream.of(values()).filter(s -> s.getIdentifier().equalsIgnoreCase(identifier)).findFirst().orElse(null);
    }
}
