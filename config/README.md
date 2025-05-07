# Config

There are many ways to configure a Spring Boot application to send logs, metrics, and traces 
to an observability system. The project in this folder systematically explores how to do this by 
taking a simple application that returns a random quote and sending its logs, metrics, and traces 
to the observability system.

## Scenarios

There are 8 applications in this section:

* 0-logs-to-loki - Shows how to send logback logs from Spring Boot to Loki using a Loki logback appender 
* 1-metrics-to-prometheus - Shows how to send metrics to Prometheus (Prometheus does the scraping directly)
* 2-traces-to-tempo - Shows how to send traces and spans to Tempo using the Zipkin compatibility API
* 3-all-to-backend - Shows how to send logs, metrics, and traces to Loki, Prometheus, and Tempo in one app (the integration is between the Boot app and the backend)

Each of the above apps demonstrates how to configure the classpath and the Spring Boot `application.yaml`. The above
apps are then repeated with a different approach where we switch Spring Boot to a pure OpenTelemetry implementation, 
shown in the following set of samples:

* 4-log-to-otel - Shows how logs are sent to the OpenTelemetry collector using OTLP 
* 5-metrics-to-otel - Shows how metrics are sent to the OpenTelemetry collector using OTLP
* 6-traces-to-otel - Shows how traces are sent to the OpenTelemetry collector using OTLP
* 7-all-to-otel - Shows how logs, metrics, and traces are sent to the OpenTelemetry collector using OTLP

## Application Code 

The application code in all the above scenarios is the same; the only differences
are in the classpath and the configuration of the application. The app
exposes a single URL `/random-quote` that returns a JSON object with a 
random quotation.

```text
http :8080/random-quote
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/json
Date: Wed, 07 May 2025 13:20:47 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "author": "Winston Churchill",
    "id": 1,
    "quote": "Never, never, never give up"
}

```

### Quotes Repository 

The quotes come from the database using a Spring Data JPA repository:

```java
public interface QuoteRepository extends JpaRepository<Quote, Integer> {

  @Query(nativeQuery = true, value = "SELECT id,quote,author FROM quotes ORDER BY RANDOM() LIMIT 1")
  Quote findRandomQuote();
}
```

```java
@Entity
@Table(name = "quotes")
public class Quote {
  @Id
  @Column(name = "id")
  private Integer id;

  @Column(name = "quote")
  private String quote;

  @Column(name = "author")
  private String author;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getQuote() {
    return quote;
  }

  public void setQuote(String quote) {
    this.quote = quote;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }
}
```
### Logs Controller 

In the logs version of the app, we have a single log message printed from
the logs controller shown below:

```java
@RestController
public class QuoteController {

  private final Logger log = LoggerFactory.getLogger(QuoteController.class);
  private final QuoteRepository quoteRepository;

  public QuoteController(QuoteRepository quoteRepository) {
    this.quoteRepository = quoteRepository;
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {
    Quote result = quoteRepository.findRandomQuote();
    log.info("Random quote generated: {} by {}", result.getQuote(), result.getAuthor());
    return result;
  }
}
```

### Metrics Controller

In the metrics version, we add a counter for tracking the number of generated quotes:

```java
@RestController
public class QuoteController {

  private final Logger log = LoggerFactory.getLogger(QuoteController.class);
  private final QuoteRepository quoteRepository;
  private final Counter generatedQuotes;

  public QuoteController(QuoteRepository quoteRepository, MeterRegistry meterRegistry) {
    this.quoteRepository = quoteRepository;
    this.generatedQuotes = meterRegistry.counter("quotes.generated");
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {
    Quote result = quoteRepository.findRandomQuote();
    this.generatedQuotes.increment();
    log.info("Random quote generated: {} by {}", result.getQuote(), result.getAuthor());
    return result;
  }
}
```

### Traces Controller

In the traces version, we add a custom tag to the current span and we also configure
tracing on the JDBC source.

```java

@RestController
public class QuoteController {

  private final Logger log = LoggerFactory.getLogger(QuoteController.class);
  private final QuoteRepository quoteRepository;
  private final Counter generatedQuotes;
  private final Tracer tracer;

  public QuoteController(QuoteRepository quoteRepository, MeterRegistry meterRegistry, Tracer tracer) {
    this.quoteRepository = quoteRepository;
    this.generatedQuotes = meterRegistry.counter("quotes.generated");
    this.tracer = tracer;
  }

  @GetMapping("/random-quote")
  public Quote randomQuote() {
    Quote result = quoteRepository.findRandomQuote();
    this.generatedQuotes.increment();
    Span currentSpan = tracer.currentSpan();
    if (currentSpan != null) {
      currentSpan.tag("quote", result.getQuote());
      log.info("Quote added to current span: {}", result.getQuote());
    }
    log.info("Random quote generated: {} by {}", result.getQuote(), result.getAuthor());
    return result;
  }
}
```
### Code Summary
By using the same code across all apps, with progressive additions (first a log message, then a metric, then span attributes), we can focus on exploring the classpath configuration and the `application.yaml` settings.
The goal is to help you understand how to configure Spring Boot for optimal observability and provide working examples from which you can copy and paste configuration settings.
