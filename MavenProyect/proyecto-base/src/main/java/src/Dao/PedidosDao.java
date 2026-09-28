package src.Dao;

import src.model.Pedido;

public interface PedidosDao {
    void guardar(Pedido pedido);
    void actualizarEstado(Pedido pedido);
}