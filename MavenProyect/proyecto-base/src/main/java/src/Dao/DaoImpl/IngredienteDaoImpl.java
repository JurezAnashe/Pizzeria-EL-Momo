package src.Dao.DaoImpl;

import src.Dao.IngredienteDao;
import src.model.Ingrediente;
import src.model.Queso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDaoImpl implements IngredienteDao {

    private static final String url = "jdbc:mysql://localhost:3306/pizzeria";
    private static final String user = "root";
    private static final String password = "usbw";

    @Override
    public Connection ConexionBd() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void Crear(Ingrediente ingrediente) {
        String sql = "INSERT INTO ingredientes (id, nombre, stock_disponible, costo) VALUES (?, ?, ?, ?)";

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql)) {

            ps.setInt(1, ingrediente.getId());
            ps.setString(2, ingrediente.getNombre());
            ps.setInt(3, ingrediente.getStockDisponible());
            ps.setDouble(4, ingrediente.getCosto());

            ps.executeUpdate();
            System.out.println("Ingrediente agregado");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Object> ListarTodo() {
        String sql = "SELECT id, nombre, stock_disponible, costo FROM ingredientes";
        List<Object> ingredientes = new ArrayList<>();

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Queso ingrediente = new Queso();
                ingrediente.setId(rs.getInt("id"));
                ingrediente.setNombre(rs.getString("nombre"));
                ingrediente.setStockDisponible(rs.getInt("stock_disponible"));
                ingrediente.setCosto(rs.getDouble("costo"));

                ingredientes.add(ingrediente);
            }
            System.out.println("Ingredientes: " + ingredientes.size());

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ingredientes;
    }
}