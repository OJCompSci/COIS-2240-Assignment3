
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RentalSystemGUI extends Application {

    @Override
    public void start(Stage primaryStage) {

        RentalSystem rentalSystem = RentalSystem.getInstance();
        rentalSystem.loadData();  
        TabPane tabPane = new TabPane();

        Tab vehiclesTab = new Tab("Vehicles");
        vehiclesTab.setClosable(false);
        vehiclesTab.setContent(createVehiclesPane(rentalSystem));

        Tab customersTab = new Tab("Customers");
        customersTab.setClosable(false);
        customersTab.setContent(createCustomersPane(rentalSystem));

        Tab rentalsTab = new Tab("Rent / Return");
        rentalsTab.setClosable(false);
        rentalsTab.setContent(createRentReturnPane(rentalSystem));

        tabPane.getTabs().addAll(vehiclesTab, customersTab, rentalsTab);

        BorderPane root = new BorderPane(tabPane);
        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("Vehicle Rental System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ===== Vehicles tab =====
    private Pane createVehiclesPane(RentalSystem rentalSystem) {
        TableView<Vehicle> table = new TableView<>();

        TableColumn<Vehicle, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue() instanceof Car ? "Car" :
                        data.getValue() instanceof Minibus ? "Minibus" :
                        data.getValue() instanceof PickupTruck ? "Pickup" : "Other"));

        TableColumn<Vehicle, String> plateCol = new TableColumn<>("Plate");
        plateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLicensePlate()));

        TableColumn<Vehicle, String> makeCol = new TableColumn<>("Make");
        makeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMake()));

        TableColumn<Vehicle, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getModel()));

        TableColumn<Vehicle, Number> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getYear()));

        TableColumn<Vehicle, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().toString()));

        table.getColumns().addAll(typeCol, plateCol, makeCol, modelCol, yearCol, statusCol);
        table.getItems().addAll(rentalSystem.getAllVehicles()); 

        // Form controls
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Car", "Minibus", "Pickup");
        typeBox.setPromptText("Type");

        TextField plateField = new TextField();
        plateField.setPromptText("Plate");

        TextField makeField = new TextField();
        makeField.setPromptText("Make");

        TextField modelField = new TextField();
        modelField.setPromptText("Model");

        TextField yearField = new TextField();
        yearField.setPromptText("Year");

        // Simple extra fields for subtype (you can expand later)
        TextField seatsField = new TextField();
        seatsField.setPromptText("#Seats (Car)");

        CheckBox accessibleBox = new CheckBox("Accessible (Minibus)");

        TextField cargoField = new TextField();
        cargoField.setPromptText("Cargo (Pickup)");

        CheckBox trailerBox = new CheckBox("Trailer (Pickup)");

        Button addButton = new Button("Add Vehicle");
        Label messageLabel = new Label();

        addButton.setOnAction(e -> {
            try {
                String type = typeBox.getValue();
                String plate = plateField.getText().trim();
                String make = makeField.getText().trim();
                String model = modelField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());

                Vehicle v = null;
                if ("Car".equals(type)) {
                    int seats = Integer.parseInt(seatsField.getText().trim());
                    v = new Car(make, model, year, seats);
                } else if ("Minibus".equals(type)) {
                    boolean accessible = accessibleBox.isSelected();
                    v = new Minibus(make, model, year, accessible);
                } else if ("Pickup".equals(type)) {
                    double cargo = Double.parseDouble(cargoField.getText().trim());
                    boolean hasTrailer = trailerBox.isSelected();
                    v = new PickupTruck(make, model, year, cargo, hasTrailer);
                }

                if (v == null) {
                    messageLabel.setText("Select a vehicle type.");
                    return;
                }

                v.setLicensePlate(plate);
                boolean ok = rentalSystem.addVehicle(v);
                if (ok) {
                    table.getItems().add(v);
                    messageLabel.setText("Vehicle added.");
                } else {
                    messageLabel.setText("Add failed (duplicate or invalid).");
                }
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        HBox form1 = new HBox(8, typeBox, plateField, makeField, modelField, yearField);
        HBox form2 = new HBox(8, seatsField, accessibleBox, cargoField, trailerBox, addButton);
        VBox root = new VBox(10, table, form1, form2, messageLabel);
        root.setPadding(new Insets(10));
        return root;
    }

    // ===== Customers tab =====
    private Pane createCustomersPane(RentalSystem rentalSystem) {
        TableView<Customer> table = new TableView<>();

        TableColumn<Customer, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCustomerId()));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomerName()));

        table.getColumns().addAll(idCol, nameCol);
        table.getItems().addAll(rentalSystem.getAllCustomers()); 

        TextField idField = new TextField();
        idField.setPromptText("ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        Button addButton = new Button("Add Customer");
        Label messageLabel = new Label();

        addButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                Customer c = new Customer(id, name);
                boolean ok = rentalSystem.addCustomer(c);
                if (ok) {
                    table.getItems().add(c);
                    messageLabel.setText("Customer added.");
                } else {
                    messageLabel.setText("Add failed (duplicate ID?).");
                }
            } catch (Exception ex) {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        HBox form = new HBox(8, idField, nameField, addButton);
        VBox root = new VBox(10, table, form, messageLabel);
        root.setPadding(new Insets(10));
        return root;
    }

    // ===== Rent / Return tab =====
    private Pane createRentReturnPane(RentalSystem rentalSystem) {
        TableView<Vehicle> vehicleTable = new TableView<>();
        TableColumn<Vehicle, String> plateCol = new TableColumn<>("Plate");
        plateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLicensePlate()));
        TableColumn<Vehicle, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().toString()));
        vehicleTable.getColumns().addAll(plateCol, statusCol);
        vehicleTable.getItems().addAll(rentalSystem.getAllVehicles());

        TableView<Customer> customerTable = new TableView<>();
        TableColumn<Customer, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCustomerId()));
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomerName()));
        customerTable.getColumns().addAll(idCol, nameCol);
        customerTable.getItems().addAll(rentalSystem.getAllCustomers());

        TextField amountField = new TextField();
        amountField.setPromptText("Rent amount");

        TextField feesField = new TextField();
        feesField.setPromptText("Return extra fees");

        Button rentButton = new Button("Rent");
        Button returnButton = new Button("Return");
        Label messageLabel = new Label();

        rentButton.setOnAction(e -> {
            Vehicle v = vehicleTable.getSelectionModel().getSelectedItem();
            Customer c = customerTable.getSelectionModel().getSelectedItem();
            if (v == null || c == null) {
                messageLabel.setText("Select a vehicle and a customer.");
                return;
            }
            double amount = 0.0;
            if (!amountField.getText().trim().isEmpty()) {
                amount = Double.parseDouble(amountField.getText().trim());
            }
            boolean ok = rentalSystem.rentVehicle(v, c, LocalDate.now(), amount);
            if (ok) {
                messageLabel.setText("Vehicle rented.");
            } else {
                messageLabel.setText("Rent failed.");
            }
            vehicleTable.refresh();
        });

        returnButton.setOnAction(e -> {
            Vehicle v = vehicleTable.getSelectionModel().getSelectedItem();
            Customer c = customerTable.getSelectionModel().getSelectedItem();
            if (v == null || c == null) {
                messageLabel.setText("Select a vehicle and a customer.");
                return;
            }
            double fees = 0.0;
            if (!feesField.getText().trim().isEmpty()) {
                fees = Double.parseDouble(feesField.getText().trim());
            }
            boolean ok = rentalSystem.returnVehicle(v, c, LocalDate.now(), fees);
            if (ok) {
                messageLabel.setText("Vehicle returned.");
            } else {
                messageLabel.setText("Return failed.");
            }
            vehicleTable.refresh();
        });

        VBox left = new VBox(10, new Label("Vehicles"), vehicleTable);
        VBox right = new VBox(10, new Label("Customers"), customerTable);
        HBox centerTables = new HBox(10, left, right);

        HBox rentBox = new HBox(8, new Label("Amount:"), amountField, rentButton);
        HBox returnBox = new HBox(8, new Label("Fees:"), feesField, returnButton);

        VBox root = new VBox(10, centerTables, rentBox, returnBox, messageLabel);
        root.setPadding(new Insets(10));
        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
