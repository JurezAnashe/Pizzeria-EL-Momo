package src.Service;

import src.model.Pizza;
import src.model.Pedido;
import src.model.Cliente;
import src.model.Dinero;
import src.model.Ingrediente;

import java.util.List;

import src.Excepciones.DineroInsuficienteException;
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

    public void entregarPedido(Pedido pedido, Cliente cliente, Dinero caja) {
        if (pedido == null || cliente == null || caja == null) {
            System.out.println("Error: No se puede entregar el pedido");
            return;
        }

        pedido.setPedidoEntregado(true);
        pedido.setEstado("ENTREGADO");

        double dineroActualizado = caja.getDineroActual() + pedido.getPrecioTotal();
        caja.setDineroActual(dineroActualizado);

        cliente.setPedidosTotales(cliente.getPedidosTotales() + 1);

        System.out.println("Pedido #" + pedido.getId() + " entregado");
        System.out.println("Dinero total en la caja: $" + caja.getDineroActual());
        System.out.println(
                "El cliente " + cliente.getNombre() + " tiene " + cliente.getPedidosTotales() + " pedidos");
    }

    public void listarPedidos(List<Pedido> listaPedidos) {
        System.out.println("Pedidos: ");
        if (listaPedidos == null || listaPedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados en el sistema");
            return;
        }

        for (Pedido p : listaPedidos) {
            System.out.println(
                    "ID Pedido: " + p.getId() + " Cliente ID: " + p.getClienteID() + " Estado: " + p.getEstado()
                            + " Total: $" + p.getPrecioTotal() + " Entregado: "
                            + (p.isPedidoEntregado() ? "Si" : "No"));
        }
    }

    public void reponerStock(int idIngrediente, int cantidadReponer, Dinero caja) throws DineroInsuficienteException {
        Ingrediente ing = ingredienteService.buscarPorId(idIngrediente);

        if (ing == null) {
            System.out.println("Error: El ingrediente no existe");
            return;
        }

        double costoTotal = ing.getCosto() * cantidadReponer;

        if (caja.getDineroActual() < costoTotal) {
            throw new DineroInsuficienteException(
                    "No hay suficiente dinero en la caja para reponer " + ing.getNombre());
        }

        caja.setDineroActual(caja.getDineroActual() - costoTotal);

        ing.setStockDisponible(ing.getStockDisponible() + cantidadReponer);
        ingredienteService.actualizarIngrediente(ing);

        System.out.println(
                "Se sumaron " + cantidadReponer + " unidades a " + ing.getNombre());
        System.out.println("Queda en la caja: $" + caja.getDineroActual());
    }
}