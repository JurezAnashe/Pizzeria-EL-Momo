package src.Dao;

import src.model.Ingrediente;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IngredienteDao {
    void Crear(Ingrediente ingrediente);

    void actualizar(Ingrediente ingrediente);

    List<Ingrediente> ListarTodo();

    Ingrediente buscarPorId(int id);

    Connection ConexionBd() throws SQLException;
}