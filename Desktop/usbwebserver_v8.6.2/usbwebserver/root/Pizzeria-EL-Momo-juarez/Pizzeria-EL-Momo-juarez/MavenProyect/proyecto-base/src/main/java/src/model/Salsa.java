package src.model;

public class Salsa extends Ingrediente implements Preparables {
    private int intensidadSabor;

    public Salsa(int intensidadSabor, int id, String nombre, int stockDisponible, double costo) {
        super(id, nombre, stockDisponible, costo);
        this.intensidadSabor = intensidadSabor;
    }

    public int getIntensidadSabor() {
        return intensidadSabor;
    }

    @Override
    public String toString() {
        return super.toString() + " Intensidad de sabor: " + intensidadSabor;
    }

    @Override
    public void preparar() {
        System.out.println("Revolviendo la salsa y poniendola en la pizza");
    }
}
