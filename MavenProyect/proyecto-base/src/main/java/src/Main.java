package src;

import src.Service.EmpleadoService;

public class Main {

    public static void main(String[] args) throws Exception {

        EmpleadoService service = new EmpleadoService();

        service.Registrar("Lucas", "Juarez", 11111111, "Analista", 50000, true);
        service.Registrar("Santiago", "Rodriguez", 22222222, "Desarrollador", 60000, true);
        service.Registrar("Chowey", "Morrell", 33333333, "Desarrollador", 55000, false);
        service.Registrar("Lisandro", "Sadañoski", 44444444, "Gerente", 80000, true);
        service.Registrar("Junior", "Lavado", 55555555, "Desarrollador", 70000, true);
        service.Registrar("Diego", "Rondal", 66666666, "Soporte", 45000, true);
        service.Registrar("Sophia", "Uribe", 77777777, "Marketing", 55000, true);
        service.Registrar("Juan", "Suarez", 88888888, "Recursos Humanos", 65000, true);
        service.Registrar("Octavio", "Pagni", 99999999, "Ventas", 60000, true);
        service.Registrar("Santiago", "Molina", 10101010, "Finanzas", 75000, false);

        System.out.println("Lista de empleados:");
        service.ListarTodo();

    }
}