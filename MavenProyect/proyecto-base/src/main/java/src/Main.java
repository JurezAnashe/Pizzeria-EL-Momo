package src;

import src.model.Ingrediente;
import src.model.Pizza;
import src.model.Pedido;
import src.model.Cliente;
import src.model.Dinero;
import src.Service.IngredienteService;
import src.Service.PizzaService;
import src.Excepciones.PedidosPendientesException;
import src.Excepciones.DineroInsuficienteException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IngredienteService ingredienteService = new IngredienteService();
        PizzaService pizzaService = new PizzaService(ingredienteService);

        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        Dinero caja = new Dinero(15000.0);
        List<Pedido> listaPedidosGlobal = new ArrayList<>();

        do {
            System.out.println("Pizzeria el Momo");
            System.out.println("1. Ver ingredientes y stock disponible");
            System.out.println("2. Armar y pedir pizza");
            System.out.println("3. Listar pedidos");
            System.out.println("4. Reponer stock");
            System.out.println("5. Entregar pedido");
            System.out.println("6. Finalizar dia");
            System.out.println("0. Salir");
            System.out.print("Eligi una opcion: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
            } else {
                System.out.println("Ingresa un numero valido");
                scanner.next();
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("Ingredientes:");
                    List<Ingrediente> lista = ingredienteService.obtenerTodos();
                    for (Ingrediente ing : lista) {
                        System.out.println(" ID: " + ing.getId() + " Nombre: " + ing.getNombre() +
                                " Stock: " + ing.getStockDisponible() + " Sale: $" + ing.getCosto());
                    }
                    break;

                case 2:
                    System.out.println("Armar la pizza:");

                    List<Ingrediente> disponibles = ingredienteService.obtenerTodos();
                    System.out.println("Ingredientes disponibles:");
                    for (Ingrediente ing : disponibles) {
                        System.out.println(" ID: " + ing.getId() + " Nombre: " + ing.getNombre() +
                                " Stock: " + ing.getStockDisponible() + " Sale: $" + ing.getCosto());
                    }

                    Pizza miPizza = new Pizza();
                    boolean agregando = true;

                    while (agregando) {
                        System.out.print("Ingresa el ID del ingrediente o 0 para terminar: ");
                        int eligio = scanner.nextInt();

                        if (eligio == 0) {
                            agregando = false;
                        } else {
                            Ingrediente seleccionado = null;
                            for (Ingrediente ing : disponibles) {
                                if (ing.getId() == eligio) {
                                    seleccionado = ing;
                                    break;
                                }
                            }

                            if (seleccionado != null) {
                                try {
                                    if (seleccionado.getStockDisponible() <= 0) {
                                        throw new src.Excepciones.StockInsuficienteException(
                                                "No hay stock suficiente de " + seleccionado.getNombre());
                                    }

                                    pizzaService.agregarIngredienteAPedido(miPizza, seleccionado);
                                    ingredienteService.usarIngrediente(seleccionado.getId(), 1);
                                    System.out.println("Ingrediente agregado");

                                } catch (src.Excepciones.StockInsuficienteException
                                        | src.Excepciones.IngredientesExcedidosException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                            } else {
                                System.out.println("Error: No existe un ingrediente con ese ID");
                            }
                        }
                    }

                    if (miPizza.getIngredientes() != null && !miPizza.getIngredientes().isEmpty()) {
                        try {
                            System.out.print("Ingrese el ID del cliente: ");
                            int clienteId = scanner.nextInt();

                            Pedido nuevoPedido = pizzaService.crearPedido(clienteId, miPizza, listaPedidosGlobal);
                            if (nuevoPedido != null) {
                                listaPedidosGlobal.add(nuevoPedido);
                                System.out.println("Pedido creado, su ID es: " + nuevoPedido.getId());
                            }
                        } catch (src.Excepciones.LimitePedidosException e) {
                            System.out.println("Limite de pedidos superado: " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("Error al crear el pedido: " + e.getMessage());
                        }
                    } else {
                        System.out.println("No se armo ninguna pizza");
                    }
                    break;

                case 3:
                    System.out.println("Lista de pedidos:");
                    pizzaService.listarPedidos(listaPedidosGlobal);
                    break;

                case 4:
                    System.out.println("Reponer stock:");
                    System.out.print("Ingresa el ID del ingrediente para reponerlo: ");
                    int idIng = scanner.nextInt();
                    System.out.print("Ingresa la cantidad de unidades: ");
                    int cantidad = scanner.nextInt();

                    try {
                        pizzaService.reponerStock(idIng, cantidad, caja);
                    } catch (DineroInsuficienteException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.println("Entregar pedido:");
                    pizzaService.listarPedidos(listaPedidosGlobal);

                    if (!listaPedidosGlobal.isEmpty()) {
                        System.out.print("Ingresa el ID del pedido que desea entregar: ");
                        int idPed = scanner.nextInt();

                        Pedido pedidoEncontrado = null;
                        for (Pedido p : listaPedidosGlobal) {
                            if (p.getId() == idPed) {
                                pedidoEncontrado = p;
                                break;
                            }
                        }

                        if (pedidoEncontrado != null) {
                            Cliente clienteTemp = new Cliente(pedidoEncontrado.getClienteID(),
                                    "Cliente " + pedidoEncontrado.getClienteID(), 0);
                            pizzaService.entregarPedido(pedidoEncontrado, clienteTemp, caja);
                        } else {
                            System.out.println("No hay pedidos con ese ID");
                        }
                    }
                    break;

                case 6:
                    System.out.println("Finalizar Dia");
                    try {
                        pizzaService.finalizarDia(listaPedidosGlobal);
                        System.exit(0);
                    } catch (PedidosPendientesException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 0:
                    System.out.println("Saliendo de la pizzeria");
                    break;

                default:
                    System.out.println("Opcion no valida");
            }

        } while (opcion != 0);

        scanner.close();
    }
}