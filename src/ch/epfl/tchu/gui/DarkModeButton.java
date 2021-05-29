package ch.epfl.tchu.gui;

import javafx.scene.layout.Pane;

public class DarkModeButton {

    public static void changeToDarkMode(String styleSheet, Pane pane){
        DecksViewCreator.darkModeButton.selectedProperty().addListener((obs, wasSelected, isSelected)->{
            if (isSelected){
                pane.getStylesheets().add(styleSheet);
            }
            else{
                pane.getStylesheets().remove(styleSheet);
            }
        });
    }
}
