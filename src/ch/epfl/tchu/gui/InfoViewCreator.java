package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;

import javax.swing.*;
import java.util.Map;

class InfoViewCreator {

    private static int CIRCLE_SIZE = 5;

    private InfoViewCreator(){}

        public static Pane createInfoView (PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState observableGameState,
                                           ObservableList<Text> observableTexts){

            VBox infoPane = new VBox();
            infoPane.getStylesheets().addAll("info.css", "colors.css");

            VBox playerStats = new VBox();
            playerStats.setId("player-stats");

            Separator separator = new Separator();

            TextFlow gameInfo = new TextFlow();
            gameInfo.setId("game-info");

            infoPane.getChildren().addAll(playerStats, separator, gameInfo);

            Bindings.bindContent(gameInfo.getChildren(), observableTexts);

            for (PlayerId player : PlayerId.ALL){

                TextFlow playerN = new TextFlow();
                playerStats.getChildren().add(playerN);
                playerN.getStyleClass().add(player.toString());

                Circle circle = new Circle(CIRCLE_SIZE);
                playerN.getChildren().add(circle);
                circle.getStyleClass().add("filled");

                Text textStats = new Text();
                playerN.getChildren().add(textStats);
                textStats.getStyleClass().add("filled");

                textStats.setText(StringsFr.PLAYER_STATS);

            }













    }
}
