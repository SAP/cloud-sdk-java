## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- The OpenAPI generator doesn't add `//NOPMD` after imports anymore.

### ✨ New Functionality

- Add experimental support for [_Destination Fragments_](https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/extending-destinations-with-fragments).
  Fragment names can be passed upon requesting destinations via `DestinationServiceOptionsAugmenter.fragmentName("my-fragment-name")`.
  For further details refer to [the documentation]().

### 📈 Improvements

- 

### 🐛 Fixed Issues

- The OpenAPI Generator correctly declares `@Nonnull` and `@Nullable` annotations on generated endpoint methods.
- The OpenAPI Generator uses correct camelCase again, when creating methods to _add items_ to a collections.
  Version `5.10.0` used incorrect `addfooItems(Foo)` instead of `addFooItems(Foo)`.
  This is fixed now.