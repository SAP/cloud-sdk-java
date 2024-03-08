## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Failed OData v4 Batch requests now return the specific failed request from the exception: `ODataResponseException.getFailedBatchRequest()`. 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- Fix an issue where the `AuthTokenAccessor` would not recognise JWT tokens passed in via the `dwc-jwt` header.
