package cat.inspladelestany.facturacio.dao;

import cat.inspladelestany.facturacio.model.Producte;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfície DAO per treballar amb productes.
 * L'alumnat ha d'implementar aquestes operacions a JdbcProducteDAO.
 */
public interface ProducteDAO {
    Producte inserir(Producte producte) throws SQLException;
    Producte buscarPerId(long id) throws SQLException;
    List<Producte> llistarTots() throws SQLException;
    boolean actualitzar(Producte producte) throws SQLException;
    boolean eliminar(long id) throws SQLException;
}
