/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.test;

import io.vavr.control.Try;

import java.net.URI;

public interface RequestBuilderPet<WhatT> {

    Try<WhatT> execute(URI origin);
}
