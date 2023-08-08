package acoplamiento.problema2;

// import hashmap
import java.util.HashMap;

public class Inventario {
    public HashMap<String, Producto> productos;

    public Inventario(){
        this.productos = new HashMap<String, Producto>();
    }

    public static double darPrecio(Carrito carrito) {
        double precio = 0;
        for (Producto producto : carrito.productos) {
            precio += producto.precio;
        }
        return precio;
    }

    public void actualizarInventario(Carrito carrito) {
        for (Producto producto : carrito.productos) {
            this.productos.remove(producto.nombre);
        }
    }


}
