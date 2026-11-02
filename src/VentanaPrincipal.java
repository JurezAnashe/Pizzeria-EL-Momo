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

        // Configuracion visual y de dimensiones de la ventana principal (ajustamos a 8
        // filas)
        setTitle("Pizzeria EL Momo - Sistema de Gestion");
        setSize(450, 550);

        // Nota: juego.java lo cambia a HIDE_ON_CLOSE para que no se pierdan los datos
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 1, 10, 10));

        JButton btnIngredientes = new JButton("1. Ver ingredientes y stock");
        JButton btnArmarPizza = new JButton("2. Armar y pedir pizza");
        JButton btnListarPedidos = new JButton("3. Listar pedidos");
        JButton btnReponerStock = new JButton("4. Reponer stock");
        JButton btnEntregarPedido = new JButton("5. Entregar pedido");
        JButton btnFinalizarDia = new JButton("6. Finalizar dia");
        JButton btnPrepararPizza = new JButton("7. Preparar pizza de un pedido");
        JButton btnSalir = new JButton("0. Salir");

        // --- ASIGNACION DE ACCIONES A LOS BOTONES (Listeners) ---

        btnIngredientes.addActionListener(e -> new VentanaIngredientes().setVisible(true));
        btnArmarPizza.addActionListener(e -> armarPizzaDialogo());
        btnListarPedidos.addActionListener(e -> new VentanaPedidos(listaPedidosGlobal).setVisible(true));
        btnReponerStock.addActionListener(e -> reponerStockDialogo());
        btnEntregarPedido.addActionListener(e -> entregarPedidoDialogo());

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

        btnPrepararPizza.addActionListener(e -> prepararPizzaDialogo());

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
        add(btnPrepararPizza);
        add(btnSalir);
    }

    private void armarPizzaDialogo() {
        try {
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

            while (agregando) {
                String input = JOptionPane.showInputDialog(this,
                        sb.toString() + "\nIngresa el ID del ingrediente (o 0 para terminar):",
                        "Armar Pizza", JOptionPane.QUESTION_MESSAGE);

                if (input == null)
                    return;

                int eligio = Integer.parseInt(input.trim());
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
                        if (seleccionado.getStockDisponible() <= 0) {
                            JOptionPane.showMessageDialog(this,
                                    "No hay stock suficiente de " + seleccionado.getNombre(), "Stock",
                                    JOptionPane.WARNING_MESSAGE);
                            continue;
                        }
                        pizzaService.agregarIngredienteAPedido(miPizza, seleccionado);
                        ingredienteService.usarIngrediente(seleccionado.getId(), 1);
                        JOptionPane.showMessageDialog(this, "Ingrediente agregado");
                    } else {
                        JOptionPane.showMessageDialog(this, "No existe un ingrediente con ese ID.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

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

    /**
     * Devuelve los ingredientes de un pedido entre paréntesis, ej: "(Queso,
     * Jamon)".
     */
    private String ingredientesEntreParentesis(Pedido p) {
        if (p.getPizza() == null || p.getPizza().getIngredientes() == null
                || p.getPizza().getIngredientes().isEmpty()) {
            return "(sin ingredientes)";
        }
        StringBuilder sb = new StringBuilder("(");
        List<Ingrediente> ings = p.getPizza().getIngredientes();
        for (int i = 0; i < ings.size(); i++) {
            sb.append(ings.get(i).getNombre());
            if (i < ings.size() - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Opción 7: se elige un pedido (con sus ingredientes entre paréntesis) y luego
     * se ingresan, uno por uno, los IDs de los ingredientes que pidió el personaje.
     * Al ingresar cada ID se ejecuta preparar() de ese ingrediente.
     */
    private void prepararPizzaDialogo() {
        if (listaPedidosGlobal == null || listaPedidosGlobal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pedidos registrados para preparar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1) Elegir el pedido, mostrando los ingredientes al lado entre ()
            StringBuilder sb = new StringBuilder("Pedidos disponibles:\n\n");
            for (Pedido p : listaPedidosGlobal) {
                sb.append("ID Pedido: ").append(p.getId())
                        .append(" - Cliente ID: ").append(p.getClienteID())
                        .append(" ").append(ingredientesEntreParentesis(p)).append("\n");
            }

            String idStr = JOptionPane.showInputDialog(this,
                    sb.toString() + "\nIngresa el ID del pedido que deseas preparar:",
                    "Preparar Pizza", JOptionPane.QUESTION_MESSAGE);

            if (idStr == null)
                return;
            int idPedidoPrep = Integer.parseInt(idStr.trim());

            Pedido pedidoParaPreparar = null;
            for (Pedido p : listaPedidosGlobal) {
                if (p.getId() == idPedidoPrep) {
                    pedidoParaPreparar = p;
                    break;
                }
            }

            if (pedidoParaPreparar == null || pedidoParaPreparar.getPizza() == null) {
                JOptionPane.showMessageDialog(this,
                        "Error: No se encontró un pedido con ese ID.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Pizza pizzaSeleccionada = pedidoParaPreparar.getPizza();
            if (pizzaSeleccionada.getIngredientes() == null || pizzaSeleccionada.getIngredientes().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El pedido no contiene ingredientes registrados en la pizza.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2) Ingredientes pendientes de preparar (copia para ir quitando los hechos)
            List<Ingrediente> pendientes = new ArrayList<>(pizzaSeleccionada.getIngredientes());

            while (!pendientes.isEmpty()) {
                StringBuilder lista = new StringBuilder("Pedido ID: " + pedidoParaPreparar.getId()
                        + "\nIngredientes pendientes:\n\n");
                for (Ingrediente ing : pendientes) {
                    lista.append("ID: ").append(ing.getId())
                            .append(" - ").append(ing.getNombre()).append("\n");
                }

                String ingStr = JOptionPane.showInputDialog(this,
                        lista.toString() + "\nIngresa el ID del ingrediente que pidió el personaje (o 0 para salir):",
                        "Preparar Pizza", JOptionPane.QUESTION_MESSAGE);

                if (ingStr == null)
                    return;

                int idIng = Integer.parseInt(ingStr.trim());
                if (idIng == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Preparación interrumpida. Faltan " + pendientes.size() + " ingrediente(s).",
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Buscar ese ingrediente entre los pendientes
                Ingrediente encontrado = null;
                for (Ingrediente ing : pendientes) {
                    if (ing.getId() == idIng) {
                        encontrado = ing;
                        break;
                    }
                }

                if (encontrado == null) {
                    JOptionPane.showMessageDialog(this,
                            "Ese ingrediente no está pendiente en este pedido.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // Acá aparece la interfaz / acción de preparar()
                encontrado.preparar();
                pendientes.remove(encontrado);
            }

            JOptionPane.showMessageDialog(this,
                    "La pizza del pedido ID: " + pedidoParaPreparar.getId() +
                            " está completamente lista para despachar.",
                    "Preparación Completa", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa un número válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}