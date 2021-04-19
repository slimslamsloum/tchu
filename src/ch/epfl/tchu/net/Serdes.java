package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import java.util.Base64;
import java.util.List;

public class Serdes {

  public final static Serde<Integer> intSerde = Serde.of(
          i -> Integer.toString(i),
          Integer::parseInt);

  public final static Serde<Card> cardSerde = Serde.oneOf(Card.ALL);

  public final static Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());

  public final static Serde<Ticket> ticketSerde = Serde.oneOf(ChMap.tickets());

  public final static Serde<List<Card>> listCardSerde = Serde.listOf(cardSerde, ",");

  public final static Serde<List<Card>> listRouteSerde = Serde.listOf(routeSerde, ",");

  public final static Serde<List<Card>> listStringSerde = Serde.listOf(stringSerde, ",");

  public final static Serde<SortedBag<Card>> sbCardSerde = Serde.bagOf(cardSerde, ",");

  public final static Serde<SortedBag<Ticket>> sbTicketSerde = Serde.bagOf(ticketSerde, ",");

  public final static Serde<List<SortedBag<Card>>> listSbCardSerde = Serde.listOf(sbCardSerde, ",");


}
