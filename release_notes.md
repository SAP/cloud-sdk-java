## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Timeouts for OAuth2 token retrievals can now be customized.
  As part of `ServiceBindingDestinationOptions` the new option `OAuth2Options.TokenRetrievalTimeout` can now be passed to set a custom timeout.
  Refer to [this documentation](https://sap.github.io/cloud-sdk/docs/java/features/connectivity/service-bindings#about-the-options) for more details.
- In `DestinationService` class allow for optional argument `DestinationServiceRetrievalStrategy` in method `getAllDestinationProperties`.
  This additional API allows for ensuring tenant-specific destination lookups.
  Available values are: `CURRENT_TENANT` (default), `ALWAYS_PROVIDER` and `ONLY_SUBSCRIBER`.
  

### 📈 Improvements

- \[OpenAPI Generator\] Setting the Maven plugin configuration property `openapi.generate.deleteOutputDirectory` to `true` will no longer result in deletion of all files from the `outputDirectory` prior to generation.
  Instead, only the `apiPackage`- and `apiPackage`-related directories will be cleaned.
  This reduces the risk of deleting files unexpectedly and allows for reusing the same `outputDirectory` for multiple generator plugin invocations.
- \[OpenAPI Generator\] The property accessors of generated model classes now have consistent `@Nullable` and `@Nonnull` annotation.
- \[OpenAPI Generator\] Enable the option `<enumUnknownDefaultCase>` that allows for lenient handling of unknown enum values coming from a server.
- Upgrade  to version `1.66.0` of `gRPC` dependencies coming in transitively when using `connectivity-ztis`
- Improve the error handling for OData batch requests.
  In case an OData error is given within a batch response it will now be parsed and returned as `ODataServiceErrorException`.
- Reduce the amount of DwC headers sent when communicating via megaclite.
  This reduces the risk of exceeding the maximum header size limit of the Cloud Foundry infrastructure.
- Improve the error handling for requests to the destination service.
  In case of an error a potential response body will now be logged with the error message.

### 🐛 Fixed Issues
- fix: issue [#557](https://github.com/SAP/cloud-sdk-java/issues/557):  DwC Auth Token not available (DwC + IAS) by @jingweiz2017 in #568
- Fix an issue where proxy headers are applied multiple times for OnPremise destinations.
