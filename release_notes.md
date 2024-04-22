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
- Improve connecting to IAS-based applications and services.
  Scenarios where an IAS tenant is connected to multiple subaccounts of an application are now supported.
  - Note that when mocking an IAS binding for testing the binding entry `app_tid` is now required.

### 🐛 Fixed Issues

- Fix an issue that would cause a NPE when using bound services backed by IAS via the [dedicated service binding format](/docs/java/features/connectivity/service-bindings#service-binding-format).
- Fix an issue that would cause a NPE when using the OData `applyAction` method with a `null` parameter value.
