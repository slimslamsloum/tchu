package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import static ch.epfl.tchu.gui.StringsFr.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Different types of Serde specific to tChu that will be used
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

//All serdes below are final and static because they will be used in other classes
//and won't be subject to any further changes
public class Serdes {

    /**
     * Serde that de/serializes a integer
     */
    public final static Serde<Integer> intSerde = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    /**
     * Serde that de/serializes a string
     */
    public final static Serde<String> stringSerde=Serde.of(
            i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            i -> new String(Base64.getDecoder().decode(i.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));

    /**
     * Serde that de/serializes a playerId
     */
    public final static Serde<PlayerId> playerIdSerde = Serde.oneOf(PlayerId.ALL);

    /**
     * Serde that de/serializes a turnkind
     */
    public final static Serde<Player.TurnKind> turnKindSerde = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde that de/serializes a card
     */
    public final static Serde<Card> cardSerde = Serde.oneOf(Card.ALL);

    /**
     * Serde that de/serializes a route
     */
    public final static Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());

    /**
     * Serde that de/serializes a ticket
     */
    public final static Serde<Ticket> ticketSerde = Serde.oneOf(ChMap.tickets());

    /**
     * Serde that de/serializes a list of cards
     */
    public final static Serde<List<Card>> listCardSerde = Serde.listOf(cardSerde, SEMICOLON_SEPARATOR);

    /**
     * Serde that de/serializes a list of routes
     */
    public final static Serde<List<Route>> listRouteSerde = Serde.listOf(routeSerde, COMMA_SEPARATOR);

    /**
     * Serde that de/serializes a list of string
     */
    public final static Serde<List<String>> listStringSerde = Serde.listOf(stringSerde, COMMA_SEPARATOR);

    /**
     * Serde that de/serializes a SortedBag of Cards
     */
    public final static Serde<SortedBag<Card>> sbCardSerde = Serde.bagOf(cardSerde, COMMA_SEPARATOR);

    /**
     * Serde that de/serializes a SortedBag of tickets
     */
    public final static Serde<SortedBag<Ticket>> sbTicketSerde = Serde.bagOf(ticketSerde, COMMA_SEPARATOR);

    /**
     * Serde that de/serializes a list of SortedBags of tickets
     */
    public final static Serde<List<SortedBag<Card>>> listSbCardSerde = Serde.listOf(sbCardSerde, COMMA_SEPARATOR);

    /**
     * Serde that de/serializes a Public Card State
     */
    public final static Serde<PublicCardState> publicCardStateSerde = Serde.of(
            i -> String.join(SEMICOLON_SEPARATOR, listCardSerde.serialize(i.faceUpCards()), intSerde.serialize(i.deckSize()), intSerde.serialize(i.discardsSize())),
            str -> {
                String[] noSeparator = str.split(Pattern.quote(SEMICOLON_SEPARATOR), -1);
                return new PublicCardState(listCardSerde.deserialize(noSeparator[0]), intSerde.deserialize(noSeparator[1]), intSerde.deserialize(noSeparator[2]));
            }
    );

    /**
     * Serde that de/serializes a Public Player State
     */
    public final static Serde<PublicPlayerState> publicPlayerStateSerde = Serde.of(
            i -> String.join(SEMICOLON_SEPARATOR, intSerde.serialize(i.ticketCount()), intSerde.serialize(i.cardCount()), listRouteSerde.serialize(i.routes())),
            str ->{
                String[] noSeparator = str.split(Pattern.quote(SEMICOLON_SEPARATOR), -1);
                return new PublicPlayerState(intSerde.deserialize(noSeparator[0]), intSerde.deserialize(noSeparator[1]), listRouteSerde.deserialize(noSeparator[2]));
            }
    );

    /**
     * Serde that de/serializes a list of Player State
     */
    public final static Serde<PlayerState> playerStateSerde =Serde.of(
            i -> String.join(SEMICOLON_SEPARATOR, sbTicketSerde.serialize(i.tickets()), sbCardSerde.serialize(i.cards()), listRouteSerde.serialize(i.routes())),
            str -> {
                String[] noSeparator = str.split(Pattern.quote(SEMICOLON_SEPARATOR), -1);
                return new PlayerState(sbTicketSerde.deserialize(noSeparator[0]), sbCardSerde.deserialize(noSeparator[1]), listRouteSerde.deserialize(noSeparator[2]));
            }
    );

    /**
     * Serde that de/serializes a Public Game State
     */
    public final static Serde<PublicGameState> publicGameStateSerde = Serde.of(
            i -> String.join(COLON_SEPARATOR, intSerde.serialize(i.ticketsCount()), publicCardStateSerde.serialize(i.cardState()), playerIdSerde.serialize(i.currentPlayerId()) ,
                    publicPlayerStateSerde.serialize(i.playerState(PlayerId.PLAYER_1)), publicPlayerStateSerde.serialize(i.playerState(PlayerId.PLAYER_2)), playerIdSerde.serialize(i.lastPlayer())),
            str-> {
                String[] noSeparator = str.split(Pattern.quote(COLON_SEPARATOR), -1);
                Map<PlayerId, PublicPlayerState> playerIdPlayerStateMap = new HashMap<>();
                playerIdPlayerStateMap.put(PlayerId.PLAYER_1, publicPlayerStateSerde.deserialize(noSeparator[3]));
                playerIdPlayerStateMap.put(PlayerId.PLAYER_2, publicPlayerStateSerde.deserialize(noSeparator[4]));
                return new PublicGameState(intSerde.deserialize(noSeparator[0]), publicCardStateSerde.deserialize(noSeparator[1]), playerIdSerde.deserialize(noSeparator[2]),
                        playerIdPlayerStateMap, playerIdSerde.deserialize(noSeparator[5]));
            }
    );
}