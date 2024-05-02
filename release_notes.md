## 5.X.0-SNAPSHOT

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases)

### 🚧 Known Issues

- 

### 🔧 Compatibility Notes

- Deprecate the `BtpServiceOptions.IasOptions.withTargetUri` method overloads.
  As a replacement, users should use the corresponding `BtpServiceOptions.AuthenticationServiceOptions.withTargetUri` method.
  Additionally, the existing behavior of the deprecated methods has been changed:
  They are no longer returning an instance of `BtpServiceOptions.IasOptions.IasTargetUri` but instead return now an instance of `BtpServiceOptions.AuthenticationServiceOptions.TargetUri`.
  The `BtpServiceOptions.IasOptions.IasTargetUri` class is also deprecated.

  **Please Note**: 
  The deprecated methods will stay functioning (with the mentioned behavior changes) for a while to give users time to migrate to the new methods.
  Nevertheless, we are planning to **remove** the deprecated methods and the class **still within version 5 of the SAP Cloud SDK**.

### ✨ New Functionality

- Service Bindings for the SAP XSUAA service (Service Identifier `xsuaa`) can now be converted into destinations just as any other supported service.
  This is most useful when combined with the newly introduced `BtpServiceOptions.AuthenticationServiceOptions.withTargetUri` methods, which allows for communication between services that are backed by the same XSUAA instance.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- 
