## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- (Beta) Add support for the SAP-internal Zero Trust Identity Service
  - Add a new module `connectivity-ztis`.
  - Add support for the credential-type `X509_ATTESTED` for all OAuth2 flows.

### 📈 Improvements

- Improve the efficiency of HTTP clients: The default cache duration for HTTP clients have been increased to expire one hour after last access (was 5 minutes after creation).
  Aside from a performance improvement, this improves the handling of cookies, as they are retained for much longer.

### 🐛 Fixed Issues

- Stop unnecessarily throwing and catching `NullPointerException` when interacting with `DefaultHttpDestination#equals(...)` and `#hashCode()`.
