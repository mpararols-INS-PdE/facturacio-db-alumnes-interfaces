package cat.inspladelestany.facturacio.app;

import cat.inspladelestany.facturacio.dao.ClientDAO;
import cat.inspladelestany.facturacio.dao.JdbcClientDAO;
import cat.inspladelestany.facturacio.model.Client;
import cat.inspladelestany.facturacio.persistence.SchemaInitializer;

import java.sql.SQLException;

public class PrincipalConsola {
    public static void main(String[] args) {
        SchemaInitializer.initialize();
        ClientDAO clientDAO = new JdbcClientDAO();

        try {
            Client client = new Client("Client de prova", "00000000A", "prova@example.com");
            clientDAO.inserir(client);

            System.out.println("Clients guardats a la base de dades:");
            for (Client c : clientDAO.llistarTots()) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            System.err.println("Error treballant amb la base de dades: " + e.getMessage());
        }
    }
}
