package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class ServerMain extends Application {

    public static void main(String[] args) { launch(args); }


    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> params= getParameters().getRaw();
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player player1 = new GraphicalPlayerAdapter();
            Player player2 = new RemotePlayerProxy(serverSocket.accept());
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
            Map<PlayerId, String> names =
                    Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
            Map<PlayerId, Player> players =
                    Map.of(PLAYER_1, player1,
                            PLAYER_2, player2);
            Random rng = new Random();
            if (params.size()==2){
                new Thread(() -> Game.play(players,names,tickets,rng)).start();
            }
            else {
                new Thread( () ->  Game.play(players,
                        Map.of(PLAYER_1, params.get(0), PLAYER_2, params.get(1)),tickets, rng)).start();
            }
        }
    }
}