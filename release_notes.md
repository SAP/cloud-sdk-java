## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- Changes regarding the TLS `Upgrade` header thanks to [Apache httpclient5 5.4.2](https://github.com/apache/httpcomponents-client/commit/5ab09ea39fed1c39ea35905532ba1567c785330a)
  - `TlsUpgrade.DISABLED` no changes
  - `TlsUpgrade.ENABLED` will not send the `Upgrade` header for non-proxy connections anymore
  - `TlsUpgrade.AUTOMATIC` **Default behaviour** will not send the `Upgrade` header anymore
    - Except for `proxyType(ProxyType.INTERNET)`

### ✨ New Functionality

- OpenAPI: Add `toMap()` and deprecate `getCustomField(String)` on generated model classes.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- 
