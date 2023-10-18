package tienda;


public class Cliente {
    private String tipo;
    private String nombre;

    public Cliente(String tipo, String nombre) {
        this.tipo = tipo;
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

}
