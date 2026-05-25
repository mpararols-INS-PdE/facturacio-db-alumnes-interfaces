package cat.inspladelestany.facturacio.app;

import cat.inspladelestany.facturacio.dao.ClientDAO;
import cat.inspladelestany.facturacio.dao.JdbcClientDAO;
import cat.inspladelestany.facturacio.dao.FacturaDAO;
import cat.inspladelestany.facturacio.dao.JdbcFacturaDAO;
import cat.inspladelestany.facturacio.dao.ProducteDAO;
import cat.inspladelestany.facturacio.dao.JdbcProducteDAO;
import cat.inspladelestany.facturacio.model.Client;
import cat.inspladelestany.facturacio.model.Factura;
import cat.inspladelestany.facturacio.model.LiniaFactura;
import cat.inspladelestany.facturacio.model.Producte;
import cat.inspladelestany.facturacio.persistence.SchemaInitializer;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FacturacioApp extends Application {
    private final int HIGHT_RESS = 640;
    private final int WIDTH_RESS = 860;

    private final ClientDAO clientDAO = new JdbcClientDAO();
    private final ProducteDAO producteDAO = new JdbcProducteDAO();
    private final FacturaDAO facturaDAO = new JdbcFacturaDAO();

    private TableView<Client> taulaClients;
    private TableView<Producte> taulaProductes;
    private TableView<Factura> taulaFactures;
    private ComboBox<Client> comboClientsFactures;

    @Override
    public void start(Stage stage) {
        SchemaInitializer.initialize();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(crearTabClients());
        tabPane.getTabs().add(crearTabProductes());
        tabPane.getTabs().add(crearTabFactures());

        BorderPane root = new BorderPane(tabPane);
        root.setPadding(new Insets(12));

        Scene scene = new Scene(root, WIDTH_RESS, HIGHT_RESS);
        stage.setTitle("Facturació simple - DAO i JDBC");
        stage.setScene(scene);
        stage.show();

        carregarClients();
        carregarProductes();
    }

    private Tab crearTabClients() {
        TextField nomField = new TextField();
        TextField nifField = new TextField();
        TextField emailField = new TextField();

        GridPane formulari = crearGridFormulari();
        formulari.add(new Label("Nom"), 0, 0);
        formulari.add(nomField, 1, 0);
        formulari.add(new Label("NIF"), 0, 1);
        formulari.add(nifField, 1, 1);
        formulari.add(new Label("Email"), 0, 2);
        formulari.add(emailField, 1, 2);

        Button afegirBtn = new Button("Afegir client");
        Button actualitzarBtn = new Button("Actualitzar seleccionat");
        Button eliminarBtn = new Button("Eliminar seleccionat");
        Button carregarBtn = new Button("Recarregar");

        afegirBtn.setOnAction(event -> executarAccio(() -> {
            validarText(nomField, "El nom és obligatori");
            validarText(nifField, "El NIF és obligatori");
            clientDAO.inserir(new Client(nomField.getText(), nifField.getText(), emailField.getText()));
            netejar(nomField, nifField, emailField);
            carregarClients();
        }));

        actualitzarBtn.setOnAction(event -> executarAccio(() -> {
            Client seleccionat = taulaClients.getSelectionModel().getSelectedItem();
            if (seleccionat == null) {
                mostrarAvis("Selecciona un client de la taula.");
                return;
            }
            validarText(nomField, "El nom és obligatori");
            validarText(nifField, "El NIF és obligatori");
            seleccionat.setNom(nomField.getText());
            seleccionat.setNif(nifField.getText());
            seleccionat.setEmail(emailField.getText());
            clientDAO.actualitzar(seleccionat);
            carregarClients();
        }));

        eliminarBtn.setOnAction(event -> executarAccio(() -> {
            Client seleccionat = taulaClients.getSelectionModel().getSelectedItem();
            if (seleccionat == null) {
                mostrarAvis("Selecciona un client de la taula.");
                return;
            }
            clientDAO.eliminar(seleccionat.getId());
            netejar(nomField, nifField, emailField);
            carregarClients();
        }));

        carregarBtn.setOnAction(event -> carregarClients());

        taulaClients = crearTaulaClients();
        taulaClients.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                nomField.setText(selected.getNom());
                nifField.setText(selected.getNif());
                emailField.setText(selected.getEmail());
            }
        });

        HBox botons = new HBox(8, afegirBtn, actualitzarBtn, eliminarBtn, carregarBtn);
        botons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(12, titol("Clients"), formulari, botons, taulaClients);
        content.setPadding(new Insets(12));
        VBox.setVgrow(taulaClients, Priority.ALWAYS);

        Tab tab = new Tab("Clients", content);
        tab.setClosable(false);
        return tab;
    }

    private Tab crearTabProductes() {
        TextField nomField = new TextField();
        TextField preuField = new TextField();
        TextField estocField = new TextField();

        GridPane formulari = crearGridFormulari();
        formulari.add(new Label("Nom"), 0, 0);
        formulari.add(nomField, 1, 0);
        formulari.add(new Label("Preu"), 0, 1);
        formulari.add(preuField, 1, 1);
        formulari.add(new Label("Estoc"), 0, 2);
        formulari.add(estocField, 1, 2);

        Button afegirBtn = new Button("Afegir producte");
        Button actualitzarBtn = new Button("Actualitzar seleccionat");
        Button eliminarBtn = new Button("Eliminar seleccionat");
        Button carregarBtn = new Button("Recarregar");

        afegirBtn.setOnAction(event -> executarAccio(() -> {
            validarText(nomField, "El nom és obligatori");
            Producte producte = new Producte(
                    nomField.getText(),
                    llegirDouble(preuField, "El preu ha de ser numèric"),
                    llegirEnter(estocField, "L'estoc ha de ser enter")
            );
            producteDAO.inserir(producte);
            netejar(nomField, preuField, estocField);
            carregarProductes();
        }));

        actualitzarBtn.setOnAction(event -> executarAccio(() -> {
            Producte seleccionat = taulaProductes.getSelectionModel().getSelectedItem();
            if (seleccionat == null) {
                mostrarAvis("Selecciona un producte de la taula.");
                return;
            }
            validarText(nomField, "El nom és obligatori");
            seleccionat.setNom(nomField.getText());
            seleccionat.setPreu(llegirDouble(preuField, "El preu ha de ser numèric"));
            seleccionat.setEstoc(llegirEnter(estocField, "L'estoc ha de ser enter"));
            producteDAO.actualitzar(seleccionat);
            carregarProductes();
        }));

        eliminarBtn.setOnAction(event -> executarAccio(() -> {
            Producte seleccionat = taulaProductes.getSelectionModel().getSelectedItem();
            if (seleccionat == null) {
                mostrarAvis("Selecciona un producte de la taula.");
                return;
            }
            producteDAO.eliminar(seleccionat.getId());
            netejar(nomField, preuField, estocField);
            carregarProductes();
        }));

        carregarBtn.setOnAction(event -> carregarProductes());

        taulaProductes = crearTaulaProductes();
        taulaProductes.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                nomField.setText(selected.getNom());
                preuField.setText(String.valueOf(selected.getPreu()));
                estocField.setText(String.valueOf(selected.getEstoc()));
            }
        });

        HBox botons = new HBox(8, afegirBtn, actualitzarBtn, eliminarBtn, carregarBtn);
        botons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(12, titol("Productes"), formulari, botons, taulaProductes);
        content.setPadding(new Insets(12));
        VBox.setVgrow(taulaProductes, Priority.ALWAYS);

        Tab tab = new Tab("Productes", content);
        tab.setClosable(false);
        return tab;
    }

    private Tab crearTabFactures() {
        TextField facturaIdField = new TextField();
        TextField producteIdField = new TextField();
        TextField quantitatField = new TextField();
        TextField preuUnitariField = new TextField();

        comboClientsFactures = new ComboBox<>();
        comboClientsFactures.setMaxWidth(Double.MAX_VALUE);
        comboClientsFactures.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        comboClientsFactures.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });

        facturaIdField.setEditable(false);
        facturaIdField.setPromptText("Es crea automàticament o seleccionant de la taula");

        GridPane formulari = crearGridFormulari();
        formulari.add(new Label("Client"), 0, 0);
        formulari.add(comboClientsFactures, 1, 0);
        formulari.add(new Label("Factura ID"), 0, 1);
        formulari.add(facturaIdField, 1, 1);
        formulari.add(new Label("Producte ID"), 0, 2);
        formulari.add(producteIdField, 1, 2);
        formulari.add(new Label("Quantitat"), 0, 3);
        formulari.add(quantitatField, 1, 3);
        formulari.add(new Label("Preu unitari"), 0, 4);
        formulari.add(preuUnitariField, 1, 4);

        Button crearFacturaBtn = new Button("Crear factura client");
        Button afegirLiniaBtn = new Button("Afegir línia");
        Button veureClientBtn = new Button("Veure factures client");
        Button marcarPagadaBtn = new Button("Marcar factura pagada");

        crearFacturaBtn.setOnAction(event -> executarAccio(() -> {
            long clientId = llegirClientSeleccionat("Selecciona un client.");
            Factura factura = new Factura(clientId, LocalDate.now().toString(), "PENDENT");
            facturaDAO.crearFactura(factura);
            facturaIdField.setText(String.valueOf(factura.getId()));
            carregarFacturesClient(clientId);
        }));

        afegirLiniaBtn.setOnAction(event -> executarAccio(() -> {
            LiniaFactura linia = new LiniaFactura(
                    llegirLong(facturaIdField, "La factura ID ha de ser numèrica"),
                    llegirLong(producteIdField, "El producte ID ha de ser numèric"),
                    llegirEnter(quantitatField, "La quantitat ha de ser entera"),
                    llegirDouble(preuUnitariField, "El preu unitari ha de ser numèric")
            );
            facturaDAO.afegirLinia(linia);
            long clientId = llegirClientSeleccionat("Selecciona un client.");
            carregarFacturesClient(clientId);
        }));

        veureClientBtn.setOnAction(event -> executarAccio(() -> {
            long clientId = llegirClientSeleccionat("Selecciona un client.");
            carregarFacturesClient(clientId);
        }));

        marcarPagadaBtn.setOnAction(event -> executarAccio(() -> {
            long facturaId = llegirLong(facturaIdField, "La factura ID ha de ser numèrica");
            facturaDAO.marcarComPagada(facturaId);
            long clientId = llegirClientSeleccionat("Selecciona un client.");
            carregarFacturesClient(clientId);
        }));

        taulaFactures = crearTaulaFactures();
        taulaFactures.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                facturaIdField.setText(String.valueOf(selected.getId()));
                seleccionarClientCombo(selected.getClientId());
            }
        });

        comboClientsFactures.valueProperty().addListener((obs, oldValue, selected) -> {
            facturaIdField.clear();
            if (selected != null) {
                carregarFacturesClient(selected.getId());
            } else {
                taulaFactures.setItems(FXCollections.observableArrayList());
            }
        });

        Label ajuda = new Label("Per crear una factura: selecciona el client i prem 'Crear factura client'. " +
                "Després selecciona (o crea) una factura i afegeix línies indicant Producte ID, quantitat i preu unitari.");
        ajuda.setWrapText(true);

        HBox botons = new HBox(8, crearFacturaBtn, afegirLiniaBtn, veureClientBtn, marcarPagadaBtn);
        botons.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(12, titol("Factures"), ajuda, formulari, botons, taulaFactures);
        content.setPadding(new Insets(12));
        VBox.setVgrow(taulaFactures, Priority.ALWAYS);

        Tab tab = new Tab("Factures", content);
        tab.setClosable(false);
        return tab;
    }

    private TableView<Client> crearTaulaClients() {
        TableView<Client> table = new TableView<>();
        TableColumn<Client, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));

        TableColumn<Client, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNom()));
        nomCol.setPrefWidth(240);

        TableColumn<Client, String> nifCol = new TableColumn<>("NIF");
        nifCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNif()));
        nifCol.setPrefWidth(140);

        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEmail()));
        emailCol.setPrefWidth(260);

        table.getColumns().addAll(idCol, nomCol, nifCol, emailCol);
        return table;
    }

    private TableView<Producte> crearTaulaProductes() {
        TableView<Producte> table = new TableView<>();
        TableColumn<Producte, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));

        TableColumn<Producte, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNom()));
        nomCol.setPrefWidth(280);

        TableColumn<Producte, Double> preuCol = new TableColumn<>("Preu");
        preuCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPreu()));

        TableColumn<Producte, Integer> estocCol = new TableColumn<>("Estoc");
        estocCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEstoc()));

        table.getColumns().addAll(idCol, nomCol, preuCol, estocCol);
        return table;
    }

    private TableView<Factura> crearTaulaFactures() {
        TableView<Factura> table = new TableView<>();
        TableColumn<Factura, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));

        TableColumn<Factura, Long> clientCol = new TableColumn<>("Client ID");
        clientCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getClientId()));

        TableColumn<Factura, String> dataCol = new TableColumn<>("Data");
        dataCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getData()));
        dataCol.setPrefWidth(140);

        TableColumn<Factura, String> estatCol = new TableColumn<>("Estat");
        estatCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getEstat()));
        estatCol.setPrefWidth(120);

        TableColumn<Factura, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTotal()));

        table.getColumns().addAll(idCol, clientCol, dataCol, estatCol, totalCol);
        return table;
    }

    private void carregarClients() {
        executarAccio(() -> {
            var clients = FXCollections.observableArrayList(clientDAO.llistarTots());
            taulaClients.setItems(clients);
            if (comboClientsFactures != null) {
                Client seleccionat = comboClientsFactures.getValue();
                comboClientsFactures.setItems(clients);
                if (seleccionat != null) {
                    seleccionarClientCombo(seleccionat.getId());
                }
            }
        });
    }

    private void carregarProductes() {
        executarAccio(() -> taulaProductes.setItems(FXCollections.observableArrayList(producteDAO.llistarTots())));
    }

    private void carregarFacturesClient(long clientId) {
        executarAccio(() -> taulaFactures.setItems(FXCollections.observableArrayList(facturaDAO.llistarFacturesClient(clientId))));
    }

    private GridPane crearGridFormulari() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(8));
        return grid;
    }

    private Label titol(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        return label;
    }

    private void executarAccio(AccioBD accio) {
        try {
            accio.executar();
        } catch (UnsupportedOperationException e) {
            mostrarError("Encara hi ha un mètode DAO pendent d'implementar", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error", e.getMessage());
        }
    }

    private void validarText(TextField field, String missatge) {
        if (field.getText() == null || field.getText().isBlank()) {
            throw new IllegalArgumentException(missatge);
        }
    }

    private int llegirEnter(TextField field, String missatge) {
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(missatge);
        }
    }

    private long llegirLong(TextField field, String missatge) {
        try {
            return Long.parseLong(field.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(missatge);
        }
    }

    private double llegirDouble(TextField field, String missatge) {
        try {
            return Double.parseDouble(field.getText().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(missatge);
        }
    }

    private void netejar(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private long llegirClientSeleccionat(String missatgeSiNoNhiHa) {
        Client client = comboClientsFactures == null ? null : comboClientsFactures.getValue();
        if (client == null) {
            throw new IllegalArgumentException(missatgeSiNoNhiHa);
        }
        return client.getId();
    }

    private void seleccionarClientCombo(long clientId) {
        if (comboClientsFactures == null || comboClientsFactures.getItems() == null) {
            return;
        }
        for (Client client : comboClientsFactures.getItems()) {
            if (client.getId() == clientId) {
                comboClientsFactures.getSelectionModel().select(client);
                return;
            }
        }
    }

    private void mostrarAvis(String missatge) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Avís");
        alert.setHeaderText(null);
        alert.setContentText(missatge);
        alert.showAndWait();
    }

    private void mostrarError(String titol, String missatge) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titol);
        alert.setHeaderText(titol);
        alert.setContentText(missatge == null ? "S'ha produït un error." : missatge);
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface AccioBD {
        void executar() throws Exception;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
