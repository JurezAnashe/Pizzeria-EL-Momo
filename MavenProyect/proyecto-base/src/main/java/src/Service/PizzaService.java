package src.Service;

import src.Dao.ClientesDao;
import src.Dao.DineroDao;
import src.Dao.PedidoIngDao;
import src.Dao.PedidosDao;
import src.Dao.DaoImpl.ClientesDaoImpl;
import src.Dao.DaoImpl.DineroDaoImpl;
import src.Dao.DaoImpl.PedidoIngDaoImpl;
import src.Dao.DaoImpl.PedidosDaoImpl;
import src.Excepciones.*;
import src.model.*;

import java.util.List;

public class PizzaService {

    private IngredienteService ingredienteService;
    private double plataGastada;
    private int erroresCometidos;
    private ClientesDao clienteDao = new ClientesDaoImpl();
    private PedidosDao pedidoDao = new PedidosDaoImpl();
    private PedidoIngDao pedidoIngredientesDao = new PedidoIngDaoImpl();
    private DineroDao dineroDao = (DineroDao) new DineroDaoImpl();

    private static final int MAX_INGREDIENTES = 5;
    private static final int MAX_PEDIDOS_PENDIENTES = 3;

    public PizzaService(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
        this.plataGastada = 0.0;
        this.erroresCometidos = 0;
    }

    public void agregarIngredienteAPedido(Pizza pizza, Ingrediente ingrediente) throws IngredientesExcedidosException {
        if (pizza.getIngredientes() != null && pizza.getIngredientes().size() >= MAX_INGREDIENTES) {
            erroresCometidos++;
            throw new IngredientesExcedidosException(
                    "No se pueden agregar mas de " + MAX_INGREDIENTES + " ingredientes a la pizza.");
        }
        pizza.agregarIngrediente(ingrediente);
    }

    public Pedido crearPedido(int clienteId, Pizza pizza, List<Pedido> pedidosActuales) throws LimitePedidosException {
        int pedidosPendientesCliente = 0;
        if (pedidosActuales != null) {
            for (Pedido p : pedidosActuales) {
                if (p.getClienteID() == clienteId && !p.isPedidoEntregado()) {
                    pedidosPendientesCliente++;
                }
            }
        }

        if (pedidosPendientesCliente >= MAX_PEDIDOS_PENDIENTES) {
            erroresCometidos++;
            throw new LimitePedidosException(
                    "El cliente con ID " + clienteId + " tiene demasiados pedidos pendientes");
        }

        int idGenerado = (int) (Math.random() * 1000) + 1;
        String estadoInicial = "PENDIENTE";
        double precio = pizza.getPrecioTotal();
        boolean entregado = false;

        Pedido nuevoPedido = new Pedido(idGenerado, clienteId, estadoInicial, precio, entregado, pizza);
        clienteDao.guardarOSumarPedido(clienteId);
        pedidoDao.guardar(nuevoPedido);
        pedidoIngredientesDao.guardarIngredientes(nuevoPedido);
        return nuevoPedido;
    }

    public void listarPedidos(List<Pedido> pedidos) {
        if (pedidos == null || pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.println("Pedido ID: " + p.getId() + " Cliente ID: " + p.getClienteID() +
                    " Estado: " + (p.isPedidoEntregado() ? "ENTREGADO" : "PENDIENTE"));
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
        dineroDao.guardarOActualizar(caja);
        this.plataGastada += costoTotal;
        ing.setStockDisponible(ing.getStockDisponible() + cantidadReponer);
        ingredienteService.actualizarIngrediente(ing);

        System.out.println(
                "Se sumaron " + cantidadReponer + " unidades a " + ing.getNombre());
        System.out.println("Queda en la caja: $" + caja.getDineroActual());
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
        clienteDao.guardarOSumarPedido(cliente.getId());

        System.out.println("El pedido " + pedido.getId() + " fue entregado");
        System.out.println("Dinero total en la caja: $" + caja.getDineroActual());
        System.out.println(
                "El cliente " + cliente.getNombre() + " tiene " + cliente.getPedidosTotales() + " pedidos");
    }

    public void finalizarDia(List<Pedido> pedidos) throws PedidosPendientesException {
        if (pedidos != null) {
            for (Pedido p : pedidos) {
                if (!p.isPedidoEntregado()) {
                    erroresCometidos++;
                    throw new PedidosPendientesException(
                            "No se puede finalizar el dia porque el pedido " + p.getId() + " sigue PENDIENTE");
                }
            }
        }

        System.out.println("Resumen del dia:");
        System.out.println("Plata Gastada: $" + plataGastada);
        System.out.println("Errores Cometidos: " + erroresCometidos);
    }
}