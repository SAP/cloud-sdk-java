# 5.1.0-SNAPSHOT

release-date: January XX, 2024
docs: https://sap.github.io/cloud-sdk/docs/java/release-notes

## 🚧 Known Issues

-


## 🔧 Compatibility Notes

- `UriBuilder.build(scheme, userInfo, host, port, path, query, fragment)` has been deprecated in favor of
 `UriBuilder.build(scheme, authority, path, query, fragment)`.
- `com.sap.cloud.sdk.cloudplatform.connectivity.DestinationService.Cache` now enables change detection by default, but can be disabled via `DestinationService.Cache.disableChangeDetection()`. 
  - `DestinationService.Cache.enableChangeDetection()` has been deprecated.


## ✨ New Functionality

-


## 📈 Improvements

- SAP dependency updates:
  - Update the [SAP Security Library](https://github.com/SAP/cloud-security-services-integration-library) from `3.3.0` to `3.3.1`
- Other dependency updates:
  - Update [Apache HttpClient 5](https://search.maven.org/artifact/org.apache.httpcomponents.client5/httpclient5) from `5.2.1` to `5.3`
  - Update [Apache HttpCore 5](https://search.maven.org/search?q=a:httpcore5) from `5.2.3` to `5.2.4`

## 🐛 Fixed Issues

- Fix an issue where an invalid hostname in a destination would lead to an empty hostname. The hostname is now accepted.


