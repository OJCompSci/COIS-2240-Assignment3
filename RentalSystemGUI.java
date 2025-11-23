import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RentalSystemGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Get the existing singleton backend
        RentalSystem rentalSystem = RentalSystem.getInstance();
        rentalSystem.loadData(); // reuse your loading

        TabPane tabPane = new TabPane();

        Tab vehiclesTab = new Tab("Vehicles");
        vehiclesTab.setClosable(false);

        Tab customersTab = new Tab("Customers");
        customersTab.setClosable(false);

        Tab rentalsTab = new Tab("Rent / Return");
        rentalsTab.setClosable(false);

        tabPane.getTabs().addAll(vehiclesTab, customersTab, rentalsTab);

        BorderPane root = new BorderPane(tabPane);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Vehicle Rental System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
