package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
/**
 * Server hosting the game
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class ServerMain extends Application {

    /**
     * Main method of the server
     * @param args the arguments of the client
     */
    public static void main(String[] args) { launch(args); }

    /**
     * Method that starts the game for the server
     * @param primaryStage obligatory argument - not used in this overridden definition
     */
    public void start(Stage primaryStage) throws Exception {
        String name1 = getParameters().getRaw().isEmpty() ?
                "Ada" : getParameters().getRaw().get(0);
        String name2 = getParameters().getRaw().isEmpty() ?
                "Charles" : getParameters().getRaw().get(1);
        ServerSocket serverSocket = new ServerSocket(5109);
        Socket socket = serverSocket.accept();
        Player player1 = new GraphicalPlayerAdapter();
        Player player2 = new RemotePlayerProxy(socket);
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Map<PlayerId, String> names = Map.of(PLAYER_1, name1, PLAYER_2, name2);

        Map<PlayerId, Player> players =
                Map.of(PLAYER_1, player1,
                        PLAYER_2, player2);
        Random rng = new Random();
        new Thread(() -> Game.play(players,names,tickets,rng)).start();
    }
}