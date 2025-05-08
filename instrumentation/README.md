# Instrumentation

There are different ways to instrument a Spring Boot application to collect logs, metrics, and
traces for observability. The projects in this folder systematically explore different approaches to
instrumentation by taking a simple application that returns a random quote and instrumenting it
using different libraries and techniques.

## Scenarios

There are 3 applications in this section:

* 0-instrumentation-observation-api - Shows how to instrument a Spring Boot application using
  Spring's Observation API.
* 1-instrumentation-otel-sdk - Shows how to instrument a Spring Boot application using the
  OpenTelemetry SDK
* 2-instrumentation-mixed-apis - Shows how to use a mix of instrumentation approaches in a single
  application 
