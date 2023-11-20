# Disclaimer: We are working on the migration of this repository. The planned timeline for the OS migration and SDK version 5 is Q4 2023. Please stay tuned.


<a href="https://sap.github.io/cloud-sdk/docs/java/overview-cloud-sdk-for-java"><img src="https://help.sap.com/doc/2324e9c3b28748a4ae2ad08166d77675/1.0/en-US/logo-for-java.svg" alt="SAP Cloud SDK for Java Logo" height="122.92" width="226.773"/></a>

![build](https://github.com/SAP/cloud-sdk-java/workflows/build/badge.svg)
[![REUSE status](https://api.reuse.software/badge/github.com/SAP/cloud-sdk-java)](https://api.reuse.software/info/github.com/SAP/cloud-sdk-java)
[![Fosstars security rating](https://github.com/SAP/cloud-sdk-java/blob/fosstars-report/fosstars_badge.svg)](https://github.com/SAP/cloud-sdk-java/blob/fosstars-report/fosstars_report.md)
[![Maven Central](https://img.shields.io/badge/maven_central-4.0.0-blue.svg)](https://search.maven.org/search?q=g:com.sap.cloud.sdk%20AND%20a:sdk-modules-bom%20AND%20v:4.*)

# SAP Cloud SDK for Java

## Contents
1. [About This Project](#about-this-project)
2. [Release Notes](#release-notes)
3. [Documentation](#documentation)
4. [Requirements and Setup](#requirements-and-setup)
   1. [Building with Maven](#building-with-maven)
5. [Migrating to Version 5](#migrating-to-version-5)
6. [Features](#features)
   1. [OData](#odata)
   2. [OpenAPI](#openapi)
   3. [Connectivity](#connectivity)
6. [Support, Feedback, Contributing](#support-feedback-contributing)
7. [Code of Conduct](#code-of-conduct)
8. [Licensing](#licensing)

## About This Project

The SAP Cloud SDK for Java makes it easy to build highly connected, resilient, multi tenant SaaS applications on the SAP Business Technology Platform (SAP BTP).
It enables you to perform outbound requests using various protocols while only writing the business logic.

## Release Notes
- For an overview of the latest changes, please refer to our [release notes.](https://sap.github.io/cloud-sdk/docs/java/v5/release-notes).

## Documentation
- Our documentation can be found [here](https://sap.github.io/cloud-sdk/docs/java/getting-started), which includes an [overview of available tutorials](https://sap.github.io/cloud-sdk/docs/java/guides/tutorial-overview-sdk-java).

## Requirements and Setup

Prerequisites:
- [Git](https://git-scm.com/downloads)
- [Any OpenJDK variant, for example SapMachine](https://sap.github.io/SapMachine/)
- [Maven](https://maven.apache.org/)
- (Optional) [Intellij](./docs/how-to/setup-intellij.md)

We provide maven archetypes which are the recommended way to start new projects based on SAP Cloud SDK for Java.

See the [getting started section of our documentation](https://sap.github.io/cloud-sdk/docs/java/getting-started) for more details.

### Building with Maven

Build, test, and install into local Maven repository:
```
mvn clean install
```
To skip tests, add `-Dmaven.test.skip`.

## Migrating to Version 5

- Please refer to our Migration Guide found [here](https://sap.github.io/cloud-sdk/docs/java/v5/guides/5.0-upgrade).

## Features

### OData

The SAP Cloud SDK for Java supports OData v2 and OData v4 protocols by offering Type-Safe and Generic OData Clients and the OData Code Generator. 

Popular use cases include:
- Extending an SAP product or service, building a middle-ware, publishing a cloud app
- Developing and publishing an OData service
- Coding convenience and inter-operability 

See the [OData section of our documentation](https://sap.github.io/cloud-sdk/docs/java/v5/features/odata/overview#popular-use-cases-for-type-safe-odata-client) for more information.

### OpenAPI

We make the consumption of OpenAPI services easy and convenient by providing a code generator that converts OpenAPI specifications into type-safe Java client libraries that work within the SAP Business Technology Platform.

Learn more in the [OpenAPI section of our documentation.](https://sap.github.io/cloud-sdk/docs/java/v5/features/rest/overview)

### Connectivity

The Destination API provides convenient abstraction for establishing connections to other systems and services with various authentication methods.

Popular use cases include:
- Access destinations defined in the SAP BTP Cockpit
- Define and configure your own destinations
- Perform REST, RFC, or SOAP requests
- Use ready-made HTTP Clients to execute custom requests

Please refer to the [Connectivity section of our documentation](https://sap.github.io/cloud-sdk/docs/java/v5/features/connectivity/destination-service) for details.

## Support, Feedback, Contributing

This project is open to feature requests/suggestions, bug reports etc. via [GitHub issues](https://github.com/sap/cloud-sdk-java/issues). Contribution and feedback are encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](CONTRIBUTING.md).

## Code of Conduct

We as members, contributors, and leaders pledge to make participation in our community a harassment-free experience for everyone. By participating in this project, you agree to abide by its [Code of Conduct](https://github.com/SAP/.github/blob/main/CODE_OF_CONDUCT.md) at all times.

## Licensing

Copyright 2023 SAP SE or an SAP affiliate company and cloud-sdk-java contributors. Please see our [LICENSE](LICENSE) for copyright and license information. Detailed information including third-party components and their licensing/copyright information is available [via the REUSE tool](https://api.reuse.software/info/github.com/SAP/cloud-sdk-java).
