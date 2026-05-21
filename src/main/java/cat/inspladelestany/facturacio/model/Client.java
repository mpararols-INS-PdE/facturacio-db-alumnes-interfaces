package cat.inspladelestany.facturacio.model;

public class Client {
    private long id;
    private String nom;
    private String nif;
    private String email;

    public Client() {
    }

    public Client(String nom, String nif, String email) {
        this.nom = nom;
        this.nif = nif;
        this.email = email;
    }

    public Client(long id, String nom, String nif, String email) {
        this.id = id;
        this.nom = nom;
        this.nif = nif;
        this.email = email;
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

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return id + " - " + nom + " (" + nif + ")";
    }
}
