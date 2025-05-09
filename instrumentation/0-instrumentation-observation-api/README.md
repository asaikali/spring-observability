# Spring Boot Observation API Demo

This project demonstrates how to instrument a Spring Boot application using Spring's Observation API for comprehensive observability, including metrics, traces, and logs.

## Overview

The Observation API is a powerful abstraction provided by Spring that allows you to instrument your application code for observability. This demo shows how to use the Observation API to collect telemetry data from a simple application that returns random quotes.

## Key Features

- **Metrics Collection**: Uses Micrometer with Prometheus registry to collect and expose metrics
- **Distributed Tracing**: Uses Micrometer Tracing with Brave bridge to create and export traces to Zipkin/Tempo
- **Structured Logging**: Uses Loki4j to send structured logs to Loki
- **Custom Observations**: Demonstrates how to create custom observations with context data
- **Database Observability**: Includes database observability with datasource-micrometer-spring-boot

## How It Works

The application demonstrates the Observation API through a simple REST endpoint that returns random quotes from a database. The key components are:

### 1. Custom Context

`QuoteContext` extends `Observation.Context` to store application-specific data (the Quote) within the observation:

```java
public class QuoteContext extends Context {
  private Quote quote;
  
  // Getters and setters
}
```

### 2. Observation Convention

`QuoteContextConvention` implements `ObservationConvention<QuoteContext>` to define conventions for observations with QuoteContext:

```java
public class QuoteContextConvention implements ObservationConvention<QuoteContext> {
  @Override
  public boolean supportsContext(Context context) {
    return context instanceof QuoteContext;
  }

  @Override
  public String getName() {
    return "adib";
  }
}
```

### 3. Observation Handler

`QuoteContextObservationHandler` implements `ObservationHandler<QuoteContext>` to handle observation events:

```java
@Component
class QuoteContextObservationHandler implements ObservationHandler<QuoteContext> {
  // ...
  
  @Override
  public void onStop(QuoteContext context) {
    var quote = context.getQuote();
    
    // Create metrics
    DistributionSummary.builder("quotes.by.author")
        .tag("author", quote.getAuthor())
        .register(meterRegistry)
        .record(1);
    
    // Log information
    log.info("Random quote generated: {} by {}", quote.getQuote(), quote.getAuthor());
  }
  
  @Override
  public void onError(QuoteContext context) {
    // Handle errors
    Throwable exception = context.getError();
    log.error("Error generating quote", exception);
  }
}
```

### 4. Using Observations in Controllers

The `QuoteController` demonstrates how to use the Observation API to wrap method calls:

```java
@GetMapping("/random-quote")
public Quote randomQuote() {
  var context = new QuoteContext();
  
  Observation observation =
      Observation.createNotStarted(
          null, new QuoteContextConvention(), () -> context, observationRegistry);
  
  return observation.observe(
      () -> {
        Quote result = quoteRepository.findRandomQuote();
        context.setQuote(result);
        observation.highCardinalityKeyValue("quote", result.getQuote());
        return result;
      });
}
```

## Configuration

The application is configured for observability through:

1. **Dependencies** in `pom.xml`:
   - `spring-boot-starter-actuator` - Enables Spring Boot Actuator endpoints
   - `micrometer-registry-prometheus` - Registers Prometheus as the Micrometer backend
   - `micrometer-tracing-bridge-brave` - Provides integration with Brave for tracing
   - `zipkin-reporter-brave` - Sends spans to a Zipkin-compatible backend
   - `loki-logback-appender` - Sends structured logs to Loki

2. **Application Properties** in `application.yml`:
   - Enables tracing with 100% sampling
   - Configures Zipkin endpoint for sending traces
   - Exposes all actuator endpoints

3. **Logging Configuration** in `logback-spring.xml`:
   - Configures Loki4j appender for structured logging
   - Adds application metadata as labels for filtering

## Running the Application

1. Start the required observability backends:
   - Prometheus for metrics
   - Tempo/Zipkin for traces
   - Loki for logs

2. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

3. Access the random quote endpoint:
   ```
   curl http://localhost:8080/random-quote
   ```

4. View observability data:
   - Metrics: http://localhost:8080/actuator/prometheus
   - Traces: In your Tempo/Zipkin UI
   - Logs: In your Loki/Grafana UI

## How This Fits in the Spring Observability Ecosystem

This project demonstrates Scenario 4 from the main project README, where:
- App Instrumentation: observation-api
- Micrometer Implementation: native
- Collector: native

This approach provides a clean developer experience for Spring developers while maintaining direct integration with observability backends without additional layers.

## Learn More

For more information about Spring Boot observability:
- [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Spring Boot Observability Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.observability)
