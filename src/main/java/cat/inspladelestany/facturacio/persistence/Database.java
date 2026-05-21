package cat.inspladelestany.facturacio.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_FOLDER = "data";
    private static final String DB_FILE = "facturacio.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FOLDER + "/" + DB_FILE;

    private Database() {
        // Classe d'utilitats: no s'ha d'instanciar.
    }

    public static Connection getConnection() throws SQLException {
        crearCarpetaDadesSiCal();
        Connection connection = DriverManager.getConnection(DB_URL);
        activarClausForanes(connection);
        return connection;
    }

    private static void crearCarpetaDadesSiCal() {
        try {
            Files.createDirectories(Path.of(DB_FOLDER));
        } catch (IOException e) {
            throw new RuntimeException("No s'ha pogut crear la carpeta de dades", e);
        }
    }

    private static void activarClausForanes(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }
}
