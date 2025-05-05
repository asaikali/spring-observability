#!/usr/bin/env bash
set -euo pipefail

# ───────────────────────────────────────────────────────────────────────────────
# push-loki.sh
# Push a single test message to Loki, with execution trace on the curl only,
# and a proper if/fi around that curl.
#
# Configuration (override by exporting before running):
#   LOKI_URL       default: http://localhost:3100
#   SERVICE_LABEL  default: push-loki
# ───────────────────────────────────────────────────────────────────────────────

LOKI_URL=${LOKI_URL:-http://localhost:3100}
SERVICE_LABEL=${SERVICE_LABEL:-push-loki}

# build the payload
PAYLOAD=$(
  cat <<EOF
{
  "streams": [
    {
      "labels": "{app=\"${SERVICE_LABEL}\",level=\"INFO\"}",
      "entries": [
        {
          "ts": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
          "line": "[TEST] Test message from ${SERVICE_LABEL} script"
        }
      ]
    }
  ]
}
EOF
)

# Turn on tracing just for the curl call, and wrap it in an if/fi
if curl -vvvv -sf -X POST "${LOKI_URL}/loki/api/v1/push" \
      -H "Content-Type: application/json" \
      -d "${PAYLOAD}"; then
  echo "✅ Loki accepted the log (HTTP 2xx/3xx)"
else
  echo "❌ Loki push failed" >&2
  exit 1
fi
