package cat.inspladelestany.facturacio.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class SchemaInitializer {
    private static final String SCHEMA_RESOURCE = "/db/schema.sql";

    private SchemaInitializer() {
        // Classe d'utilitats: no s'ha d'instanciar.
    }

    public static void initialize() {
        String schema = readResource(SCHEMA_RESOURCE);
        String[] statements = schema.split(";");

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {

            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inicialitzant l'esquema de la base de dades", e);
        }
    }

    private static String readResource(String resource) {
        try (InputStream inputStream = SchemaInitializer.class.getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("No s'ha trobat el recurs: " + resource);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException("No s'ha pogut llegir el fitxer schema.sql", e);
        }
    }
}
