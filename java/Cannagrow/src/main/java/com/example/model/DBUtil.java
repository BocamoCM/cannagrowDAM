package com.example.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.File;

public class DBUtil {
	// Default values - will be used if properties file is not found
	private static String URL = "jdbc:mysql://192.168.50.189:3306/CannaGrowBD?useSSL=false&allowPublicKeyRetrieval=true";
	private static String USER = "root";
	private static String PASS = "rootpassword";

	private static final String CONFIG_FILE = "database.properties";
	private static boolean configLoaded = false;

	static {
		// Try to load custom configuration first
		loadConfig();

		// Then load MySQL driver
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("No se pudo cargar el driver de MySQL: " + e.getMessage());
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Carga configuración desde archivo properties si existe
	 */
	private static void loadConfig() {
		if (configLoaded) return;

		Properties props = new Properties();
		boolean loaded = false;

		// Try current directory
		File currentDirConfig = new File(CONFIG_FILE);
		if (currentDirConfig.exists()) {
			try (FileInputStream fis = new FileInputStream(currentDirConfig)) {
				props.load(fis);
				loaded = true;
				System.out.println("Cargando configuración de base de datos desde: " + currentDirConfig.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Error leyendo " + CONFIG_FILE + ": " + e.getMessage());
			}
		}

		// Try application directory
		if (!loaded) {
			File appDirConfig = new File(System.getProperty("user.dir"), CONFIG_FILE);
			if (appDirConfig.exists()) {
				try (FileInputStream fis = new FileInputStream(appDirConfig)) {
					props.load(fis);
					loaded = true;
					System.out.println("Cargando configuración de base de datos desde: " + appDirConfig.getAbsolutePath());
				} catch (IOException e) {
					System.err.println("Error leyendo " + CONFIG_FILE + ": " + e.getMessage());
				}
			}
		}

		// Apply properties if loaded
		if (loaded) {
			URL = props.getProperty("db.url", URL);
			USER = props.getProperty("db.user", USER);
			PASS = props.getProperty("db.password", PASS);
			configLoaded = true;

			// Log connection details (remove in production)
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
			// Add connection timeout to avoid long hangs
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

			// Provide more detailed error message
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
	 * Prueba la conexión a la base de datos
	 * @return true si la conexión fue exitosa, false en caso contrario
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