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
/**
 * the Client for the distant player
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */
public class RemotePlayerClient {

    //All the attributes : the distant player, the host of the server and the port to which it is connected
    private final Player player;
    private final String host;
    private final int port;

    /**
     * The constructor of the RemotePlayerClient
     * @param player the distant player
     * @param host the host of the server
     * @param port the port to which it is connected
     */
    public RemotePlayerClient(Player player, String host, int port) {
        this.player=player;
        this.host=host;
        this.port=port;
    }

    /**
     * Method that allows the client to receive messages from the server and to do the right task according to its content
     */
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
                String messageId = separatedMessage[0]; // gives the type of action to be done
                // According to the content of the messageId, which is a value of the enum type MessageId,
                // It does a specific action
                switch (MessageId.valueOf(messageId)){
                    case INIT_PLAYERS : // calls the method initPlayers of the player
                        PlayerId currentPlayer = Serdes.playerIdSerde.deserialize(separatedMessage[1]);
                        List<String> players = Serdes.listStringSerde.deserialize(separatedMessage[2]);
                        Map<PlayerId,String> playerIdMap = new TreeMap<>();
                        playerIdMap.put(currentPlayer,players.get(0));
                        playerIdMap.put(currentPlayer.next(),players.get(1));
                        player.initPlayers(currentPlayer,playerIdMap);
                        break;
                    case RECEIVE_INFO: // calls the method receiveInfo of the player for him to receive an information
                        String info = Serdes.stringSerde.deserialize(separatedMessage[1]);
                        player.receiveInfo(info);
                        break;
                    case UPDATE_STATE: // calls the method updateState of the player to update his state
                        PublicGameState newState = Serdes.publicGameStateSerde.deserialize(separatedMessage[1]);
                        PlayerState ownState = Serdes.playerStateSerde.deserialize(separatedMessage[2]);
                        player.updateState(newState,ownState);
                        break;
                    case SET_INITIAL_TICKETS: // calls the method setInitialTicketChoice of the player
                        SortedBag<Ticket> tickets = Serdes.sbTicketSerde.deserialize(separatedMessage[1]);
                        player.setInitialTicketChoice(tickets);
                        break;
                    case CHOOSE_INITIAL_TICKETS: // calls the method chooseInitialTickets and sends the choice to the proxy
                        SortedBag<Ticket> initialTickets = player.chooseInitialTickets();
                        String sbInitialTicketsSerde = Serdes.sbTicketSerde.serialize(initialTickets);
                        RemotePlayerClient.sendToProxy(w,sbInitialTicketsSerde);
                        break;
                    case NEXT_TURN: // calls the method next.Turn() of the player and sends its choice to the proxy
                        Player.TurnKind turnKind = player.nextTurn();
                        String turnKindSerde = Serdes.turnKindSerde.serialize(turnKind);
                        RemotePlayerClient.sendToProxy(w,turnKindSerde);
                        break;
                    case CHOOSE_TICKETS: // calls the method chooseTickets() of the player and sends the chosen tickets to the proxy
                        SortedBag<Ticket> givenTickets = Serdes.sbTicketSerde.deserialize(separatedMessage[1]);
                        SortedBag<Ticket> chosenTickets = player.chooseTickets(givenTickets);
                        String sbChosenTicketsSerde = Serdes.sbTicketSerde.serialize(chosenTickets);
                        RemotePlayerClient.sendToProxy(w,sbChosenTicketsSerde);
                        break;
                    case DRAW_SLOT: // calls the method draw.slot() of the player and sends its choice to the proxy
                        int slot = player.drawSlot();
                        String slotSerde = Serdes.intSerde.serialize(slot);
                        RemotePlayerClient.sendToProxy(w,slotSerde);
                        break;
                    case ROUTE: // calls the method claimed.route() of the player and sends the route to the proxy
                        Route claimedRoute = player.claimedRoute();
                        String claimedRouteSerde = Serdes.routeSerde.serialize(claimedRoute);
                        RemotePlayerClient.sendToProxy(w,claimedRouteSerde);
                        break;
                    case CARDS: // calls the method initialClaimCards() of the player and sends its choice to the proxy
                        SortedBag<Card> initialClaimCards = player.initialClaimCards();
                        String initialClaimCardsSerde = Serdes.sbCardSerde.serialize(initialClaimCards);
                        RemotePlayerClient.sendToProxy(w,initialClaimCardsSerde);
                        break;
                    case CHOOSE_ADDITIONAL_CARDS: // calls the method chooseAdditionalCards() of the player and sends its choice to the proxy
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

    /**
     * Method that allows to send a message to the proxy server
     * @param w the BufferedWriter
     * @param messageSerde the message to be sent
     */
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