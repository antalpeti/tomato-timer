package com.tomatotimer;

import com.tomatotimer.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        StackPane root = loader.load();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("TomatoTimer");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(160);
        primaryStage.setMinHeight(44);

        MainController controller = loader.getController();
        controller.init(primaryStage);

        primaryStage.show();
    }

    public static void main(String[] args) {
        // Set the Windows AppUserModelID before Application.launch() so it is
        // registered before the JavaFX toolkit initialises native Windows peer
        // windows.  No-op on non-Windows and safe when JNA is absent.
        WindowsAppIdHelper.apply();
        launch(args);
    }
}

