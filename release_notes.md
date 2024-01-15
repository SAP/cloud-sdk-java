## 5.2.0-SNAPSHOT - January XX, 2024

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases/tag/rel%2F5.X.0)

### 🚧 Known Issues

-

### 🔧 Compatibility Notes

- `com.sap.cloud.sdk.cloudplatform.connectivity.DestinationService.Cache` now enables change detection by default, but can be disabled via `DestinationService.Cache.disableChangeDetection()`. 
  - `DestinationService.Cache.enableChangeDetection()` has been deprecated.

### ✨ New Functionality

- 

### 📈 Improvements

- Improved the upgrade path from Cloud SDK version 4 by gracefully handling older implementations of `Resilience4jDecorationStrategy`.
  Previously any occurrence of the `com.sap.cloud.sdk.frameworks:resilience4j` maven module needed to be excluded (in case it came in transitively) to not conflict with `com.sap.cloud.sdk.cloudplatform:resilience4j`.
  This is no longer required, as the Cloud SDK 4 strategy will gracefully be ignored, if there is exactly one alternative.
- Improved the application of resilience patterns for OAuth authorization flows.
  Stateful resilience patterns (e.g. circuit breaker) will now be applied per `client_id` of the OAuth client (was per instance of the OAuth service / destination).

### 🐛 Fixed Issues

- Fixed an issue where adding header providers to a destination after it had already been used to obtain an `HttpClient` would not work as expected.
