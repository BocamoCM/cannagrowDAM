package com.example.model;

import java.util.*;

/**
 * Modelo para gestionar el carrito de la compra
 */
public class CarritoModel {

    // Mapa que asocia cada producto con su cantidad
    private static Map<Producto, Integer> items = new HashMap<>();

    // Total del carrito
    private static float total = 0.0f;

    /**
     * Agrega un producto al carrito
     * @param producto El producto a agregar
     * @param cantidad La cantidad a agregar
     * @return true si se agregó correctamente, false si no hay suficiente stock
     */
    public static boolean agregarProducto(Producto producto, int cantidad) {
        // Verificar stock disponible
        if (producto.getStock() < cantidad) {
            return false;
        }

        // Si el producto ya está en el carrito, sumar la cantidad
        if (items.containsKey(producto)) {
            int cantidadActual = items.get(producto);
            items.put(producto, cantidadActual + cantidad);
        } else {
            // Si es un producto nuevo, agregarlo al carrito
            items.put(producto, cantidad);
        }

        // Actualizar el total
        calcularTotal();

        return true;
    }

    /**
     * Elimina un producto del carrito
     * @param producto El producto a eliminar
     */
    public static void eliminarProducto(Producto producto) {
        items.remove(producto);
        calcularTotal();
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     * @param producto El producto a actualizar
     * @param cantidad La nueva cantidad
     * @return true si se actualizó correctamente, false si no hay suficiente stock
     */
    public static boolean actualizarCantidad(Producto producto, int cantidad) {
        // Si la cantidad es 0 o negativa, eliminar el producto
        if (cantidad <= 0) {
            eliminarProducto(producto);
            return true;
        }

        // Verificar stock disponible
        if (producto.getStock() < cantidad) {
            return false;
        }

        // Actualizar la cantidad
        items.put(producto, cantidad);
        calcularTotal();

        return true;
    }

    /**
     * Obtiene todos los items del carrito
     * @return Lista con todos los items del carrito
     */
    public static List<ItemCarrito> getItems() {
        List<ItemCarrito> listaItems = new ArrayList<>();

        for (Map.Entry<Producto, Integer> entry : items.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            float subtotal = producto.getPrecio() * cantidad;

            listaItems.add(new ItemCarrito(producto, cantidad, subtotal));
        }

        return listaItems;
    }

    public static boolean crearPedidoDesdeCarrito(int clienteId, String vehiculoMatricula) {
        if (items.isEmpty()) return false;

        // Crear objeto Pedido
        PedidoModel.Pedido pedido = new PedidoModel.Pedido(
                new Date(),                  // Fecha actual
                total,                       // Total del carrito
                PedidoModel.EstadoPedido.PENDIENTE,  // Estado inicial
                clienteId,
                vehiculoMatricula
        );

        // Añadir los detalles del carrito
        for (Map.Entry<Producto, Integer> entry : items.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            PedidoModel.DetallePedido detalle = new PedidoModel.DetallePedido(
                    producto.getId(), cantidad, producto.getPrecio()
            );
            detalle.setProducto(producto); // para info visual opcional
            pedido.agregarDetalle(detalle);
        }

        // Crear el pedido en la base de datos
        int pedidoId = PedidoModel.crearPedido(pedido);
        if (pedidoId != -1) {
            vaciarCarrito(); // Vaciar carrito después de completar
            return true;
        }

        return false;
    }

    /**
     * Calcula el total del carrito
     */
    private static void calcularTotal() {
        total = 0.0f;

        for (Map.Entry<Producto, Integer> entry : items.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();

            total += producto.getPrecio() * cantidad;
        }
    }

    /**
     * Obtiene el total del carrito
     * @return El total del carrito
     */
    public static float getTotal() {
        return total;
    }

    /**
     * Vacía el carrito
     */
    public static void vaciarCarrito() {
        items.clear();
        total = 0.0f;
    }

    /**
     * Comprueba si el carrito está vacío
     * @return true si el carrito está vacío, false en caso contrario
     */
    public static boolean estaVacio() {
        return items.isEmpty();
    }

    /**
     * Obtiene el número de productos diferentes en el carrito
     * @return Número de productos diferentes
     */
    public static int getNumeroItems() {
        return items.size();
    }

    /**
     * Obtiene la cantidad total de productos en el carrito
     * @return Cantidad total de productos
     */
    public static int getCantidadTotal() {
        int cantidadTotal = 0;

        for (int cantidad : items.values()) {
            cantidadTotal += cantidad;
        }

        return cantidadTotal;
    }

    /**
     * Clase interna para representar un item del carrito
     */
    public static class ItemCarrito {
        private Producto producto;
        private int cantidad;
        private float subtotal;

        public ItemCarrito(Producto producto, int cantidad, float subtotal) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.subtotal = subtotal;
        }

        public Producto getProducto() {
            return producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public float getSubtotal() {
            return subtotal;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
            this.subtotal = producto.getPrecio() * cantidad;
        }
    }
}