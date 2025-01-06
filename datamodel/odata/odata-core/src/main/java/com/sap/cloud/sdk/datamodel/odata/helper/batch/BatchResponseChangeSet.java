/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;

/**
 * Interface to evaluate the response of a single changeset from an OData batch response.
 */
public interface BatchResponseChangeSet
{
    /**
     * Get all newly created entities from this changeset.
     *
     * @return A list of generic {@link VdmEntity} instances. The consumer can type-check, evaluate and cast its
     *         entries.
     */
    @Nonnull
    List<VdmEntity<?>> getCreatedEntities();
}
