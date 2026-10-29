package src;

import src.model.Ingrediente;
import src.Service.IngredienteService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaIngredientes extends JFrame {
    private IngredienteService ingredienteService;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaIngredientes() {
        ingredienteService = new IngredienteService();

        setTitle("Gestión de Ingredientes - Pizzería EL Momo");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cierra solo esta ventana sin apagar el programa

        // Definimos las columnas de la tabla
        String[] columnas = { "ID", "Nombre", "Stock Disponible", "Precio ($)" };
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);

        // Cargamos los datos desde tu servicio conectado a la base de datos
        cargarIngredientes();

        // Agregamos la tabla dentro de un ScrollPane por si la lista es larga
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void cargarIngredientes() {
        try {
            List<Ingrediente> lista = ingredienteService.obtenerTodos();
            for (Ingrediente ing : lista) {
                Object[] fila = {
                        ing.getId(),
                        ing.getNombre(),
                        ing.getStockDisponible(),
                        ing.getCosto()
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}