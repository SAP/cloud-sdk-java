## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- Close CSRF Token Retrieval response `HttpEntity` and underlying `InputStream` manually.
  In case of an error the connection will be closed eagerly.

### 🐛 Fixed Issues

- Stop unnecessarily throwing and catching `NullPointerException` when interacting with `DefaultHttpDestination#equals(...)` and `#hashCode()`.
