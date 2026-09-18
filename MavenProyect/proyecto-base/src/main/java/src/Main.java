package src;
import src.model.Ingrediente;
import src.Service.IngredienteService;
import src.model.Queso;
import src.model.Preparables;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        IngredienteService ingredienteService = new IngredienteService();

        Queso quesoMuzzarella = new Queso();
        quesoMuzzarella.setId(1);
        quesoMuzzarella.setNombre("Muzzarella");
        quesoMuzzarella.setStockDisponible(50);
        quesoMuzzarella.setCosto(2500.0);

        ingredienteService.prepararIngrediente(quesoMuzzarella);

        System.out.println("Ingrediente Insertado");
        ingredienteService.guardarIngrediente(quesoMuzzarella);

       
        System.out.println("Listando ingredientes");
        List<Ingrediente> lista = ingredienteService.obtenerTodos();
        
    }
}