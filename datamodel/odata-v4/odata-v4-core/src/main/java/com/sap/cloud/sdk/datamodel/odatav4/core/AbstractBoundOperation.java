/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Map;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataFunctionParameters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
abstract class AbstractBoundOperation<BindingT, ResultT> implements BoundOperation<BindingT, ResultT>
{
    @Nonnull
    private final Class<BindingT> bindingType;
    @Nonnull
    private final Class<ResultT> returnType;
    @Nonnull
    private final String qualifiedName;

    abstract static class AbstractBoundFunction<BindingT, ResultT> extends AbstractBoundOperation<BindingT, ResultT>
        implements
        BoundFunction<BindingT, ResultT>
    {
        @Getter( AccessLevel.PUBLIC )
        @Nonnull
        private final ODataFunctionParameters parameters;

        AbstractBoundFunction(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> args )
        {
            super(src, target, name);
            parameters = ODataFunctionParameters.of(args, ODataProtocol.V4);
        }
    }

    abstract static class AbstractBoundAction<BindingT, ResultT> extends AbstractBoundOperation<BindingT, ResultT>
        implements
        BoundAction<BindingT, ResultT>
    {
        @Getter
        @Nonnull
        private final Map<String, Object> parameters;

        AbstractBoundAction(
            @Nonnull final Class<BindingT> src,
            @Nonnull final Class<ResultT> target,
            @Nonnull final String name,
            @Nonnull final Map<String, Object> parameters )
        {
            super(src, target, name);
            this.parameters = parameters;
        }
    }
}
