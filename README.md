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
  - **Native**: Data is sent to the observability tool using the tool’s native 
    protocol, or the tool scrapes data from an endpoint within the app. For 
    example, pushing spans to Zipkin or exposing a Prometheus-compatible 
    metrics endpoint.
  - **OpenTelemetry Collector**: Data is sent to the OpenTelemetry Collector 
    using the OpenTelemetry Protocol (OTLP). The OpenTelemetry Collector can 
    also be configured with receivers compatible with other monitoring tools,
    such as Zipkin or Prometheus.

## Scenarios

The following table outlines possible configurations and highlights recommended 
setups based on compatibility across each dimension:

| Scenario | App <br> Instrumentation | Micrometer <br> Implementation | Collector       | Recommended           |
|----------|--------------------------|--------------------------------|-----------------|------------------------|
| 1        | none                     | native                         | native          | yes                    |
| 2        | none                     | native                         | otel-collector  | yes                    |
| 3        | none                     | otel-sdk                       | otel-collector  | yes                    |
| 4        | observation-api          | native                         | native          | yes                    |
| 5        | observation-api          | native                         | otel-collector  | yes                    |
| 6        | observation-api          | otel-sdk                       | otel-collector  | yes                    |
| 7        | otel-sdk                 | otel-sdk                       | otel-collector  | yes                    |
| 8        | otel-sdk                 | native                         | otel-collector  | no, due to conflicts   |

## Software Prerequisites

### Java tooling

* [Java 21 JDK](https://sdkman.io/)
* [Java 21 GraalVM](https://sdkman.io/)
* [Maven](https://maven.apache.org/index.html)
* Favourite Java IDE one of
    * [Eclipse Spring Tool Suite](https://spring.io/tools)
    * [IntelliJ](https://www.jetbrains.com/idea/download)
    * [VSCode](https://code.visualstudio.com/)

### network toolse
* [cURL](https://curl.se/docs/manpage.html) 
* [HTTPie](https://httpie.io/) 

### Containerization tools
* [Docker](https://www.docker.com/products/docker-desktop)
