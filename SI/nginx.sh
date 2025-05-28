#!/bin/bash

# Configuración
URL="http://localhost"
EXPECTED_STATUS="200"

echo "🌐 Verificando disponibilidad del servicio web en $URL..."

STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$URL")

if [[ "$STATUS_CODE" == "$EXPECTED_STATUS" ]]; then
    echo "✅ Nginx responde correctamente (HTTP $STATUS_CODE)"
else
    echo "❌ Nginx no responde o hay un error (HTTP $STATUS_CODE)" >&2
    exit 1
fi
