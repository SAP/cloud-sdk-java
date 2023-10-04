/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelByKeyFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelCreateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelDeleteFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelUpdateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelByKeyFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelCreateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelDeleteFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelUpdateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.batch.DefaultEntitywithkeynamedfieldServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/EntityWithKeyNamedField?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>EntityWithKeyNamedField</td></tr></table>
 * 
 */
public class DefaultEntitywithkeynamedfieldService
    implements EntitywithkeynamedfieldService
{

    @Nonnull
    private final String servicePath;

    /**
     * Creates a service using {@link EntitywithkeynamedfieldService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultEntitywithkeynamedfieldService() {
        servicePath = EntitywithkeynamedfieldService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultEntitywithkeynamedfieldService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultEntitywithkeynamedfieldService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultEntitywithkeynamedfieldService(servicePath);
    }

    @Override
    @Nonnull
    public DefaultEntitywithkeynamedfieldServiceBatch batch() {
        return new DefaultEntitywithkeynamedfieldServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public SomeTypeLabelFluentHelper getAllSomeTypeLabel() {
        return new SomeTypeLabelFluentHelper(servicePath, "SomeConreteType");
    }

    @Override
    @Nonnull
    public SomeTypeLabelByKeyFluentHelper getSomeTypeLabelByKey(final UUID key_2) {
        return new SomeTypeLabelByKeyFluentHelper(servicePath, "SomeConreteType", key_2);
    }

    @Override
    @Nonnull
    public SomeTypeLabelCreateFluentHelper createSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel) {
        return new SomeTypeLabelCreateFluentHelper(servicePath, someTypeLabel, "SomeConreteType");
    }

    @Override
    @Nonnull
    public SomeTypeLabelUpdateFluentHelper updateSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel) {
        return new SomeTypeLabelUpdateFluentHelper(servicePath, someTypeLabel, "SomeConreteType");
    }

    @Override
    @Nonnull
    public SomeTypeLabelDeleteFluentHelper deleteSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel) {
        return new SomeTypeLabelDeleteFluentHelper(servicePath, someTypeLabel, "SomeConreteType");
    }

    @Override
    @Nonnull
    public EntityWithoutKeyLabelFluentHelper getAllEntityWithoutKeyLabel() {
        return new EntityWithoutKeyLabelFluentHelper(servicePath, "WithoutKeyType");
    }

    @Override
    @Nonnull
    public EntityWithoutKeyLabelByKeyFluentHelper getEntityWithoutKeyLabelByKey() {
        return new EntityWithoutKeyLabelByKeyFluentHelper(servicePath, "WithoutKeyType");
    }

    @Override
    @Nonnull
    public EntityWithoutKeyLabelCreateFluentHelper createEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel) {
        return new EntityWithoutKeyLabelCreateFluentHelper(servicePath, entityWithoutKeyLabel, "WithoutKeyType");
    }

    @Override
    @Nonnull
    public EntityWithoutKeyLabelUpdateFluentHelper updateEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel) {
        return new EntityWithoutKeyLabelUpdateFluentHelper(servicePath, entityWithoutKeyLabel, "WithoutKeyType");
    }

    @Override
    @Nonnull
    public EntityWithoutKeyLabelDeleteFluentHelper deleteEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel) {
        return new EntityWithoutKeyLabelDeleteFluentHelper(servicePath, entityWithoutKeyLabel, "WithoutKeyType");
    }

}
