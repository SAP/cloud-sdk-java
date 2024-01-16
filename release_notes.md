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
- Dependency Updates:
  - Other dependency updates:
      - Update [Guava](https://central.sonatype.com/artifact/com.google.guava/guava/33.0.0-jre) from `32.1.3-jre` to `33.0.0-jre`
      - Update [Jackson](https://central.sonatype.com/artifact/com.fasterxml.jackson.core/jackson-core/2.16.1) from `2.15.3` to `2.16.1`
      - Update [Commons Lang](https://central.sonatype.com/artifact/org.apache.commons/commons-lang3/3.14.0) from `3.13.0` to `3.14.0`


### 🐛 Fixed Issues

- Fixed an issue where adding header providers to a destination after it had already been used to obtain an `HttpClient` would not work as expected.
