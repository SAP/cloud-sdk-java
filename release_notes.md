## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- Add support for `TypeDefinition` entries in OData V4 EDMX files.
- OpenAPI generator improvements:
  - Add `oneOf` support for OpenAPI generation.
    - The option `enableOneOfAnyOfGeneration` on the `openapi-generator-maven-plugin` can be used
      with the additional property `useOneOfInterfaces` to generate interfaces for `oneOf` schemas. 
  - Add `generateApis` and `generateModels` options to the `openapi-generator-maven-plugin` to
    disable the generation of APIs and models respectively.

### 📈 Improvements

- Stabilize most of the remaining experimental APIs without changes, e.g.
  - RequestHeaderAccessor
  - ServiceBindingDestinationLoader

### 🐛 Fixed Issues

- 
