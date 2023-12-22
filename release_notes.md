# 5.1.0-SNAPSHOT

release-date: January XX, 2024
docs: https://sap.github.io/cloud-sdk/docs/java/release-notes

## 🚧 Known Issues

-


## 🔧 Compatibility Notes

- An earlier version of the [V5 Upgrade Guide](https://sap.github.io/cloud-sdk/docs/java/guides/5.0-upgrade-steps) contained an instruction to move handling of `DestinationAccessExceptions` from `DestinationAccessor.getDestination()` to `destination.getHeaders()`.
  This instruction was incorrect and has been removed.
  In case you have followed this instruction, please revert the change.
- `UriBuilder.build(scheme, userInfo, host, port, path, query, fragment)` has been deprecated in favor of `UriBuilder.build(scheme, authority, path, query, fragment)`.
- Deprecate the strategies `LOOKUP_ONLY` and `LOOKUP_THEN_EXCHANGE` of `DestinationServiceTokenExchangeStrategy`.
  They are replaced by the `FORWARD_USER_TOKEN` strategy.
  If there are any issues when using `FORWARD_USER_TOKEN` for destinations that require user tokens, please report them and use `EXCHANGE_ONLY` for such cases.
- `DestinationService.getAllDestinationProperties()` and `DestinationService.getAllDestinationProperties(DestinationOptions opts)` have been deprecated in favor of `DestinationService.getAllDestinationProperties()`.

## ✨ New Functionality

- Added new API to retrieve destination properties only from the BTP Destination Service. 
  `DestinationService` now offers `getDestinationProperties(String destinationName)` and `getAllDestinationProperties()`.


## 📈 Improvements

- A warning is now logged when destinations with expired authentication tokens are used for requests.
- SAP dependency updates:
  - Update the [SAP Security Library](https://github.com/SAP/cloud-security-services-integration-library) from `3.3.0` to `3.3.1`
- Other dependency updates:
  - Update [Apache HttpClient 5](https://search.maven.org/artifact/org.apache.httpcomponents.client5/httpclient5) from `5.2.1` to `5.3`
  - Update [Apache HttpCore 5](https://search.maven.org/search?q=a:httpcore5) from `5.2.3` to `5.2.4`

## 🐛 Fixed Issues

- Fix an issue where an invalid hostname in a destination would lead to an empty hostname. The hostname is now accepted.
- Fix an issue where errors from token flows of destinations retrieved from the BTP destination service were not handled consistently.
  For the non-default strategy `LOOKUP_ONLY` errors were handled too late and results would get cached unintentionally.
  


