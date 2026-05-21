package cat.inspladelestany.facturacio.model;

public class Factura {
    private long id;
    private long clientId;
    private String data;
    private String estat;
    private double total;

    public Factura() {
    }

    public Factura(long clientId, String data, String estat) {
        this.clientId = clientId;
        this.data = data;
        this.estat = estat;
    }

    public Factura(long id, long clientId, String data, String estat, double total) {
        this.id = id;
        this.clientId = clientId;
        this.data = data;
        this.estat = estat;
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Factura " + id + " - client " + clientId + " - " + data + " - " + estat + " - total: " + total;
    }
}
