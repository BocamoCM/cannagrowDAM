#!/bin/bash


TAIL_LINES=50
COMPOSE_FILE="docker-compose.yml"

if [[ ! -f "$COMPOSE_FILE" ]]; then
    echo "❌ No se encontró el archivo $COMPOSE_FILE en el directorio actual." >&2
    exit 1
fi


echo "📄 Mostrando los últimos $TAIL_LINES registros de los servicios en '$COMPOSE_FILE'..."
docker compose logs --tail="$TAIL_LINES" --no-color
