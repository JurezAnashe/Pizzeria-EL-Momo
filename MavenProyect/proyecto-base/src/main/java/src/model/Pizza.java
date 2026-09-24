package src.model;

import java.util.ArrayList;
import java.util.List;

public class Pizza implements Preparables {
    private int id;
    private List<Ingrediente> ingredientesSeleccionados;
    private double precioTotal;

    public Pizza() {
        this.ingredientesSeleccionados = new ArrayList<>();
        this.precioTotal = 0;
    }

    public void agregarIngrediente(Ingrediente ingrediente) {
        this.ingredientesSeleccionados.add(ingrediente);
        this.precioTotal += ingrediente.getCosto();
    }

    @Override
    public void preparar() {
        System.out.println("Armando una pizza con " + ingredientesSeleccionados.size() + " ingredientes");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Ingrediente> getIngredientesSeleccionados() {
        return ingredientesSeleccionados;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }
}
