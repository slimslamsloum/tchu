package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Serdes {

  public final static Serde<Integer> intSerde = Serde.of(
          i -> Integer.toString(i),
          Integer::parseInt);

  public final static Serde<String> stringSerde=Serde.of(
          i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
          i -> Arrays.toString(Base64.getDecoder().decode(new String(i.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))));

  public final static Serde<PlayerId> playerIdSerde = Serde.oneOf(PlayerId.ALL);

  public final static Serde<Player.TurnKind> turnKindSerde = Serde.oneOf(Player.TurnKind.ALL);

  public final static Serde<Card> cardSerde = Serde.oneOf(Card.ALL);

  public final static Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());

  public final static Serde<Ticket> ticketSerde = Serde.oneOf(ChMap.tickets());

  public final static Serde<List<Card>> listCardSerde = Serde.listOf(cardSerde, ",");

  public final static Serde<List<Route>> listRouteSerde = Serde.listOf(routeSerde, ",");

  public final static Serde<List<String>> listStringSerde = Serde.listOf(stringSerde, ",");

  public final static Serde<SortedBag<Card>> sbCardSerde = Serde.bagOf(cardSerde, ",");

  public final static Serde<SortedBag<Ticket>> sbTicketSerde = Serde.bagOf(ticketSerde, ",");

  public final static Serde<List<SortedBag<Card>>> listSbCardSerde = Serde.listOf(sbCardSerde, ";");

  public final static Serde<PublicCardState> publicCardStateSerde = Serde.of(
          i -> String.join(";", listCardSerde.serialize(i.faceUpCards()), intSerde.serialize(i.deckSize()), intSerde.serialize(i.discardsSize())),
                  str -> {
            String[] noSeparator = str.split(";", -1);
            return new PublicCardState(listCardSerde.deserialize(noSeparator[0]), intSerde.deserialize(noSeparator[1]), intSerde.deserialize(noSeparator[2]));
          }
          );


  public final static Serde<PublicPlayerState> publicPlayerStateSerde = Serde.of(
          i -> String.join(";", intSerde.serialize(i.ticketCount()), intSerde.serialize(i.cardCount()), listRouteSerde.serialize(i.routes())),
          str ->{
            String[] noSeparator = str.split(";", -1);
            return new PublicPlayerState(intSerde.deserialize(noSeparator[0]), intSerde.deserialize(noSeparator[1]), listRouteSerde.deserialize(noSeparator[2]));
          }


  );

  public final static Serde<PlayerState> playerStateSerde =Serde.of(
          i -> String.join(";", sbTicketSerde.serialize(i.tickets()), sbCardSerde.serialize(i.cards()), listRouteSerde.serialize(i.routes())),
          str -> {
            String[] noSeparator = str.split(";", -1);
            return new PlayerState(sbTicketSerde.deserialize(noSeparator[0]), sbCardSerde.deserialize(noSeparator[1]), listRouteSerde.deserialize(noSeparator[2]));
          }
  );

  public final static Serde<PublicGameState> publicGameStateSerde = Serde.of(
          i -> String.join(":", intSerde.serialize(i.ticketsCount()), publicCardStateSerde.serialize(i.cardState()), playerIdSerde.serialize(i.currentPlayerId()) ,
                  publicPlayerStateSerde.serialize(i.playerState(PlayerId.PLAYER_1)), publicPlayerStateSerde.serialize(i.playerState(PlayerId.PLAYER_2)), playerIdSerde.serialize(i.lastPlayer())),
          str-> {
            String[] noSeparator = str.split(";", -1);
            Map<PlayerId, PublicPlayerState> playerIdPlayerStateMap = new HashMap<>();
            playerIdPlayerStateMap.put(PlayerId.PLAYER_1, publicPlayerStateSerde.deserialize(noSeparator[3]));
            playerIdPlayerStateMap.put(PlayerId.PLAYER_2, publicPlayerStateSerde.deserialize(noSeparator[4]));
            return new PublicGameState(intSerde.deserialize(noSeparator[0]), publicCardStateSerde.deserialize(noSeparator[1]), playerIdSerde.deserialize(noSeparator[2]),
                   playerIdPlayerStateMap, playerIdSerde.deserialize(noSeparator[5]));
          }
  );


}
