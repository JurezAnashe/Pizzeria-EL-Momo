package src;

import src.Dao.DaoImpl.DineroDaoImpl;
import src.Service.IngredienteService;
import src.Service.PizzaService;
import src.model.Dinero;
import src.model.Ingrediente;
import src.model.Pedido;
import src.model.Pizza;
import src.model.Cliente;
import src.Excepciones.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private IngredienteService ingredienteService;
    private PizzaService pizzaService;
    private Dinero caja;
    private List<Pedido> listaPedidosGlobal;

    public VentanaPrincipal() {
        ingredienteService = new IngredienteService();
        pizzaService = new PizzaService(ingredienteService);

        try {
            DineroDaoImpl dineroDao = new DineroDaoImpl();
            caja = dineroDao.obtenerDinero();
            if (caja.getDineroActual() == 0) {
                caja.setDineroActual(15000.0);
            }
        } catch (Exception e) {
            caja = new Dinero(15000.0);
        }

        listaPedidosGlobal = new ArrayList<>();

        // Configuracion visual y de dimensiones de la ventana principal
        setTitle("Pizzeria EL Momo - Sistema de Gestion");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1, 10, 10));

        JButton btnIngredientes = new JButton("1. Ver ingredientes y stock");
        JButton btnArmarPizza = new JButton("2. Armar y pedir pizza");
        JButton btnListarPedidos = new JButton("3. Listar pedidos");
        JButton btnReponerStock = new JButton("4. Reponer stock");
        JButton btnEntregarPedido = new JButton("5. Entregar pedido");
        JButton btnFinalizarDia = new JButton("6. Finalizar dia");
        JButton btnSalir = new JButton("0. Salir");

        // --- ASIGNACION DE ACCIONES A LOS BOTONES (Listeners) ---

        // 1. Abre la ventana de tabla para ver los ingredientes y su stock actual desde
        // la base de datos
        btnIngredientes.addActionListener(e -> new VentanaIngredientes().setVisible(true));

        // 2. Abre el dialogo interactivo para armar una pizza seleccionando
        // ingredientes paso a paso
        btnArmarPizza.addActionListener(e -> armarPizzaDialogo());

        // 3. Abre la ventana con la tabla de pedidos registrados en la sesion actual
        btnListarPedidos.addActionListener(e -> new VentanaPedidos(listaPedidosGlobal).setVisible(true));

        // 4. Abre el dialogo para reponer stock de un ingrediente descontando dinero de
        // la caja
        btnReponerStock.addActionListener(e -> reponerStockDialogo());

        // 5. Abre el dialogo para registrar la entrega de un pedido y sumar su pago a
        // la caja
        btnEntregarPedido.addActionListener(e -> entregarPedidoDialogo());

        // 6. Valida que no queden pedidos pendientes y finaliza el dia laboral
        // guardando cambios
        btnFinalizarDia.addActionListener(e -> {
            try {
                pizzaService.finalizarDia(listaPedidosGlobal);
                JOptionPane.showMessageDialog(this,
                        "Dia finalizado con exito!\nDinero en caja: $" + caja.getDineroActual(),
                        "Fin del Dia", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } catch (PedidosPendientesException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Pedidos Pendientes",
                        JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 0. Cierra la aplicacion por completo
        btnSalir.addActionListener(e -> System.exit(0));

        // Margenes esteticos alrededor del panel principal de la ventana
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Agregamos los botones en orden al contenedor principal
        add(btnIngredientes);
        add(btnArmarPizza);
        add(btnListarPedidos);
        add(btnReponerStock);
        add(btnEntregarPedido);
        add(btnFinalizarDia);
        add(btnSalir);
    }

    // Metodo privado para gestionar el armado de pizzas mediante cuadros de dialogo
    // interactivos
    private void armarPizzaDialogo() {
        try {
            // Obtenemos todos los ingredientes disponibles desde el servicio
            List<Ingrediente> disponibles = ingredienteService.obtenerTodos();
            StringBuilder sb = new StringBuilder("Ingredientes disponibles:\n\n");
            for (Ingrediente ing : disponibles) {
                sb.append("ID: ").append(ing.getId())
                        .append(" - ").append(ing.getNombre())
                        .append(" (Stock: ").append(ing.getStockDisponible())
                        .append(", Precio: $").append(ing.getCosto()).append(")\n");
            }

            Pizza miPizza = new Pizza();
            boolean agregando = true;

            // Bucle para permitir al usuario elegir multiples ingredientes para su pizza
            while (agregando) {
                String input = JOptionPane.showInputDialog(this,
                        sb.toString() + "\nIngresa el ID del ingrediente (o 0 para terminar):",
                        "Armar Pizza", JOptionPane.QUESTION_MESSAGE);

                if (input == null)
                    return; // Si el usuario cancela la ventana de dialogo

                int eligio = Integer.parseInt(input.trim());
                if (eligio == 0) {
                    agregando = false; // Finaliza la seleccion de ingredientes
                } else {
                    Ingrediente seleccionado = null;
                    for (Ingrediente ing : disponibles) {
                        if (ing.getId() == eligio) {
                            seleccionado = ing;
                            break;
                        }
                    }

                    if (seleccionado != null) {
                        // Verificamos si hay stock fisico suficiente en el inventario
                        if (seleccionado.getStockDisponible() <= 0) {
                            JOptionPane.showMessageDialog(this,
                                    "No hay stock suficiente de " + seleccionado.getNombre(), "Stock",
                                    JOptionPane.WARNING_MESSAGE);
                            continue;
                        }
                        // Agregamos el ingrediente a la pizza y descontamos stock temporalmente
                        pizzaService.agregarIngredienteAPedido(miPizza, seleccionado);
                        ingredienteService.usarIngrediente(seleccionado.getId(), 1);
                        JOptionPane.showMessageDialog(this, "Ingrediente agregado");
                    } else {
                        JOptionPane.showMessageDialog(this, "No existe un ingrediente con ese ID.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            // Si se seleccionaron ingredientes, pedimos el ID del cliente para dar de alta
            // el pedido
            if (miPizza.getIngredientes() != null && !miPizza.getIngredientes().isEmpty()) {
                String clienteStr = JOptionPane.showInputDialog(this, "Ingrese el ID del cliente:", "Cliente",
                        JOptionPane.QUESTION_MESSAGE);
                if (clienteStr != null) {
                    int clienteId = Integer.parseInt(clienteStr.trim());
                    Pedido nuevoPedido = pizzaService.crearPedido(clienteId, miPizza, listaPedidosGlobal);
                    if (nuevoPedido != null) {
                        listaPedidosGlobal.add(nuevoPedido);
                        JOptionPane.showMessageDialog(this, "Pedido creado con exito ID: " + nuevoPedido.getId());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se armo ninguna pizza.", "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa un numero valido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo privado para reponer stock de un ingrediente pagando su costo con el
    // dinero de la caja
    private void reponerStockDialogo() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Ingresa el ID del ingrediente a reponer:",
                    "Reponer Stock", JOptionPane.QUESTION_MESSAGE);
            if (idStr == null)
                return;
            int idIng = Integer.parseInt(idStr.trim());

            String cantStr = JOptionPane.showInputDialog(this, "Ingresa la cantidad de unidades:", "Reponer Stock",
                    JOptionPane.QUESTION_MESSAGE);
            if (cantStr == null)
                return;
            int cantidad = Integer.parseInt(cantStr.trim());

            // Ejecutamos la reposicion descontando los costos del objeto caja
            pizzaService.reponerStock(idIng, cantidad, caja);
            JOptionPane.showMessageDialog(this,
                    "Stock repuesto correctamente.\nDinero en caja: $" + caja.getDineroActual());

        } catch (DineroInsuficienteException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Dinero Insuficiente",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metodo privado para marcar un pedido como entregado y sumar el ingreso
    // monetario correspondiente a la caja
    private void entregarPedidoDialogo() {
        if (listaPedidosGlobal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pedidos pendientes o registrados.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String idStr = JOptionPane.showInputDialog(this, "Ingresa el ID del pedido que deseas entregar:",
                    "Entregar Pedido", JOptionPane.QUESTION_MESSAGE);
            if (idStr == null)
                return;
            int idPed = Integer.parseInt(idStr.trim());

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
                JOptionPane.showMessageDialog(this,
                        "Pedido " + idPed + " entregado!\nDinero actual en caja: $" + caja.getDineroActual());
            } else {
                JOptionPane.showMessageDialog(this, "No se encontro ningun pedido con ese ID.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}