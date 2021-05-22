package ch.epfl.tchu.gui;


import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
/**
 * The client
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class ClientMain extends Application {

    /**
     * Main method of the client
     * @param args the arguments of the client
     */
    public static void main(String[] args) { launch(args); }

    /**
     * Method that starts the game for the client
     * @param primaryStage obligatory argument - not used in this overridden definition
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> params= getParameters().getRaw();
        Player player = new GraphicalPlayerAdapter();
        String host = params.isEmpty() ? "localhost" : params.get(0);
        int port = params.isEmpty() ? 5108 : Integer.parseInt(params.get(1));
        RemotePlayerClient client = new RemotePlayerClient(player,host,port);
        new Thread(client::run).start();
    }
}