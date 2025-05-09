# spring-observability

Modern observability equips developers with tools and frameworks to capture 
critical telemetry data, including traces, logs, and metrics. This repository
explores various observability configurations for Spring applications, helping 
you select the optimal setup based on your specific needs. To understand the 
observability landscape fully, we evaluate each configuration across three key 
dimensions:

- **App Instrumentation** → This dimension specifies the type of observability
  instrumentation embedded within the application.
  - **none**: The application has no explicit instrumentation and relies on 
    external agents or instrumentation within the libraries it uses. For 
    instance, the app itself may lack instrumentation, but the Spring framework
    does, allowing for observability through Spring’s built-in instrumentation.
  - **observation-api**: The application is instrumented using the Micrometer 
    Observation API.
  - **otel-sdk**: The application is instrumented with the OpenTelemetry SDK 
    for Java.

- **Micrometer Implementation** → This dimension describes the underlying 
  implementation that the Micrometer framework uses to collect and export 
  observability data.
  - **native**: Micrometer operates with its native implementation, directly 
    integrating with compatible tools like Prometheus or Zipkin.
  - **otel-sdk**: Micrometer operates on top of the OpenTelemetry SDK for Java.

- **Collector** → This dimension indicates how observability data is exported 
  to the observability platform.
  - **native**: Data is sent to the observability tool using the tool’s native 
    protocol, or the tool scrapes data from an endpoint within the app. For 
    example, pushing spans to Zipkin or exposing a Prometheus-compatible 
    metrics endpoint.
  - **otel-collector**: Data is sent to the OpenTelemetry Collector 
    using the OpenTelemetry Protocol (OTLP). The OpenTelemetry Collector can 
    also be configured with receivers compatible with other monitoring tools,
    such as Zipkin or Prometheus.

## Scenarios

The following table outlines possible configurations and highlights recommended 
setups based on compatibility across each dimension:

| Scenario | App <br> Instrumentation | Micrometer <br> Implementation | Collector       | Notes / Issues                                               |
|----------|--------------------------|--------------------------------|-----------------|--------------------------------------------------------------|
| 1        | none                     | native                         | native          |                                                              |
| 2        | none                     | native                         | otel-collector  |                                                              |
| 3        | none                     | otel-sdk                       | otel-collector  |                                                              |
| 4        | observation-api          | native                         | native          |                                                              |
| 5        | observation-api          | native                         | otel-collector  |                                                              |
| 6        | observation-api          | otel-sdk                       | otel-collector  |                                                              |
| 7        | otel-sdk                 | otel-sdk                       | otel-collector  |                                                              |
| 8        | otel-sdk                 | native                         | otel-collector  | how to get same trace id <br> between micrometer / otel-sdk? |

Which of the above scenarios should adopt? It depends on what you value the most. 

If you are all-in on OpenTelemetry and are trying to maximize the usage of 
OpenTelemetry in all layers of the stack then scenario 3 or 7 above can 
meet your needs you get to use a maximum amount of OpenTelemetry and the 
instrumentation built into the Spring projects using micrometer observation 
api natively shows up in your OpenTelemetry enabled observability platform. 

If you are all in on the best Spring developer experience and maximum 
OpenTelemetry, then scenario 6 is for you. Use the 
observation-api to instrument your own application code, and micrometer 
configured to use the otel-sdk, you get all the benefits of OpenTelemetry 
with a better developer experience for spring developers.

If you are looking to minimize the number of layers/intermediaries between your 
application and the observability platform of your choice then scenario 4
is best for you. 

If you are looking for maximum compatibility with observability platforms then
it is best if you use the micrometer observation api. this let you  decide
how to configure micrometer at deployment time. Scenarios 4,5,6 are good
choices. 

## Repository Structure

This repository is organized into several key directories:

### Config Directory

The `config` directory contains sample applications demonstrating different ways to configure Spring Boot for observability. These examples cover various approaches to sending logs, metrics, and traces to different backends, both directly and through the OpenTelemetry collector.

See the [config README](config/README.md) for a detailed list of the sample applications and their specific configurations.

### Instrumentation Directory

The `instrumentation` directory contains sample applications demonstrating different approaches to instrumenting Spring Boot applications. These examples showcase various instrumentation techniques using different APIs and SDKs.

See the [instrumentation README](instrumentation/README.md) for a detailed list of the sample applications and their specific instrumentation approaches.

### Docker Directory

The `docker` directory contains configurations for running various observability backends and supporting services needed by the sample applications.

## Software Prerequisites

### Java tooling

* [Java 21 JDK](https://sdkman.io/)
* [Maven](https://maven.apache.org/index.html)
* Favourite Java IDE one of
    * [Eclipse Spring Tool Suite](https://spring.io/tools)
    * [IntelliJ](https://www.jetbrains.com/idea/download)
    * [VSCode](https://code.visualstudio.com/)

### network tools
* [cURL](https://curl.se/docs/manpage.html) 
* [HTTPie](https://httpie.io/) 

### Containerization tools
* [Docker](https://www.docker.com/products/docker-desktop)

## Resources

### YouTube 
[Spring Boot Observability Uncovered: Enabling & Using the Observation API Dan Vega](https://www.youtube.com/watch?v=exRkiVLyPpc)
[Micrometer Mastery: Unleash Advanced Observability in your JVM Apps by Tommy Ludwig & Jonatan Ivanov](https://www.youtube.com/watch?v=Qyku6cR6ADY)

