package src.model;

public class Masa extends Ingrediente implements Preparables {
    private int tiempoFermentacion;
    private boolean esIntegral;

    public Masa(int tiempoFermentacion, boolean esIntegral, int id, String nombre, int stockDisponible, double costo) {
        super(id, nombre, stockDisponible, costo);
        this.tiempoFermentacion = tiempoFermentacion;
        this.esIntegral = esIntegral;
    }

    public int getTiempoFermentacion() {
        return tiempoFermentacion;
    }

    public boolean isEsIntegral() {
        return esIntegral;
    }

    @Override
    public String toString() {
        return super.toString() + " Tiempo de fermantacion: " + tiempoFermentacion + " Es integral: " + esIntegral;
    }

    @Override
    public void preparar() {
        System.out.println("Amazando la masa... Dejandola reposar... Estirandola... Poniendola en el molde");
    }
}
