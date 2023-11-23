# 5.0.0-SNAPSHOT

release-date: October XX, 2023
docs:
blog: https://blogs.sap.com/?p=xxx

## knownIssues

-

## compatibilityNotes

- The SAP Cloud SDK has switched its license from [SAP DEVELOPER LICENSE AGREEMENT](https://tools.hana.ondemand.com/developer-license-3_1.txt) to [The Apache Software License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)
- The SAP Cloud SDK has switched the minimum required Java version to 17
- The SAP Cloud SDK version 5 is no longer compatible with Spring version 5, the [XSUAA Client Library](https://github.com/SAP/cloud-security-services-integration-library) version 2, and the SAP Java Buildpack version 1.
- Following modules have been removed:
  - SAP Java Buildpack BOM: `com.sap.cloud.sdk:sdk-sjb-bom`
  - (Parent Module) `com.sap.cloud.sdk.plugins:plugins-parent`
  - (Auditlog Modules) `com.sap.cloud.sdk.cloudplatform:auditlog`
  - (Auditlog Modules) `com.sap.cloud.sdk.cloudplatform:auditlog-scp-cf`
  - Related to the SAP NEO runtime:
    - (Archetype) `com.sap.cloud.sdk.archetypes.scp-neo-javaee7`
    - `com.sap.cloud.sdk.cloudplatform:auditlog-scp-neo`
    - `com.sap.cloud.sdk.cloudplatform:cloudplatform-connectivity-scp-neo`
    - `com.sap.cloud.sdk.cloudplatform:cloudplatform-core-scp-neo`
    - `com.sap.cloud.sdk.cloudplatform:concurrency-scp-neo`
    - (Grouping Module) `com.sap.cloud.sdk.cloudplatform:scp-neo`
    - `com.sap.cloud.sdk.cloudplatform:security-scp-neo`
    - `com.sap.cloud.sdk.cloudplatform:tenant-scp-neo`
    - (Maven Plugin) `com.sap.cloud.sdk.plugins:scp-neo-maven-plugin`
  - Related to services:
    - BTP Business Logging - All: `com.sap.cloud.sdk.services:business-logging-all`
    - BTP Business Logging Core: `com.sap.cloud.sdk.services:business-logging-core`
    - BTP Business Logging OData: `com.sap.cloud.sdk.services:business-logging-odata`
    - BTP Business Logging REST: `com.sap.cloud.sdk.services:business-logging-rest`
    - Services - MDI Client Convenience: `com.sap.cloud.sdk.services:mdi-client-convenience`
    - Services - Blockchain Services
      - `com.sap.cloud.sdk.services:blockchain-business-services-sdk`
      - `com.sap.cloud.sdk.services:blockchain-business-services-visibility`
      - `com.sap.cloud.sdk.services:blockchain-client-fabric`
      - `com.sap.cloud.sdk.services:blockchain-client-multichain`
      - `com.sap.cloud.sdk.services:scp-blockchain`
    - Services - Shared Ledger Client: `com.sap.cloud.sdk.services:btp-shared-ledger-client`
    - Services - BTP Cloud Foundry Workflow API: `com.sap.cloud.sdk.services:scp-workflow-cf`
    - Services - SAP Business Rules (Beta): `com.sap.cloud.sdk.services:btp-business-rules`
  - Cloud Platform - SAP Passport: `com.sap.cloud.sdk.cloudplatform:sap-passport`
  - Cloud Platform - Core SAP Deploy with Confidence (Beta): `com.sap.cloud.sdk.cloudplatform:cloudplatform-core-dwc`
  - Cloud Platform - Core SAP Deploy with Confidence on Cloud Foundry (Beta): `com.sap.cloud.sdk.cloudplatform:cloudplatform-core-dwc-cf`
  - Cloud Platform - Core SAP Cloud Platform on Cloud Foundry (Beta): `com.sap.cloud.sdk.cloudplatform:cloudplatform-core-scp-cf`
  - Cloud Platform - Security SAP Deploy with Confidence (Beta): `com.sap.cloud.sdk.cloudplatform:security-dwc`
  - Cloud Platform - Security SAP CP Cloud Foundry: `com.sap.cloud.sdk.cloudplatform:security-scp-cf`
  - Cloud Platform - Tenant SAP Deploy with Confidence (Beta): `com.sap.cloud.sdk.cloudplatform:tenant-dwc`
  - Business Technology Platform - Tenant SAP CP Cloud Foundry: `com.sap.cloud.sdk.cloudplatform:tenant-scp-cf`
  - Archetypes - SAP CP Cloud Foundry + TomEE: `com.sap.cloud.sdk.archetypes:scp-cf-tomee`
  - Related to the Auditlog service:
    - Cloud Platform - Auditlog: `com.sap.cloud.sdk.cloudplatform:auditlog`
    - Cloud Platform - Auditlog - SCP CF: `com.sap.cloud.sdk.cloudplatform:auditlog-scp-cf`
    - Cloud Platform - Auditlog - SCP Neo: `com.sap.cloud.sdk.cloudplatform:auditlog-scp-neo`
    - As part of removing these modules, we also removed the integration of the Auditlog service with the S/4HANA connectivity features. Consumers who relied on this out-of-the-box integration are asked to approach the development team by [creating a GitHub support issue](https://github.com/SAP/cloud-sdk-java/issues/new) for migration guidance.
  - The module `com.sap.cloud.sdk.cloudplatform:scp-cf` has been deprecated in favor of `com.sap.cloud.sdk:sdk-core`
- Following public classes have been removed:
  - Related to the `Destination` API:
    - Within the [Business Technology Platform - Connectivity](https://search.maven.org/search?q=g:com.sap.cloud.sdk.cloudplatform%20AND%20a:cloudplatform-connectivity) Module:
      - `com.sap.cloud.sdk.cloudplatform.connectivity.AbstractHttpDestination`
      - `com.sap.cloud.sdk.cloudplatform.connectivity.WrappedDestination`
    - Within the [Cloud Platform - Connectivity SAP Business Technology Platform](https://search.maven.org/search?q=g:com.sap.cloud.sdk.cloudplatform%20AND%20a:cloudplatform-connectivity-scp) Module:
      - `com.sap.cloud.sdk.cloudplatform.connectivity.AbstractScpDestination`
      - `com.sap.cloud.sdk.cloudplatform.connectivity.AbstractScpDestinationBuilder`
    - Within the [Cloud Platform - Connectivity SAP BTP Cloud Foundry](https://search.maven.org/search?q=g:com.sap.cloud.sdk.cloudplatform%20AND%20a:cloudplatform-connectivity-scp-cf) Module:
      - `com.sap.cloud.sdk.cloudplatform.connectivity.AccessToken`
      - `com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestination`
      - `com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfHttpDestination`
      - `com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfRfcDestination`
      - `com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationServiceResponseProvider`
    - Within the [SAP S/4HANA - Connectivity](https://search.maven.org/search?q=g:com.sap.cloud.sdk.s4hana%20AND%20a:s4hana-connectivity) Module:
      - `com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestination`
      - `com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestinationProperties`
      - `com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination`
      - `com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestinationUtils`
    - All functionality of the removed classes can be replaced by using the `DefaultHttpDestination` and `DefaultRfcDestination` classes
  - Related to the SAP NEO runtime:
    - `com.sap.cloud.sdk.cloudplatform.thread.ThreadContextDecoratorInternal`
    - `com.sap.cloud.sdk.cloudplatform.naming.JndiLookupAccessor`
    - `com.sap.cloud.sdk.cloudplatform.naming.JndiLookupFacade`
    - `com.sap.cloud.sdk.cloudplatform.naming.DefaultJndiLookupFacade`
  - Related to Tenant:
    - `com.sap.cloud.sdk.cloudplatform.tenant.ScpCfTenant`
    - `com.sap.cloud.sdk.cloudplatform.tenant.ScpCfTenantFacade`
    - `com.sap.cloud.sdk.cloudplatform.tenant.DwcTenant`
    - Please use the respective `Default` classes instead.
  - Related to Principal:
    - `com.sap.cloud.sdk.cloudplatform.security.principal.ScpCfPrincipal`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.ScpCfPrincipalFacade`
    - Please use the respective `Default` classes instead.
  - Remove the following classes without replacement:
    - `com.sap.cloud.sdk.cloudplatform.ClientCredentialsValidator`
    - `com.sap.cloud.sdk.cloudplatform.util.StringConverter`
    - `com.sap.cloud.sdk.cloudplatform.util.StringValidator`
    - `com.sap.cloud.sdk.cloudplatform.security.AuthTokenBuilder`
    - `com.sap.cloud.sdk.cloudplatform.security.OAuth2ServiceProvider`
    - `com.sap.cloud.sdk.cloudplatform.connectivity.WithDestinationName`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.LocalScopePrefixProvider`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.DefaultLocalScopePrefixProvider`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.LocalScopePrefixExtractor`
    - `com.sap.cloud.sdk.cloudplatform.security.Audience`
    - `com.sap.cloud.sdk.cloudplatform.security.Authorization`
    - `com.sap.cloud.sdk.cloudplatform.security.Role`
    - `com.sap.cloud.sdk.cloudplatform.security.Scope`
    - `com.sap.cloud.sdk.cloudplatform.security.TenantSpecificAuthorization`
    - `com.sap.cloud.sdk.cloudplatform.security.UserSpecificAuthorization`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAttribute`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.CollectionPrincipalAttribute`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.SimplePrincipalAttribute`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.StringCollectionPrincipalAttribute`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.StringPrincipalAttribute`
    - `com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAttributeException`
    - `com.sap.cloud.sdk.cloudplatform.security.secret.ScpCfSecretStore`
    - `com.sap.cloud.sdk.cloudplatform.security.secret.ScpCfSecretStoreFacade`
    - `com.sap.cloud.sdk.cloudplatform.security.secret.SecretStore`
    - `com.sap.cloud.sdk.cloudplatform.security.secret.SecretStoreAccessor`
    - `com.sap.cloud.sdk.cloudplatform.security.secret.SecretStoreFacade`
    - `com.sap.cloud.sdk.cloudplatform.security.secret.exception.KeyStoreAccessException`
    - `com.sap.cloud.sdk.cloudplatform.security.secret.exception.SecretStoreAccessException`
    - `com.sap.cloud.sdk.cloudplatform.CloudPlatform`
    - `com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform`
    - `com.sap.cloud.sdk.cloudplatform.DwcCloudPlatform`
    - `com.sap.cloud.sdk.cloudplatform.DwcCfCloudPlatform`
    - `com.sap.cloud.sdk.cloudplatform.CloudPlatformFacade`
    - `com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatformFacade`
    - `com.sap.cloud.sdk.cloudplatform.DwcCfCloudPlatformFacade`
    - `com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor`
    - `com.sap.cloud.sdk.cloudplatform.WithRuntimeDependencies`
    - `com.sap.cloud.sdk.cloudplatform.exception.ConstraintViolationException`
    - `com.sap.cloud.sdk.cloudplatform.exception.DependencyNotFoundException`
    - `com.sap.cloud.sdk.cloudplatform.exception.EntityAlreadyExistsException`
    - `com.sap.cloud.sdk.cloudplatform.exception.EntityNotFoundException`
    - `com.sap.cloud.sdk.cloudplatform.exception.StringParsingException`
    - `com.sap.cloud.sdk.cloudplatform.exception.UnsupportedCloudFeatureException`
    - `com.sap.cloud.sdk.s4hana.connectivity.exception.MissingConfigException`
    - `com.sap.cloud.sdk.s4hana.connectivity.exception.VersionNotSupportedException`
    - `com.sap.cloud.sdk.testutil.ThreadContextInvocationInterceptor`
    - `com.sap.cloud.sdk.testutil.ThrowableAssertionUtil`
- Following public methods have been removed:
  - Related to the `Destination` API:
    - The `Destination#decorate` method has been removed without replacement.
- Removed the following elements from enum `com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType`:
  - `APP_TO_APP_SSO` 
  - `INTERNAL_SYSTEM_AUTHENTICATION` 
- Removed the deprecated enum element `CURRENT_TENANT_THEN_PROVIDER` from `com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationRetrievalStrategy`.
  As alternative please run the destination lookup queries separately in your application for `ONLY_SUBSCRIBER` and `ALWAYS_PROVIDER` as fallback.
- Removed the `javax.inject.Named` annotation from the OData Generator (v2, v4).
- Other changes to the Destination API:
  - Retrieving a `Destination` from the Destination service (i.e. using the `ScpCfDestinationLoader`) will now throw an exception if any of the attached certificates isn't valid.
  - Retrieving a `Destination` from the Destination service (i.e. using the `ScpCfDestinationLoader`) will no longer eagerly evaluate On-Premise related headers (if applicable). 
    Instead, those headers will be evaluated lazily upon request execution. 
    As a consequence, the `getHeaders` method might now throw an exception if the current tenant is different from the tenant the destination belongs to.
  - Changed following `DestinationPropertyKey` instances:
    - `AUTH_TYPE`: `authentication` -> `Authentication`
    - `CERTIFICATES`: `certificates` -> `cloudsdk.certificates`
    - `AUTH_TOKENS`: `authTokens` -> `cloudsdk.authTokens`
    - These changes are most relevant for users who are **not** already using these constant `DestinationPropertyKey` instances but instead retrieved properties from `DestinationProperties` (and sub-types) using the `get(String, Function)` method.
  - The public constructor of `DefaultHttpDestination` has been replaced with a static factory method `DefaultHttpDestination#fromProperties`.
    - We also added some extra static factory methods (`DefaultHttpDestination#fromMap` and `DefaultHttpDestination#fromDestination`) for convenience. Please refer to the JavaDoc for further details.
  - The public constructor of `DefaultDestination` has been replaced with a static factory method `DefaultHttpDestination#fromMap`.
    - We also added an extra static factory method `DefaultHttpDestination#fromProperties` for convenience. Please refer to the JavaDoc for further details.
  - The `DefaultHttpDestination.Builder` has been modified in the following ways:
    - The `user(String)` and `password(String)` methods have been replaced with `basicCredentials(String, String)`.
    - Using any overload of `basicCredentials` will now automatically set the `AuthenticationType` to `BASIC_AUTHENTICATION`.
    - Using `proxyConfiguration(ProxyConfiguration)` will now throw an `IllegalArgumentException` in case the contained `Credentials` are not supported. Supported types are `BearerCredentials` and `NoCredentials`.
  - The `BearerCredentials` behavior has been adjusted slightly: The `getToken()` method no longer just returns the value passed in via the constructor but instead is now guaranteed to **NOT** contain the prefix `"Bearer "`. To compensate this change, the `#getHttpHeaderValue()` method has been added, which is guaranteed to contain the `"Bearer "` prefix.
  - Invoking `HttpDestination#getHeaders(...)` may throw a `ResilienceRuntimeException` in case On-Premise Connectivity proxy headers cannot be resolved.
    As usual, the specific error cause is attached to the exception.
  - The deprecated `ClientCredentialsHttpDestination` has been removed in favor of the improved `OAuth2DestinationBuilder`.
  - The `OAuth2DestinationBuilder` has been changed to allow for setting arbitrary destination properties after the OAuth2 configuration has been set.
    - The `.withProperties(..)` aspect of the builder has been replaced with `.withProperty(..)`.
- The following classes have been moved or their modules have been renamed:
  - All classes related to the Apache Http Client 4 have been moved from `com.sap.cloud.sdk.cloudplatform:cloudplatform-connectivity` to a new module `com.sap.cloud.sdk.cloudplatform:connectivity-apache-httpclient4`
  - All classes related to the Apache Http Client 5 have been moved from `com.sap.cloud.sdk.frameworks:apache-httpclient5` to `com.sap.cloud.sdk.cloudplatform:connectivity-apache-httpclient5`
  - All classes related to Resilience4j have been moved from `com.sap.cloud.sdk.frameworks:resilience4j` to `com.sap.cloud.sdk.cloudplatform:resilience4j`
  - The following classes have been moved from `com.sap.cloud.sdk.cloudplatform:secuirty-scp-cf` to '`com.sap.cloud.sdk.cloudplatform:security`:
    - `AuthTokenThreadContextListener`
    - `BasicAuthenticationAccessor`
    - `BasicAuthenticationFacade`
    - `BasicAuthenticationThreadContextListener`
    - `DefaultAuthTokenFacade`
    - `DefaultBasicAuthenticationFacade`
    - `SecurityContextThreadContextDecorator`
    - `ScpCfSecretStore`
    - `ScpCfSecretStoreFacade`
    - `BasicAuthenicationAccessException`
  - The following classes have been moved from `com.sap.cloud.sdk.cloudplatform:cloudplatform-core-dwc` to `com.sap.cloud.sdk.cloudplatform:connectivity-dwc`:
    - `DwcHeaderUtils`
    - `DwcHeaderNotFoundException`
  - `com.sap.cloud.sdk.cloudplatform.security.DwcPrincipalFacade` has been moved from `com.sap.cloud.sdk.cloudplatform:security-dwc` to `com.sap.cloud.sdk.cloudplatform:connectivity-dwc`
  - `com.sap.cloud.sdk.cloudplatform.tenant.DwcTenantFacade` has been moved from `com.sap.cloud.sdk.cloudplatform:tenant-dwc` to `com.sap.cloud.sdk.cloudplatform:connectivity-dwc`
  - The module `cloudplatform-connectivity-scp-cf` got renamed to `connectivity-destination-service`, and all classes therein that started with `ScpCf...` got this prefix renamed to `DestinatinService...`. To prevent breaking changes with the renamed module the `cloudplatform-connectivity-scp-cf` module is still available as a dependency, but it is empty and just contains a reference to the new module `connectivity-destination-service`.
- The `HttpClientAccessor` and `ApacheHttpClient5Accessor` classes are generalised to accept `Destination` instances, making invocations to `.asHttp()` superfluous when obtaining HTTP clients.
- The `getSslContext()` method was removed from the `CloudPlatform` interface and the implementation was moved to the modules `connectivity-apache-httpclient4` and `connectivity-apache-httpclient5`.
- The OData, OpenAPI and SOAP APIs are generalised to accept instances of `Destination`, making invocations to `.asHttp()` superfluous when executing OData or REST requests.
    - OData V2 and OpenAPI clients need to be re-generated to adjust for this change.
- The public constructor of `DefaultPrincipal` now only accepts a String argument for `principalId`.
- The `PrincipalFacade` of the `PrincipalAccessor` will default to `DefaultPrincipalFacade` in the case that a facade cannot be found.
- The `RequestHeaderContainer` no longer splits header values.
- The `ODataRequestResult` no longer splits header values.
- The `AuthTokenFacade` of the `AuthTokenAccessor` will default to `DefaultAuthTokenFacade`
- The `TenantFacade` of the `TenantAccessor` will default to `DefaultTenantFacade`

## newFunctionality

- New destination builders:
  These builders will perform a **shallow copy** of the provided `DestinationProperties`, which might lead to some shared state between the original `DestinationProperties` and the to-be-created `DefaultHttpDestination`.
  Please refer to the JavaDoc of this new method for further details.
  - The `DefaultHttpDestination.Builder` can now be constructed from an existing `DestinationProperties` instance by using `DefaultHttpDestination.fromProperties(DestinationProperties)`.
  - The `DefaultHttpDestination.Builder` can now be constructed from an existing `DefaultHttpDestination` instance by using `DefaultHttpDestination.toBuilder()`.
  - The `DefaultDestination.Builder` can now be constructed from an existing `DefaultDestination` instance by using `DefaultDestination.toBuilder()`.
- The `BasicCredentials` and `BearerCredentials` classes now offer a new method `#getHttpHeaderValue()` which will return the encoded and prefixed value of the credentials, e.g. `"Basic <encodedCredentials>"` or `"Bearer <token>"`.
- A `DefaultTenant` can now be initialised with an optional `subdomain` in addition to the required `tenantId`
- The new module `com.sap.cloud.sdk:sdk-core` provides an easy way to import the essential Cloud SDK dependencies.

- Dependency Updates:
  - SAP dependency updates:
    - Update `com.sap.cloud.security:java-bom` from `2.14.2` to `3.2.1`
  - Other dependency updates:
    - Major version updates:
      - Update `org.springframework:spring-framework-bom` from `5.3.29` to `6.0.13`. **Note** This dependency is used by the SDK **internally only**. In other words: This update **does not** affect consuming projects that use the `sdk-bom` for dependency management.
      - Update `org.slf4j:slf4j-api` from `1.7.36` to `2.0.9`

## improvements

- Both `HttpDestination` as well as `HttpDestinationProperties` are now sub-types of `Destination` for improved compatibility with the Cloud SDK APIs.
- The `Destination#asHttp()` and `Destination#asRfc()` methods no longer always return a new instance of `HttpDestination` and `RfcDestination` if the current objects is already a `HttpDestination` or `RfcDestination` respectively.
- `Destination#asHttp()` no longer throws an exception in case the `Destination` originates from the Destination service and the attached auth token contains an error.
  Instead, an exception will be thrown upon invoking the `getHeaders()` method, for example, during request execution.

## fixedIssues

- Fixed a bug where an `Authorization` header was attached multiple times to outgoing HTTP requests under some circumstances
- Fixed an issue where the `DestinationType` of an `DefaultHttpDestination` could be changed to anything but `DestinationType.HTTP`
- Fixed an issue with custom GSON serialization of OpenAPI generated classes having unexpected `customFieldNames` properties in JSON payload.
