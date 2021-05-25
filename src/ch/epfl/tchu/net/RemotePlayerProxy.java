package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * A remote player proxy which will communicate with the remote player's client
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class RemotePlayerProxy implements Player {

    //Attributes needed for the proxy: a BufferedReader and BufferedWriter which will be able to read or write within
    //a given socket
    private final BufferedReader r;
    private final BufferedWriter w;

    /**
     * Remote Player Proxy constructor
     * @param socket which needs to be the same socket as the client's
     * @throws IOException
     * connects the BufferedReader and BufferedWriter to the socket given in argument
     */
    public RemotePlayerProxy(Socket socket) throws IOException {
        r =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream(),
                                US_ASCII));
        w =
                new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(),
                                US_ASCII));
    }

    /**
     * Serializes arguments ownId, playerNames and sends them in a message of type INIT_PLAYERS to the socket
     * @param ownId id of player on which method is called
     * @param playerNames map of Player Ids linked to player names
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> listPlayers = List.of(playerNames.get(ownId), playerNames.get(ownId.next()));
        String serializedPlayers = Serde.listOf(Serdes.stringSerde, ",").serialize(listPlayers);
        String serializedPlayerID = Serdes.playerIdSerde.serialize(ownId);
        sendMessage(List.of(serializedPlayerID,serializedPlayers), MessageId.INIT_PLAYERS);
    }

    /**
     * Serializes info and sends them in a message of type RECEIVE_INFO to the socket
     * @param info info communicated to the player
     */
    @Override
    public void receiveInfo(String info) {
        sendMessage(List.of(Serdes.stringSerde.serialize(info)), MessageId.RECEIVE_INFO);
    }

    /**
     * Serializes newState, ownState and sends them in a message of type UPDATE_STATE to the socket
     * @param newState new gamestate
     * @param ownState player's player state
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String serializedPGS = Serdes.publicGameStateSerde.serialize(newState);
        String serializedPS = Serdes.playerStateSerde.serialize(ownState);
        List<String> list = List.of(serializedPGS,serializedPS);
        sendMessage(list, MessageId.UPDATE_STATE);
    }

    /**
     * Serializes tickets and sends them in a message of type SET_INITIAL_TICKETS to the socket
     * @param tickets 5 tickets distributed at the beginning of the game to a player
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(List.of(Serdes.sbTicketSerde.serialize(tickets)), MessageId.SET_INITIAL_TICKETS);
    }

    /**
     * Sends message of type CHOOSE_INITIAL_TICKETS without any argument to serialize
     * @return the next received message which contains the initial tickets the player has chosen
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(List.of(), MessageId.CHOOSE_INITIAL_TICKETS);
        return Serdes.sbTicketSerde.deserialize(receiveMessage());
    }

    /**
     * Sends message of type NEXT_TURN without any argument to serialize
     * @return the next received message which contains the turn kind the player has chosen
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(List.of(), MessageId.NEXT_TURN);
        return Serdes.turnKindSerde.deserialize(receiveMessage());
    }

    /**
     * Sends message of type DRAW_SLOT without any argument to serialize
     * @return the next received message which contains the slot number the player has chosen when picking a face
     * up cards
     */
    @Override
    public int drawSlot() {
        sendMessage(List.of(), MessageId.DRAW_SLOT);
        return Serdes.intSerde.deserialize(receiveMessage());
    }

    /**
     * Sends message of type ROUTE without any argument to serialize
     * @return the next received message which contains the route the player has claimed
     */
    @Override
    public Route claimedRoute() {
        sendMessage(List.of(), MessageId.ROUTE);
        return Serdes.routeSerde.deserialize(receiveMessage());
    }

    /**
     * Serializes argument options, and send it in a message of type CHOOSE_TICKETS
     * @param options tickets the player has drawn from the ticket pile
     * @return the next received message which contains the tickets the player has chosen
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String serializedSbTickets = Serdes.sbTicketSerde.serialize(options);
        sendMessage(List.of(serializedSbTickets), MessageId.CHOOSE_TICKETS);
        return Serdes.sbTicketSerde.deserialize(receiveMessage());
    }

    /**
     * Sends message of type CARDS without any arguments
     * @return the next received message which contains the initial claim cards
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(List.of(), MessageId.CARDS);
        return Serdes.sbCardSerde.deserialize(receiveMessage());
    }

    /**
     * Serializes argument options, sends it in a message of type CHOOSE_ADDITIONAL_CARDS.
     * @param options possible SortedBags that can be used to claim the tunnel
     * @return next message which contains the chosen additional cards
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String serializedSbCards = Serdes.listSbCardSerde.serialize(options);
        sendMessage(List.of(serializedSbCards), MessageId.CHOOSE_ADDITIONAL_CARDS);
        return Serdes.sbCardSerde.deserialize(receiveMessage());
    }

    /**
     * Private method that sends a message to the client (to be deserialized) with the socket
     * @param list of serialized strings to send to the client
     * @param messageId type of message sent to the client
     * @throws UncheckedIOException if an IOException is caught
     */
    private void sendMessage(List<String> list, MessageId messageId){
        try {
            w.write(messageId.name());
            w.write(" ");
            if (!list.isEmpty()){
                for (String string : list){
                    w.write(string);
                    if (list.indexOf(string) != list.size() -1){w.write(" ");}
                }
            }
            w.write('\n');
            w.flush();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Method that receives a message by the client from the socket and reads it
     * @return the string the composes the message
     */
    private String receiveMessage(){
        try {
            return r.readLine();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}