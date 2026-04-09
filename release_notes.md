## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- `ODataResourcePath#addSegment(...)` and `addParameterToLastSegment(...)` now return a new path instance instead of mutating the existing one. Custom extensions that relied on in-place mutation need to reassign the returned path.
- [OpenAPI Apache Generator] Remove no args constructor in generated API clients.

### ✨ New Functionality

- Added support for SAP Cloud Identity Services (SCI) `sap_id_type` and `sub` claims in OIDC principal extraction. When `sap_id_type=user`, the `sub` claim is now used as the [Subject Name Identifier](https://help.sap.com/docs/SAP_DATASPHERE/9f804b8efa8043539289f42f372c4862/fac3155d77154775b919ceba36ffc325.html) (User ID, Email, or Custom Attribute as configured in SCI).

### 📈 Improvements

- Improved the support for the credential type `X509_ATTESTED`. `HttpDestination` objects created via `ServiceBindingDestinationLoader` no longer need to be re-created for the rotation of certificates to take effect.

### 🐛 Fixed Issues

- Fixed stateful OData request path construction caused by shared `ODataResourcePath` instances being mutated when building count, read-by-key, and function requests.
