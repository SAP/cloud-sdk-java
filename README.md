![build](https://github.com/SAP/cloud-sdk-java/actions/workflows/continuous-integration.yaml/badge.svg?branch=main)
[![REUSE status](https://api.reuse.software/badge/github.com/SAP/cloud-sdk-java)](https://api.reuse.software/info/github.com/SAP/cloud-sdk-java)
[![Fosstars security rating](https://github.com/SAP/cloud-sdk-java/blob/fosstars-report/fosstars_badge.svg)](https://github.com/SAP/cloud-sdk-java/blob/fosstars-report/fosstars_report.md)
[![Maven Central](https://img.shields.io/badge/maven_central-5.24.0-blue.svg)](https://search.maven.org/search?q=g:com.sap.cloud.sdk%20AND%20a:sdk-core%20AND%20v:5.24.0)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/SAP/cloud-sdk-java)

# <img src="https://sap.github.io/cloud-sdk/img/logo.svg" alt="SAP Cloud SDK" width="30"/> SAP Cloud SDK for Java

🆕 Documentation portal for the [SAP Cloud SDK for AI](https://sap.github.io/ai-sdk/docs/java/overview-cloud-sdk-for-ai-java).

## Contents

1. [About This Project](#about-this-project)
2. [Features](#features)
    1. [OData](#odata)
    2. [OpenAPI](#openapi)
    3. [Connectivity](#connectivity)
3. [Licensing](#licensing)
4. [Support, Feedback, Contributing](#support-feedback-contributing)
5. [Code of Conduct](#code-of-conduct)
6. [Building the Project](#building-the-project)

## About This Project

The SAP Cloud SDK for Java makes it easy to build highly connected, resilient, multi tenant SaaS applications on the SAP Business Technology Platform (SAP BTP).
It enables you to perform outbound requests using various protocols while only writing the business logic.

For more information head over to the official [SAP Cloud SDK for Java Documentation](https://sap.github.io/cloud-sdk/docs/java/overview-cloud-sdk-for-java). To get started using the SAP Cloud SDK for Java, please refer to the [Getting Started Guide](https://sap.github.io/cloud-sdk/docs/java/getting-started).

## Features

### OData

The SAP Cloud SDK for Java supports OData v2 and OData v4 protocols by offering Type-Safe and Generic OData Clients and the OData Code Generator.

Popular use cases include:
- Extending an SAP product or service, building a middle-ware, publishing a cloud app
- Developing and publishing an OData service
- Coding convenience and inter-operability

See the [OData section of our documentation](https://sap.github.io/cloud-sdk/docs/java/features/odata/overview) for more information.

### OpenAPI

We make the consumption of OpenAPI services easy and convenient by providing a code generator that converts OpenAPI specifications into type-safe Java client libraries that work within the SAP Business Technology Platform.

Learn more in the [OpenAPI section of our documentation.](https://sap.github.io/cloud-sdk/docs/java/features/rest/overview)

### Connectivity

The Destination API provides convenient abstraction for establishing connections to other systems and services with various authentication methods.

Popular use cases include:
- Access destinations defined in the SAP BTP Cockpit
- Define and configure your own destinations
- Perform REST, RFC, or SOAP requests
- Use ready-made HTTP Clients to execute custom requests

Please refer to the [Connectivity section of our documentation](https://sap.github.io/cloud-sdk/docs/java/features/connectivity/destination-service) for details.

## Licensing

Copyright 2025 SAP SE or an SAP affiliate company and cloud-sdk-java contributors. Please see our [LICENSE](LICENSE) for copyright and license information. Detailed information including third-party components and their licensing/copyright information is available [via the REUSE tool](https://api.reuse.software/info/github.com/SAP/cloud-sdk-java).

## Support, Feedback, Contributing

This project is open to feature requests/suggestions, bug reports etc. via [GitHub issues](https://github.com/sap/cloud-sdk-java/issues). Contribution and feedback are encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](CONTRIBUTING.md).

## Code of Conduct

We as members, contributors, and leaders pledge to make participation in our community a harassment-free experience for everyone. By participating in this project, you agree to abide by its [Code of Conduct](https://github.com/SAP/.github/blob/main/CODE_OF_CONDUCT.md) at all times.

## Building the Project

Prerequisites:
- A Java 17 JDK (for example [SapMachine](https://sap.github.io/SapMachine/)).
  - Higher JDK versions currently may produce warnings during compilation which the project is configured to fail on.
- [Maven](https://maven.apache.org/) in version 3.9.5 or higher.

Build, test, and install into local Maven repository:

```bash
mvn clean install
```

Further options:
- To skip tests, add `-DskipTests`.
- To skip formatting, add `-DskipFormatting`.

To configure code style, formatting and linting for [IntelliJ IDEA](https://www.jetbrains.com/idea/), follow [these instructions](./docs/how-to/setup-intellij.md). 
