## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- `ODataResourcePath#addSegment(...)` and `addParameterToLastSegment(...)` now return a new path instance instead of mutating the existing one. Custom extensions that relied on in-place mutation need to reassign the returned path.

### ✨ New Functionality

- 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- Fixed stateful OData request path construction caused by shared `ODataResourcePath` instances being mutated when building count, read-by-key, and function requests.
