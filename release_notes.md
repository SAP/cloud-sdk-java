## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- We noticed an implicit behavior change for updated Apache HttpClient from `5.5.1` to `5.6`.
  TLS/SSL connections are now checked for hostname verification on behalf of the provided server certificate.
  Even with enabled trust-all-certificates flag, connections to servers with mismatching hostnames will be rejected.

### ✨ New Functionality

- `DestinationService.tryGetDestination` now checks if the given destination exists before trying to call it directly.
  This behaviour is enabled by default and can be disabled via `DestinationService.Cache.disablePreLookupCheck`.
- Temporary: Use `email` as fallback principal id when `user_uuid` is missing. Will switch to using `sub` once IAS exposes `idtype` (tracked in [SCICAI-1323](https://jira.tools.sap/browse/SCICAI-1323)).
- Cloud SDK OpenAPI Generator now supports `apache-httpclient` library marking Spring dependencies as optional.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- 
