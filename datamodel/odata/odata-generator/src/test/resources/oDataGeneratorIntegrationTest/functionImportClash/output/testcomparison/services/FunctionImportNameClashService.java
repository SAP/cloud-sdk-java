/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
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
import testcomparison.namespaces.functionimportnameclash.batch.FunctionImportNameClashServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/FunctionImportNameClash?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>FunctionImportNameClash</td></tr></table>
 * 
 */
public interface FunctionImportNameClashService
    extends BatchService<FunctionImportNameClashServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/sap/opu/odata/sap/API_TEST_SRV";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     * 
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    FunctionImportNameClashService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Fetch multiple {@link testcomparison.namespaces.functionimportnameclash.BP BP} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.functionimportnameclash.BP BP} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    BPFluentHelper getAllBP();

    /**
     * Fetch a single {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity using key fields.
     * 
     * @param code
     *     
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    BPByKeyFluentHelper getBPByKey(final String code);

    /**
     * Create a new {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity and save it to the S/4HANA system.
     * 
     * @param bP
     *     {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    BPCreateFluentHelper createBP(
        @Nonnull
        final BP bP);

    /**
     * Update an existing {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity and save it to the S/4HANA system.
     * 
     * @param bP
     *     {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPUpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    BPUpdateFluentHelper updateBP(
        @Nonnull
        final BP bP);

    /**
     * Deletes an existing {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity in the S/4HANA system.
     * 
     * @param bP
     *     {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPDeleteFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    BPDeleteFluentHelper deleteBP(
        @Nonnull
        final BP bP);

    /**
     * <p>Creates a fluent helper for the <b>BPByKey</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>BPByKey</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPByKeyFluentHelper_2#execute execute} method on the fluent helper object.
     */
    @Nonnull
    BPByKeyFluentHelper_2 bPByKey();

    /**
     * <p>Creates a fluent helper for the <b>BPCreate</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>BPCreate</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPCreateFluentHelper_2#execute execute} method on the fluent helper object.
     */
    @Nonnull
    BPCreateFluentHelper_2 bPCreate();

    /**
     * <p>Creates a fluent helper for the <b>BPDelete</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>BPDelete</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPDeleteFluentHelper_2#execute execute} method on the fluent helper object.
     */
    @Nonnull
    BPDeleteFluentHelper_2 bPDelete();

    /**
     * <p>Creates a fluent helper for the <b>BPUpdate</b> OData function import.</p>
     * 
     * @return
     *     A fluent helper object that will execute the <b>BPUpdate</b> OData function import with the provided parameters. To perform execution, call the {@link testcomparison.namespaces.functionimportnameclash.BPUpdateFluentHelper_2#execute execute} method on the fluent helper object.
     */
    @Nonnull
    BPUpdateFluentHelper_2 bPUpdate();

}
