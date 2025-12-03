package com.aims;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/aims/views/home/Home.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setTitle("AIMS - Home");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
