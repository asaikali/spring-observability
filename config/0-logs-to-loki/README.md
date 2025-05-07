# logs-to-loki

This sample application demonstrates how to export logs from a Spring Boot application to Loki using 
the native Loki ingestion endpoint. 

## Exporting Logs to Loki from Spring Boot

In most production environments, logs are collected by the platform hosting the application. For
example, when a Spring Boot app runs on Kubernetes or Cloud Foundry, its `stdout` and
`stderr` streams are automatically captured by the platform's logging infrastructure and forwarded
to a centralized log aggregation system. In these cases, developers typically don't need to modify
their logging configuration — they just deploy the application as usual.

However, when running outside a managed platform, you may need to configure Logback
explicitly to push log events to a backend like Loki. This sample shows how to do exactly that.

To integrate directly with Loki from Logback, we use the [Loki4j Logback Appender
](https://github.com/loki4j/loki-logback-appender), a lightweight client that sends logs to Loki
via its HTTP push API. It supports structured logging, batching, and custom label configuration,
making it ideal for direct application-level log shipping.

---
## `logback.xml` vs `logback-spring.xml` in Spring Boot

In a Spring Boot application, you typically configure logging using Logback, a powerful and
flexible logging framework. Logback can be configured with XML files — and Spring Boot recognizes
two flavors:

### `logback.xml` — the standard Logback configuration

This is the file that Logback itself natively understands. It’s pure Logback — no Spring
features, no environment awareness. You define appenders, loggers, and patterns, and Logback
initializes based solely on that file.

If you name your file `logback.xml`, Spring Boot will pass it directly to Logback without
modifying it. Logback parses it using its own rules. That means:

- ❌ You **cannot** use Spring properties like `${server.port}`.
  - ❌ You **cannot** conditionally load logging config based on Spring profiles.
  - ✅ You **can** use everything Logback natively supports.

This is fine for static, self-contained logging configurations — especially outside of Spring Boot.

###  `logback-spring.xml` — the Spring Boot–aware configuration

This is a Spring Boot enhancement. Instead of giving your config directly to Logback, you name
it `logback-spring.xml`, and Spring Boot parses it itself. This gives you a bunch of extra
capabilities:

- ✅ You can use Spring property placeholders, like `${server.port}` or `${custom.config}`.
  - ✅ You can use the `<springProfile>` element to define profile-specific logging rules, like
    showing `DEBUG` logs in `dev` but only `WARN` in `prod`.
  - ✅ You can modularize logging logic — Spring Boot knows how to merge and inject values
    intelligently before handing it off to Logback.

Spring Boot uses its internal `LoggingApplicationListener` to read and transform this file into
runtime Logback configuration, using Logback’s API — not by writing a file, but by calling
methods in code.

### So which should you use?

- If you're in a Spring Boot application and want to use Spring-specific features like profiles
  or `${...}` placeholders — use `logback-spring.xml`.
  - If you're building a non-Spring application, or want maximum compatibility with plain
    Logback — use `logback.xml`.

---

### 💡 Summary

> Use `logback.xml` when you need plain Logback configuration with no Spring-specific features.  
> Use `logback-spring.xml` if you're using Spring Boot and want access to Spring environment
> variables and profile-specific logging config.


| Feature                              | `logback.xml` | `logback-spring.xml` |
|--------------------------------------|---------------|-----------------------|
| Native Logback XML syntax            | ✅             | ✅                    |
| Spring property resolution (`${...}`)| ❌             | ✅                    |
| Spring profiles (`<springProfile>`)  | ❌             | ✅                    |
| External config ordering support     | ❌             | ✅                    |
| Used directly by Logback             | ✅             | ❌ (parsed by Spring Boot) |

---
## How Loki Handles Structured Logs

### At Write Time (Ingestion)

- Only the defined `labels` (e.g., `app`, `env`, `level`) are indexed
  - The log line body — even if JSON — is treated as an opaque string
  - No parsing or field extraction is done on the log line at this stage

> ✅ Fast ingestion, small index  
> ❌ No field-level search unless fields are promoted to labels

---

### At Query Time

- Loki first uses labels to filter matching log streams (very fast)
  - Then it scans the log lines within those streams
  - You can use `| json` to:
    - Parse each log line as JSON
    - Extract fields dynamically
    - Filter based on field values

#### Example

~~~logql
{app="my-app", level="error"} | json | trace_id="abc123"
~~~

- ✅ `app` and `level` are used to filter streams
  - ✅ `| json` parses the log body into structured fields
  - ✅ `trace_id="abc123"` matches logs with that field

### ✅ Summary Table

| Phase   | What Happens                                         |
|---------|------------------------------------------------------|
| **Write** | Labels indexed; log line stored as-is               |
| **Query** | Labels filter streams; `| json` parses body at query time` |

---

## Where Does Loki Fit in the Log‑Aggregation Landscape?

There are three broad ways teams collect and store logs:

| Approach | Typical Stack | Strengths | Trade‑offs |
|----------|---------------|-----------|------------|
| **Full‑text search engines** | Elasticsearch / OpenSearch + Kibana (the classic “ELK” stack), Splunk, Datadog Logs | 🔎 Field‑level queries, analytics, dashboards, alerting | 💰 High cost at scale, heavy RAM/CPU, complex ops |
| **Cheap object‑storage pipelines** | Fluent Bit → S3/GCS, Parquet files, Athena/BigQuery for ad‑hoc queries | 💸 Ultra‑low storage cost, simple, compliant | 🐢 Cold queries, no real‑time search, DIY tooling |
| **Label‑based, time‑series log stores** | **Grafana Loki** (+ Grafana) | ⚡ Fast on labels, lightweight index, integrates with Prometheus/Tempo | 🔍 No full‑text index → body parsed at query time (`| json`, regex) |

**Where Loki fits**

* Loki indexes only labels (e.g., `app`, `env`, `level`), not the full log body.
  * ✅ Extremely cheap and fast for DevOps filtering (`{app="orders", level="error"}`)
  * ❌ Deep field searches require on‑the‑fly parsing.
* Works natively with Grafana dashboards and the LogQL query language.
* Ideal when you value cost‑efficiency and tight Prometheus‑style integration over heavy‑duty log analytics.

In practice many teams:

* Use Loki for day‑to‑day service debugging and SRE dashboards.
* Reserve Elasticsearch/Splunk for compliance, audit, or BI use‑cases that demand full‑text indexing.
* Or off‑load cold logs to object storage for long‑term retention.

> **Rule of thumb:**  
> *Need lightning‑fast, low‑cost operational logs?* → **Loki**.  
> *Need Google‑like search on every JSON field?* → **ELK / Splunk**.  
> *Need to keep logs for years as cheap as possible?* → **S3 + Athena**.

---
##  Native “On‑the‑Wire” Format Loki Expects

Loki’s `/loki/api/v1/push` endpoint accepts a single **JSON document** shaped like this:

```json
{
  "streams": [
    {
      "stream": {
        "app": "logs-to-loki",
        "level": "INFO",
        "env": "dev"
      },
      "values": [
        [
          "1715000000123456789",
          "2025-05-06T12:00:00.123-04:00  INFO  Started application"
        ],
        [
          "1715000001123456789",
          "2025-05-06T12:00:01.123-04:00  INFO  GET /quotes -> 200"
        ]
      ]
    }
  ]
}
```
- **`stream`** – a flat map of **labels** (key = string, value = string).  
  Loki indexes these so queries like `{app="logs-to-loki", level="INFO"}` are lightning‑fast.

- **`values`** – an array of 2‑element arrays:  
  `["<timestamp‑ns>", "<log line>"]`
  - **Timestamp** must be a string representing **nanoseconds since the Unix epoch**  
    (Loki’s preferred precision).
  - **Log line** is stored as an *opaque* string – Loki doesn’t parse it at ingest.

Each distinct label set (`app`, `env`, `level`, …) forms its own **stream**.  
Clients such as *Loki4j*, *Promtail*, *Fluent Bit*, or the *OpenTelemetry Collector* batch log lines into this structure before sending them over HTTP.


