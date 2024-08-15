## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- \[OpenAPI Generator\] Setting the Maven plugin configuration property `openapi.generate.deleteOutputDirectory` to `true` will no longer result in deletion of all files from the `outputDirectory` prior to generation.
  Instead, only the `apiPackage`- and `apiPackage`-related directories will be cleaned.
  This reduces the risk of deleting files unexpectedly and allows for reusing the same `outputDirectory` for multiple generator plugin invocations.
- Upgrade  to version `1.66.0` of `gRPC` dependencies coming in transitively when using `connectivity-ztis`
- Improve the error handling for batch requests.
  In case an OData error is given within a batch response it will now be parsed and returned as `ODataServiceErrorException`.

### 🐛 Fixed Issues

- 
