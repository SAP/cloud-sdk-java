# Contributing

## Code of Conduct

All members of the project community must abide by the [Contributor Covenant, version 2.1](CODE_OF_CONDUCT.md).
Only by respecting each other we can develop a productive, collaborative community.
Instances of abusive, harassing, or otherwise unacceptable behavior may be reported by contacting [a project maintainer](.reuse/dep5).

## Engaging in Our Project

We use GitHub to manage reviews of pull requests.

* If you are a new contributor, see: [Steps to Contribute](#steps-to-contribute)

* Before implementing your change, create an issue that describes the problem you would like to solve or the code that should be enhanced. Please note that you are willing to work on that issue.

* The team will review the issue and decide whether it should be implemented as a pull request. In that case, they will assign the issue to you. If the team decides against picking up the issue, the team will post a comment with an explanation.

## Steps to Contribute

Should you wish to work on an issue, please claim it first by commenting on the GitHub issue that you want to work on. This is to prevent duplicated efforts from other contributors on the same issue.

If you have questions about one of the issues, please comment on them, and one of the maintainers will clarify.

## Contributing Code or Documentation

You are welcome to contribute code in order to fix a bug or to implement a new feature that is logged as an issue.

The following rule governs code contributions:

* Contributions must be licensed under the [Apache 2.0 License](./LICENSE)
* Due to legal reasons, contributors will be asked to accept a Developer Certificate of Origin (DCO) when they create the first pull request to this project. This happens in an automated fashion during the submission process. SAP uses [the standard DCO text of the Linux Foundation](https://developercertificate.org/).

## Issues and Planning

* We use GitHub issues to track bugs and enhancement requests.

* Please provide as much context as possible when you open an issue. The information you provide must be comprehensive enough to reproduce that issue for the assignee.

## Write Tests

Please make sure that any changes to existing code or any new code is properly tested.
This makes it much easier to maintain in the long run and to verify whether your change has the intended effect.
Additionally, tests are required to maintain traceability.
If you are unsure how to test your code or where to put your tests, don't hesitate to contact us.

## Ensure Compatibility
The SDK has to support a wide range of technologies such as different Cloud platforms and application containers. Therefore, please adhere to the following guidelines:

- Ensure compatibility with Java 8.
- Ensure compatibility with Java EE and Spring. To achieve this, only use web filters, listeners, and servlets from the [Servlet 3.0](https://jcp.org/en/jsr/detail?id=315) standard as they are all supported by Java EE as well as Spring with the [servlet component scan](http://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/ServletComponentScan.html).

## Ensure Consistent Technologies/Dependencies
Avoid creating a "zoo of technologies" which tends to introduce more technical debt. This should not mean stagnation, but every change must be based on a factual argument of benefits over the existing technology in question. 
In particular:

- Only use one build system (in our case, Maven).
- Only introduce new libraries or frameworks for good reasons. 
- Ideally, investigate upfront if there are known vulnerabilities to any new dependencies you plan to introduce.

## Consult Architectural Decisions
Please consult our [documentation](./docs) and [architectural decisions](./docs/architecture) to understand their rationale.

## Code Style and Conventions
- Have a look at the code base and adapt to its style.
- Compiler warnings are considered as errors. Always try to fix warnings instead of simply suppressing them. Warnings should only be suppressed if there is a very good reason.
- Foster modularization. For instance, always try to restrict the access level of classes, class members, and methods as much as possible. This helps, for example, to maintain stable APIs and facilitates internal refactorings. Never broaden access levels without understanding the implications, for example, by consulting the original author.
- Use a functional style and immutability for less side-effects instead of over-reusing existing objects to avoid issues that are hard to find (e.g., concurrency issues). Keep in mind that the JVM's garbage collector is already optimized to deal with the creation of new objects by efficiently reusing them.
- Be careful with the use of static objects and methods to avoid concurrency issues. Keep in mind that those are actually quite common in web applications.
- Never use [raw types](https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.8) (e.g. `List` instead of `List<String>`). Instead, use an appropriate type or wildcard `<?>`.
- Handling of `null`:
    - Always annotate public methods to specify clear contracts.
      Use the JSR 305 annotations [`@Nonnull`](https://static.javadoc.io/com.google.code.findbugs/jsr305/3.0.1/javax/annotation/Nonnull.html) and [`@Nullable`](https://static.javadoc.io/com.google.code.findbugs/jsr305/3.0.1/javax/annotation/Nullable.html) for return type and non-primitive parameter types.
      This enables improved code analysis and consumption of the SDK in languages like [Kotlin](https://kotlinlang.org/).
    - Make sure to check your assumptions, e.g., whether or not a `Map` might actually contain and return an object that is expected to be there.
    - If a violation of the before-mentioned assumption would not be directly visible (e.g. with a `NullPointerException`), make sure to assert your assumption.
    - When providing getters for returning values that may be `null`, consider using `Optional<T>`. 
      While it is difficult to come up with a general guideline on when to return `null` or `Optional<T>`, as a rule of thumb, try to avoid overhead in private and internal methods by using the [`@Nullable`](https://static.javadoc.io/com.google.code.findbugs/jsr305/3.0.1/javax/annotation/Nullable.html) annotation and returning `null` directly. 
      On public APIs, where consumers may be less aware of the `null` value that can be returned, prefer the use of `Optional<T>`.
    - Do not use `Optional<T>` for arguments or member variables, use the [`@Nullable`](https://static.javadoc.io/com.google.code.findbugs/jsr305/3.0.1/javax/annotation/Nullable.html) annotation instead.
- If possible, try to apply the `final` keyword for variables, class members, and method parameters. In contrast, be more careful when making classes and methods `final` as this can make mocking more cumbersome.
- Avoid comments as far as possible, as they indicate a [code smell](https://sourcemaking.com/refactoring/smells/comments). Rather use proper naming of variables, methods and classes to express semantics and concepts.
- Keep method implementations short. Rather break a method up into multiple methods to keep them short and concise, because long method bodies indicate a [code smell](https://refactoring.guru/smells/long-method).
