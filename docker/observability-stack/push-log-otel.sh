#!/usr/bin/env bash
set -euo pipefail

# ───────────────────────────────────────────────────────────────────────────────
# push-log-otel.sh
# Push a single test message to the OpenTelemetry Collector HTTP endpoint,
# with execution tracing enabled only for the curl, and guaranteed success/failure output.
#
# Configuration (override by exporting before running):
#   OTEL_URL       default: http://localhost:4318
#   SERVICE_LABEL  default: push-log-otel
# Prerequisite: curl
# ───────────────────────────────────────────────────────────────────────────────

OTEL_URL=${OTEL_URL:-http://localhost:4318}
SERVICE_LABEL=${SERVICE_LABEL:-push-log-otel}

# build the payload
PAYLOAD=$(
  cat <<EOF
{
  "resourceLogs": [
    {
      "resource": {
        "attributes": [
          { "key": "service.name", "value": { "stringValue": "${SERVICE_LABEL}" } }
        ]
      },
      "scopeLogs": [
        {
          "scope": {},
          "logRecords": [
            {
              "timeUnixNano": "$(( $(date -u +%s) * 1000000000 ))",
              "body": { "stringValue": "[TEST] Test message from ${SERVICE_LABEL} script" },
              "severityText": "INFO"
            }
          ]
        }
      ]
    }
  ]
}
EOF
)

# trace only the curl, but catch its exit in an if-statement
set -x
if curl -sf -X POST "${OTEL_URL}/v1/logs" \
      -H "Content-Type: application/json" \
      -d "${PAYLOAD}"; then
  set +x
  echo "✅ OTel Collector accepted the log (HTTP 2xx/3xx)"
else
  set +x
  echo "❌ OTel Collector push failed" >&2
  exit 1
fi
