import os
import discord
from discord import app_commands
from discord.ext import commands, tasks
import mysql.connector
from mysql.connector import Error
from datetime import datetime, timedelta
import logging
import traceback
from dotenv import load_dotenv
import subprocess
# Cargar variables de entorno
load_dotenv()

# Configurar logging mejorado
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("bot.log", encoding='utf-8'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger("discord_bot")
logger.setLevel(logging.INFO)

# Configuración del bot y la base de datos
DISCORD_TOKEN = os.getenv("DISCORD_TOKEN")
if not DISCORD_TOKEN:
    logger.error("Token de Discord no encontrado. Asegúrate de configurar la variable DISCORD_TOKEN en el archivo .env")
    exit(1)

# Configuración de canales y configuraciones
VOICE_CHANNEL_ID = os.getenv("VOICE_CHANNEL_ID")
try:
    VOICE_CHANNEL_ID = int(VOICE_CHANNEL_ID) if VOICE_CHANNEL_ID else 0
except ValueError:
    logger.error(f"ID de canal inválido: {VOICE_CHANNEL_ID}")
    VOICE_CHANNEL_ID = 0

DB_CONFIG = {
    'host': os.getenv('DB_HOST', 'localhost'),
    'port': int(os.getenv('DB_PORT', '3306')),
    'user': os.getenv('DB_USER', 'root'),
    'password': os.getenv('DB_PASSWORD', 'rootpassword'),
    'database': os.getenv('DB_NAME', 'CannaGrowBD'),
    'charset': 'utf8mb4',
    'connection_timeout': 10,
    'pool_name': 'mypool',
    'pool_size': 5
}

class DatabaseConnection:
    """Gestión de conexiones de base de datos con mejor manejo de errores"""
    def __init__(self):
        self.connection_pool = None
        self._create_connection_pool()

    def _create_connection_pool(self):
        """Crear un pool de conexiones"""
        try:
            self.connection_pool = mysql.connector.pooling.MySQLConnectionPool(**DB_CONFIG)
            logger.info("Pool de conexiones a la base de datos creado exitosamente")
        except Error as e:
            logger.error(f"Error al crear pool de conexiones: {e}")
            raise

    def get_connection(self):
        """Obtener una conexión del pool"""
        try:
            connection = self.connection_pool.get_connection()
            return connection
        except Error as e:
            logger.error(f"Error al obtener conexión: {e}")
            raise

    def execute_query(self, query, params=None):
        """Ejecutar consulta con manejo de errores y pool de conexiones"""
        connection = None
        try:
            connection = self.get_connection()
            cursor = connection.cursor(dictionary=True)

            cursor.execute(query, params) if params else cursor.execute(query)

            # Manejar diferentes tipos de consultas
            if query.strip().upper().startswith(('SELECT', 'SHOW')):
                result = cursor.fetchall()
                cursor.close()
                return result
            else:
                connection.commit()
                affected_rows = cursor.rowcount
                cursor.close()
                return affected_rows
        except Error as e:
            logger.error(f"Error al ejecutar consulta: {e}")
            logger.error(f"Query: {query}")
            logger.error(f"Params: {params}")
            return None
        finally:
            if connection and connection.is_connected():
                connection.close()

# Configuración de intents
intents = discord.Intents.default()
intents.message_content = True
intents.members = True  # Importante para acceder a información de miembros

# Instancias del bot
bot = commands.Bot(command_prefix="!", intents=intents)
db = DatabaseConnection()

# Tarea periódica para actualizar canal de voz con múltiples mejoras
@tasks.loop(seconds=60)
async def actualizar_usuarios_activos():
    """Actualizar canal de voz con usuarios activos"""
    if not bot.is_ready():
        return

    try:
        # Consulta de usuarios activos para Cliente y Empleado
        query = """
            SELECT
                tipo_usuario,
                COUNT(DISTINCT usuario_id) AS activos
            FROM SesionActiva
            WHERE
                (activa = 1 OR activa IS TRUE)
                AND (
                    fin_sesion IS NULL
                    OR fin_sesion > DATE_SUB(NOW(), INTERVAL 15 MINUTE)
                )
            GROUP BY tipo_usuario
        """
        resultado = db.execute_query(query)

        # Inicializar total de activos a 0 si no hay resultados
        total_activos = 0
        if resultado:
            total_activos = sum(row['activos'] for row in resultado)

        # Desglose por tipo de usuario (para logging)
        desglose_activos = {row['tipo_usuario']: row['activos'] for row in resultado} if resultado else {}
        logger.info(f"Usuarios activos: {desglose_activos}")

        # Obtener canal con manejo de errores
        try:
            canal = bot.get_channel(VOICE_CHANNEL_ID)
            if not canal:
                logger.error(f"Canal no encontrado. Verifica el ID: {VOICE_CHANNEL_ID}")
                return

            # Intentar cambiar nombre del canal
            nuevo_nombre = f"🟢 Activos: {total_activos}"
            await canal.edit(name=nuevo_nombre)
            logger.info(f"Canal actualizado exitosamente: {nuevo_nombre}")

        except discord.Forbidden:
            logger.error("No tengo permiso para editar el canal")
        except discord.HTTPException as e:
            logger.error(f"Error al editar canal: {e}")

    except Exception as e:
        logger.error(f"Error crítico en actualizar_usuarios_activos: {e}")
        logger.error(traceback.format_exc())

# Comando para registrar una sesión activa (opcional)
@bot.tree.command(name="activar", description="Registra tu sesión como activa")
async def activar_sesion(interaction: discord.Interaction):
    """Comando para marcar una sesión como activa"""
    usuario_id = str(interaction.user.id)
    usuario_nombre = interaction.user.name

    try:
        # Verificar si el usuario es un cliente o empleado
        query_usuario = """
            SELECT 'Cliente' as tipo_usuario
            FROM Cliente
            WHERE discordid = %s
            UNION
            SELECT 'Empleado' as tipo_usuario
            FROM Empleado
            WHERE discordid = %s
        """
        usuario_result = db.execute_query(query_usuario, (usuario_id, usuario_id))

        if not usuario_result:
            await interaction.response.send_message("No estás registrado. Contacta al administrador.", ephemeral=True)
            return

        # Usar el primer tipo de usuario encontrado
        tipo_usuario = usuario_result[0]['tipo_usuario']

        # Insertar o actualizar sesión activa
        query_sesion = """
            INSERT INTO SesionActiva
            (usuario_id, tipo_usuario, nombre_usuario, activa, inicio_sesion)
            VALUES (%s, %s, %s, 1, NOW())
            ON DUPLICATE KEY UPDATE
            activa = 1,
            inicio_sesion = COALESCE(inicio_sesion, NOW())
        """
        db.execute_query(query_sesion, (usuario_id, tipo_usuario, usuario_nombre))

        await interaction.response.send_message(f"Tu sesión ha sido marcada como activa ({tipo_usuario}).", ephemeral=True)

    except Exception as e:
        logger.error(f"Error en comando /activar: {e}")
        logger.error(traceback.format_exc())
        await interaction.response.send_message("Hubo un error al activar tu sesión.", ephemeral=True)

@bot.event
async def on_ready():
    """Evento cuando el bot está listo"""
    try:
        # Sincronizar comandos
        synced = await bot.tree.sync()
        logger.info(f"Bot {bot.user} está conectado y listo")
        logger.info(f"Sincronizados {len(synced)} comandos")

        # Iniciar tarea de actualización solo si no está corriendo
        if not actualizar_usuarios_activos.is_running():
            actualizar_usuarios_activos.start()

    except Exception as e:
        logger.error(f"Error al sincronizar comandos: {e}")
        logger.error(traceback.format_exc())

@bot.tree.command(name="pedidos", description="Muestra tus pedidos registrados")
async def pedidos(interaction: discord.Interaction):
    """Comando para mostrar pedidos de un usuario"""
    discord_id = str(interaction.user.id)
    usuario_nombre = interaction.user.name

    await interaction.response.defer(ephemeral=True)

    try:
        # Obtener ID de cliente
        query_cliente = "SELECT id FROM Cliente WHERE discordid = %s"
        cliente_result = db.execute_query(query_cliente, (discord_id,))

        if not cliente_result:
            await interaction.followup.send("No estás registrado como cliente. Contacta al administrador.")
            return

        cliente_id = cliente_result[0]['id']

        # Consulta de pedidos
        query_pedidos = """
            SELECT id, fecha, estado, total
            FROM Pedido
            WHERE cliente_id = %s
            ORDER BY fecha DESC
            LIMIT 10  # Limitar a los 10 pedidos más recientes
        """
        pedidos = db.execute_query(query_pedidos, (cliente_id,))

        if not pedidos:
            await interaction.followup.send("No tienes pedidos registrados.")
            return

        # Crear embed mejorado
        embed = discord.Embed(
            title="Tus Pedidos",
            description=f"Se encontraron {len(pedidos)} pedidos recientes.",
            color=discord.Color.green(),
            timestamp=datetime.now()
        )

        # Emojis de estado con descripciones más claras
        estado_emojis = {
            'Pendiente': '🟠 En preparación',
            'Enviado': '🔵 En camino',
            'Entregado': '🟢 Completado',
            'Cancelado': '🔴 Cancelado'
        }

        for pedido in pedidos:
            estado = pedido['estado']
            emoji_estado = estado_emojis.get(estado, '⚪ Estado desconocido')
            fecha = pedido['fecha'].strftime('%d/%m/%Y') if pedido['fecha'] else 'Fecha no disponible'

            embed.add_field(
                name=f"Pedido #{pedido['id']}",
                value=(
                    f"**Estado:** {emoji_estado}\n"
                    f"**Fecha:** {fecha}\n"
                    f"**Total:** €{pedido['total']:.2f}"
                ),
                inline=False
            )

        embed.set_footer(text=f"Usuario: {usuario_nombre}")
        await interaction.followup.send(embed=embed)

    except Exception as e:
        logger.error(f"Error en comando /pedidos: {e}")
        logger.error(traceback.format_exc())
        await interaction.followup.send("Hubo un error al obtener tus pedidos. Por favor, contacta con soporte.")

def main():
    """Función principal para iniciar el bot"""
    try:
        logger.info("Iniciando el bot...")
        bot.run(DISCORD_TOKEN)
    except Exception as e:
        logger.error(f"Error crítico al iniciar el bot: {e}")
        logger.error(traceback.format_exc())
async def ejecutar_script_base(interaction: discord.Interaction, nombre_script: str):
    script_path = os.path.join(os.getcwd(), nombre_script)

    if not os.path.isfile(script_path):
        await interaction.response.send_message("❌ El script no existe en el sistema.", ephemeral=True)
        return

    try:
        await interaction.response.defer(ephemeral=True)
        result = subprocess.run(["bash", script_path], capture_output=True, text=True, timeout=30)

        output = result.stdout.strip() or "✅ Script ejecutado sin salida."
        error = result.stderr.strip()

        response = f"**Salida del script `{nombre_script}`:**\n```bash\n{output}\n```"
        if error:
            response += f"\n⚠️ **Error:**\n```bash\n{error}\n```"

        await interaction.followup.send(response)
    except subprocess.TimeoutExpired:
        await interaction.followup.send("⏱️ Tiempo de espera excedido al ejecutar el script.")
    except Exception as e:
        logger.error(f"Error al ejecutar script {nombre_script}: {e}")
        await interaction.followup.send("❌ Error interno al ejecutar el script.")
@bot.tree.command(name="restart", description="Reinicia el sistema con restart.sh")
async def restart(interaction: discord.Interaction):
    await ejecutar_script_base(interaction, "restart.sh")

@bot.tree.command(name="status", description="Muestra el estado con status.sh")
async def status(interaction: discord.Interaction):
    await ejecutar_script_base(interaction, "status.sh")

@bot.tree.command(name="backup", description="Realiza un respaldo con backup.sh")
async def backup(interaction: discord.Interaction):
    await ejecutar_script_base(interaction, "backup.sh")

@bot.tree.command(name="mysql_health", description="Chequea MySQL con mysql-health.sh")
async def mysql_health(interaction: discord.Interaction):
    await ejecutar_script_base(interaction, "mysql-health.sh")

@bot.tree.command(name="nginx_test", description="Prueba configuración de Nginx")
async def nginx_test(interaction: discord.Interaction):
    await ejecutar_script_base(interaction, "nginx-test.sh")

@bot.tree.command(name="logs", description="Muestra logs recientes con logs.sh")
async def logs(interaction: discord.Interaction):
    await ejecutar_script_base(interaction, "logs.sh")

if __name__ == "__main__":
    main()
