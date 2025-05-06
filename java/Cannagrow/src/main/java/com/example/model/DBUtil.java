package com.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	private static final String URL = "jdbc:mysql://192.168.50.189:3306/exampledb";
	private static final String USER = "user";
	private static final String PASS = "userpassword";

	static {
		try {
			// Registra el driver MySQL una sola vez
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// Falla la inicialización si no se encuentra el driver
			throw new ExceptionInInitializerError("No se pudo cargar el driver de MySQL: " + e.getMessage());
		}
	}

	/**
	 * Obtiene una nueva conexión JDBC.
	 * @return Connection abierta; el llamador debe cerrarla.
	 * @throws SQLException si ocurre un error al conectar.
	 */
	public Connection getConexion() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
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
