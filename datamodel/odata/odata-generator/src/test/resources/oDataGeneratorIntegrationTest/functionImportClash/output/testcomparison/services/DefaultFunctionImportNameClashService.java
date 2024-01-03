/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import testcomparison.namespaces.functionimportnameclash.BP;
import testcomparison.namespaces.functionimportnameclash.BPByKeyFluentHelper;
import testcomparison.namespaces.functionimportnameclash.BPByKeyFluentHelper_2;
import testcomparison.namespaces.functionimportnameclash.BPCreateFluentHelper;
import testcomparison.namespaces.functionimportnameclash.BPCreateFluentHelper_2;
import testcomparison.namespaces.functionimportnameclash.BPDeleteFluentHelper;
import testcomparison.namespaces.functionimportnameclash.BPDeleteFluentHelper_2;
import testcomparison.namespaces.functionimportnameclash.BPFluentHelper;
import testcomparison.namespaces.functionimportnameclash.BPUpdateFluentHelper;
import testcomparison.namespaces.functionimportnameclash.BPUpdateFluentHelper_2;
import testcomparison.namespaces.functionimportnameclash.batch.DefaultFunctionImportNameClashServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/FunctionImportNameClash?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>FunctionImportNameClash</td></tr></table>
 * 
 */
public class DefaultFunctionImportNameClashService
    implements FunctionImportNameClashService
{

    @Nonnull
    private final String servicePath;

    /**
     * Creates a service using {@link FunctionImportNameClashService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultFunctionImportNameClashService() {
        servicePath = FunctionImportNameClashService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultFunctionImportNameClashService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultFunctionImportNameClashService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultFunctionImportNameClashService(servicePath);
    }

    @Override
    @Nonnull
    public DefaultFunctionImportNameClashServiceBatch batch() {
        return new DefaultFunctionImportNameClashServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public BPFluentHelper getAllBP() {
        return new BPFluentHelper(servicePath, "BP");
    }

    @Override
    @Nonnull
    public BPByKeyFluentHelper getBPByKey(final String code) {
        return new BPByKeyFluentHelper(servicePath, "BP", code);
    }

    @Override
    @Nonnull
    public BPCreateFluentHelper createBP(
        @Nonnull
        final BP bP) {
        return new BPCreateFluentHelper(servicePath, bP, "BP");
    }

    @Override
    @Nonnull
    public BPUpdateFluentHelper updateBP(
        @Nonnull
        final BP bP) {
        return new BPUpdateFluentHelper(servicePath, bP, "BP");
    }

    @Override
    @Nonnull
    public BPDeleteFluentHelper deleteBP(
        @Nonnull
        final BP bP) {
        return new BPDeleteFluentHelper(servicePath, bP, "BP");
    }

    @Override
    @Nonnull
    public BPByKeyFluentHelper_2 bPByKey() {
        return new BPByKeyFluentHelper_2(servicePath);
    }

    @Override
    @Nonnull
    public BPCreateFluentHelper_2 bPCreate() {
        return new BPCreateFluentHelper_2(servicePath);
    }

    @Override
    @Nonnull
    public BPDeleteFluentHelper_2 bPDelete() {
        return new BPDeleteFluentHelper_2(servicePath);
    }

    @Override
    @Nonnull
    public BPUpdateFluentHelper_2 bPUpdate() {
        return new BPUpdateFluentHelper_2(servicePath);
    }

}
