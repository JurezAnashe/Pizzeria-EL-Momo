package src.model;

public class Carne extends Ingrediente implements Preparables {
    private String tipoCarne;
    private double porcentajeGrasa;

    public Carne(String tipoCarne, double porcentajeGrasa, int id, String nombre, int stockDisponible, double costo) {
        super(id, nombre, stockDisponible, costo);
        this.tipoCarne = tipoCarne;
        this.porcentajeGrasa = porcentajeGrasa;
    }

    public String getTipoCarne() {
        return tipoCarne;
    }

    public double isPorcentajeGrasa() {
        return porcentajeGrasa;
    }

    @Override
    public String toString() {
        return super.toString() + " Tipo de carne: " + tipoCarne + " Porcentaje de grasa: " + porcentajeGrasa;
    }

    @Override
    public void preparar() {
        System.out.println("Cortando la carne " + getNombre() + " y poniendola en la pizza");
    }
}
