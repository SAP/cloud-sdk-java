## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- Changed a behavior details when obtaining tokens from IAS with the default strategy `CURRENT_TENANT`. 
  In case the current tenant is the provider tenant, and `TenantAccessor.getCurrentTenant()` is returning a `Tenant` object, this object is now required to have a subdomain != null.

### ✨ New Functionality

- 

### 📈 Improvements

- 

### 🐛 Fixed Issues

- OpenAPI: When `apiMaturity` is set to `beta`, generated enums will now be `@Beta` annotated.
