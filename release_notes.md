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
- OData v2 and v4 generators now use `LinkedHashMap` for the properties of the generated classes to maintain the order of the properties.

### 🐛 Fixed Issues

- Fix ApacheHttpClient5Wrapper to propagate the configuration to Spring RestTemplate.
- Fix OData v2 and v4 generators to work when property name is `value` or `values` and is of collection type.
  - The internal variable is now respectively `cloudSdkValue` or `cloudSdkValues` to avoid conflicts with the `value` or `values` property.
