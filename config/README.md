# config

There are  many ways to configuring a Spring Boot application to send logs, metrics, and traces 
to an observability system. The project in this folder sytemtiaclyl explore how to do this buy 
taking a simple application that returns a random quote and getting its log then metrics then traces 
to the obseravbiiltity system. 

## Secanrios 

There are 8 applicaitons in this section.

* 0-logs-to-loki - shows how to get logback logs from spring boot to loki using a loki logback appender 
* 1-metrics-to-prometheus - show how to get metrics to promeutheus, prometheus dose the scrapping directly,
* 2-tarces-to-tepmo - shows how to get tarces and spans to tempo using the zipkin compatability 
* 3-all-to-backend - shows how get logs, metrics, and traces to loki, prometheus, tempo in one app, the integartion is between the boot app and teh backend.

Each of the above apps shows how to configure the classapth and the Spring boot `application.yaml`. The above
apps are then repeated where we switch Spring Boot to a pure OpenTelementry approach, shown in a a set 
of samples.

* 4-log-to-otel - show how logs are sent to otel collector using OTLP 
* 5-metrics-to-otel - shows how metrics are sent to otel collector using OTLP
* 6-traces-to-otel - shows how traces are sent to the otel collector using OTLP
* 7-all-to-otel - shows how logs, metrics, and traces are sent to the otel collector using OTLP 

## Application Code 

The applicaiton code in all the above secarios is the same, the only thing
different is the classapth, and the configuration of the application. The app
exposes a single url `/random-quote` that returns a json object with a 
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

The quotes come from the database using Spring Data JPA repository 

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

In the logs version of the app, we have a single log message printed form
the logs controller shown below. 

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

In the metrics version, we add a counter for counting the number of generated quotes

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

In the traces version, we add custom tag to the current span and we also configuring
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
    if( currentSpan != null) {
      currentSpan.tag("quote", result.getQuote());
      log.info("quoted added to current: {}", result.getQuote());
    }
    log.info("Random quote generated: {} by {}", result.getQuote(), result.getAuthor());
    return result;
  }
}
```
### Code Summary
By using the same code across the apps, with us adding log message, then a metric, then span attributes we can focus on exploring what the classapth configuration looks like and what the applicatiion.yaml looks like.
the goal is to allow you to understadnw how to configure spring boot for optimal observability and give you a working example you can cut and paste configuration settings from.
