package cat.inspladelestany.facturacio.dao;

import cat.inspladelestany.facturacio.model.Factura;
import cat.inspladelestany.facturacio.model.LiniaFactura;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementació JDBC del DAO de factures.
 * Aquest DAO és una mica més avançat perquè treballa amb més d'una taula.
 */
public class JdbcFacturaDAO implements FacturaDAO {

    @Override
    public Factura crearFactura(Factura factura) throws SQLException {
        // TODO 6: inserir una factura a la taula factura.
        // SQL: INSERT INTO factura(client_id, data, estat) VALUES (?, ?, ?)
        // Recorda recuperar la clau generada i assignar-la a factura.setId(...)
        throw new UnsupportedOperationException("TODO: implementar crearFactura(Factura)");
    }

    @Override
    public LiniaFactura afegirLinia(LiniaFactura linia) throws SQLException {
        // TODO 7: inserir una línia a la taula linia_factura.
        // SQL: INSERT INTO linia_factura(factura_id, producte_id, quantitat, preu_unitari) VALUES (?, ?, ?, ?)
        throw new UnsupportedOperationException("TODO: implementar afegirLinia(LiniaFactura)");
    }

    @Override
    public List<Factura> llistarFacturesClient(long clientId) throws SQLException {
        // TODO 8: llistar les factures d'un client.
        // La consulta pot calcular el total amb SUM(quantitat * preu_unitari).
        // Si encara no vols fer SUM, pots posar total = 0 i fer-ho més endavant.
        throw new UnsupportedOperationException("TODO: implementar llistarFacturesClient(long)");
    }

    @Override
    public List<Factura> llistarTotes() throws SQLException {
        // TODO 9: llistar totes les factures.
        // Ampliació recomanada.
        throw new UnsupportedOperationException("TODO: implementar llistarTotes()");
    }

    @Override
    public double calcularTotalFactura(long facturaId) throws SQLException {
        // TODO 10: calcular el total d'una factura.
        // SQL: SELECT COALESCE(SUM(quantitat * preu_unitari), 0) AS total FROM linia_factura WHERE factura_id = ?
        throw new UnsupportedOperationException("TODO: implementar calcularTotalFactura(long)");
    }

    @Override
    public boolean marcarComPagada(long facturaId) throws SQLException {
        // TODO 11: canviar l'estat d'una factura a PAGADA.
        // SQL: UPDATE factura SET estat = 'PAGADA' WHERE id = ?
        throw new UnsupportedOperationException("TODO: implementar marcarComPagada(long)");
    }

    private Factura mapFactura(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getLong("id"),
                rs.getLong("client_id"),
                rs.getString("data"),
                rs.getString("estat"),
                rs.getDouble("total")
        );
    }
}
