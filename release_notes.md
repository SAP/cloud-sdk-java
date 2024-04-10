## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- Improve the efficiency of HTTP clients: The default cache duration for HTTP clients have been increased to expire one hour after last access (was 5 minutes after creation).
  Aside from a performance improvement, this improves the handling of cookies, as they are retained for much longer.

### 🐛 Fixed Issues

- Fix an issue that would cause a NPE when using bound services backed by IAS via the [dedicated service binding format](/docs/java/features/connectivity/service-bindings#service-binding-format).
