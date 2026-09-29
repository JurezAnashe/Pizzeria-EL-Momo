package src.Dao.DaoImpl;

import src.Dao.ClientesDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientesDaoImpl implements ClientesDao {

    private static final String url = "jdbc:mysql://localhost:3307/pizzeria";
    private static final String user = "root";
    private static final String password = "";

    private Connection ConexionBd() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void guardarOSumarPedido(int clienteId) {
        String sqlExiste = "SELECT id, pedidos_totales FROM clientes WHERE id = ?";
        String sqlInsert = "INSERT INTO clientes (id, pedidos_totales) VALUES (?, 1)";
        String sqlUpdate = "UPDATE clientes SET pedidos_totales = pedidos_totales + 1 WHERE id = ?";

        try (Connection cx = ConexionBd();
             PreparedStatement psExiste = cx.prepareStatement(sqlExiste)) {
            
            psExiste.setInt(1, clienteId);
            ResultSet rs = psExiste.executeQuery();

            if (rs.next()) {
                try (PreparedStatement psUpdate = cx.prepareStatement(sqlUpdate)) {
                    psUpdate.setInt(1, clienteId);
                    psUpdate.executeUpdate();
                    System.out.println("Cliente ID " + clienteId + ": se agrego un pedido a la bdd");
                }
            } else {
                try (PreparedStatement psInsert = cx.prepareStatement(sqlInsert)) {
                    psInsert.setInt(1, clienteId);
                    psInsert.executeUpdate();
                    System.out.println("Nuevo cliente ID " + clienteId + " registrado en la BD");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
    }
}