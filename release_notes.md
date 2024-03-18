## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- Change the default implementations of `HttpClientCache` and `ApacheHttpClient5Cache` to throw an exception in cases where previously only an error was logged.
  The previous behavior of logging an error and falling back to creating a new http client granted a grace period to fix the underlying issue.
  
### ✨ New Functionality

- Support service bindings to the [SAP BTP AI Core Service](https://api.sap.com/api/AI_CORE_API) by default in the `ServiceBindingDestinationLoader` API.
- Failed OData v4 Batch requests now return the specific failed request from the exception: `ODataResponseException.getRequest()`.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- Fix an issue where the same `HttpClient` would be used for different users when using `PrincipalPropagation` and thus could potentially share the same (session) cookies.
- Fix an issue where destinations for the Business Logging service that are created from a service binding (using the `ServiceBindingDestinationLoader` API) contained the concrete API path.
  This behavior caused problems when using such a destination in a client generated with the SAP Cloud SDK's OpenApi generator.
- [DwC] Fix an issue where the `AuthTokenAccessor` would not recognise JWT tokens passed in via the `dwc-jwt` header.
- [DwC] Fix an issue where the current tenant would not be resolved if the `dwc-subdomain` header was missing.
