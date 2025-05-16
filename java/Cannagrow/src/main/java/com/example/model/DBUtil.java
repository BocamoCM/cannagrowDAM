package com.example.model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase DBUtil
 */
public class DBUtil {
	// Valores predeterminados, en caso de no encontrar el archivo properties
	private static String URL = "jdbc:mysql://149.74.26.171:3306/CannaGrowBD?useSSL=false&allowPublicKeyRetrieval=true";
	private static String USER = "root";
	private static String PASS = "rootpassword";

	private static final String CONFIG_FILE = "database.properties";
	private static boolean configLoaded = false;

	static {
		// Cargar la configuración del archivo properties
		loadConfig();

		// Cargar el driver de MySQL
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("No se pudo cargar el driver de MySQL: " + e.getMessage());
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Carga la configuración desde el archivo properties si existe.
	 */
	private static void loadConfig() {
		if (configLoaded) return;

		Properties props = new Properties();
		boolean loaded = false;

		// Intentar cargar desde el classpath
		try (InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			if (inputStream != null) {
				props.load(inputStream);
				loaded = true;
				System.out.println("Cargando configuración de base de datos desde el classpath.");
			}
		} catch (IOException e) {
			System.err.println("Error leyendo " + CONFIG_FILE + ": " + e.getMessage());
		}

		// Si se cargó, aplicar las propiedades
		if (loaded) {
			URL = props.getProperty("db.url", URL);
			USER = props.getProperty("db.user", USER);
			PASS = props.getProperty("db.password", PASS);
			configLoaded = true;

			// Log de los detalles de la conexión (puedes quitar esto en producción)
			System.out.println("Conexión de base de datos configurada:");
			System.out.println("URL: " + URL);
			System.out.println("Usuario: " + USER);
		} else {
			System.out.println("Usando configuración predeterminada de base de datos (no se encontró " + CONFIG_FILE + ")");
		}
	}

	/**
	 * Obtiene una nueva conexión JDBC con manejo de errores mejorado.
	 * @return Connection abierta; el llamador debe cerrarla.
	 * @throws SQLException si ocurre un error al conectar.
	 */
	public static Connection getConexion() throws SQLException {
		try {
			// Añadir tiempo de espera para evitar bloqueos largos
			String urlWithTimeout = URL;
			if (!URL.contains("connectTimeout")) {
				urlWithTimeout += (URL.contains("?") ? "&" : "?") +
						"connectTimeout=5000&socketTimeout=30000";
			}

			return DriverManager.getConnection(urlWithTimeout, USER, PASS);
		} catch (SQLException e) {
			System.err.println("Error conectando a la base de datos:");
			System.err.println("URL: " + URL);
			System.err.println("Error: " + e.getMessage());

			// Mensajes con posibles soluciones
			if (e.getMessage().contains("Communications link failure") ||
					e.getMessage().contains("Connection refused")) {
				System.err.println("\nPOSIBLES SOLUCIONES:");
				System.err.println("1. Verifique que el servidor MySQL esté ejecutándose");
				System.err.println("2. Compruebe que la dirección IP y puerto sean correctos");
				System.err.println("3. Si usa Docker, asegúrese que el contenedor esté activo y mapeado al puerto correcto");
				System.err.println("4. Verifique que no haya un firewall bloqueando la conexión");
			}

			throw e;
		}
	}

	/**
	 * Prueba la conexión a la base de datos.
	 * @return true si la conexión fue exitosa, false en caso contrario.
	 */
	public static boolean testConnection() {
		try (Connection conn = getConexion()) {
			return conn != null && !conn.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Cierra una conexión si no es null.
	 * @param conexion la conexión a cerrar.
	 */
	public static void cerrarConexion(Connection conexion) {
		if (conexion != null) {
			try {
				conexion.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
