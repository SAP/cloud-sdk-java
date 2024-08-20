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

### 📈 Improvements

- \[OpenAPI Generator\] Setting the Maven plugin configuration property `openapi.generate.deleteOutputDirectory` to `true` will no longer result in deletion of all files from the `outputDirectory` prior to generation.
  Instead, only the `apiPackage`- and `apiPackage`-related directories will be cleaned.
  This reduces the risk of deleting files unexpectedly and allows for reusing the same `outputDirectory` for multiple generator plugin invocations.
- Upgrade  to version `1.66.0` of `gRPC` dependencies coming in transitively when using `connectivity-ztis`
- Improve the error handling for OData batch requests.
  In case an OData error is given within a batch response it will now be parsed and returned as `ODataServiceErrorException`.

### 🐛 Fixed Issues

- 
