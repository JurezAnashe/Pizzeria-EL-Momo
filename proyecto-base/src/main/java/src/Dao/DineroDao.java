package src.Dao;

import src.model.Dinero;

public interface DineroDao {
    void guardarOActualizar(Dinero caja);
    Dinero obtenerDinero();
}