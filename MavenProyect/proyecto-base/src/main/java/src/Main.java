package src;

import src.model.Ingrediente;
import src.model.Pizza;
import src.Service.IngredienteService;
import src.Service.PizzaService;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IngredienteService ingredienteService = new IngredienteService();
        PizzaService pizzaService = new PizzaService(ingredienteService);

        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\nPizzeria el momo");
            System.out.println("1. Ver ingredientes y stock disponible");
            System.out.println("2. Armar y pedir pizza");
            System.out.println("3. Salir");
            System.out.print("Elija una opcion: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
            } else {
                System.out.println("Ingresa un numero valido");
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("\nIngredientes:");
                    List<Ingrediente> lista = ingredienteService.obtenerTodos();
                    for (Ingrediente ing : lista) {
                        System.out.println(" ID: " + ing.getId() + " Nombre: " + ing.getNombre() +
                                " Stock: " + ing.getStockDisponible() + " Sale: $" + ing.getCosto());
                    }
                    break;

                case 2:
                    System.out.println("\nArmar el pedido de la pizza");

                    List<Ingrediente> disponibles = ingredienteService.obtenerTodos();
                    System.out.println("Ingredientes disponibles:");
                    for (Ingrediente ing : disponibles) {
                        System.out.println(
                                " ID: " + ing.getId() + " Nombre: " + ing.getNombre() + " Sale: $" + ing.getCosto());
                    }

                    Pizza miPizza = new Pizza();
                    boolean agregando = true;

                    while (agregando) {
                        System.out.print("\nIngresa el ID del ingrediente o 0 para terminar: ");
                        int eligio = scanner.nextInt();

                        if (eligio == 0) {
                            agregando = false;
                        } else {
                            Ingrediente seleccionado = null;
                            for (Ingrediente ing : disponibles) {
                                if (ing.getId() == eligio) {
                                    seleccionado = ing;
                                    break;
                                }
                            }

                            if (seleccionado != null) {
                                miPizza.agregarIngrediente(seleccionado);
                                System.out.println("Agregado: " + seleccionado.getNombre());
                            } else {
                                System.out.println("Error: No existe un ingrediente con ese ID");
                            }
                        }
                    }

                    miPizza.preparar();

                    try {
                        pizzaService.hacerpizza(miPizza);
                        System.out.println("Stock actualizado");
                    } catch (Exception e) {
                        System.out.println("Error al hacer el pedido: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("\nSaliendo de la pizzeria");
                    break;

                default:
                    System.out.println("Intenta de nuevo");
            }

        } while (opcion != 3);

        scanner.close();
    }
}