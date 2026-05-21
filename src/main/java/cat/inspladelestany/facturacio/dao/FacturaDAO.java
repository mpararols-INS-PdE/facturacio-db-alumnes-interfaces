package cat.inspladelestany.facturacio.dao;

import cat.inspladelestany.facturacio.model.Factura;
import cat.inspladelestany.facturacio.model.LiniaFactura;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfície DAO per treballar amb factures i línies de factura.
 */
public interface FacturaDAO {
    Factura crearFactura(Factura factura) throws SQLException;
    LiniaFactura afegirLinia(LiniaFactura linia) throws SQLException;
    List<Factura> llistarFacturesClient(long clientId) throws SQLException;
    List<Factura> llistarTotes() throws SQLException;
    double calcularTotalFactura(long facturaId) throws SQLException;
    boolean marcarComPagada(long facturaId) throws SQLException;
}
