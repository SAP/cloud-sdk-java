## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Add experimental support for updating nested fields in OData V2 complex types via PATCH requests
  - Use optional argument `FluentHelperUpdate#modifyingEntity( ModifyPatchStrategy )` to control updates with delta or full complex property payloads.

### 📈 Improvements

- Improve the OData V4 class `BatchRequestBuilder` to now also implement the `ModificationRequestBuilder` interface. 

### 🐛 Fixed Issues

- Fix non-compilable code using OpenAPI generator with schema definitions having `additionalProperties: true`.
  Previously they would result in model classes extending `HashMap`, which disabled proper deserialization and serialization.
