package src.Dao.DaoImpl;

import src.Dao.IngredienteDao;
import src.model.Ingrediente;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDaoImpl implements IngredienteDao {

    private static final String url = "jdbc:mysql://localhost:3307/pizzeria";
    private static final String user = "root";
    private static final String password = "";

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
        String sql = "SELECT id, nombre, stock_disponible, costo, tipo FROM ingredientes";
        List<Ingrediente> ingredientes = new ArrayList<>();

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                Ingrediente ingrediente = null;

                if ("Carne".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Carne(tipo, 0, 0, tipo, 0, 0);
                } else if ("Queso".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Queso();
                } else if ("Masa".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Masa(0, false, 0, tipo, 0, 0);
                } else if ("Salsa".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Salsa(0, 0, tipo, 0, 0);
                } else {
                    ingrediente = new src.model.Queso();
                }

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
                String tipo = rs.getString("tipo");

                if ("Carne".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Carne(tipo, id, id, tipo, id, id);
                } else if ("Queso".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Queso();
                } else if ("Masa".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Masa(id, false, id, tipo, id, id);
                } else if ("Salsa".equalsIgnoreCase(tipo)) {
                    ingrediente = new src.model.Salsa(id, id, tipo, id, id);
                } else {
                    ingrediente = new src.model.Queso();
                }

                ingrediente.setId(rs.getInt("id"));
                ingrediente.setNombre(rs.getString("nombre"));
                ingrediente.setStockDisponible(rs.getInt("stock_disponible"));
                ingrediente.setCosto(rs.getDouble("costo"));
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
             
            cx.setAutoCommit(true);

            ps.setInt(1, ingrediente.getStockDisponible());
            ps.setDouble(2, ingrediente.getCosto());
            ps.setInt(3, ingrediente.getId());

            int filasAfectadas = ps.executeUpdate();
            
            System.out.println("Se actualizaron " + filasAfectadas + " filas para el ID " + ingrediente.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}