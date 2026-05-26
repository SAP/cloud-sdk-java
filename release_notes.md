## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- OAuth token requests to IAS now attempt to dynamically resolve the IAS tenant host, if not given.
  When the current tenant does not contain a subdomain, and the IAS service binding contains the property `btp-tenant-api`, then an HTTP call to that URL is performed to obtain the IAS tenant host.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- 
