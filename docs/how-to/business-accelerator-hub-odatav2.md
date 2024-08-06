# API Consumption

## Java

### About
The [SAP Cloud SDK](https://sap.github.io/cloud-sdk/) is a versatile set of libraries for developers to build backend applications in a cloud-native way and host them on SAP Business Technology Platform or other runtimes.
It allows the user to develop, extend, and communicate with SAP solutions like SAP S/4HANA Cloud, SAP SuccessFactors, and many others.

For HTTP protocols _OpenAPI_ and _OData_, the _SAP Cloud SDK_ provides convenient features to transform service specifications into client libraries that are type-safe, resilient and performant.

### Generate Client Library for OData V2 Services

#### Generation Steps

You can generate a typed client for this service yourself.
Follow the sample instructions below to complete the generation steps:

* (Install Apache Maven version `3.9.5` or above.)
* (Install Java `17` or above.)
* (Have a Maven based Java project, with a `pom.xml` in `<project_root>/`)
* Download and put the API specification into `<project_root>/src/main/resources/`.
* Configure the code generator in `<project_root>/pom.xml`.
* Consume the generated code.

The details of the generation steps may vary depending on the custom project setup.

#### Download the API Specification

* To download the API specification from the Overview tab, scroll to the _API Resources_ section.
  **Choose EDMX format.**
  The other formats are not supported by _SAP Cloud SDK_ code generation for OData.
* Put the specification inside the project folder to `<project_root>/src/main/resources/`

#### Configure the Code Generator

* The following dependencies are required for the code, that will be generated.
  Add the following to the _dependencies_ section of the `<project_root>/pom.xml`:
    ```xml
    <dependency>
      <groupId>com.sap.cloud.sdk.datamodel</groupId>
      <artifactId>odata-core</artifactId>
      <version>5.11.0</version> <!-- Please use always the latest version! -->
    </dependency>
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.34</version> <!-- Please use always the latest version! -->
      <scope>provided</scope>
    </dependency>
    ```
  Versions tags may need to be applied to the above declarations.

* Under the _plugins_ section in the `<project_root>/pom.xml` add the generator plugin for OData.
  ```xml
  <plugin>
    <groupId>com.sap.cloud.sdk.datamodel</groupId>
    <artifactId>odata-generator-maven-plugin</artifactId>
    <version>5.11.0</version> <!-- Please use always the latest version! -->
    <executions>
      <execution>
        <id>generate-consumption</id>
        <phase>generate-sources</phase>
        <goals>
          <goal>generate</goal>
        </goals>
        <configuration>
          <inputDirectory>${project.basedir}/src/main/resources</inputDirectory>
          <outputDirectory>${project.basedir}/src/main/java/generated</outputDirectory>
          <deleteOutputDirectory>true</deleteOutputDirectory>
          <packageName>com.mycompany.vdm</packageName>
          <serviceMethodsPerEntitySet>true</serviceMethodsPerEntitySet>
        </configuration>
      </execution>
    </executions>
  </plugin>
  ```
  * Review the configuration section of the plugin to make sure `inputDirectory` points at the directory where the EDMX specification is stored.
  * Generated classes will be stored in the directory specified by `outputDirectory`.
  * <details><summary>
    Set other configuration parameters to your preferences or leave them as is.
    Maven allows for different approaches.
    </summary>
    
    * Above configuration deletes old generated code and generates new code with every compilation.
    * Remove or change the `phase` to customize the plugin invocation order in the build.
    * Move the `outputDirectory` to a dedicated folder outside of `/src/main/java` to not pollute the Java sources.
      Use Maven plugin `org.apache.maven.plugins:maven-compiler-plugin` or `org.codehaus.mojo:build-helper-maven-plugin` to enable additional source folders.
    * Move the `outputDirectory` to the `/target` folder to avoid checking in generated code to the project sources repository. 

    </details>

#### Consume the Generated Code.

* Run `mvn clean install` in the root folder of your project.
* You should now find generated classes in the output directory specified in your generator maven plugin configuration.
* In case of any issues carefully check your configuration and refer to our [extended typed client generation manual](https://sap.github.io/cloud-sdk/docs/java/features/odata/vdm-generator).
* Congratulations! Check the usage example below to import and invoke the generated OData client library.

Usage Example:

* To consume the service via the generated typed client library, run the code snippet below:
  ```java
  Destination destination = DestinationAccessor.getDestination("MyDestination")
  List<Product> returnedList = new DefaultSdkGroceryStoreService().getAllProduct().executeRequest(destination);
  ```

Further samples:
* Please find our [sample project for OData v2 specification](https://github.com/SAP/cloud-sdk-java/tree/main/datamodel/odata/odata-api-sample)

Troubleshooting:
* Please find [our repository](https://github.com/SAP/cloud-sdk-java) to check out and investigate the code.
* Use the [_Issues_ section of the repository](https://github.com/SAP/cloud-sdk-java/issues) to discuss unresolved problems.
