## 5.4.0-SNAPSHOT - February XX, 2024

[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases/tag/rel%2F5.X.0)

### 🚧 Known Issues

-

### 🔧 Compatibility Notes

- 

### ✨ New Functionality

- 

### 📈 Improvements

- Destination key-stores of type _PKCS #12_ with empty (or no) password will be correctly loaded, including their certificates.
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
| [json](https://search.maven.org/search?q=g%3Aorg.json%2Ba%3Ajson) (`org.json`) | `20231013` | `20240205` |
| [java-modules-bom](https://search.maven.org/search?q=g%3Acom.sap.cloud.environment.servicebinding%2Ba%3Ajava-modules-bom) (`com.sap.cloud.environment.servicebinding`) | `0.10.1` | `0.10.2` |

</details>

### 🐛 Fixed Issues

- 
