//Prompt 1:

package src;

import src.model.Pedido;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaPedidos extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaPedidos(List<Pedido> pedidos) {
        setTitle("Listado de Pedidos - Pizzería EL Momo");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columnas = { "ID Pedido", "ID Cliente", "Estado", "Precio Total" };
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);

        cargarPedidos(pedidos);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void cargarPedidos(List<Pedido> pedidos) {
        if (pedidos != null) {
            for (Pedido p : pedidos) {
                Object[] fila = {
                        p.getId(),
                        p.getClienteID(),
                        p.isPedidoEntregado() ? "ENTREGADO" : "PENDIENTE",
                        "$" + p.getPrecioTotal()
                };
                modeloTabla.addRow(fila);
            }
        }
    }
}