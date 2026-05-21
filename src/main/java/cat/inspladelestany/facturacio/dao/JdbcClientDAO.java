package cat.inspladelestany.facturacio.dao;

import cat.inspladelestany.facturacio.model.Client;
import cat.inspladelestany.facturacio.persistence.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementació JDBC del DAO de clients.
 * Aquesta classe està completa i serveix com a exemple per implementar JdbcProducteDAO i JdbcFacturaDAO.
 */
public class JdbcClientDAO implements ClientDAO {

    @Override
    public Client inserir(Client client) throws SQLException {
        // INSERT amb paràmetres: els ? s'omplen amb setString/setLong/etc.
        String sql = "INSERT INTO client(nom, nif, email) VALUES (?, ?, ?)";

        // try-with-resources: tanca automàticament Connection i PreparedStatement en sortir del bloc
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // En un PreparedStatement, els índexs de paràmetres comencen a 1
            ps.setString(1, client.getNom());
            ps.setString(2, client.getNif());
            ps.setString(3, client.getEmail());
            // executeUpdate() s'utilitza per INSERT/UPDATE/DELETE (retorna files afectades)
            ps.executeUpdate();

            // Recuperar la clau generada (id AUTOINCREMENT) per guardar-la a l'objecte Java
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    client.setId(rs.getLong(1));
                }
            }
        }

        return client;
    }

    @Override
    public Client buscarPerId(long id) throws SQLException {
        // SELECT per id amb PreparedStatement: evita concatenar SQL i permet passar paràmetres de forma segura
        String sql = "SELECT id, nom, nif, email FROM client WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                // executeQuery() retorna un ResultSet (0 o més files). rs.next() mou el cursor a la següent fila.
                if (rs.next()) {
                    return mapClient(rs);
                }
            }
        }

        return null;
    }

    @Override
    public Client buscarPerNif(String nif) throws SQLException {
        String sql = "SELECT id, nom, nif, email FROM client WHERE nif = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nif);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapClient(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Client> llistarTots() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, nif, email FROM client ORDER BY nom";

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Quan hi ha moltes files, recorrem el ResultSet amb while (rs.next())
            while (rs.next()) {
                clients.add(mapClient(rs));
            }
        }

        return clients;
    }

    @Override
    public boolean actualitzar(Client client) throws SQLException {
        String sql = "UPDATE client SET nom = ?, nif = ?, email = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, client.getNom());
            ps.setString(2, client.getNif());
            ps.setString(3, client.getEmail());
            ps.setLong(4, client.getId());

            // executeUpdate() retorna quantes files s'han modificat (0 si no existeix l'id)
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(long id) throws SQLException {
        String sql = "DELETE FROM client WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Client mapClient(ResultSet rs) throws SQLException {
        // Mapatge ResultSet -> objecte de domini (1 fila del ResultSet es converteix en 1 Client)
        // S'accedeix a columnes pel seu nom (tal com surten al SELECT)
        return new Client(
                rs.getLong("id"),
                rs.getString("nom"),
                rs.getString("nif"),
                rs.getString("email")
        );
    }
}
