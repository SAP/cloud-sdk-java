# 5.1.0-SNAPSHOT

release-date: January XX, 2024
docs: https://sap.github.io/cloud-sdk/docs/java/release-notes

## 🚧 Known Issues

-


## 🔧 Compatibility Notes

- 


## ✨ New Functionality

-


## 📈 Improvements

- SAP dependency updates:
  - Update the [SAP Security Library](https://github.com/SAP/cloud-security-services-integration-library) from `3.3.0` to `3.3.1`

## 🐛 Fixed Issues

- Fix an issue where an invalid URL in a destination would lead to an empty hostname.
  Now a `DestinationAccessException` will be thrown upon executing a request, if the URL domain contains a hostname that violates [RFC 2396](https://www.ietf.org/rfc/rfc2396.txt).


