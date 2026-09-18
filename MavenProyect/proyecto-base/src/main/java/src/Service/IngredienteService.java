package src.Service;

import src.Dao.IngredienteDao;
import src.Dao.DaoImpl.IngredienteDaoImpl;
import src.model.Ingrediente;
import src.model.Preparables;

import java.util.List;

public class IngredienteService {

    private IngredienteDao ingredienteDao;

    public IngredienteService() {
        this.ingredienteDao = new IngredienteDaoImpl();
    }

    public void guardarIngrediente(Ingrediente ingrediente) {
        if (ingrediente == null) {
            System.out.println("Error: El ingrediente no puede ser nulo");
            return;
        }
        if (ingrediente.getNombre() == null || ingrediente.getNombre().isEmpty()) {
            System.out.println("Error: El nombre del ingrediente no puede estar vacio");
            return;
        }
        ingredienteDao.Crear(ingrediente);
    }

    public List<Ingrediente> obtenerTodos() {
        return ingredienteDao.ListarTodo();
    }

    public void prepararIngrediente(Preparables ingrediente) {
        if (ingrediente != null) {
            ingrediente.preparar();
        } else {
            System.out.println("Error: El ingrediente a preparar es nulo");
        }
    }
}