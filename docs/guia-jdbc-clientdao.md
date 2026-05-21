# Guia JDBC: entendre `JdbcClientDAO` pas a pas

Aquest document explica amb detall com funciona la implementació JDBC del DAO de clients.

Objectiu: que puguis llegir `JdbcClientDAO` i entendre què fa cada peça quan treballes amb:

- `Connection`
- `PreparedStatement`
- `ResultSet`
- `executeUpdate()` / `executeQuery()`
- claus generades (`Statement.RETURN_GENERATED_KEYS`)
- `try-with-resources`

Fitxers implicats:

- `src/main/java/cat/inspladelestany/facturacio/dao/ClientDAO.java`
- `src/main/java/cat/inspladelestany/facturacio/dao/JdbcClientDAO.java`
- `src/main/java/cat/inspladelestany/facturacio/persistence/Database.java`
- `src/main/resources/db/schema.sql`

## 1) Què és un DAO?

Un DAO (Data Access Object) és una classe que encapsula l’accés a dades. En lloc que la UI faci SQL, la UI crida mètodes del DAO:

- “inserir un client”
- “buscar un client”
- “llistar tots els clients”
- “actualitzar”
- “eliminar”

Això separa:

- **domini** (`Client`) → dades i lògica de negoci (si n’hi ha)
- **persistència** (DAO JDBC) → SQL + JDBC

## 2) L’esquema de la taula `client`

A `src/main/resources/db/schema.sql` tens:

```sql
CREATE TABLE IF NOT EXISTS client (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    nif TEXT NOT NULL UNIQUE,
    email TEXT
);
```

Idees clau:

- `id` s’autogenera (AUTOINCREMENT) quan fas `INSERT`.
- `nom` i `nif` són obligatoris.
- `nif` és únic: si intentes inserir un nif repetit, SQLite donarà error.

## 3) La connexió a la BD: `Database.getConnection()`

El mètode `src/main/java/.../persistence/Database.java` fa:

1. crea la carpeta `data/` si no existeix
2. obre una connexió JDBC a `jdbc:sqlite:data/facturacio.db`
3. activa claus foranes amb `PRAGMA foreign_keys = ON`

La idea important és que **cada mètode del DAO obre la seva connexió** amb:

```java
try (Connection connection = Database.getConnection(); ...) {
    ...
}
```

I això implica:

- si el bloc acaba (o hi ha excepció), la connexió es tanca sola (gràcies al `try-with-resources`)
- no cal “connection.close()” manual

## 4) `PreparedStatement`: per què no concatenem Strings?

Exemple d’`INSERT` a `JdbcClientDAO`:

```java
String sql = "INSERT INTO client(nom, nif, email) VALUES (?, ?, ?)";
```

Els `?` són paràmetres.

Després s’omplen amb:

```java
ps.setString(1, client.getNom());
ps.setString(2, client.getNif());
ps.setString(3, client.getEmail());
```

Beneficis:

- evita errors de cometes i formats
- evita SQL injection (no és el focus aquí, però és un avantatge real)
- fa el codi més net i consistent

## 5) `executeUpdate()` vs `executeQuery()`

- `executeUpdate()` s’utilitza quan la consulta **modifica** dades:
  - `INSERT`, `UPDATE`, `DELETE`
  - retorna el nombre de files afectades

- `executeQuery()` s’utilitza quan la consulta **retorna resultats**:
  - `SELECT`
  - retorna un `ResultSet` que has de recórrer

## 6) Inserir i recuperar la clau generada (`getGeneratedKeys`)

Quan fem un `INSERT`, la BD genera un `id`.

Per recuperar-lo:

1) el `PreparedStatement` s’ha de crear amb:

```java
connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
```

2) després de l’`executeUpdate()`, demanes les claus:

```java
try (ResultSet rs = ps.getGeneratedKeys()) {
    if (rs.next()) {
        client.setId(rs.getLong(1));
    }
}
```

Interpretació:

- `rs.next()` mou el cursor a la primera fila de resultats
- `getLong(1)` agafa la primera columna (normalment l’`id` generat)
- s’assigna l’id a l’objecte Java per poder-lo reutilitzar després

## 7) Buscar per id (`buscarPerId`)

SQL:

```sql
SELECT id, nom, nif, email FROM client WHERE id = ?
```

Flux JDBC:

1. crear connexió
2. preparar consulta
3. posar paràmetre `id`
4. executar `executeQuery()`
5. si hi ha resultat, mapar-lo a `Client`

En JDBC:

- `ResultSet` comença “abans” de la primera fila.
- si `rs.next()` és `false` → no hi ha cap fila → retornes `null`.

## 8) Llistar tots (`llistarTots`)

SQL:

```sql
SELECT id, nom, nif, email FROM client ORDER BY nom
```

Aquí el `ResultSet` pot tenir moltes files, així que:

```java
while (rs.next()) {
    clients.add(mapClient(rs));
}
```

## 9) `mapClient(ResultSet)`: separar el “mapeig” del SQL

`mapClient` és un patró molt útil:

- el codi SQL decideix quines columnes retorna
- `mapClient` converteix una fila del `ResultSet` en un objecte `Client`

Exemple:

```java
return new Client(
    rs.getLong("id"),
    rs.getString("nom"),
    rs.getString("nif"),
    rs.getString("email")
);
```

Avantatges:

- el codi queda més curt i més llegible
- quan implementis `JdbcProducteDAO` i `JdbcFacturaDAO`, repetiràs exactament aquesta idea (`mapProducte`, `mapFactura`, ...)

## 10) Actualitzar i eliminar: interpretar el retorn de `executeUpdate()`

Tant a `actualitzar` com a `eliminar`, el DAO retorna `boolean`.

Per què?

- `executeUpdate()` retorna `0` si no ha modificat cap fila (p. ex. no existeix aquell `id`)
- retorna `> 0` si ha modificat alguna cosa

Per això fan:

```java
return ps.executeUpdate() > 0;
```

## 11) Errors típics i què volen dir

Alguns errors que poden aparèixer (com a missatge o excepció SQL):

- `UNIQUE constraint failed: client.nif` → estàs intentant inserir/actualitzar un NIF repetit.
- `NOT NULL constraint failed: client.nom` → estàs enviant un camp obligatori buit o null.
- `FOREIGN KEY constraint failed` → estàs inserint una factura o línia amb ids que no existeixen (a altres taules).

El projecte ja captura excepcions a la UI i mostra un missatge.

## 12) Com traslladar això a `JdbcProducteDAO` i `JdbcFacturaDAO`

Quan implementis els TODOs:

- copia el patró general de `JdbcClientDAO`
- defineix el SQL amb `?`
- omple paràmetres amb `ps.setX(...)`
- per `SELECT`: usa `executeQuery()` i `ResultSet`
- per `INSERT/UPDATE/DELETE`: usa `executeUpdate()`
- crea un mètode `mapX(ResultSet)` per no repetir codi

Per practicar: intenta implementar primer un mètode molt simple (p. ex. `buscarPerId`) i després l’`INSERT` amb claus generades.

