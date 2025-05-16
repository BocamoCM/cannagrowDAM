package com.example.cannagrow;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Clase para cachear datos de usuarios (clientes), evitando múltiples consultas a la base de datos.
 */
public class UsuarioCache {

    // Cache thread-safe para almacenar clienteId y su nombre
    private static final Map<Integer, String> cacheClientes = new ConcurrentHashMap<>();

    /**
     * Obtiene el nombre del cliente por su ID desde la cache.
     * Si no está en cache, retorna un valor por defecto.
     *
     * @param clienteId id del cliente
     * @return nombre del cliente o "Desconocido" si no existe en cache
     */
    public static String getNombreCliente(int clienteId) {
        return cacheClientes.getOrDefault(clienteId, "Desconocido");
    }

    /**
     * Agrega o actualiza un cliente en la cache.
     *
     * @param clienteId id del cliente
     * @param nombreCliente nombre del cliente
     */
    public static void agregarCliente(int clienteId, String nombreCliente) {
        if (nombreCliente != null) {
            cacheClientes.put(clienteId, nombreCliente);
        }
    }

    /**
     * Verifica si el cliente ya existe en la cache.
     *
     * @param clienteId id del cliente
     * @return true si existe, false si no
     */
    public static boolean existeCliente(int clienteId) {
        return cacheClientes.containsKey(clienteId);
    }

    /**
     * Limpia toda la cache.
     */
    public static void limpiarCache() {
        cacheClientes.clear();
    }
}
