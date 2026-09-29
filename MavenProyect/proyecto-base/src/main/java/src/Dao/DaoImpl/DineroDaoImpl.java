package src.Dao.DaoImpl;

import src.Dao.DineroDao;
import src.model.Dinero;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DineroDaoImpl implements DineroDao {

    private static final String url = "jdbc:mysql://localhost:3307/pizzeria";
    private static final String user = "root";
    private static final String password = "";

    private Connection ConexionBd() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void guardarOActualizar(Dinero dinero) {
        String sqlExiste = "SELECT id FROM dinero WHERE id = 1";
        String sqlInsert = "INSERT INTO dinero (id, dinero_total) VALUES (1, ?)";
        String sqlUpdate = "UPDATE dinero SET dinero_total = ? WHERE id = 1";

        try (Connection cx = ConexionBd();
             PreparedStatement psExiste = cx.prepareStatement(sqlExiste)) {

            ResultSet rs = psExiste.executeQuery();

            if (rs.next()) {
                try (PreparedStatement psUpdate = cx.prepareStatement(sqlUpdate)) {
                    psUpdate.setDouble(1, dinero.getDineroActual());
                    psUpdate.executeUpdate();
                    System.out.println("Caja actualizada en la BD: $" + dinero.getDineroActual());
                }
            } else {
                try (PreparedStatement psInsert = cx.prepareStatement(sqlInsert)) {
                    psInsert.setDouble(1, dinero.getDineroActual());
                    psInsert.executeUpdate();
                    System.out.println("Caja: $" + dinero.getDineroActual());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Dinero obtenerDinero() {
        String sql = "SELECT dinero_total FROM dinero WHERE id = 1";
        Dinero caja = new Dinero(0);

        try (Connection cx = ConexionBd();
             PreparedStatement ps = cx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                caja.setDineroActual(rs.getDouble("dinero_total"));
            }

        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }

        return caja;
    }
}