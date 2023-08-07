package complejidad.problema3;

public class Producto {
    private String nombre;
    private int precio;

    public Producto(String nombre, int precio){
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre(){
        return this.nombre;
    }

    public int getPrecio(){
        return this.precio;
    }
}
