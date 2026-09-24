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
        String sql = "INSERT INTO ingredientes (id, nombre, stock_disponible, costo, tipo) VALUES (?, ?, ?, ?, ?)";

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql)) {

            ps.setInt(1, ingrediente.getId());
            ps.setString(2, ingrediente.getNombre());
            ps.setInt(3, ingrediente.getStockDisponible());
            ps.setDouble(4, ingrediente.getCosto());

            ps.setString(5, ingrediente.getClass().getSimpleName());

            ps.executeUpdate();
            System.out.println("Ingrediente agregado");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Ingrediente> ListarTodo() {
        String sql = "SELECT id, nombre, stock_disponible, costo FROM ingredientes";
        List<Ingrediente> ingredientes = new ArrayList<>();

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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ingredientes;
    }

    @Override
    public Ingrediente buscarPorId(int id) {
        Ingrediente ingrediente = null;
        String sql = "SELECT * FROM ingredientes WHERE id = ?";

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Queso q = new Queso();
                q.setId(rs.getInt("id"));
                q.setNombre(rs.getString("nombre"));
                q.setStockDisponible(rs.getInt("stock_disponible"));
                q.setCosto(rs.getDouble("costo"));
                ingrediente = q;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingrediente;
    }

    @Override
    public void actualizar(Ingrediente ingrediente) {
        String sql = "UPDATE ingredientes SET stock_disponible = ?, costo = ? WHERE id = ?";

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setInt(1, ingrediente.getStockDisponible());
            ps.setDouble(2, ingrediente.getCosto());
            ps.setInt(3, ingrediente.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}