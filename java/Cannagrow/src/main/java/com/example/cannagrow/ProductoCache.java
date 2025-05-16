package com.example.cannagrow;

import com.example.model.Producto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controlador para  optimizar el rendimiento en la visualización del catálogo de productos.
 */
public class ProductoCache {

    // Mapa concurrente para evitar problemas en acceso concurrente
    private static final Map<Integer, Producto> cacheProductos = new ConcurrentHashMap<>();

    /**
     * Obtiene un producto del cache por su ID.
     * @param productoId ID del producto
     * @return Producto si existe en cache, null si no está
     */
    public static Producto obtenerProducto(int productoId) {
        return cacheProductos.get(productoId);
    }

    /**
     * Agrega o actualiza un producto en la cache.
     * @param productoId ID del producto
     * @param producto Objeto Producto a cachear
     */
    public static void agregarProducto(int productoId, Producto producto) {
        if (producto != null) {
            cacheProductos.put(productoId, producto);
        }
    }

    /**
     * Verifica si un producto ya existe en cache.
     * @param productoId ID del producto
     * @return true si está en cache, false si no
     */
    public static boolean existeProducto(int productoId) {
        return cacheProductos.containsKey(productoId);
    }

    /**
     * Opcional: limpiar toda la cache, por ejemplo al cerrar sesión o actualizar datos.
     */
    public static void limpiarCache() {
        cacheProductos.clear();
    }
}
