package cat.inspladelestany.facturacio.model;

public class Producte {
    private long id;
    private String nom;
    private double preu;
    private int estoc;

    public Producte() {
    }

    public Producte(String nom, double preu, int estoc) {
        this.nom = nom;
        this.preu = preu;
        this.estoc = estoc;
    }

    public Producte(long id, String nom, double preu, int estoc) {
        this.id = id;
        this.nom = nom;
        this.preu = preu;
        this.estoc = estoc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPreu() {
        return preu;
    }

    public void setPreu(double preu) {
        this.preu = preu;
    }

    public int getEstoc() {
        return estoc;
    }

    public void setEstoc(int estoc) {
        this.estoc = estoc;
    }

    @Override
    public String toString() {
        return id + " - " + nom + " (" + preu + " €)";
    }
}
