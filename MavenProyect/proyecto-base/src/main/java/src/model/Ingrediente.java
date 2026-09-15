package src.model;

public abstract class Ingrediente {

    private int id;
    private String nombre;
    private int stockDisponible;
    private double costo;

    public Ingrediente(int id, String nombre, int stockDisponible, double costo) {
        this.id = id;
        this.nombre = nombre;
        this.stockDisponible = stockDisponible;
        this.costo = costo;
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

    public int getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(int stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    @Override
    public String toString() {
        return id + " " + nombre + " " + stockDisponible + " " + costo + " ";
    }

}