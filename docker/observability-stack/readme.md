# observability-stack

This project includes a Docker Compose configuration to launch a local 
observability stack with **Grafana**, **Tempo**, **Loki**, and **Prometheus**. 
This setup is optimized for local development, providing an easy way to monitor,
trace, and log your applications using industry-standard tools. It’s ideal for
testing observability configurations on a laptop without needing a complex 
cloud setup.

## Components

- **Grafana**: Provides a dashboard for visualizing metrics, logs, and traces. 
  It integrates with Prometheus, Loki, and Tempo to offer a comprehensive 
  observability experience.
- **Prometheus**: Collects and stores application metrics, making them available
  for monitoring and alerting. Grafana can be used to visualize these metrics.
- **Loki**: Handles log aggregation, allowing you to query and analyze 
  application logs within Grafana.
- **Tempo**: Provides distributed tracing, enabling you to follow the flow of 
  requests across services. Grafana can visualize these traces to help identify 
  performance issues.

## Usage

1. **Start the Stack**: Run `docker compose up -d` or `./up.sh`
2. **Stop the Stack**: Run `docker compose down` or `./down.sh`
3. **Cleanup Volumes**: Run `docker compose down --volumes` or `./cleanup.sh`
3. **Check Status**: Run `docker compose ps` or `./status.sh`
4. **View Logs**: Run `docker compose logs -f <service-name>` to view logs for
   a specific service. For example, `docker compose logs -f tempo`.

