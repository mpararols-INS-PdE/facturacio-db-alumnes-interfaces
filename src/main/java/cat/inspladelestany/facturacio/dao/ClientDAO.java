package cat.inspladelestany.facturacio.dao;

import cat.inspladelestany.facturacio.model.Client;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfície DAO per treballar amb clients.
 * Defineix quines operacions ha de tenir qualsevol classe que vulgui guardar clients.
 */
public interface ClientDAO {
    Client inserir(Client client) throws SQLException;
    Client buscarPerId(long id) throws SQLException;
    Client buscarPerNif(String nif) throws SQLException;
    List<Client> llistarTots() throws SQLException;
    boolean actualitzar(Client client) throws SQLException;
    boolean eliminar(long id) throws SQLException;
}
