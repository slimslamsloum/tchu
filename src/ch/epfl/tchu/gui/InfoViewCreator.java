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

    private static int CIRCLE_RADIUS = 5;

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

            Bindings.bindContent(gameInfo.getChildren(), observableTexts);

            for (PlayerId player : PlayerId.ALL){

                TextFlow playerN = new TextFlow();
                playerStats.getChildren().add(playerN);
                playerN.getStyleClass().add(player.name());

                Circle circle = new Circle(CIRCLE_RADIUS);
                circle.getStyleClass().add("filled");

                Text playerStatsText = new Text();

                StringExpression stringExpression = Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(player),
                        observableGameState.nbTickets(player), observableGameState.nbCards(player),
                        observableGameState.nbCars(player), observableGameState.nbPoints(player));

                playerStatsText.textProperty().bind(stringExpression);

                playerN.getChildren().addAll(circle, playerStatsText);
            }

            infoPane.getChildren().addAll(playerStats, separator, gameInfo);

            return infoPane;
    }
}
