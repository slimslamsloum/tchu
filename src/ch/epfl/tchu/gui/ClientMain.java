package ch.epfl.tchu.gui;


import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class ClientMain extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        List<String> params= getParameters().getRaw();
        Player player = new GraphicalPlayerAdapter();
        RemotePlayerClient client;
        if(params.size()==2){
            String host = params.get(0);
            int port = Integer.parseInt(params.get(1));
            client = new RemotePlayerClient(player,host,port);
        }
        else {
            client = new RemotePlayerClient(player,"localhost",5108);
        }
        new Thread(client::run).start();
    }
}