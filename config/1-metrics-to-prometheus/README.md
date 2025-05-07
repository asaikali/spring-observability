# metrics-to-prometheus

This sample application demonstrates how to export metrics from a Spring Boot application to Prometheus using 
the Micrometer metrics facade and the Prometheus registry.

## Exporting Metrics to Prometheus from Spring Boot

In most production environments, metrics are collected by the platform hosting the application. For
example, when a Spring Boot app runs on Kubernetes, metrics can be automatically scraped by Prometheus
through service discovery. However, the application still needs to expose these metrics in a format
that Prometheus can understand.

Spring Boot makes this easy through its integration with Micrometer, a vendor-neutral application metrics
facade that supports numerous monitoring systems, including Prometheus. This sample shows how to configure
a Spring Boot application to expose metrics to Prometheus.

To integrate with Prometheus from Spring Boot, we use:
1. Spring Boot Actuator - exposes application metrics and health information
2. Micrometer - provides a simple facade for instrumenting applications
3. Micrometer Prometheus Registry - formats metrics in Prometheus-compatible format

---
## How Spring Boot Integrates with Micrometer and Prometheus

Spring Boot provides built-in support for Micrometer, which serves as an abstraction layer for various
monitoring systems. When you add the appropriate dependencies, Spring Boot automatically configures
Micrometer to collect metrics from your application.

### Key Components:

1. **Spring Boot Actuator**: Provides production-ready features including metrics endpoints
2. **Micrometer Core**: The metrics instrumentation library that powers the delivery of application metrics
3. **Micrometer Prometheus Registry**: Formats metrics for Prometheus consumption

### Auto-configured Metrics:

Spring Boot automatically configures a variety of metrics, including:

- JVM metrics (memory, garbage collection, threads, etc.)
- System metrics (CPU usage, file descriptors, etc.)
- Spring MVC metrics (request counts, timings, etc.)
- Tomcat metrics (if using the embedded server)
- DataSource metrics (connection pool usage, etc.)

### Custom Metrics:

You can also define custom metrics using Micrometer's API:

```java
// Counter example
Counter counter = registry.counter("my.counter", "tag1", "value1");
counter.increment();

// Timer example
Timer timer = registry.timer("my.timer", "tag1", "value1");
timer.record(() -> {
    // code to time
});

// Gauge example
Gauge.builder("my.gauge", myObject, MyObject::getValue)
    .tag("tag1", "value1")
    .register(registry);
```

---
## Prometheus Metrics Endpoint

With the Micrometer Prometheus registry configured, Spring Boot exposes a `/actuator/prometheus` endpoint
that outputs metrics in Prometheus format. Prometheus can then scrape this endpoint to collect metrics.

The endpoint returns metrics in the Prometheus text-based format:

```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="PS Eden Space",} 2.5165016E7
```

---
## Where Does Prometheus Fit in the Metrics Landscape?

There are several approaches to collecting and analyzing metrics:

| Approach | Typical Stack | Strengths | Trade-offs |
|----------|---------------|-----------|------------|
| **Pull-based time-series databases** | Prometheus + Grafana | ⚡ Highly efficient, simple to operate, great for alerting | 🔍 Limited to numerical time-series data, requires scraping |
| **Push-based aggregation services** | Datadog, New Relic, Dynatrace | 🔎 Rich UI, APM features, managed service | 💰 Higher cost, vendor lock-in |

**Where Prometheus fits**:

* Prometheus uses a pull model where it scrapes metrics from HTTP endpoints.
  * ✅ Simple, reliable, and efficient for collecting numerical time-series data
  * ✅ Works well with dynamic environments through service discovery
  * ❌ Not designed for event logging or distributed tracing
* Works natively with Grafana for visualization and PromQL for querying.
* Ideal for operational monitoring and alerting in cloud-native environments.

In practice many teams:

* Use Prometheus for real-time operational metrics and alerting.
* Integrate with long-term storage solutions like Thanos or Cortex for historical data.
* Combine with Loki (logs) and Tempo (traces) for complete observability.

> **Rule of thumb:**  
> *Need real-time operational metrics and alerting?* → **Prometheus**.  
> *Need comprehensive APM with minimal setup?* → **Datadog / New Relic**.  
> *Need vendor-neutral, unified observability?* → **OpenTelemetry**.

---
## Native "On-the-Wire" Format Prometheus Expects

Prometheus scrapes metrics in a simple text-based format:

```
# HELP http_server_requests_seconds Duration of HTTP server request handling
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{method="GET",outcome="SUCCESS",status="200",uri="/actuator/prometheus",} 10.0
http_server_requests_seconds_sum{method="GET",outcome="SUCCESS",status="200",uri="/actuator/prometheus",} 0.237012397
http_server_requests_seconds_count{method="GET",outcome="SUCCESS",status="200",uri="/quotes",} 5.0
http_server_requests_seconds_sum{method="GET",outcome="SUCCESS",status="200",uri="/quotes",} 0.175943679
# HELP process_cpu_usage The "recent cpu usage" for the Java Virtual Machine process
# TYPE process_cpu_usage gauge
process_cpu_usage 0.0
```

The format consists of:

- **Comments** (lines starting with `#`):
  - `# HELP` - describes what the metric represents
  - `# TYPE` - indicates the metric type (counter, gauge, histogram, summary)

- **Metric samples**:
  - `metric_name{label1="value1",label2="value2"} value [timestamp]`
  - Labels provide dimensions for filtering and grouping
  - The value is the actual measurement
  - Timestamp is optional and rarely used in scrape responses

Prometheus supports four core metric types:

1. **Counter** - a cumulative metric that only increases (e.g., request count)
2. **Gauge** - a metric that can go up and down (e.g., memory usage)
3. **Histogram** - samples observations and counts them in configurable buckets (e.g., request duration)
4. **Summary** - similar to histogram, but calculates quantiles over a sliding time window

Spring Boot and Micrometer handle the conversion of Java metrics to this format automatically when you include the Prometheus registry.

---
## Configuring Prometheus to Scrape Your Application

In a typical setup, you would configure Prometheus to scrape your application's `/actuator/prometheus` endpoint. Here's a basic Prometheus configuration:

```yaml
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['your-app-host:8080']
```
