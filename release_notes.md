## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Support service bindings to the [SAP BTP AI Core Service](https://api.sap.com/api/AI_CORE_API) by default in the `ServiceBindingDestinationLoader` API. 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- [DwC] Fix an issue where the `AuthTokenAccessor` would not recognise JWT tokens passed in via the `dwc-jwt` header.
- [DwC] Fix an issue where the current tenant would not be resolved if the `dwc-subdomain` header was missing. 
