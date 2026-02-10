## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- [Connectivity Destination Service] Migrated to Apache Httpclient 5.
  - The replacement for `HttpClientAccessor` is `ApacheHttpClient5Accessor`

### ✨ New Functionality

- [OpenAPI] Cloud SDK OpenAPI Generator now supports `apache-httpclient` library besides Spring RestTemplate through the newly introduced module `openapi-core-apache`.
- [Connectivity HttpClient5] _(Experimental)_ Added opt-in API for caching HTTP connection pool managers to reduce memory consumption. 
  Connection pool managers can consume ~100KB each, and this feature allows sharing them based on configurable caching strategies:
  ```java
  ApacheHttpClient5Factory factory = new ApacheHttpClient5FactoryBuilder()
    .connectionPoolManagerProvider(ConnectionPoolManagerProviders.noCache()) // new API (default behavior)
    .connectionPoolManagerProvider(ConnectionPoolManagerProviders.cached().byOnBehalfOf()) // new API
    .build();
  ```
  Available caching strategies include `byCurrentTenant()`, `byDestinationName()`, `byOnBehalfOf()`, and custom key extractors via `by(Function)`.
  The `byOnBehalfOf()` strategy intelligently determines tenant isolation requirements based on the destination's `OnBehalfOf` indication.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- 
