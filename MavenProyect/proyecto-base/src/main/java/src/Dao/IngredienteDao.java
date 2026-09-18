package src.Dao;

import src.model.Ingrediente;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IngredienteDao {
    void Crear(Ingrediente ingrediente);

    List<Ingrediente> ListarTodo();

    Connection ConexionBd() throws SQLException;
}