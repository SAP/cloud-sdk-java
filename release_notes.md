## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

-

### 🔧 Compatibility Notes

-

### ✨ New Functionality

- `DestinationService.tryGetDestination` now checks if the given destination exists before trying to call it directly.
  This behaviour is enabled by default and can be disabled via `DestinationService.Cache.disablePreLookupCheck`.
- Added native DestinationLoader implementation through the `TransparentProxy` class. This enables seamless destination
  retrieval via `tryGetDestination` and provides full Cloud SDK integration for applications running in Kubernetes
  environments where Transparent Proxy is available.

### 📈 Improvements

-

### 🐛 Fixed Issues

- Fix unintended modification of `serviceNameMappings.properties` during OData service regeneration altering stored
  mappings.
  Additionally, service name cleanup is now case-insensitive for consistency.
