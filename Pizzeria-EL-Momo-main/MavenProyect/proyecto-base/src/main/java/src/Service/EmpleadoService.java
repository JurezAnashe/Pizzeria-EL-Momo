package src.Service;

import src.Dao.IngredienteDao;
import src.Dao.DaoImpl.IngredienteDaoImpl;
import src.Excepciones.CargoInvalidoException;
import src.Excepciones.DniDuplicadoException;
import src.Excepciones.DniInvalidoException;
import src.Excepciones.SalarioInvalidoException;
import src.model.Ingrediente;

import java.util.Set;

public class EmpleadoService {

    private final IngredienteDao EmpleadoDao;

    public EmpleadoService() {
        this.EmpleadoDao = new IngredienteDaoImpl();
    }

    private static final Set<String> CARGOS_VALIDOS = Set.of("Analista", "Desarrollador", "Gerente", "Soporte");

    public void Registrar(String nombre, String apellido, int dni, String cargo, double salario, boolean activo)
            throws DniInvalidoException, DniDuplicadoException, SalarioInvalidoException, CargoInvalidoException {
        validarDatos(dni, cargo, salario);

        if (EmpleadoDao.BuscarPorId(String.valueOf(dni)) != null) {
            throw new DniDuplicadoException("El DNI ya existe: " + dni);
        }

        Ingrediente empleado = new Ingrediente(nombre, apellido, String.valueOf(dni), cargo, salario, activo);
        EmpleadoDao.Crear(empleado);
    }

    public void ListarTodo() {
        EmpleadoDao.ListarTodo();
    }

    private void validarDatos(int dni, String cargo, double salario)
            throws DniInvalidoException, SalarioInvalidoException, CargoInvalidoException {
        int digitos = String.valueOf(Math.abs(dni)).length();
        if (dni <= 0 || digitos < 7 || digitos > 8) {
            throw new DniInvalidoException("El DNI debe tener 7 u 8 digitos");
        }
        if (salario <= 0) {
            throw new SalarioInvalidoException("El salario debe ser mayor que cero");
        }
        if (!CARGOS_VALIDOS.contains(cargo)) {
            throw new CargoInvalidoException("Cargo no permitido: " + cargo);
        }
    }

}
