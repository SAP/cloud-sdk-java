## 5.3.0-SNAPSHOT - January XX, 2024

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases/tag/rel%2F5.X.0)

### 🚧 Known Issues

- Destination key-stores of type _PKCS #12_ with empty (or no) password will be correctly loaded, including their certificates.

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- The destination property `HTML5.ForwardAuthToken` is now evaluated and forwards the current AuthToken to the target system.
- Improve how certificates are evaluated for the authentication types `SAMLAssertion` and `OAUTH2_SAML_BEARER_ASSERTION`.
  The certificates are no longer unnecessarily parsed when obtaining the destination.
- The `spring-boot3` archetype no longer contains the `integration-tests` module. The contained tests are now part of the test suite of the `application` module.
- Dependency Updates:
  - Update `org.apache.olingo` from `4.10.0` to `5.0.0`
  - Update `io.github.resilience4j` from `1.7.1` to `2.2.0`
  - [com.sap.cloud.security](https://github.com/SAP/cloud-security-xsuaa-integration) from `3.3.3` to `3.3.4`
  - [com.sap.cloud.environment.servicebinding](https://github.com/SAP/btp-environment-variable-access) from `0.10.1` to `0.10.2` 
  - `org.slf4j:slf4j-api` from `2.0.9` to `2.0.11`
  - `commons-io:commons-io` from `2.15.0` to `2.15.1`
  - Update `com.sap.cloud:neo-java-web-api` from `4.67.12` to `4.68.9`
  - Update [`org.apache.httpcomponents.client5:httpclient5`](https://github.com/apache/httpcomponents-client) from `5.3` to `5.3.1`


<details><summary>Dependency Updates</summary>

| Dependency | From | To |
| --- | --- | --- |
| [commons-codec](https://search.maven.org/search?q=g%3Acommons-codec%2Ba%3Acommons-codec) (`commons-codec`) | `1.16.0` | `1.16.1` |
| [java-modules-bom](https://search.maven.org/search?q=g%3Acom.sap.cloud.environment.servicebinding%2Ba%3Ajava-modules-bom) (`com.sap.cloud.environment.servicebinding`) | `0.10.2` | `0.10.3` |
| [jcl-over-slf4j](https://search.maven.org/search?q=g%3Aorg.slf4j%2Ba%3Ajcl-over-slf4j) (`org.slf4j`) | `2.0.11` | `2.0.12` |
| [joda-time](https://search.maven.org/search?q=g%3Ajoda-time%2Ba%3Ajoda-time) (`joda-time`) | `2.12.6` | `2.12.7` |
| [neo-java-web-api](https://search.maven.org/search?q=g%3Acom.sap.cloud%2Ba%3Aneo-java-web-api) (`com.sap.cloud`) | `4.68.9` | `4.69.7` |
| [openapi-generator](https://search.maven.org/search?q=g%3Aorg.openapitools%2Ba%3Aopenapi-generator) (`org.openapitools`) | `7.2.0` | `7.3.0` |
| [slf4j-api](https://search.maven.org/search?q=g%3Aorg.slf4j%2Ba%3Aslf4j-api) (`org.slf4j`) | `2.0.11` | `2.0.12` |
| [slf4j-ext](https://search.maven.org/search?q=g%3Aorg.slf4j%2Ba%3Aslf4j-ext) (`org.slf4j`) | `2.0.11` | `2.0.12` |

</details>

### 🐛 Fixed Issues

- 