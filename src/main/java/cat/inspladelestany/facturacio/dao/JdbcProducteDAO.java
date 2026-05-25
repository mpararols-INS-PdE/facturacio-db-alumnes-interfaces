package cat.inspladelestany.facturacio.dao;

import cat.inspladelestany.facturacio.model.Producte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementació JDBC del DAO de productes.
 * Mira JdbcClientDAO.java per veure un exemple complet amb INSERT, SELECT, UPDATE i DELETE.
 */
public class JdbcProducteDAO implements ProducteDAO {

    @Override
    public Producte inserir(Producte producte) throws SQLException {
        // TODO 1: inserir un producte a la taula producte.
        // Recorda recuperar la clau generada i assignar-la amb producte.setId(...)
        throw new UnsupportedOperationException("TODO: implementar inserir(Producte)");
    }

    @Override
    public Producte buscarPerId(long id) throws SQLException {
        // TODO 2: buscar un producte per id.
        // Si no existeix, retorna null.
        throw new UnsupportedOperationException("TODO: implementar buscarPerId(long)");
    }

    @Override
    public List<Producte> llistarTots() throws SQLException {
        // TODO 3: retornar tots els productes ordenats pel nom.
        throw new UnsupportedOperationException("TODO: implementar llistarTots()");
    }

    @Override
    public boolean actualitzar(Producte producte) throws SQLException {
        // TODO 4: actualitzar nom, preu i estoc d'un producte existent.
        // Retorna true si s'ha modificat alguna fila.
        throw new UnsupportedOperationException("TODO: implementar actualitzar(Producte)");
    }

    @Override
    public boolean eliminar(long id) throws SQLException {
        // TODO 5: eliminar un producte pel seu id.
        // Retorna true si s'ha eliminat alguna fila.
        throw new UnsupportedOperationException("TODO: implementar eliminar(long)");
    }

    private Producte mapProducte(ResultSet rs) throws SQLException {
        return new Producte(
                rs.getLong("id"),
                rs.getString("nom"),
                rs.getDouble("preu"),
                rs.getInt("estoc")
        );
    }
}
