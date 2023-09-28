package com.test;

import io.vavr.Tuple3;

import java.net.URI;

public abstract class RequestBuilderFeed<EntityT, PortionsT> {
    public Tuple3<String, EntityT, PortionsT> execute(URI origin) {
        return null;
    }
}
