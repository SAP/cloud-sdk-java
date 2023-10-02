/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.test;

import java.util.List;
import com.sap.cloud.sdk.datamodel.metadata.generator.JavaServiceMethodResolverTest;

public abstract class ServiceEntity<EntityT> extends JavaServiceMethodResolverTest.ThingService {
    public RequestBuilderPet<Animal> pet(Animal animal) {
        return null;
    }

    public RequestBuilderFeed<EntityT, Integer> feed(List<Animal> animals) {
        return null;
    }
}
