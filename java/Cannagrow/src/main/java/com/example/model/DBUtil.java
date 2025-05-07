package com.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	private static final String URL = "jdbc:mysql://192.168.50.189:3306/CannaGrowBD?useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "rootpassword";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError("No se pudo cargar el driver de MySQL: " + e.getMessage());
		}
	}

	/**
	 * Obtiene una nueva conexión JDBC.
	 * @return Connection abierta; el llamador debe cerrarla.
	 * @throws SQLException si ocurre un error al conectar.
	 */
	public static Connection getConexion() throws SQLException {
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
