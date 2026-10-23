package src.Dao.DaoImpl;

import src.Dao.PedidosDao;
import src.model.Pedido;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PedidosDaoImpl implements PedidosDao {

    private static final String url = "jdbc:mysql://localhost:3307/pizzeria";
    private static final String user = "root";
    private static final String password = "";

    private Connection ConexionBd() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void guardar(Pedido pedido) {
        String sql = "INSERT INTO pedidos (id, cliente_id, precio_total, pedido_entregado) VALUES (?, ?, ?, ?)";

        try (Connection cx = ConexionBd();
             PreparedStatement ps = cx.prepareStatement(sql)) {

            ps.setInt(1, pedido.getId());
            ps.setInt(2, pedido.getClienteID());
            ps.setDouble(3, pedido.getPrecioTotal());
            ps.setBoolean(4, pedido.isPedidoEntregado());

            ps.executeUpdate();
            System.out.println("Pedido ID " + pedido.getId() + " guardado en la bdd");

        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    @Override
    public void actualizarEstado(Pedido pedido) {
        String sql = "UPDATE pedidos SET pedido_entregado = ? WHERE id = ?";

        try (Connection cx = ConexionBd();
             PreparedStatement ps = cx.prepareStatement(sql)) {

            ps.setBoolean(1, pedido.isPedidoEntregado());
            ps.setInt(2, pedido.getId());

            ps.executeUpdate();
            System.out.println("Estado del pedido ID " + pedido.getId() + " actualizado en la bdd");

        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}