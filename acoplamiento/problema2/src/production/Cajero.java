package production;

import java.util.ArrayList;

public class Cajero {
    public static Inventario inventario;

    public Cajero(Inventario inventario) {
        this.inventario = inventario;
    }

    public static void procesarTransaccion(Carrito carrito) {
        double precio = Inventario.darPrecio(carrito);
        System.out.println("El precio total es: " + precio);
        inventario.actualizarInventario(carrito);
    }

    public static void main(String[] args) {
        Inventario inventario = new Inventario();
        Cajero cajero = new Cajero(inventario);

        Producto producto1 = new Producto("Producto 1", 1000);
        Producto producto2 = new Producto("Producto 2", 2000);
        Producto producto3 = new Producto("Producto 3", 3000);

        inventario.productos.put(producto1.nombre, producto1);
        inventario.productos.put(producto2.nombre, producto2);
        inventario.productos.put(producto3.nombre, producto3);

        Carrito carrito = new Carrito();

        carrito.productos = new ArrayList<Producto>();
        carrito.productos.add(producto1);
        carrito.productos.add(producto2);

        procesarTransaccion(carrito);


    }


}
