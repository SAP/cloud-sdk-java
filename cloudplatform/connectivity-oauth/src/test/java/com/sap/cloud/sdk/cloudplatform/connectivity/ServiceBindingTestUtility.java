/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

class ServiceBindingTestUtility
{
    @SafeVarargs
    @Nonnull
    static ServiceBinding bindingWithCredentials(
        @Nonnull final ServiceIdentifier identifier,
        @Nonnull final Map.Entry<String, Object>... entries )
    {
        final Map<String, Object> credentials = nestedMap(entries);

        return DefaultServiceBinding
            .builder()
            .copy(Collections.emptyMap())
            .withCredentials(credentials)
            .withServiceIdentifier(identifier)
            .build();
    }

    @SafeVarargs
    @Nonnull
    static Map<String, Object> nestedMap( @Nonnull final Map.Entry<String, Object>... entries )
    {
        final Map<String, Object> map = new HashMap<>();
        for( final Map.Entry<String, Object> entry : entries ) {
            final List<String> keyPath = Arrays.asList(entry.getKey().split("\\."));
            addNestedEntry(map, keyPath, entry.getValue());
        }

        return map;
    }

    @SuppressWarnings( "unchecked" )
    private static void addNestedEntry(
        @Nonnull final Map<String, Object> container,
        @Nonnull final List<String> keyPath,
        @Nonnull final Object value )
    {
        if( keyPath.isEmpty() ) {
            throw new IllegalArgumentException("The keyPath must not be empty.");
        }

        final String key = keyPath.get(0);
        if( keyPath.size() == 1 ) {
            container.put(key, value);
            return;
        }

        final Object maybeExistingValue = container.get(key);
        final Map<String, Object> nextContainer;
        if( maybeExistingValue == null ) {
            nextContainer = new HashMap<>();
            container.put(key, nextContainer);
        } else if( maybeExistingValue instanceof Map ) {
            nextContainer = (Map<String, Object>) maybeExistingValue;
        } else {
            throw new IllegalStateException(
                String
                    .format(
                        "There is already an entry called '%s' but it is not an instance of Map, so we cannot put a nested value inside.",
                        key));
        }

        addNestedEntry(nextContainer, keyPath.subList(1, keyPath.size()), value);
    }
}
