package src.Dao.DaoImpl;

import src.Dao.EmpleadoDao;
import src.model.Inglediente;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDaoImpl implements EmpleadoDao {

    private static final String url = "jdbc:mysql://localhost:3306/globant_db";
    private static final String user = "root";
    private static final String password = "usbw";

    @Override
    public Connection ConexionBd() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void Crear(Inglediente empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, dni, cargo, salario, activo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getDni());
            ps.setString(4, empleado.getCargo());
            ps.setDouble(5, empleado.getSalario());
            ps.setBoolean(6, empleado.isActivo());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Inglediente BuscarPorDni(String dni) {
        String sql = "SELECT id, nombre, apellido, dni, cargo, salario, activo FROM empleados WHERE dni = ?";

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Inglediente empleado = new Inglediente(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("dni"),
                            rs.getString("cargo"),
                            rs.getDouble("salario"),
                            rs.getBoolean("activo"));
                    empleado.setId(rs.getInt("id"));
                    return empleado;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Inglediente> ListarTodo() {
        String sql = "SELECT id, nombre, apellido, dni, cargo, salario, activo FROM empleados";
        List<Inglediente> empleados = new ArrayList<>();

        try (Connection cx = ConexionBd();
                PreparedStatement ps = cx.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Inglediente empleado = new Inglediente(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("cargo"),
                        rs.getDouble("salario"),
                        rs.getBoolean("activo"));
                empleado.setId(rs.getInt("id"));
                empleados.add(empleado);
                System.out.println(empleado);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return empleados;
    }
}
