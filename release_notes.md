## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Add support for `TypeDefinition` entries in OData V4 EDMX files.
- Add `generateApis` and `generateModels` options to the `openapi-generator-maven-plugin` to
  disable the generation of APIs and models respectively.

### 📈 Improvements

- Stabilize most of the remaining experimental APIs without changes, e.g.
  - RequestHeaderAccessor
  - ServiceBindingDestinationLoader

### 🐛 Fixed Issues

- Fix ApacheHttpClient5Wrapper to propagate the configuration to Spring RestTemplate.
- Fix OData v4 generator to work when property name is `values` and is of collection type.
  - The internal variable is now `cloudSdkValues` to avoid conflicts with the `values` property.
