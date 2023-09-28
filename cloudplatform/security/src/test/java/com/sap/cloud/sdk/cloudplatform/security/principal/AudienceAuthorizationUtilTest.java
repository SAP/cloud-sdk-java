package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

@Deprecated
public class AudienceAuthorizationUtilTest
{
    @Test
    public void testGroupAuthorizationsByAudience()
    {
        final Set<com.sap.cloud.sdk.cloudplatform.security.Audience> audiences =
            Sets
                .newHashSet(
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sb-sirem-ops!t9657"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657.Schemas"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("uaa"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657.Application"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657.Tenants"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657.Toggles"));

        final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations =
            Sets
                .newHashSet(
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization(
                        "sirem-ops!t9657.Toggles.ToggleFeatures"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem-ops!t9657.Schemas.Export"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("uaa.resource"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem!b9657.Callback"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem-ops!t9657.Application.Monitor"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem-ops!t9657.Schemas.Import"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem!b9657.Tenants.Configure"));

        final Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
            AudienceAuthorizationUtil.getAuthorizationsByAudience(audiences, authorizations);

        final com.sap.cloud.sdk.cloudplatform.security.Audience sbSiremOpsAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sb-sirem-ops!t9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremOpsAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience uuaAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("uaa");

        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsToggleFeaturesAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Toggles.ToggleFeatures");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsSchemasExportAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Export");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization uaaResourceAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("resource");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremCallbackAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Callback");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsApplicationMonitorAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Application.Monitor");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremOpsSchemasImportAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Import");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization siremConfigureTenantsAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Tenants.Configure");

        assertThat(authorizationsByAudience)
            .containsOnlyKeys(sbSiremOpsAudience, siremAudience, siremOpsAudience, uuaAudience);

        assertThat(authorizationsByAudience.get(sbSiremOpsAudience)).isEmpty();
        assertThat(authorizationsByAudience.get(siremOpsAudience))
            .containsExactlyInAnyOrder(
                siremOpsToggleFeaturesAuthorization,
                siremOpsSchemasExportAuthorization,
                siremOpsSchemasImportAuthorization,
                siremOpsApplicationMonitorAuthorization);
        assertThat(authorizationsByAudience.get(siremAudience))
            .containsExactlyInAnyOrder(siremCallbackAuthorization, siremConfigureTenantsAuthorization);
        assertThat(authorizationsByAudience.get(uuaAudience)).containsExactlyInAnyOrder(uaaResourceAuthorization);
    }

    @Test
    public void testGroupAuthorizationsWhenNoAudiencesGiven()
    {
        final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations =
            Sets
                .newHashSet(
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization(
                        "sirem-ops!t9657.Toggles.ToggleFeatures"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem-ops!t9657.Schemas.Export"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("uaa.resource"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem!b9657.Callback"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem-ops!t9657.Application.Monitor"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem-ops!t9657.Schemas.Import"),
                    new com.sap.cloud.sdk.cloudplatform.security.Authorization("sirem!b9657.Tenants.Configure"));

        final Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
            AudienceAuthorizationUtil.getAuthorizationsByAudience(Collections.emptySet(), authorizations);

        final com.sap.cloud.sdk.cloudplatform.security.Audience uuaAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("uaa");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremOpsAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657");

        final com.sap.cloud.sdk.cloudplatform.security.Authorization resourceAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("resource");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization applicationMonitorAuthorizatiom =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Application.Monitor");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization toggleFeaturesAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Toggles.ToggleFeatures");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization schemasImportAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Import");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization schemasExportAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Schemas.Export");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization callbackAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Callback");
        final com.sap.cloud.sdk.cloudplatform.security.Authorization tenantsConfigureAuthorization =
            new com.sap.cloud.sdk.cloudplatform.security.Authorization("Tenants.Configure");

        assertThat(authorizationsByAudience).containsOnlyKeys(uuaAudience, siremAudience, siremOpsAudience);

        assertThat(authorizationsByAudience.get(uuaAudience)).containsExactlyInAnyOrder(resourceAuthorization);
        assertThat(authorizationsByAudience.get(siremOpsAudience))
            .containsExactlyInAnyOrder(
                applicationMonitorAuthorizatiom,
                toggleFeaturesAuthorization,
                schemasExportAuthorization,
                schemasImportAuthorization);
        assertThat(authorizationsByAudience.get(siremAudience))
            .containsExactlyInAnyOrder(callbackAuthorization, tenantsConfigureAuthorization);

    }

    @Test
    public void testGroupAuthorizationsWhenNoAuthorizationsAndNoAudiencesGiven()
    {
        final Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
            AudienceAuthorizationUtil.getAuthorizationsByAudience(Collections.emptySet(), Collections.emptySet());

        assertThat(authorizationsByAudience).isEmpty();
    }

    @Test
    public void testGroupAuthorizationsWhenNoAuthorizationsGiven()
    {
        final Set<com.sap.cloud.sdk.cloudplatform.security.Audience> audiences =
            Sets
                .newHashSet(
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sb-sirem-ops!t9657"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657.Schemas"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("uaa"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657.Application"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657.Tenants"),
                    new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657.Toggles"));

        final Map<com.sap.cloud.sdk.cloudplatform.security.Audience, Set<com.sap.cloud.sdk.cloudplatform.security.Authorization>> authorizationsByAudience =
            AudienceAuthorizationUtil.getAuthorizationsByAudience(audiences, Collections.emptySet());

        final com.sap.cloud.sdk.cloudplatform.security.Audience sbSiremOpsAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sb-sirem-ops!t9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremOpsAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem-ops!t9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience siremAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("sirem!b9657");
        final com.sap.cloud.sdk.cloudplatform.security.Audience uuaAudience =
            new com.sap.cloud.sdk.cloudplatform.security.Audience("uaa");

        assertThat(authorizationsByAudience)
            .containsOnlyKeys(sbSiremOpsAudience, uuaAudience, siremAudience, siremOpsAudience);

        assertThat(authorizationsByAudience.get(sbSiremOpsAudience)).isEmpty();
        assertThat(authorizationsByAudience.get(uuaAudience)).isEmpty();
        assertThat(authorizationsByAudience.get(siremOpsAudience)).isEmpty();
        assertThat(authorizationsByAudience.get(siremAudience)).isEmpty();
    }
}
