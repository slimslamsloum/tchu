package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class arbitraryTest {

    @Test
    public void testingTheTestingMethods() {
        List<Character> chars = List.of('a', 'b', 'c', 'd', 'e', 'f');
        List<String> possibles = cartesianProductStrings(chars, 2);
        assertEquals((int) Math.pow(chars.size(), 2), possibles.size());
        assertEquals(200, getAllChars(200).size());

        List<List<String>> cartesianProductItems = cartesianProductList(List.of("a","b","c","d"), 3);
        assertEquals(Math.pow(4,3), cartesianProductItems.size());

        List<List<String>> allPossibilities = allPossibilities(List.of("a","b","c"), 3);
        assertEquals(Math.pow(3,3) + Math.pow(3, 2) + Math.pow(3, 1), allPossibilities.size());
    }

    @Test
    public void integerSerde() {
        for(int i = -500; i < 500; i++) {
            assertEquals(i, Serdes.intSerde.deserialize(Serdes.intSerde.serialize(i)));
        }
    }

    @Test
    public void stringSerde() {
        List<String> possibleChars = cartesianProductStrings(getAllChars(128), 3);
        for(String str : possibleChars) {
            assertEquals(str, Serdes.stringSerde.deserialize(Serdes.stringSerde.serialize(str)));
        }
    }

    @Test
    public void playerIdSerde() {
        for(PlayerId pid : PlayerId.ALL) {
            assertEquals(pid, Serdes.playerIdSerde.deserialize(Serdes.playerIdSerde.serialize(pid)));
        }
    }

    @Test
    public void turnKindSerde() {
        for(Player.TurnKind pid : Player.TurnKind.ALL) {
            assertEquals(pid, Serdes.turnKindSerde.deserialize(Serdes.turnKindSerde.serialize(pid)));
        }
    }

    @Test
    public void cardSerde() {
        for(Card pid : Card.ALL) {
            assertEquals(pid, Serdes.cardSerde.deserialize(Serdes.cardSerde.serialize(pid)));
        }
    }

    @Test
    public void routeSerde() {
        for(Route route : ChMap.routes()) {
            assertEquals(route, Serdes.routeSerde.deserialize(Serdes.routeSerde.serialize(route)));
        }
    }

    @Test
    public void ticketsSerde() {
        for(Ticket ticket : ChMap.tickets()) {
            assertEquals(ticket, Serdes.ticketSerde.deserialize(Serdes.ticketSerde.serialize(ticket)));
        }
    }

    @Test
    public void listStringSerde() {
        List<String> strings = cartesianProductStrings(getAllChars(10), 1);

        assertEquals(Serdes.listStringSerde.deserialize(Serdes.listStringSerde.serialize(Collections.emptyList())), Collections.emptyList());
        checkListSerde(Serdes.listStringSerde, strings, 7);
    }

    @Test
    public void listCardSerde() {
        checkListSerde(Serdes.listCardSerde, Card.ALL, 3);
    }

    @Test
    public void listRouteSerde() {
        checkListSerde(Serdes.listRouteSerde, ChMap.routes(), 4);
    }

    @Test
    public void sortedBagCard() {
        checkSortedBagSerde(Serdes.sbCardSerde, Card.ALL, 4);
    }

    @Test
    public void sortedBagTicket() {
        checkSortedBagSerde(Serdes.sbTicketSerde, ChMap.tickets(), 4);
    }

    @Test
    public void listSortedBagCard() {
        checkListSerde(Serdes.listSbCardSerde, cartesianProductList(Card.ALL, 3).stream().map(SortedBag::of).distinct().collect(Collectors.toList()), 3);
    }

    @Test
    public void publicCardState() {
        List<List<Card>> faceUpCardsPossibilities = cartesianProductList(Card.ALL, 3);

        for(List<Card> faceUpCards : faceUpCardsPossibilities) {
            faceUpCards.add(Card.LOCOMOTIVE);
            faceUpCards.add(Card.ORANGE);
            for (int deckSize = 0; deckSize < 20; deckSize++) {
                for (int discardsSize = 0; discardsSize < 20; discardsSize++) {
                    PublicCardState cs = new PublicCardState(faceUpCards, deckSize, discardsSize);
                    PublicCardState deserializedCs = Serdes.publicCardStateSerde.deserialize(Serdes.publicCardStateSerde.serialize(cs));
                    assertEquals(cs.faceUpCards(), deserializedCs.faceUpCards());
                    assertEquals(cs.deckSize(), deserializedCs.deckSize());
                    assertEquals(cs.discardsSize(), deserializedCs.discardsSize());
                }
            }
        }
    }


    @Test
    public void publicPlayerState() {
        List<List<Route>> routePossibilities = allPossibilities(ChMap.routes(), 2);
        routePossibilities.add(List.of(ChMap.routes().get(2), ChMap.routes().get(3), ChMap.routes().get(25)));
        for(List<Route> routePossibility: routePossibilities) {
            for (int ticketCount = 0; ticketCount < 10; ticketCount++) {
                for (int cardCount = 0; cardCount < 10; cardCount++) {
                    PublicPlayerState ps = new PublicPlayerState(ticketCount, cardCount, routePossibility);
                    PublicPlayerState deserializedPs = Serdes.publicPlayerStateSerde.deserialize(Serdes.publicPlayerStateSerde.serialize(ps));
                    assertEquals(ps.ticketCount(), deserializedPs.ticketCount());
                    assertEquals(ps.cardCount(), deserializedPs.cardCount());
                    assertEquals(ps.routes(), deserializedPs.routes());
                }
            }
        }
    }

    @Test
    public void playerState() {
        List<List<Route>> routePossibilities = allPossibilities(ChMap.routes().subList(0,10), 2).subList(0, 10);
        routePossibilities.add(List.of(ChMap.routes().get(2), ChMap.routes().get(3), ChMap.routes().get(25)));
        List<List<Card>> cardsPossibilities = cartesianProductList(Card.ALL.subList(0,4), 3).subList(0, 10);
        List<List<Ticket>> ticketsPossibilities = allPossibilities(ChMap.tickets().subList(0,5), 5).subList(0, 10);
        for(List<Route> routesPossibility: routePossibilities) {
            for(List<Card> cardsPossibility: cardsPossibilities) {
                SortedBag<Card> cards = SortedBag.of(cardsPossibility);
                for(List<Ticket> ticketPossibility: ticketsPossibilities) {
                    SortedBag<Ticket> tickets = SortedBag.of(ticketPossibility);
                    PlayerState p = new PlayerState(tickets, cards, routesPossibility);
                    PlayerState deserializedPs = Serdes.playerStateSerde.deserialize(Serdes.playerStateSerde.serialize(p));
                    assertEquals(p.tickets(), deserializedPs.tickets());
                    assertEquals(p.cards(), deserializedPs.cards());
                    assertEquals(p.routes(), deserializedPs.routes());
                }
            }
        }
    }

    @Test
    public void testVoles() {
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1), PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gameState = new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", Serdes.publicGameStateSerde.serialize(gameState));
        PublicGameState deserializedGs = Serdes.publicGameStateSerde.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:");


        assertEquals(gameState.ticketsCount(), deserializedGs.ticketsCount());

        PublicCardState deserializedCs = deserializedGs.cardState();
        assertEquals(cs.faceUpCards(), deserializedCs.faceUpCards());
        assertEquals(cs.deckSize(), deserializedCs.deckSize());
        assertEquals(cs.discardsSize(), deserializedCs.discardsSize());

        assertEquals(gameState.currentPlayerId(), deserializedGs.currentPlayerId());
        // checking player1
        PublicPlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
        PublicPlayerState playerStateSerialized1 = deserializedGs.playerState(PlayerId.PLAYER_1);
        assertEquals(playerState1.ticketCount(), playerStateSerialized1.ticketCount());
        assertEquals(playerState1.cardCount(), playerStateSerialized1.cardCount());
        assertEquals(playerState1.routes(), playerStateSerialized1.routes());
        // checking player2
        PublicPlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);
        PublicPlayerState playerStateSerialized2 = deserializedGs.playerState(PlayerId.PLAYER_2);
        assertEquals(playerState2.ticketCount(), playerStateSerialized2.ticketCount());
        assertEquals(playerState2.cardCount(), playerStateSerialized2.cardCount());
        assertEquals(playerState2.routes(), playerStateSerialized2.routes());
        // checking lastplayer
        assertEquals(gameState.lastPlayer(), deserializedGs.lastPlayer());
    }

    @Test
    public void publicGameState() {
        List<List<Card>> faceUpCardsPossibilities = cartesianProductList(Card.ALL.subList(0,5), 5).subList(0, 50);
        List<PublicCardState> pcsPossibilities = new ArrayList<>();

        for(List<Card> faceUpCards : faceUpCardsPossibilities) {
            for (int deckSize = 0; deckSize < 2; deckSize++) {
                for (int discardsSize = 0; discardsSize < 1; discardsSize++) {
                    pcsPossibilities.add(new PublicCardState(faceUpCards, deckSize, discardsSize));
                }
            }
        }

        List<PublicPlayerState> playerStatePossibilities = new ArrayList<>();

        List<List<Route>> routePossibilities = allPossibilities(ChMap.routes().subList(0,4), 5).subList(0, 25);
        for(List<Route> routePossibility: routePossibilities) {
            for (int ticketCount = 0; ticketCount < 5; ticketCount++) {
                for (int cardCount = 0; cardCount < 7; cardCount++) {
                    playerStatePossibilities.add(new PublicPlayerState(ticketCount, cardCount, routePossibility));
                }
            }
        }

        Collections.shuffle(playerStatePossibilities);
        List<List<PublicPlayerState>> playerStates = cartesianProductList(playerStatePossibilities.subList(0,10), 2).subList(0, 25);

        List<PlayerId> playerIdsWithNull = new ArrayList<>(PlayerId.ALL);
        playerIdsWithNull.add(null);

        for(PlayerId currentPlayer : PlayerId.ALL) {
            for (PlayerId lastPlayer : playerIdsWithNull) {
                for (PublicCardState cardState : pcsPossibilities) {
                    for (List<PublicPlayerState> ps : playerStates) {
                        for (int ticketsCount = 0; ticketsCount < 3; ticketsCount++) {
                            PublicGameState gameState = new PublicGameState(ticketsCount, cardState, currentPlayer, Map.of(PlayerId.PLAYER_1, ps.get(0), PlayerId.PLAYER_2, ps.get(1)), lastPlayer);
                            PublicGameState deserializedGs = Serdes.publicGameStateSerde.deserialize(Serdes.publicGameStateSerde.serialize(gameState));

                            assertEquals(gameState.ticketsCount(), deserializedGs.ticketsCount());

                            PublicCardState cs = gameState.cardState();
                            PublicCardState deserializedCs = deserializedGs.cardState();
                            assertEquals(cs.faceUpCards(), deserializedCs.faceUpCards());
                            assertEquals(cs.deckSize(), deserializedCs.deckSize());
                            assertEquals(cs.discardsSize(), deserializedCs.discardsSize());

                            assertEquals(gameState.currentPlayerId(), deserializedGs.currentPlayerId());
                            // checking player1
                            PublicPlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
                            PublicPlayerState playerStateSerialized1 = deserializedGs.playerState(PlayerId.PLAYER_1);
                            assertEquals(playerState1.ticketCount(), playerStateSerialized1.ticketCount());
                            assertEquals(playerState1.cardCount(), playerStateSerialized1.cardCount());
                            assertEquals(playerState1.routes(), playerStateSerialized1.routes());
                            // checking player2
                            PublicPlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);
                            PublicPlayerState playerStateSerialized2 = deserializedGs.playerState(PlayerId.PLAYER_2);
                            assertEquals(playerState2.ticketCount(), playerStateSerialized2.ticketCount());
                            assertEquals(playerState2.cardCount(), playerStateSerialized2.cardCount());
                            assertEquals(playerState2.routes(), playerStateSerialized2.routes());
                            // checking lastplayer
                            assertEquals(gameState.lastPlayer(), deserializedGs.lastPlayer());
                        }
                    }
                }
            }
        }
    }

    public <T extends Comparable<T>> void checkSortedBagSerde(Serde<SortedBag<T>> serde, List<T> elementsForCartesianProduct, int cartesianProductLimit) {
        for(int i = 1; i < cartesianProductLimit; i++) {
            List<SortedBag<T>> possibleCards = cartesianProductList(elementsForCartesianProduct, i).stream().map(SortedBag::of).distinct().collect(Collectors.toList());

            for(SortedBag<T> testingElement : possibleCards) {
                SortedBag<T> deserializedElement = serde.deserialize(serde.serialize(testingElement));

                assertEquals(testingElement, deserializedElement);
            }
        }
    }

    public <T> void checkListSerde(Serde<List<T>> serde, List<T> elementsForCartesianProduct, int cartesianProductLimit) {
        for(int i = 1; i < cartesianProductLimit; i++) {
            List<List<T>> possibleCards = cartesianProductList(elementsForCartesianProduct, i);

            for(List<T> testingElement : possibleCards) {
                List<T> deserializedElement = serde.deserialize(serde.serialize(testingElement));

                assertEquals(testingElement, deserializedElement);
            }
        }
    }

    public List<Character> getAllChars(int max) {
        return IntStream.range(0, max).mapToObj(n -> ((char) n)).collect(Collectors.toList());
    }

    public List<String> cartesianProductStrings(List<Character> charactersList, int maxLength) {
        List<String> ret = charactersList.stream().map(d -> Character.toString(d)).collect(Collectors.toList());

        int max = maxLength - 1;
        for(int i = 0; i < max; i++) { // doing it max times
            List<String> newList = new ArrayList<>(); // the new list containing the new elements

            for (String s : ret) {
                for (Character character : charactersList) {
                    newList.add(s + character);
                }
            }

            ret = newList;
        }

        return ret;
    }

    public <T> List<List<T>> allPossibilities(List<T> elements, int maxLength) {
        return IntStream.range(1, maxLength + 1).mapToObj(d -> cartesianProductList(elements, d)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public <T> List<List<T>> cartesianProductList(List<T> elements, int length) {
        List<List<T>> ret = elements.stream().map(List::of).collect(Collectors.toList());

        length--;
        for(int i = 0; i < length; i++) {
            List<List<T>> newList = new ArrayList<>();

            for(List<T> t : ret) {
                for(T t1: elements) {
                    newList.add(Stream.concat(t.stream(), Stream.of(t1)).collect(Collectors.toList()));
                }
            }

            ret = newList;
        }

        return ret;
    }
}
