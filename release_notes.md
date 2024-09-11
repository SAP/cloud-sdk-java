## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- Minimum required versions:
  - SAP BTP Security Services Integration Libraries `com.sap.cloud.security` 3.4.3 
  - CAP `com.sap.cds` 2.9.3
  - SAP Java Buildpack `com.sap.cloud.sjb` 2.10.0
- Using IAS requires XSUAA version to be minimum `3.4.0`.
- Change the `DefaultHttpDestination.Builder` to throw an exception when the proxy configuration can not be determined for on-premise destinations.
  Previously, only an error was logged to give a grace period for analyzing and fixing the underlying issue.

### ✨ New Functionality

- 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- OpenAPI QueryParameters are now encoded
