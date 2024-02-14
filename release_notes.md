## 5.3.0-SNAPSHOT - January XX, 2024

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases/tag/rel%2F5.X.0)

### 🚧 Known Issues

-

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- The destination property `HTML5.ForwardAuthToken` is now evaluated and forwards the current AuthToken to the target system.
- Improve how certificates are evaluated for the authentication types `SAMLAssertion` and `OAUTH2_SAML_BEARER_ASSERTION`.
  The certificates are no longer unnecessarily parsed when obtaining the destination.
- The `spring-boot3` archetype no longer contains the `integration-tests` module. The contained tests are now part of the test suite of the `application` module.
- OpenAPI clients can now be generated with specifications with `oneOf` and `anyOf` keywords by enabling their processing by using `<enableOneofAnyofGeneration>` in `openapi-generator-maven-plugin`(This option is turned off by default):
  ```xml
  <plugin>
      <groupId>com.sap.cloud.sdk.datamodel</groupId>
      <artifactId>openapi-generator-maven-plugin</artifactId>
      ...   
      <configuration>
         ...
        <enableOneofAnyofGeneration>true</enableOneofAnyofGeneration>
      </configuration>
  </plugin>
  ```
  But,the generated client may not be feature complete and work as expected for all cases involving `anyOf/oneOf`.
- Dependency Updates:
  - Update `org.apache.olingo` from `4.10.0` to `5.0.0`
  - Update `io.github.resilience4j` from `1.7.1` to `2.2.0`
  - [com.sap.cloud.security](https://github.com/SAP/cloud-security-xsuaa-integration) from `3.3.3` to `3.3.4`
  - [com.sap.cloud.environment.servicebinding](https://github.com/SAP/btp-environment-variable-access) from `0.10.1` to `0.10.2` 
  - `org.slf4j:slf4j-api` from `2.0.9` to `2.0.11`
  - `commons-io:commons-io` from `2.15.0` to `2.15.1`
  - Update `com.sap.cloud:neo-java-web-api` from `4.67.12` to `4.68.9`
  - Update [`org.apache.httpcomponents.client5:httpclient5`](https://github.com/apache/httpcomponents-client) from `5.3` to `5.3.1`

### 🐛 Fixed Issues

- 
