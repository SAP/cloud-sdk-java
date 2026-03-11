## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- [Connectivity Destination Service] Migrated to Apache Httpclient 5.
  - The replacement for `HttpClientAccessor` is `ApacheHttpClient5Accessor`

### ✨ New Functionality

- [OpenAPI] Cloud SDK OpenAPI Generator now supports `apache-httpclient` library besides Spring RestTemplate through the newly introduced module `openapi-core-apache`.
- [IAS] Add `IasOptions.withTokenFormat()` to allow specifying token format

### 📈 Improvements

- 

### 🐛 Fixed Issues

- [OData v4] Binary deserialization can now handle both `Base64URL` and `Base64`.
