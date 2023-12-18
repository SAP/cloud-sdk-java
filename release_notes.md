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

- A warning is now logged when destinations with expired authentication tokens are used for requests.
- SAP dependency updates:
  - Update the [SAP Security Library](https://github.com/SAP/cloud-security-services-integration-library) from `3.3.0` to `3.3.1`

## 🐛 Fixed Issues

- Fix an issue where an invalid hostname in a destination would lead to an empty hostname. The hostname is now accepted.


