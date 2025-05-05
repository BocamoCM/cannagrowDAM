package com.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	
	Connection conexion = null;
	
	public Connection getConexion() {
		
		String cadenaConexion = "jdbc:mysql://localhost:3306/cannagrowBDA";
		String usuario = "root";
		String password = "";
		
		try {
			
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
			this.conexion = DriverManager.getConnection(cadenaConexion, usuario, password);
			
		} catch (SQLException e) {
			e.printStackTrace();			
		} 
		
		return conexion;

	}

	public void cerrarConexion() {
		if (this.conexion != null) {
			try {
				this.conexion.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
