## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- Using the `X509_ATTESTED` credential type now requires a version >= `3.4.0` of the [BTP Security Library](https://github.com/SAP/cloud-security-services-integration-library).
- Deprecate the IAS communication option `withConsumerClient(clientid, tenantid)`. The option is non-functional. Use `withConsumerClientId(clientid)` instead, the tenant will automatically be inferred from the context upon execution.

### ✨ New Functionality

- Support the `X509_ATTESTED` credential type for XSUAA service bindings.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- Fix an issue that prevented OAuth flows from working correctly for subscriber tenants when using IAS with credential type `X509_ATTESTED`
