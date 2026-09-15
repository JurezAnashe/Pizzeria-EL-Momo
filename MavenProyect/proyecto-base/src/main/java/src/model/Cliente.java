package src.model;

public class Cliente {
    private int id;
    private String nombre;
    private int pedidosTotales;

    public Cliente(int id, String nombre, int pedidosTotales) {
        this.id = id;
        this.nombre = nombre;
        this.pedidosTotales = pedidosTotales;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPedidosTotales() {
        return pedidosTotales;
    }

    public void setPedidosTotales(int pedidosTotales) {
        this.pedidosTotales = pedidosTotales;
    }

    @Override
    public String toString() {
        return id + " " + nombre + " " + pedidosTotales;
    }
}
