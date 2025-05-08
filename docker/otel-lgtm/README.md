# OpenTelemetry LGTM (Loki, Grafana, Tempo, Mimir)

This directory contains configuration for running the Grafana OTEL-LGTM Docker image, which provides a complete OpenTelemetry backend in a single container.

## What is OTEL-LGTM?

OTEL-LGTM is a Docker image that bundles:
- **L**oki (for logs)
- **G**rafana (for visualization)
- **T**empo (for traces)
- **M**imir (for metrics, based on Prometheus)

It provides a simple way to get started with OpenTelemetry by packaging all the necessary backend components in a single container.

## Usage

You can start the OTEL-LGTM stack using either:

1. The provided shell script:
   ```
   ./otel-lgtm
   ```

2. Docker Compose:
   ```
   docker-compose up
   ```

## Exposed Ports

- 3000: Grafana UI
- 4317: OTLP gRPC (Collector)
- 4318: OTLP HTTP (Collector)
- 9090: Prometheus (metrics scraping UI + API)
- 3100: Loki (log ingestion & query)
- 3200: Tempo (trace HTTP ingest & query)

## References

- [Introducing Grafana OTEL-LGTM: An OpenTelemetry backend in a Docker image](https://grafana.com/blog/2024/03/13/an-opentelemetry-backend-in-a-docker-image-introducing-grafana/otel-lgtm/)
- [GitHub Repository](https://github.com/grafana/docker-otel-lgtm)
