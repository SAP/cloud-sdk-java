## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- The OpenAPI generator doesn't add `//NOPMD` after imports anymore.

### ✨ New Functionality

- 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- The OpenAPI Generator correctly declares `@Nonnull` and `@Nullable` annotations on generated endpoint methods.
- The OpenAPI Generator uses correct camelCase again, when creating methods to _add items_ to a collections.
  Version `5.10.0` used incorrect `addfooItems(Foo)` instead of `addFooItems(Foo)`.
  This is fixed now.