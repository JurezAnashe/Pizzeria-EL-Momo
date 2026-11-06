package src.model;

public class Queso extends Ingrediente implements Preparables {
    private int diasMaduracion;
    private boolean artesanal;

    public Queso() {
        super();
    }

    public Queso(int diasMaduracion, boolean artesanal, int id, String nombre, int stockDisponible, double costo) {
        super(id, nombre, stockDisponible, costo);
        this.diasMaduracion = diasMaduracion;
        this.artesanal = artesanal;
    }

    public int getDiasMaduracion() {
        return diasMaduracion;
    }

    public boolean isArtesanal() {
        return artesanal;
    }

    @Override
    public String toString() {
        return super.toString() + " Dias de maduracion: " + diasMaduracion + " Artesanal: " + artesanal;
    }

    @Override
    public void preparar() {
        System.out.println("Cortando el queso " + getNombre() + " y poniendolo en la pizza");
    }
}
