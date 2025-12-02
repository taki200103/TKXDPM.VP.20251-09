package com.aims;

import com.aims.views.home.HomeForm;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Tạo HomeForm
            HomeForm homeForm = new HomeForm();
            
            // Set stage cho homeForm
            homeForm.getStage().setTitle("AIMS - Home");
            homeForm.getStage().setWidth(1200);
            homeForm.getStage().setHeight(800);
            homeForm.getStage().setMinWidth(800);
            homeForm.getStage().setMinHeight(600);
            
            // Hiển thị cửa sổ
            homeForm.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
