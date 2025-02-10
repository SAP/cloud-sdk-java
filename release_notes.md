## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- Fix OData `VDMObject.getChangedFields()` to use `BigDecimal.compareTo()` instead of `BigDecimal.equals()`.
  - Example: a `BigDecimal` field updated from `1` to `1.0` will not be considered as changed anymore.
