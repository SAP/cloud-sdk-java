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

- Support the [OAuth Refresh Token](https://help.sap.com/docs/connectivity/sap-btp-connectivity-cf/oauth-refresh-token-authentication) authentication type of the Destination Service.
  Find more details on how to use the functionality in the [documentation](https://sap.github.io/cloud-sdk/docs/java/features/connectivity/btp-destination-service#about-the-destinationservice).
- Service Bindings for the SAP XSUAA service (Service Identifier `xsuaa`) can now be converted into destinations just as any other supported service.
  This is most useful when combined with the newly introduced `BtpServiceOptions.AuthenticationServiceOptions.withTargetUri` methods, which allows for communication between services that are backed by the same XSUAA instance.

### 📈 Improvements

- 

### 🐛 Fixed Issues

- Fix a regression that was introduced with the SAP Cloud SDK 5.0 release where the principal would no longer be derived from a `Basic` authorization header, in cases where neither a JWT nor an OIDC token was present.
- Fix a regression that was introduced with the SAP Cloud SDK 5.0 release where auth tokens sent by the Destination service would no longer be stored in the `cloudsdk.authTokens` destination property for non-HTTP destinations.
