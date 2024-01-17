## 5.2.0-SNAPSHOT - January XX, 2024

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases/tag/rel%2F5.X.0)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- The de-facto standard `resilience4j` implementation for the resilience API is now included by default.
  In case you are loading a different implementation of the `Resilience4jDecorationStrategy` via the Java Service Provider Interface (SPI), you should use `resilience-api` and ensure `resilience4j` is not loaded transitively.
  Alternatively, you can invoke `ResilienceDecorator.setDecorationStrategy` explicitly in your code.
  - In case you are using custom facade implementations for accessors (e.g. `TenantAccessor`) this might have additional implications.
    <details><summary>Details: Impact on custom facades</summary>
    
    First, check if all of the following conditions apply for your use case:
  
    - You are providing a custom implementation of a facade interface (e.g. the `TenantFacade` registered to the `TenantAccessor`).
    - Your custom facade implementation uses `ThreadLocal` variables which are not configured to be passed on by the `ThreadContextExecutor`. 
    - Before version `5.2.0` the `resilience4j` module was not in the dependency tree and no alternative implementation was provided.
    - You are implicitly or explicitly using a resilience configuration with a `TimeLimiter` defined.
      - This means that the `TimeLimiter` was not taking effect prior to `5.2.0` and respective warnings have been logged about this.
    - The code inside the resilient execution is implicitly or explicitly using the facade implementation. 

    If all of the above conditions apply, you might need to ensure that the `ThreadLocal` variables you are using for your custom facades are passed on by the `ThreadContextExecutor`.
    Please follow [this documentation](https://sap.github.io/cloud-sdk/docs/java/features/multi-tenancy/thread-context#passing-on-other-threadlocals) on how to achieve this.
    
    </details>
- `com.sap.cloud.sdk.cloudplatform.connectivity.DestinationService.Cache` now enables change detection by default, but can be disabled via `DestinationService.Cache.disableChangeDetection()`. 
  - `DestinationService.Cache.enableChangeDetection()` has been deprecated.

### ✨ New Functionality

- 

### 📈 Improvements

- Improved the resilience module structure.
  The de-facto standard `resilience4j` implementation is now included by default.
  It is no longer necessary to explicitly reference this module in the `pom.xml`, if `resilience` is already referenced or is present transitively.
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
