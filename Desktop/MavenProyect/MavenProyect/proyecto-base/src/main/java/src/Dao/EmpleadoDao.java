package src.Dao;

import src.model.Inglediente;
import java.util.List;

public interface EmpleadoDao {
    void Crear(Inglediente empleado);

    Inglediente BuscarPorDni(String dni);

    List<Inglediente> ListarTodo();

    java.sql.Connection ConexionBd() throws java.sql.SQLException;
}