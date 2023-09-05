# Disclaimer: We are working on the migration of this repository. The planned timeline for the OS migration and SDK version 5 is Q4 2023. Please stay tuned.


<a href="https://sap.github.io/cloud-sdk/docs/java/overview-cloud-sdk-for-java"><img src="https://help.sap.com/doc/2324e9c3b28748a4ae2ad08166d77675/1.0/en-US/logo-for-java.svg" alt="SAP Cloud SDK for Java Logo" height="122.92" width="226.773"/></a>

![build](https://github.com/SAP/cloud-sdk-java/workflows/build/badge.svg)
[![REUSE status](https://api.reuse.software/badge/github.com/SAP/cloud-sdk-java)](https://api.reuse.software/info/github.com/SAP/cloud-sdk-java)
[![Fosstars security rating](https://github.com/SAP/cloud-sdk-java/blob/fosstars-report/fosstars_badge.svg)](https://github.com/SAP/cloud-sdk-java/blob/fosstars-report/fosstars_report.md)
[![Maven Central](https://img.shields.io/badge/maven_central-4.0.0-blue.svg)](https://search.maven.org/search?q=g:com.sap.cloud.sdk%20AND%20a:sdk-modules-bom%20AND%20v:4.*)

# SAP Cloud SDK for Java

## About this project

The SAP Cloud SDK for Java makes it easy to build highly connected, resilient, multi tenant SaaS applications on the SAP Business Technology Platform (SAP BTP).
It enables you to perform outbound requests using various protocols while only writing the business logic.

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

## Support, Feedback, Contributing

This project is open to feature requests/suggestions, bug reports etc. via [GitHub issues](https://github.com/sap/cloud-sdk-java/issues). Contribution and feedback are encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](CONTRIBUTING.md).

## Code of Conduct

We as members, contributors, and leaders pledge to make participation in our community a harassment-free experience for everyone. By participating in this project, you agree to abide by its [Code of Conduct](https://github.com/SAP/.github/blob/main/CODE_OF_CONDUCT.md) at all times.

## Licensing

Copyright 2023 SAP SE or an SAP affiliate company and cloud-sdk-java contributors. Please see our [LICENSE](LICENSE) for copyright and license information. Detailed information including third-party components and their licensing/copyright information is available [via the REUSE tool](https://api.reuse.software/info/github.com/SAP/cloud-sdk-java).
