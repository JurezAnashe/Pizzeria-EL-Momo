package src.Service;

import src.model.Pizza;
import src.model.Ingrediente;
import src.Excepciones.StockInsuficienteException;

public class PizzaService {

    private IngredienteService ingredienteService;

    public PizzaService(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    public void hacerpizza(Pizza pizza) throws StockInsuficienteException {
        if (pizza == null || pizza.getIngredientesSeleccionados().isEmpty()) {
            System.out.println("Error: La pizza no tiene ingredientes");
            return;
        }

        System.out.println("Haciendo el pedido");

        for (Ingrediente ing : pizza.getIngredientesSeleccionados()) {
            ingredienteService.usarIngrediente(ing.getId(), 1);
        }

        System.out.println("Pizza lista Precio final: $" + pizza.getPrecioTotal());
    }
}