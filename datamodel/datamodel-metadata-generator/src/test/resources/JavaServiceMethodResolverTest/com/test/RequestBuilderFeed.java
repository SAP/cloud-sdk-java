/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.test;

import io.vavr.Tuple3;

import java.net.URI;

public abstract class RequestBuilderFeed<EntityT, PortionsT> {
    public Tuple3<String, EntityT, PortionsT> execute(URI origin) {
        return null;
    }
}
