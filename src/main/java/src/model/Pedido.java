package src.model;

public class Pedido {
    private int id;
    private int clienteId;
    private String estado;
    private double precioTotal;
    private boolean pedidoEntregado;
    private Pizza pizza;

    public Pedido(int id, int clienteId, String estado, double precioTotal, boolean pedidoEntregado, Pizza pizza) {
        this.id = id;
        this.clienteId = clienteId;
        this.estado = estado;
        this.precioTotal = precioTotal;
        this.pedidoEntregado = pedidoEntregado;
        this.pizza = pizza;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteID() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public boolean isPedidoEntregado() {
        return pedidoEntregado;
    }

    public void setPedidoEntregado(boolean pedidoEntregado) {
        this.pedidoEntregado = pedidoEntregado;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String toString() {
        return id + " " + clienteId + " " + estado + " " + precioTotal + " " + pedidoEntregado + " ";
    }
}
