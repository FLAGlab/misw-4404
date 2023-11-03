package restaurante;

import java.util.ArrayList;
import java.util.Collections;

public class Usuario {
    private String nombre;
    private String direccion;
    private ArrayList<Pedido> pedidos;

    public Usuario(String nombre, String direccion, ArrayList<Pedido> pedidos) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.pedidos = pedidos;
    }

    public String getNombre() {
        return nombre;
    }
    public String getDireccion() {
        return direccion;
    }
    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public void setPedidos(ArrayList<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public void generarReporte() {
        int total = calcularTotal();
        System.out.println("------------------------");
        System.out.println("El total de compras para " + nombre + " es: " + total);
        ArrayList<Pedido> pedidosOrdenados = ordenarPedidosPorPrecio();
        for(Pedido pedido : pedidosOrdenados) {
            pedido.generarReporte();
        }
    }

    private int calcularTotal() {
        int total = 0;
        for (Pedido pedido : pedidos) {
            for (Producto producto : pedido.getProductos()) {
                total += producto.getPrecio();
            }
        }
        return total;
    }

    public ArrayList<Pedido> ordenarPedidosPorPrecio() {
        ArrayList<Pedido> pedidosOrdenados = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            pedidosOrdenados.add(pedido);
        }
        for (int i = 0; i < pedidosOrdenados.size(); i++) {
            for (int j = 0; j < pedidosOrdenados.size() - 1; j++) {
                if (pedidosOrdenados.get(j).calcularTotal() > pedidosOrdenados.get(j + 1).calcularTotal()) {
                    Pedido aux = pedidosOrdenados.get(j);
                    pedidosOrdenados.set(j, pedidosOrdenados.get(j + 1));
                    pedidosOrdenados.set(j + 1, aux);
                }
            }
        }
        Collections.reverse(pedidosOrdenados);
        return pedidosOrdenados;
    }
}
