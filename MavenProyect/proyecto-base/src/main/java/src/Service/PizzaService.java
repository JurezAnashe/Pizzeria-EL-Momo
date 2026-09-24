package src.Service;

import src.model.Pizza;
import src.model.Pedido;
import src.model.Ingrediente;
import src.Excepciones.StockInsuficienteException;

public class PizzaService {

    private IngredienteService ingredienteService;

    public PizzaService(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    public Pedido crearPedido(int clienteId, Pizza pizza) throws StockInsuficienteException {
        if (pizza == null || pizza.getIngredientesSeleccionados().isEmpty()) {
            System.out.println("Error: La pizza no tiene ingredientes");
            return null;
        }

        for (Ingrediente ing : pizza.getIngredientesSeleccionados()) {
            ingredienteService.usarIngrediente(ing.getId(), 1);
        }

        int idPedido = 0;
        String estadoInicial = "Pendiente";
        double total = pizza.getPrecioTotal();
        boolean entregado = false;

        Pedido nuevoPedido = new Pedido(idPedido, clienteId, estadoInicial, total, entregado, pizza);

        System.out.println("Pedido creado para el cliente ID: " + clienteId);
        System.out.println("Precio total: $" + total);
        System.out.println("Stock actualizado");

        return nuevoPedido;
    }
}