package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import static ch.epfl.tchu.gui.GuiConstants.*;

import java.util.Map;

/**
 * Creator of the view of information in the game
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

class InfoViewCreator {

    /**
     * Private class constructor so that class isn't instantiable
     */
    private InfoViewCreator(){}

    /**
     * Creator of the view of information
     * @param playerId playerId of the player watching the game
     * @param playerNames map of player names attached to playerIds
     * @param observableGameState observable state of the game
     * @param observableTexts observable texts that will appear underneath the player stats
     * @return the pane which contains the statistics of each player as well as information about the game
     */
    public static Pane createInfoView (PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState observableGameState,
                                           ObservableList<Text> observableTexts){

        //creation of main VBOX
        VBox infoPane = new VBox();
        infoPane.getStylesheets().addAll("info.css", "colors.css");

        //creation of VBOX for player statistics
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");

        //creation of separator to separate statistics and information about game
        Separator separator = new Separator();

        //creation of text flow for information related to the game
        TextFlow gameInfo = new TextFlow();
        gameInfo.setId("game-info");

        Bindings.bindContent(gameInfo.getChildren(), observableTexts);

        playerInfoView(playerId, observableGameState,playerStats, playerNames);
        playerInfoView(playerId.next(),observableGameState,playerStats, playerNames);

        //playerStats, the separator and gameInfo nodes are now the children of infoPane
        infoPane.getChildren().addAll(playerStats, separator, gameInfo);

        DarkModeButton.changeToDarkMode("darkInfo.css", infoPane);

        return infoPane;
    }

    /**
     * Creator of statistics for a given player
     * @param player player for which we're going to write statistics
     * @param observableGameState observable state of the game
     * @param playerStats vbox containing player statistics
     * @param playerNames map of player names
     */
    private static void playerInfoView(PlayerId player, ObservableGameState observableGameState, VBox playerStats,
                                       Map<PlayerId, String> playerNames){
        //creation of text flow for a player's statistic
        TextFlow playerN = new TextFlow();
        playerStats.getChildren().add(playerN);
        playerN.getStyleClass().add(player.name());

        //creation of circle
        Circle circle = new Circle(IVC_CIRCLE_RADIUS);
        circle.getStyleClass().add("filled");

        //creation of Text
        Text playerStatsText = new Text();

        //string expression is a text that displays the player's statistics (binded to observables gameState's
        //properties)
        StringExpression stringExpression = Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(player),
                observableGameState.nbTickets(player), observableGameState.nbCards(player),
                observableGameState.nbCars(player), observableGameState.nbPoints(player));

        //the string expression will be binded with the text, which is a child of the Text flow for
        //player statistics
        playerStatsText.textProperty().bind(stringExpression);
        playerStatsText.setId("text");
        playerN.getChildren().addAll(circle, playerStatsText);
    }
}
