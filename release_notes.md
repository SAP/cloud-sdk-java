## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- Deprecated `RfcDestination` and all associated usages. The replacement is `Destination`.

### ✨ New Functionality

- `war` deployment in combination with the `SAP Java Buildpack` 2.2.0 is supported again.
  Our `RFC` artifact is now released, and `JCo` functionalities are supported again.
  Please follow our guide to update to version 5 of the SDK.

### 📈 Improvements

- Improve the `DefaultHttpDestination` builder API: For destinations with proxy type `ON_PREMISE` the proxy URL can now be customized by using the `proxy` method of the builder.
- Dependency Updates:
  - SAP dependency updates:
    - Update [thing](https://link-to-thing) from `a.b.c` to `x.z.y`
  - Other dependency updates:
    - Major version updates:
      - Update [thing](https://link-to-thing) from `a.b.c` to `x.z.y`
    - Minor version updates:
      - Update [thing](https://link-to-thing) from `a.b.c` to `x.z.y`

### 🐛 Fixed Issues

- Fixed an issue where adding header providers to a destination after it had already been used to obtain an Apache `HttpClient` 5 would not work as expected.

