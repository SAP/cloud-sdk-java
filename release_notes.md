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
- Add support for setting custom headers in requests to the destination service via the new `DestinationServiceOptionsAugmenter#customHeaders`.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- [ODatav4] Fixed an issue when generating clients.
  - Property names: `value`, `item` and `properties` are now allowed.
  
