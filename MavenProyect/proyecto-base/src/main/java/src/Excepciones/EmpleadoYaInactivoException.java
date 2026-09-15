package src.Excepciones;

public class EmpleadoYaInactivoException extends Exception {
    public EmpleadoYaInactivoException(String message) {
        super(message);
    }
}