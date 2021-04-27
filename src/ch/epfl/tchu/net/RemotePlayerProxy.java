package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {

    private Socket socket;

    public RemotePlayerProxy(Socket socket){ this.socket=socket; }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> listPlayers = List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2));
        String serializedPlayers = Serde.listOf(Serdes.stringSerde, ",").serialize(listPlayers);
        String serializedPlayerID = Serdes.playerIdSerde.serialize(ownId);
        sendMessage(List.of(serializedPlayerID,serializedPlayers), MessageId.INIT_PLAYERS);
    }

    @Override
    public void receiveInfo(String info) {
        sendMessage(List.of(Serdes.stringSerde.serialize(info)), MessageId.RECEIVE_INFO);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String serializedPGS = Serdes.publicGameStateSerde.serialize(newState);
        String serializedPS = Serdes.playerStateSerde.serialize(ownState);
        List<String> list = List.of(serializedPGS,serializedPS);
        sendMessage(list, MessageId.UPDATE_STATE);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(List.of(Serdes.sbTicketSerde.serialize(tickets)), MessageId.SET_INITIAL_TICKETS);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(List.of(), MessageId.CHOOSE_INITIAL_TICKETS);
        return Serdes.sbTicketSerde.deserialize(receiveMessage());
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(List.of(), MessageId.NEXT_TURN);
        return Serdes.turnKindSerde.deserialize(receiveMessage());
    }

    @Override
    public int drawSlot() {
        sendMessage(List.of(), MessageId.DRAW_SLOT);
        return Serdes.intSerde.deserialize(receiveMessage());
    }

    @Override
    public Route claimedRoute() {
        sendMessage(List.of(), MessageId.ROUTE);
        return Serdes.routeSerde.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String serializedSbTickets = Serdes.sbTicketSerde.serialize(options);
        sendMessage(List.of(serializedSbTickets), MessageId.CHOOSE_TICKETS);
        return Serdes.sbTicketSerde.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(List.of(), MessageId.CARDS);
        return Serdes.sbCardSerde.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String serializedSbTickets = Serdes.listSbCardSerde.serialize(options);
        sendMessage(List.of(serializedSbTickets), MessageId.CHOOSE_ADDITIONAL_CARDS);
        return Serdes.sbCardSerde.deserialize(receiveMessage());
    }

    private void sendMessage(List<String> list, MessageId messageId){
        try {
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(),
                                    US_ASCII));

            w.write(messageId.name());
            w.write(" ");
            int i = 0;
            if (!list.isEmpty()){
                for (String string : list){
                    w.write(string);
                    if (i != list.size()){w.write(" ");}
                    i++;
                }
            }
            w.write('\n');
            w.flush();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receiveMessage(){
        try {
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    US_ASCII));

            return r.readLine();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
