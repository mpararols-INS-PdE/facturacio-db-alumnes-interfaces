# Facturació DB - projecte base per a l'alumnat

Projecte per practicar **DAO + JDBC + SQLite**. La interfície **JavaFX ja està feta**: la vostra feina és implementar els mètodes dels DAOs perquè l’aplicació guardi i llegeixi dades de la base de dades.

## Requisits

- JDK 17 o superior
- Maven
- IDE (IntelliJ / VS Code / Eclipse) o terminal

## Execució

Interfície gràfica JavaFX:

```bash
mvn clean javafx:run
```

Si l’executeu des de l’IDE, feu servir la classe:

```text
cat.inspladelestany.facturacio.app.Launcher
```

No executeu directament `FacturacioApp`, perquè alguns IDE poden mostrar l’error `JavaFX runtime components are missing`.

Prova de consola (només clients, per veure JDBC sense JavaFX):

```bash
mvn clean compile exec:java
```

## Estructura del projecte (packages)

```text
src/main/java/cat/inspladelestany/facturacio/
  app/         → JavaFX (UI) i una prova per consola
  dao/         → interfícies DAO i implementacions JDBC (SQL)
  model/       → classes del domini (Client, Producte, Factura, LiniaFactura)
  persistence/ → connexió a SQLite i inicialització de l’esquema

src/main/resources/db/schema.sql → creació de taules (SQLite)
data/facturacio.db              → base de dades generada (fitxer local)
```

### Què és Maven (i per què el fem servir)

Maven és una eina de construcció i gestió de projectes Java. En aquest projecte ens serveix per:

- **Gestionar dependències**: per exemple `javafx-controls` i el driver `sqlite-jdbc` es descarreguen automàticament.
- **Compilar i executar**: permet compilar el codi i executar l’aplicació amb comandes com `mvn clean javafx:run`.
- **Estructura estàndard de carpetes**: Maven espera codi a `src/main/java` i recursos a `src/main/resources`.

El fitxer important és `pom.xml`, que defineix:

- el nom del projecte (`groupId`, `artifactId`, `version`)
- la versió de Java (`maven.compiler.release`)
- les dependències
- els plugins (en aquest cas, el de JavaFX i el d’execució per consola)

## Base de dades

- El fitxer es crea automàticament a `data/facturacio.db`.
- Si voleu començar de zero: tanqueu el programa i elimineu `data/facturacio.db`.
- Esquema: `src/main/resources/db/schema.sql`.

Taules:

```text
client
producte
factura        (té client_id)
linia_factura  (té factura_id i producte_id)
```

## Patró DAO (idea clau)

Cada “repositori de dades” té:

```text
ClientDAO.java        → interfície (què ha de poder fer)
JdbcClientDAO.java    → implementació JDBC (com ho fa: SQL + ResultSet)

ProducteDAO.java      → interfície
JdbcProducteDAO.java  → implementació JDBC (a completar)

FacturaDAO.java       → interfície
JdbcFacturaDAO.java   → implementació JDBC (a completar)
```

La UI treballa amb `ClientDAO / ProducteDAO / FacturaDAO`, de manera que no depèn de SQLite ni del SQL.

# Tasques a realitzar
## Implementar els DAO pertinents

**Obligatori (core):**

```text
src/main/java/cat/inspladelestany/facturacio/dao/JdbcProducteDAO.java
src/main/java/cat/inspladelestany/facturacio/dao/JdbcFacturaDAO.java
```

**Com a referència (ja està fet, serveix d’exemple):**

```text
src/main/java/cat/inspladelestany/facturacio/dao/JdbcClientDAO.java
```

## Tasques per nivells (A / B / C)

### Nivell A (mínim per aprovar)

Objectiu: entendre el flux **Connection → PreparedStatement → ResultSet** i implementar CRUD bàsic.

- Llegir i entendre `src/main/java/cat/inspladelestany/facturacio/dao/ClientDAO.java` i `src/main/java/cat/inspladelestany/facturacio/dao/JdbcClientDAO.java`.
- Implementar `JdbcProducteDAO` TODO 1–5:
  - inserir, buscar per id, llistar, actualitzar, eliminar.
- Implementar `JdbcFacturaDAO` (mínim funcional) TODO 6–8 i 11:
  - crear factura (`crearFactura`)
  - afegir línia (`afegirLinia`)
  - veure factures d’un client (`llistarFacturesClient`) (el camp `total` pot ser 0 al principi)
  - marcar pagada (`marcarComPagada`)

Criteri d’acceptació: les pestanyes **Productes** i **Factures** han de funcionar sense excepcions i les dades han de persistir a SQLite.

### Nivell B (millora recomanada)

Objectiu: fer consultes una mica més completes i donar una experiència d’usuari més correcta.

Què vol dir exactament:

- Completar `JdbcFacturaDAO` TODO 10: `calcularTotalFactura(long facturaId)`
  - Ha de fer un `SELECT` que sumi les línies d’una factura: `SUM(quantitat * preu_unitari)`
  - Si la factura no té línies, ha de retornar `0` (per això s’usa `COALESCE`)
  - Criteri d’acceptació: després d’afegir línies a una factura, el total calculat coincideix amb la suma dels subtotals

- Millorar `llistarFacturesClient(long clientId)` perquè ompli el camp `total`
  - Opció 1 (recomanada): que la consulta ja retorni el total (amb `LEFT JOIN` + `SUM` + `GROUP BY`)
  - Opció 2 (més simple d’entendre): llistar factures i, per cada factura, cridar `calcularTotalFactura(id)` per posar `factura.setTotal(...)`
  - Criteri d’acceptació: a la taula de factures, el camp “Total” deixa de sortir a 0 quan hi ha línies

- Gestionar errors típics de BD amb missatges comprensibles
  - La UI ja mostra finestres d’error; l’objectiu és que el text sigui clar per a l’usuari
  - Exemples d’errors típics:
    - NIF duplicat (constraint UNIQUE) → mostrar “Ja existeix un client amb aquest NIF.”
    - Producte amb nom duplicat (constraint UNIQUE) → mostrar “Ja existeix un producte amb aquest nom.”
    - IDs inexistents (FOREIGN KEY) → mostrar “Aquest client/producte/factura no existeix.”
  - Criteri d’acceptació: quan passa un d’aquests casos, l’app no peta i el missatge ajuda a entendre què cal corregir

### Nivell C (ampliacions)

Objectiu: afegir funcionalitats “de producte” sense canviar el focus (DAO/JDBC).


- Validació: comprovar que l’email és vàlid abans de guardar/actualitzar un client.
- Importació CSV: carregar `clients.csv` i/o `productes.csv` i inserir-los a la BD (amb validació i missatges d’error).
- Canviar el SGBD: en comptes de SQLite local, connectar-se a un SGBD “real” (p. ex. MySQL/MariaDB o PostgreSQL) que ja hagueu treballat al mòdul de BD.
- Consultes extra:
  - implementar `llistarTotes()` de `FacturaDAO`
  - cerca de productes per nom (nou mètode a `ProducteDAO` + implementació JDBC)
- Regles de negoci simples:
  - evitar eliminar un producte si apareix en alguna `linia_factura` (missatge clar)
- Opcional avançat, només si us sobra temps
  - Segona implementació de `ClientDAO`:
    - un “decorator” amb memòria cau (cache) de `llistarTots()` per practicar el patró i no repetir consultes
  - Modificar l'interfície amb SceneBuilder

#### Notes per l’opció “Canviar el SGBD”

Idees (sense implementar res aquí):

- Afegir la dependència JDBC del SGBD al `pom.xml` (p. ex. MySQL, Oracle o PostgreSQL).
- Canviar l’URL de connexió a `src/main/java/cat/inspladelestany/facturacio/persistence/Database.java` per apuntar al servidor.
- Adaptar `src/main/resources/db/schema.sql` (tipus de dades i auto-increment) perquè sigui compatible amb el SGBD escollit.
- Verificar que les consultes SQL són compatibles (SQLite té algunes diferències).

## Guies (lectura recomanada)

- Guia detallada JDBC (ClientDAO): `docs/guia-jdbc-clientdao.md`
