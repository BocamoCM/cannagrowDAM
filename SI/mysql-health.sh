#!/bin/bash

# Configuración
CONTAINER_NAME="mysql-db"
DB_USER="root"
DB_PASSWORD="rootpassword"


if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "❌ El contenedor '${CONTAINER_NAME}' no está en ejecución." >&2
    exit 1
fi


echo "🔍 Verificando el estado de MySQL en el contenedor '${CONTAINER_NAME}'..."

if docker exec "$CONTAINER_NAME" mysqladmin ping -u"$DB_USER" -p"$DB_PASSWORD" --silent | grep -q "mysqld is alive"; then
    echo "✅ MySQL está activo y respondiendo dentro del contenedor '${CONTAINER_NAME}'."
else
    echo "❌ MySQL no está respondiendo o hay un problema de conexión." >&2
    exit 2
fi
