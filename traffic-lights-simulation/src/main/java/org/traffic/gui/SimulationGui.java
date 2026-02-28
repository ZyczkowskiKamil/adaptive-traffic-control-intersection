package org.traffic.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.traffic.controller.SimulationController;

import java.net.URL;
import java.util.Objects;

public class SimulationGui extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/simulation.fxml");
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(fxmlLocation));

        Parent root = loader.load();
        SimulationController controller = loader.getController();

        controller.setStage(primaryStage);

        primaryStage.setTitle("Traffic light simulator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
