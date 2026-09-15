package src.model;

public class Dinero {
    private double dineroActual;

    public Dinero(double dineroActual) {
        this.dineroActual = dineroActual;
    }

    public double getDineroActual() {
        return dineroActual;
    }

    public void getDineroActual(double dineroActual) {
        this.dineroActual = dineroActual;
    }

    @Override
    public String toString() {
        return dineroActual + " ";
    }
}