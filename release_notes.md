## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Introduced the `odata-core-apache-httpclient5` and `odata-v4-core-apache-httpclient5` modules, which run on top of Apache HttpClient 5. These are drop-in replacements for `odata-core` and `odata-v4-core` (same Java packages) for consumers moving off the end-of-life Apache HttpClient 4.x stack.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- [Connectivity] Fixed a bug in `ZeroTrustIdentityService` where a non-deterministic SVID could be selected when the SPIRE agent returns multiple SVIDs to the workload (e.g. after creating a second service key for the same ZTIS instance).
