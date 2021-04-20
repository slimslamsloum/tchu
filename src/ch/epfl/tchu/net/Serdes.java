package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Serdes {

  public final static Serde<Integer> intSerde = Serde.of(
          i -> Integer.toString(i),
          Integer::parseInt);

  public final static Serde<String> stringSerde=Serde.of(
          i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
          i -> Arrays.toString(Base64.getDecoder().decode(new String(i.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))));

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


          );


  public final static Serde<PublicPlayerState> publicPlayerStateSerde = Serde.of(
          i -> String.join(";", intSerde.serialize(i.ticketCount()), intSerde.serialize(i.cardCount()), listRouteSerde.serialize(i.routes())),


  );

  public final static Serde<PlayerState> playerStateSerde =Serde.of(
          i -> String.join(";", sbTicketSerde.serialize(i.tickets()), sbCardSerde.serialize(i.cards()), listRouteSerde.serialize(i.routes())),
  );

  public final static Serde<PublicGameState> publicGameStateSerde = Serde.of(
          i -> String.join(":", intSerde.serialize(i.ticketsCount()), publicCardStateSerde.serialize(i.cardState()), , playerStateSerde.serialize(i.playerState(PlayerId.PLAYER_1)),       )

  );


}
