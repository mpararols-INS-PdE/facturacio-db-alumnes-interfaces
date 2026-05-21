package cat.inspladelestany.facturacio.model;

public class LiniaFactura {
    private long id;
    private long facturaId;
    private long producteId;
    private int quantitat;
    private double preuUnitari;

    public LiniaFactura() {
    }

    public LiniaFactura(long facturaId, long producteId, int quantitat, double preuUnitari) {
        this.facturaId = facturaId;
        this.producteId = producteId;
        this.quantitat = quantitat;
        this.preuUnitari = preuUnitari;
    }

    public LiniaFactura(long id, long facturaId, long producteId, int quantitat, double preuUnitari) {
        this.id = id;
        this.facturaId = facturaId;
        this.producteId = producteId;
        this.quantitat = quantitat;
        this.preuUnitari = preuUnitari;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(long facturaId) {
        this.facturaId = facturaId;
    }

    public long getProducteId() {
        return producteId;
    }

    public void setProducteId(long producteId) {
        this.producteId = producteId;
    }

    public int getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(int quantitat) {
        this.quantitat = quantitat;
    }

    public double getPreuUnitari() {
        return preuUnitari;
    }

    public void setPreuUnitari(double preuUnitari) {
        this.preuUnitari = preuUnitari;
    }

    public double getSubtotal() {
        return quantitat * preuUnitari;
    }
}
