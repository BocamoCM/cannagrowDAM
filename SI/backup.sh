#!/bin/bash

# Configuración
TIMESTAMP=$(date +%Y-%m-%d_%H-%M-%S)
BACKUP_DIR="./backups"
BACKUP_FILE="mysql_backup_${TIMESTAMP}.sql"
CONTAINER_NAME="mysql-db"
DB_USER="root"
DB_PASSWORD="rootpassword"
DB_NAME="CannaGrowBD"


mkdir -p "$BACKUP_DIR"


echo "📦 Iniciando respaldo de la base de datos '$DB_NAME'..."

if docker exec "$CONTAINER_NAME" mysqldump -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" > "${BACKUP_DIR}/${BACKUP_FILE}"; then
    echo "✅ Backup exitoso: ${BACKUP_DIR}/${BACKUP_FILE}"
else
    echo "❌ Error: no se pudo realizar el backup." >&2
    exit 1
fi
