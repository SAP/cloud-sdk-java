## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Add built-in support for the [Transparent Proxy](https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/transparent-proxy-for-kubernetes) via a new `TransparentProxyDestination`.
  For more information, refer to the [documentation](/docs/java/features/connectivity/transparent-proxy).
- Add _experimental_ support for cross-level destination consumption via a new `CrossLevelScope` setting under `DestinationServiceOptionsAugmenter`.
- Add _experimental_ support for setting custom headers in requests to the destination service via the new `DestinationServiceOptionsAugmenter#customHeaders`.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- [OpenAPI] Fix code generator for transitive dependency version inconsistencies for Jackson.
- [ODatav4] Fix incorrect HTTP header name when sending entity version identifier in bound-action requests.
- [ODatav4] Fix an issue when generating clients.
  - Property names: `value`, `item` and `properties` are now allowed.
  
