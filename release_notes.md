## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- Consume CSRF Token Retrieval response `HttpEntity` manually, thus closing underlying `InputStream` eagerly.
  In case of an error the connection will not be left open, waiting to be closed by connection manager.

### 🐛 Fixed Issues

- Stop unnecessarily throwing and catching `NullPointerException` when interacting with `DefaultHttpDestination#equals(...)` and `#hashCode()`.
