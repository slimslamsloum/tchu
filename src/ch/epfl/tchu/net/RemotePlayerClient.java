package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient {

    private final Player player;
    private final String host;
    private final int port;

    public RemotePlayerClient(Player player, String host, int port) {
        this.player=player;
        this.host=host;
        this.port=port;
    }

    public void run() {
        try (Socket s= new Socket(host, port);
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(s.getInputStream(),
                                     US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(),
                                     US_ASCII))) {

            String str;
            while((str = r.readLine()) != null){
                String[] separatedMessage = str.split(Pattern.quote(" "), -1);
                String messageId = separatedMessage[0];

                switch (MessageId.valueOf(messageId)){
                    case INIT_PLAYERS :
                        PlayerId currentPlayer = Serdes.playerIdSerde.deserialize(separatedMessage[1]);
                        List<String> players = Serdes.listStringSerde.deserialize(separatedMessage[2]);
                        Map<PlayerId,String> playerId = new TreeMap<>();
                        playerId.put(PlayerId.PLAYER_1,players.get(0));
                        playerId.put(PlayerId.PLAYER_2,players.get(1));
                        player.initPlayers(currentPlayer,playerId);
                        break;
                    case RECEIVE_INFO:
                        String info = Serdes.stringSerde.deserialize(separatedMessage[1]);
                        player.receiveInfo(info);
                        break;
                    case UPDATE_STATE:
                        PublicGameState newState = Serdes.publicGameStateSerde.deserialize(separatedMessage[1]);
                        PlayerState ownState = Serdes.playerStateSerde.deserialize(separatedMessage[2]);
                        player.updateState(newState,ownState);
                        break;
                    case SET_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = Serdes.sbTicketSerde.deserialize(separatedMessage[1]);
                        player.setInitialTicketChoice(tickets);
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> initialTickets = player.chooseInitialTickets();
                        String sbInitialTicketsSerde = Serdes.sbTicketSerde.serialize(initialTickets);
                        RemotePlayerClient.sendToProxy(w,sbInitialTicketsSerde);
                        break;
                    case NEXT_TURN:
                        Player.TurnKind turnKind = player.nextTurn();
                        String turnKindSerde = Serdes.turnKindSerde.serialize(turnKind);
                        RemotePlayerClient.sendToProxy(w,turnKindSerde);
                        break;
                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> givenTickets = Serdes.sbTicketSerde.deserialize(separatedMessage[1]);
                        SortedBag<Ticket> chosenTickets = player.chooseTickets(givenTickets);
                        String sbChosenTicketsSerde = Serdes.sbTicketSerde.serialize(chosenTickets);
                        RemotePlayerClient.sendToProxy(w,sbChosenTicketsSerde);
                        break;
                    case DRAW_SLOT:
                        int slot = player.drawSlot();
                        String slotSerde = Serdes.intSerde.serialize(slot);
                        RemotePlayerClient.sendToProxy(w,slotSerde);
                        break;
                    case ROUTE:
                        Route claimedRoute = player.claimedRoute();
                        String claimedRouteSerde = Serdes.routeSerde.serialize(claimedRoute);
                        RemotePlayerClient.sendToProxy(w,claimedRouteSerde);
                        break;
                    case CARDS:
                        SortedBag<Card> initialClaimCards = player.initialClaimCards();
                        String initialClaimCardsSerde = Serdes.sbCardSerde.serialize(initialClaimCards);
                        RemotePlayerClient.sendToProxy(w,initialClaimCardsSerde);
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> options = Serdes.listSbCardSerde.deserialize(separatedMessage[1]);
                        SortedBag<Card> chosenAdditionalCards = player.chooseAdditionalCards(options);
                        String chosenAdditionalCardsSerde = Serdes.sbCardSerde.serialize(chosenAdditionalCards);
                        RemotePlayerClient.sendToProxy(w,chosenAdditionalCardsSerde);
                        break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    private static void sendToProxy(BufferedWriter w, String messageSerde){
        try {
            w.write(messageSerde);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
