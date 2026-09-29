package src.Dao.DaoImpl;

import src.Dao.PedidoIngDao;
import src.model.Ingrediente;
import src.model.Pedido;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PedidoIngDaoImpl implements PedidoIngDao {

    private static final String url = "jdbc:mysql://localhost:3307/pizzeria";
    private static final String user = "root";
    private static final String password = "";

    private Connection ConexionBd() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void guardarIngredientes(Pedido pedido) {
        if (pedido == null || pedido.getPizza() == null || pedido.getPizza().getIngredientes() == null) {
            return;
        }

        String sql = "INSERT INTO pedido_ingredientes (pedido_id, ingrediente_id, cantidad) VALUES (?, ?, ?)";

        try (Connection cx = ConexionBd();
             PreparedStatement ps = cx.prepareStatement(sql)) {

            for (Ingrediente ing : pedido.getPizza().getIngredientes()) {
                ps.setInt(1, pedido.getId());
                ps.setInt(2, ing.getId());
                ps.setInt(3, 1); 
                ps.addBatch(); 
            }

            ps.executeBatch();
            System.out.println("Ingredientes del pedido ID " + pedido.getId() + " guardados en la bdd");

        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
    }
}
