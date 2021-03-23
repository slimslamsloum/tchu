package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class arbitraryTest {
    private static final int INITIAL_CARD_COUNT = 4;
    private static final int TOTAL_CAR_COUNT = 40;

    private static final List<Color> COLORS =
            List.of(
                    Color.BLACK,
                    Color.VIOLET,
                    Color.BLUE,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.ORANGE,
                    Color.RED,
                    Color.WHITE);
    private static final List<Card> CAR_CARDS =
            List.of(
                    Card.BLACK,
                    Card.VIOLET,
                    Card.BLUE,
                    Card.GREEN,
                    Card.YELLOW,
                    Card.ORANGE,
                    Card.RED,
                    Card.WHITE);

    @Test
    void playerStateInitialReturnsCorrectInitialState() {
        var initialCards = SortedBag.of(INITIAL_CARD_COUNT, Card.BLUE);
        var s = PlayerState.initial(initialCards);
        assertEquals(SortedBag.of(), s.tickets());
        assertEquals(initialCards, s.cards());
        assertEquals(List.of(), s.routes());
        assertEquals(TOTAL_CAR_COUNT, s.carCount());
        assertEquals(0, s.claimPoints());
        assertEquals(0, s.ticketPoints());
        assertEquals(0, s.finalPoints());
    }

    @Test
    void playerStateConstructorWorks() {
        var rng = TestRandomizer.newRandom();
        var chMap = new ChMap();
        var routes = new ArrayList<>(chMap.ALL_ROUTES);
        var tickets = new ArrayList<>(chMap.ALL_TICKETS);
        var cards = new ArrayList<>(shuffledCards(rng));
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            Collections.shuffle(routes, rng);
            Collections.shuffle(tickets, rng);
            Collections.shuffle(cards, rng);

            var routesCount = rng.nextInt(7);
            var ticketsCount = rng.nextInt(tickets.size());
            var cardsCount = rng.nextInt(cards.size());

            var playerRoutes = Collections.unmodifiableList(routes.subList(0, routesCount));
            var playerTickets = SortedBag.of(tickets.subList(0, ticketsCount));
            var playerCards = SortedBag.of(cards.subList(0, cardsCount));

            var playerState = new PlayerState(playerTickets, playerCards, playerRoutes);

            assertEquals(playerRoutes, playerState.routes());
            assertEquals(playerTickets, playerState.tickets());
            assertEquals(ticketsCount, playerState.ticketCount());
            assertEquals(playerCards, playerState.cards());
            assertEquals(cardsCount, playerState.cardCount());
        }
    }

    @Test
    void playerStateWithAddedTicketAddsTicket() {
        var tickets = new ChMap().ALL_TICKETS;
        for (int batchSize = 1; batchSize < 5; batchSize += 1) {
            var playerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
            for (int i = 0; i + batchSize < tickets.size(); i += batchSize) {
                var nextI = i + batchSize;
                var ticketsToAdd = tickets.subList(i, nextI);
                playerState = playerState.withAddedTickets(SortedBag.of(ticketsToAdd));
                assertEquals(SortedBag.of(tickets.subList(0, nextI)), playerState.tickets());
            }
        }
    }

    @Test
    void playerStateWithAddedCardAddsCard() {
        var cards = shuffledCards(TestRandomizer.newRandom());
        var playerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        for (int i = 0; i < cards.size(); i += 1) {
            var cardToAdd = cards.get(i);
            playerState = playerState.withAddedCard(cardToAdd);
            assertEquals(SortedBag.of(cards.subList(0, i + 1)), playerState.cards());
        }
    }

    @Test
    void playerStateWithAddedCardsAddsCards() {
        var cards = shuffledCards(TestRandomizer.newRandom());
        for (int batchSize = 1; batchSize < 5; batchSize += 1) {
            var playerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
            for (int i = 0; i + batchSize < cards.size(); i += batchSize) {
                var nextI = i + batchSize;
                var cardsToAdd = cards.subList(i, nextI);
                playerState = playerState.withAddedCards(SortedBag.of(cardsToAdd));
                assertEquals(SortedBag.of(cards.subList(0, nextI)), playerState.cards());
            }
        }
    }

    @Test
    void playerStateCanClaimRouteWorksWhenNotEnoughCars() {
        var chMap = new ChMap();
        var cards = sixOfEachCard();
        for (Route route : chMap.ALL_ROUTES) {
            for (var usedCars = 30; usedCars <= 40; usedCars++) {
                var routes = routesWithTotalLength(usedCars);
                var playerState = new PlayerState(SortedBag.of(), cards, routes);
                var availableCars = TOTAL_CAR_COUNT - usedCars;

                var claimable = availableCars >= route.length();
                assertEquals(claimable, playerState.canClaimRoute(route));
            }
        }
    }

    @Test
    void playerStatePossibleClaimCardsFailsWhenNotEnoughCars() {
        var chMap = new ChMap();
        var cards = sixOfEachCard();
        for (Route route : chMap.ALL_ROUTES) {
            for (var usedCars = 30; usedCars <= 40; usedCars++) {
                var routes = routesWithTotalLength(usedCars);
                var playerState = new PlayerState(SortedBag.of(), cards, routes);
                var availableCars = TOTAL_CAR_COUNT - usedCars;

                if (availableCars < route.length())
                    assertThrows(IllegalArgumentException.class, () -> {
                        playerState.possibleClaimCards(route);
                    });
            }
        }
    }

    @Test
    void playerStatePossibleClaimCardsWorksForOvergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        var emptyPlayerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        var fullPlayerState = new PlayerState(SortedBag.of(), sixOfEachCard(), List.of());

        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Level.OVERGROUND, color);

                assertEquals(List.of(), emptyPlayerState.possibleClaimCards(r));
                assertEquals(List.of(SortedBag.of(l, card)), fullPlayerState.possibleClaimCards(r));
            }
        }
    }

    @Test
    void playerStatePossibleClaimCardsWorksOnOvergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        var emptyPlayerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        var kbyrlPlayerState = new PlayerState(SortedBag.of(), sixOfKBYRL(), List.of());
        var fullPlayerState = new PlayerState(SortedBag.of(), sixOfEachCard(), List.of());

        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Level.OVERGROUND, null);

            assertEquals(List.of(), emptyPlayerState.possibleClaimCards(r));

            var expectedKBYRL = List.of(
                    SortedBag.of(l, Card.BLACK),
                    SortedBag.of(l, Card.BLUE),
                    SortedBag.of(l, Card.YELLOW),
                    SortedBag.of(l, Card.RED));
            assertEquals(expectedKBYRL, kbyrlPlayerState.possibleClaimCards(r));

            var expectedFull = List.of(
                    SortedBag.of(l, Card.BLACK),
                    SortedBag.of(l, Card.VIOLET),
                    SortedBag.of(l, Card.BLUE),
                    SortedBag.of(l, Card.GREEN),
                    SortedBag.of(l, Card.YELLOW),
                    SortedBag.of(l, Card.ORANGE),
                    SortedBag.of(l, Card.RED),
                    SortedBag.of(l, Card.WHITE));
            assertEquals(expectedFull, fullPlayerState.possibleClaimCards(r));
        }
    }

    @Test
    void playerStatePossibleClaimCardsWorksOnUndergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        var emptyPlayerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        var kbyrlPlayerState = new PlayerState(SortedBag.of(), sixOfKBYRL(), List.of());
        var fullPlayerState = new PlayerState(SortedBag.of(), sixOfEachCard(), List.of());

        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Level.UNDERGROUND, color);

                assertEquals(List.of(), emptyPlayerState.possibleClaimCards(r));

                var expectedFull = new ArrayList<SortedBag<Card>>();
                for (var locomotives = 0; locomotives <= l; locomotives++) {
                    var cars = l - locomotives;
                    expectedFull.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
                assertEquals(expectedFull, fullPlayerState.possibleClaimCards(r));

                if (kbyr().contains(color)) {
                    assertEquals(expectedFull, kbyrlPlayerState.possibleClaimCards(r));
                } else {
                    assertEquals(
                            List.of(SortedBag.of(l, Card.LOCOMOTIVE)),
                            kbyrlPlayerState.possibleClaimCards(r));
                }
            }
        }
    }

    @Test
    void playerStatePossibleAdditionalCardsFailsWithInvalidAdditionalCardsCount() {
        var playerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        for (var additionalCardsCount : List.of(-1, 0, 4, 5, 6)) {
            assertThrows(IllegalArgumentException.class, () -> {
                playerState.possibleAdditionalCards(
                        additionalCardsCount,
                        SortedBag.of(Card.BLUE),
                        SortedBag.of(3, Card.RED));
            });
        }
    }

    @Test
    void playerStatePossibleAdditionalCardsFailsWithInvalidInitialCards() {
        var playerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        assertThrows(IllegalArgumentException.class, () -> {
            playerState.possibleAdditionalCards(
                    1,
                    SortedBag.of(),
                    SortedBag.of(3, Card.RED));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            playerState.possibleAdditionalCards(
                    1,
                    SortedBag.of(List.of(Card.RED, Card.BLUE, Card.WHITE)),
                    SortedBag.of(3, Card.RED));
        });
    }

    @Test
    void playerStatePossibleAdditionalCardsFailsWithInvalidDrawnCards() {
        var playerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        for (var drawnCardsCount : List.of(0, 1, 2, 4, 5)) {
            assertThrows(IllegalArgumentException.class, () -> {
                playerState.possibleAdditionalCards(
                        1,
                        SortedBag.of(Card.BLUE),
                        SortedBag.of(drawnCardsCount, Card.RED));
            });
        }
    }

    @Test
    void playerStatePossibleAdditionalCardsWorksWithoutLocomotivesInHand() {
        var playerCards = sixOfEachCard()
                .difference(SortedBag.of(6, Card.LOCOMOTIVE));
        assert playerCards.countOf(Card.LOCOMOTIVE) == 0;
        var playerState = new PlayerState(SortedBag.of(), playerCards, List.of());

        for (var carCard : CAR_CARDS) {
            for (int routeLength = 1; routeLength <= 6; routeLength++) {
                for (int initialLocCount = 0; initialLocCount < routeLength; initialLocCount++) {
                    var initialCarCardsCount = routeLength - initialLocCount;
                    var initialCards = SortedBag.of(
                            initialCarCardsCount, carCard,
                            initialLocCount, Card.LOCOMOTIVE);
                    for (int additionalCount = 1; additionalCount <= 3; additionalCount++) {
                        var totalCarCardsCount = initialCarCardsCount + additionalCount;
                        var otherCard = carCard == Card.RED ? Card.WHITE : Card.RED;
                        var drawnCards = SortedBag.of(
                                additionalCount, carCard,
                                3 - additionalCount, otherCard);
                        var actualPAC = playerState.possibleAdditionalCards(
                                additionalCount,
                                initialCards,
                                drawnCards);
                        var expectedPAC = totalCarCardsCount <= 6
                                ? List.of(SortedBag.of(additionalCount, carCard))
                                : List.<SortedBag<Card>>of();
                        assertEquals(expectedPAC, actualPAC);
                    }
                }
            }
        }
    }

    @Test
    void playerStatePossibleAdditionalCardsWorksWithLocomotivesInHand() {
        var playerState = new PlayerState(SortedBag.of(), sixOfEachCard(), List.of());

        for (var carCard : CAR_CARDS) {
            for (var initialCards : List.of(
                    SortedBag.of(2, carCard),
                    SortedBag.of(1, carCard, 1, Card.LOCOMOTIVE))) {
                var drawnCards = SortedBag.of(3, carCard);
                for (var additionalCardsCount = 1; additionalCardsCount <= 3; additionalCardsCount++) {
                    var actualPAC = playerState
                            .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);
                    var expectedPAC = new ArrayList<SortedBag<Card>>();
                    for (int locoCount = 0; locoCount <= additionalCardsCount; locoCount++) {
                        var carCount = additionalCardsCount - locoCount;
                        expectedPAC.add(SortedBag.of(locoCount, Card.LOCOMOTIVE, carCount, carCard));
                    }
                    assertEquals(expectedPAC, actualPAC);
                }
            }
        }
    }

    @Test
    void playerStatePossibleAdditionalCardsWorksWithOnlyLocomotivesInitials() {
        var playerCards = sixOfEachCard();
        var playerState = new PlayerState(SortedBag.of(), playerCards, List.of());

        for (int routeLength = 1; routeLength <= 6; routeLength++) {
            var initialCards = SortedBag.of(routeLength, Card.LOCOMOTIVE);
            for (int additionalCount = 1; additionalCount <= 3; additionalCount++) {
                var totalCount = initialCards.size() + additionalCount;
                var drawnCards = SortedBag.of(
                        additionalCount, Card.LOCOMOTIVE,
                        3 - additionalCount, Card.BLUE);
                var actualPAC = playerState.possibleAdditionalCards(
                        additionalCount,
                        initialCards,
                        drawnCards);
                var expectedPAC = totalCount <= 6
                        ? List.of(SortedBag.of(additionalCount, Card.LOCOMOTIVE))
                        : List.<SortedBag<Card>>of();
                assertEquals(expectedPAC, actualPAC);
            }
        }
    }

    @Test
    void playerStateWithClaimedRouteWorks() {
        var chMap = new ChMap();
        var cards = sixOfKBYRL();

        var playerState = new PlayerState(SortedBag.of(), cards, List.of());
        var r1 = new Route("AT2_VAD_1", chMap.AT2, chMap.VAD, 1, Level.UNDERGROUND, Color.RED);
        var c1 = SortedBag.of(1, Card.LOCOMOTIVE);
        playerState = playerState.withClaimedRoute(r1, c1);
        cards = cards.difference(c1);
        assertEquals(cards, playerState.cards());
        assertEquals(Set.of(r1), new HashSet<>(playerState.routes()));

        var r2 = new Route("BAL_OLT_1", chMap.BAL, chMap.OLT, 2, Level.UNDERGROUND, Color.ORANGE);
        var c2 = SortedBag.of(2, Card.ORANGE);
        playerState = playerState.withClaimedRoute(r2, c2);
        cards = cards.difference(c2);
        assertEquals(cards, playerState.cards());
        assertEquals(Set.of(r1, r2), new HashSet<>(playerState.routes()));

        var r3 = new Route("DAV_AT3_1", chMap.DAV, chMap.AT3, 3, Level.UNDERGROUND, null);
        var c3 = SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.WHITE);
        playerState = playerState.withClaimedRoute(r3, c3);
        cards = cards.difference(c3);
        assertEquals(cards, playerState.cards());
        assertEquals(Set.of(r1, r2, r3), new HashSet<>(playerState.routes()));

        var r4 = new Route("BRU_DAV_1", chMap.BRU, chMap.DAV, 4, Level.UNDERGROUND, Color.BLUE);
        var c4 = SortedBag.of(3, Card.LOCOMOTIVE, 1, Card.BLUE);
        playerState = playerState.withClaimedRoute(r4, c4);
        cards = cards.difference(c4);
        assertEquals(cards, playerState.cards());
        assertEquals(Set.of(r1, r2, r3, r4), new HashSet<>(playerState.routes()));
    }

    @Test
    void playerStateTicketPointsWorksOnKnownExample1() {
        var chMap = new ChMap();

        var routes = List.of(
                chMap.FR2_GEN_1, chMap.GEN_LAU_1, chMap.LAU_MAR_1,
                chMap.MAR_SIO_1, chMap.BRI_SIO_1, chMap.BRI_IT5_1, chMap.LAU_NEU_1,
                chMap.FRI_LAU_1, chMap.BER_FRI_1, chMap.BER_LUC_1, chMap.LUC_ZOU_1,
                chMap.ZOU_ZUR_1, chMap.STG_ZUR_1, chMap.DE5_STG_1, chMap.STG_VAD_1);

        var s1 = new PlayerState(SortedBag.of(chMap.BER_C), SortedBag.of(), routes);
        assertEquals(8, s1.ticketPoints());

        var s2 = new PlayerState(SortedBag.of(chMap.BAL_BRI), SortedBag.of(), routes);
        assertEquals(-10, s2.ticketPoints());

        var s3 = new PlayerState(SortedBag.of(chMap.IT_C), SortedBag.of(), routes);
        assertEquals(13, s3.ticketPoints());

        var s4 = new PlayerState(SortedBag.of(2, chMap.FR_C), SortedBag.of(), routes);
        assertEquals(2 * 11, s4.ticketPoints());

        var s5 = new PlayerState(SortedBag.of(1, chMap.GEN_ZUR, 1, chMap.INT_WIN), SortedBag.of(), routes);
        assertEquals(14 - 7, s5.ticketPoints());
    }

    @Test
    void playerStateTicketPointsWorksOnKnownExample2() {
        var chMap = new ChMap();
        var routes = List.of(
                chMap.BER_NEU_1, chMap.BER_SOL_1, chMap.OLT_SOL_1, chMap.BAD_OLT_1,
                chMap.BAD_ZUR_1, chMap.WIN_ZUR_1, chMap.PFA_ZUR_1, chMap.LUC_OLT_1,
                chMap.LUC_SCZ_1, chMap.SCZ_WAS_1, chMap.BEL_WAS_1, chMap.BEL_LUG_1,
                chMap.COI_WAS_1, chMap.COI_SAR_1, chMap.COI_DAV_1, chMap.DAV_AT3_1,
                chMap.BRU_COI_1);

        var s1 = new PlayerState(SortedBag.of(chMap.NEU_WIN), SortedBag.of(), routes);
        assertEquals(9, s1.ticketPoints());

        var s2 = new PlayerState(SortedBag.of(chMap.BER_LUG), SortedBag.of(), routes);
        assertEquals(12, s2.ticketPoints());

        var s3 = new PlayerState(SortedBag.of(chMap.AT_C), SortedBag.of(), routes);
        assertEquals(-5, s3.ticketPoints());

        var s4 = new PlayerState(SortedBag.of(2, chMap.IT_C), SortedBag.of(), routes);
        assertEquals(2 * -6, s4.ticketPoints());

        var s5 = new PlayerState(SortedBag.of(1, chMap.BER_ZUR, 1, chMap.STG_BRU), SortedBag.of(), routes);
        assertEquals(6 - 9, s5.ticketPoints());
    }

    @Test
    void playerStateTicketPointsWorksOnKnownExample3() {
        var chMap = new ChMap();

        var routes = List.of(
                chMap.FRI_LAU_2, chMap.BER_FRI_2, chMap.BER_LUC_2, chMap.LUC_ZOU_2,
                chMap.ZOU_ZUR_2, chMap.OLT_ZUR_1, chMap.BAL_OLT_1, chMap.BAL_DE1_1,
                chMap.BAL_DEL_1, chMap.SCZ_ZOU_2, chMap.SCZ_WAS_2, chMap.BEL_WAS_2,
                chMap.BEL_LUG_2, chMap.PFA_SCZ_1, chMap.PFA_SAR_1, chMap.SAR_VAD_1,
                chMap.AT2_VAD_1);

        var s1 = new PlayerState(SortedBag.of(chMap.LAU_LUC), SortedBag.of(), routes);
        assertEquals(8, s1.ticketPoints());

        var s2 = new PlayerState(SortedBag.of(chMap.LAU_STG), SortedBag.of(), routes);
        assertEquals(-13, s2.ticketPoints());

        var s3 = new PlayerState(SortedBag.of(chMap.BER_C), SortedBag.of(), routes);
        assertEquals(11, s3.ticketPoints());

        var s4 = new PlayerState(SortedBag.of(2, chMap.AT_C), SortedBag.of(), routes);
        assertEquals(2 * 5, s4.ticketPoints());

        var s5 = new PlayerState(SortedBag.of(1, chMap.ZUR_VAD, 1, chMap.OLT_SCE), SortedBag.of(), routes);
        assertEquals(6 - 5, s5.ticketPoints());
    }

    @Test
    void playerStateFinalPointsWorksOnKnownExample1() {
        var chMap = new ChMap();

        var routes = List.of(
                chMap.FR2_GEN_1, chMap.GEN_LAU_1, chMap.LAU_MAR_1,
                chMap.MAR_SIO_1, chMap.BRI_SIO_1, chMap.BRI_IT5_1, chMap.LAU_NEU_1,
                chMap.FRI_LAU_1, chMap.BER_FRI_1, chMap.BER_LUC_1, chMap.LUC_ZOU_1,
                chMap.ZOU_ZUR_1, chMap.STG_ZUR_1, chMap.DE5_STG_1, chMap.STG_VAD_1);
        var claimPoints = routes.stream()
                .mapToInt(Route::claimPoints)
                .sum();

        var s1 = new PlayerState(SortedBag.of(chMap.BER_C), SortedBag.of(), routes);
        assertEquals(claimPoints + 8, s1.finalPoints());

        var s2 = new PlayerState(SortedBag.of(chMap.BAL_BRI), SortedBag.of(), routes);
        assertEquals(claimPoints - 10, s2.finalPoints());

        var s3 = new PlayerState(SortedBag.of(chMap.IT_C), SortedBag.of(), routes);
        assertEquals(claimPoints + 13, s3.finalPoints());

        var s4 = new PlayerState(SortedBag.of(2, chMap.FR_C), SortedBag.of(), routes);
        assertEquals(claimPoints + 2 * 11, s4.finalPoints());

        var s5 = new PlayerState(SortedBag.of(1, chMap.GEN_ZUR, 1, chMap.INT_WIN), SortedBag.of(), routes);
        assertEquals(claimPoints + 14 - 7, s5.finalPoints());
    }

    private static List<Route> routesWithTotalLength(int length) {
        var routes = new ArrayList<Route>();
        for (int i = 0; i < length; i++) {
            var s1 = new Station(2 * i, "From" + i);
            var s2 = new Station(2 * i + 1, "To" + i);
            routes.add(new Route("r" + i, s1, s2, 1, Level.OVERGROUND, Color.ORANGE));
        }
        return Collections.unmodifiableList(routes);
    }

    private static List<Card> shuffledCards(Random rng) {
        SortedBag<Card> cards = sixOfEachCard();
        var shuffledCards = new ArrayList<>(cards.toList());
        Collections.shuffle(shuffledCards, rng);
        return Collections.unmodifiableList(shuffledCards);
    }

    private static Set<Color> kbyr() {
        return Set.of(Color.BLACK, Color.BLUE, Color.YELLOW, Color.RED);
    }

    private static SortedBag<Card> sixOfKBYRL() {
        return new SortedBag.Builder<Card>()
                .add(6, Card.BLACK)
                .add(6, Card.BLUE)
                .add(6, Card.YELLOW)
                .add(6, Card.RED)
                .add(6, Card.LOCOMOTIVE)
                .build();
    }

    private static SortedBag<Card> sixOfEachCard() {
        return new SortedBag.Builder<Card>()
                .add(6, Card.BLACK)
                .add(6, Card.VIOLET)
                .add(6, Card.BLUE)
                .add(6, Card.GREEN)
                .add(6, Card.YELLOW)
                .add(6, Card.ORANGE)
                .add(6, Card.RED)
                .add(6, Card.WHITE)
                .add(6, Card.LOCOMOTIVE)
                .build();
    }

    private static final class ChMap {
        //region Stations
        final Station BAD = new Station(0, "Baden");
        final Station BAL = new Station(1, "Bâle");
        final Station BEL = new Station(2, "Bellinzone");
        final Station BER = new Station(3, "Berne");
        final Station BRI = new Station(4, "Brigue");
        final Station BRU = new Station(5, "Brusio");
        final Station COI = new Station(6, "Coire");
        final Station DAV = new Station(7, "Davos");
        final Station DEL = new Station(8, "Delémont");
        final Station FRI = new Station(9, "Fribourg");
        final Station GEN = new Station(10, "Genève");
        final Station INT = new Station(11, "Interlaken");
        final Station KRE = new Station(12, "Kreuzlingen");
        final Station LAU = new Station(13, "Lausanne");
        final Station LCF = new Station(14, "La Chaux-de-Fonds");
        final Station LOC = new Station(15, "Locarno");
        final Station LUC = new Station(16, "Lucerne");
        final Station LUG = new Station(17, "Lugano");
        final Station MAR = new Station(18, "Martigny");
        final Station NEU = new Station(19, "Neuchâtel");
        final Station OLT = new Station(20, "Olten");
        final Station PFA = new Station(21, "Pfäffikon");
        final Station SAR = new Station(22, "Sargans");
        final Station SCE = new Station(23, "Schaffhouse");
        final Station SCZ = new Station(24, "Schwyz");
        final Station SIO = new Station(25, "Sion");
        final Station SOL = new Station(26, "Soleure");
        final Station STG = new Station(27, "Saint-Gall");
        final Station VAD = new Station(28, "Vaduz");
        final Station WAS = new Station(29, "Wassen");
        final Station WIN = new Station(30, "Winterthour");
        final Station YVE = new Station(31, "Yverdon");
        final Station ZOU = new Station(32, "Zoug");
        final Station ZUR = new Station(33, "Zürich");

        final Station DE1 = new Station(34, "Allemagne");
        final Station DE2 = new Station(35, "Allemagne");
        final Station DE3 = new Station(36, "Allemagne");
        final Station DE4 = new Station(37, "Allemagne");
        final Station DE5 = new Station(38, "Allemagne");
        final Station AT1 = new Station(39, "Autriche");
        final Station AT2 = new Station(40, "Autriche");
        final Station AT3 = new Station(41, "Autriche");
        final Station IT1 = new Station(42, "Italie");
        final Station IT2 = new Station(43, "Italie");
        final Station IT3 = new Station(44, "Italie");
        final Station IT4 = new Station(45, "Italie");
        final Station IT5 = new Station(46, "Italie");
        final Station FR1 = new Station(47, "France");
        final Station FR2 = new Station(48, "France");
        final Station FR3 = new Station(49, "France");
        final Station FR4 = new Station(50, "France");

        final List<Station> DE = List.of(DE1, DE2, DE3, DE4, DE5);
        final List<Station> AT = List.of(AT1, AT2, AT3);
        final List<Station> IT = List.of(IT1, IT2, IT3, IT4, IT5);
        final List<Station> FR = List.of(FR1, FR2, FR3, FR4);

        final List<Station> ALL_STATIONS = List.of(
                BAD, BAL, BEL, BER, BRI, BRU, COI, DAV, DEL, FRI, GEN, INT, KRE, LAU, LCF, LOC, LUC,
                LUG, MAR, NEU, OLT, PFA, SAR, SCE, SCZ, SIO, SOL, STG, VAD, WAS, WIN, YVE, ZOU, ZUR,
                DE1, DE2, DE3, DE4, DE5, AT1, AT2, AT3, IT1, IT2, IT3, IT4, IT5, FR1, FR2, FR3, FR4);
        //endregion

        //region Routes
        final Route AT1_STG_1 = new Route("AT1_STG_1", AT1, STG, 4, Level.UNDERGROUND, null);
        final Route AT2_VAD_1 = new Route("AT2_VAD_1", AT2, VAD, 1, Level.UNDERGROUND, Color.RED);
        final Route BAD_BAL_1 = new Route("BAD_BAL_1", BAD, BAL, 3, Level.UNDERGROUND, Color.RED);
        final Route BAD_OLT_1 = new Route("BAD_OLT_1", BAD, OLT, 2, Level.OVERGROUND, Color.VIOLET);
        final Route BAD_ZUR_1 = new Route("BAD_ZUR_1", BAD, ZUR, 1, Level.OVERGROUND, Color.YELLOW);
        final Route BAL_DE1_1 = new Route("BAL_DE1_1", BAL, DE1, 1, Level.UNDERGROUND, Color.BLUE);
        final Route BAL_DEL_1 = new Route("BAL_DEL_1", BAL, DEL, 2, Level.UNDERGROUND, Color.YELLOW);
        final Route BAL_OLT_1 = new Route("BAL_OLT_1", BAL, OLT, 2, Level.UNDERGROUND, Color.ORANGE);
        final Route BEL_LOC_1 = new Route("BEL_LOC_1", BEL, LOC, 1, Level.UNDERGROUND, Color.BLACK);
        final Route BEL_LUG_1 = new Route("BEL_LUG_1", BEL, LUG, 1, Level.UNDERGROUND, Color.RED);
        final Route BEL_LUG_2 = new Route("BEL_LUG_2", BEL, LUG, 1, Level.UNDERGROUND, Color.YELLOW);
        final Route BEL_WAS_1 = new Route("BEL_WAS_1", BEL, WAS, 4, Level.UNDERGROUND, null);
        final Route BEL_WAS_2 = new Route("BEL_WAS_2", BEL, WAS, 4, Level.UNDERGROUND, null);
        final Route BER_FRI_1 = new Route("BER_FRI_1", BER, FRI, 1, Level.OVERGROUND, Color.ORANGE);
        final Route BER_FRI_2 = new Route("BER_FRI_2", BER, FRI, 1, Level.OVERGROUND, Color.YELLOW);
        final Route BER_INT_1 = new Route("BER_INT_1", BER, INT, 3, Level.OVERGROUND, Color.BLUE);
        final Route BER_LUC_1 = new Route("BER_LUC_1", BER, LUC, 4, Level.OVERGROUND, null);
        final Route BER_LUC_2 = new Route("BER_LUC_2", BER, LUC, 4, Level.OVERGROUND, null);
        final Route BER_NEU_1 = new Route("BER_NEU_1", BER, NEU, 2, Level.OVERGROUND, Color.RED);
        final Route BER_SOL_1 = new Route("BER_SOL_1", BER, SOL, 2, Level.OVERGROUND, Color.BLACK);
        final Route BRI_INT_1 = new Route("BRI_INT_1", BRI, INT, 2, Level.UNDERGROUND, Color.WHITE);
        final Route BRI_IT5_1 = new Route("BRI_IT5_1", BRI, IT5, 3, Level.UNDERGROUND, Color.GREEN);
        final Route BRI_LOC_1 = new Route("BRI_LOC_1", BRI, LOC, 6, Level.UNDERGROUND, null);
        final Route BRI_SIO_1 = new Route("BRI_SIO_1", BRI, SIO, 3, Level.UNDERGROUND, Color.BLACK);
        final Route BRI_WAS_1 = new Route("BRI_WAS_1", BRI, WAS, 4, Level.UNDERGROUND, Color.RED);
        final Route BRU_COI_1 = new Route("BRU_COI_1", BRU, COI, 5, Level.UNDERGROUND, null);
        final Route BRU_DAV_1 = new Route("BRU_DAV_1", BRU, DAV, 4, Level.UNDERGROUND, Color.BLUE);
        final Route BRU_IT2_1 = new Route("BRU_IT2_1", BRU, IT2, 2, Level.UNDERGROUND, Color.GREEN);
        final Route COI_DAV_1 = new Route("COI_DAV_1", COI, DAV, 2, Level.UNDERGROUND, Color.VIOLET);
        final Route COI_SAR_1 = new Route("COI_SAR_1", COI, SAR, 1, Level.UNDERGROUND, Color.WHITE);
        final Route COI_WAS_1 = new Route("COI_WAS_1", COI, WAS, 5, Level.UNDERGROUND, null);
        final Route DAV_AT3_1 = new Route("DAV_AT3_1", DAV, AT3, 3, Level.UNDERGROUND, null);
        final Route DAV_IT1_1 = new Route("DAV_IT1_1", DAV, IT1, 3, Level.UNDERGROUND, null);
        final Route DAV_SAR_1 = new Route("DAV_SAR_1", DAV, SAR, 3, Level.UNDERGROUND, Color.BLACK);
        final Route DE2_SCE_1 = new Route("DE2_SCE_1", DE2, SCE, 1, Level.OVERGROUND, Color.YELLOW);
        final Route DE3_KRE_1 = new Route("DE3_KRE_1", DE3, KRE, 1, Level.OVERGROUND, Color.ORANGE);
        final Route DE4_KRE_1 = new Route("DE4_KRE_1", DE4, KRE, 1, Level.OVERGROUND, Color.WHITE);
        final Route DE5_STG_1 = new Route("DE5_STG_1", DE5, STG, 2, Level.OVERGROUND, null);
        final Route DEL_FR4_1 = new Route("DEL_FR4_1", DEL, FR4, 2, Level.UNDERGROUND, Color.BLACK);
        final Route DEL_LCF_1 = new Route("DEL_LCF_1", DEL, LCF, 3, Level.UNDERGROUND, Color.WHITE);
        final Route DEL_SOL_1 = new Route("DEL_SOL_1", DEL, SOL, 1, Level.UNDERGROUND, Color.VIOLET);
        final Route FR1_MAR_1 = new Route("FR1_MAR_1", FR1, MAR, 2, Level.UNDERGROUND, null);
        final Route FR2_GEN_1 = new Route("FR2_GEN_1", FR2, GEN, 1, Level.OVERGROUND, Color.YELLOW);
        final Route FR3_LCF_1 = new Route("FR3_LCF_1", FR3, LCF, 2, Level.UNDERGROUND, Color.GREEN);
        final Route FRI_LAU_1 = new Route("FRI_LAU_1", FRI, LAU, 3, Level.OVERGROUND, Color.RED);
        final Route FRI_LAU_2 = new Route("FRI_LAU_2", FRI, LAU, 3, Level.OVERGROUND, Color.VIOLET);
        final Route GEN_LAU_1 = new Route("GEN_LAU_1", GEN, LAU, 4, Level.OVERGROUND, Color.BLUE);
        final Route GEN_LAU_2 = new Route("GEN_LAU_2", GEN, LAU, 4, Level.OVERGROUND, Color.WHITE);
        final Route GEN_YVE_1 = new Route("GEN_YVE_1", GEN, YVE, 6, Level.OVERGROUND, null);
        final Route INT_LUC_1 = new Route("INT_LUC_1", INT, LUC, 4, Level.OVERGROUND, Color.VIOLET);
        final Route IT3_LUG_1 = new Route("IT3_LUG_1", IT3, LUG, 2, Level.UNDERGROUND, Color.WHITE);
        final Route IT4_LOC_1 = new Route("IT4_LOC_1", IT4, LOC, 2, Level.UNDERGROUND, Color.ORANGE);
        final Route KRE_SCE_1 = new Route("KRE_SCE_1", KRE, SCE, 3, Level.OVERGROUND, Color.VIOLET);
        final Route KRE_STG_1 = new Route("KRE_STG_1", KRE, STG, 1, Level.OVERGROUND, Color.GREEN);
        final Route KRE_WIN_1 = new Route("KRE_WIN_1", KRE, WIN, 2, Level.OVERGROUND, Color.YELLOW);
        final Route LAU_MAR_1 = new Route("LAU_MAR_1", LAU, MAR, 4, Level.UNDERGROUND, Color.ORANGE);
        final Route LAU_NEU_1 = new Route("LAU_NEU_1", LAU, NEU, 4, Level.OVERGROUND, null);
        final Route LCF_NEU_1 = new Route("LCF_NEU_1", LCF, NEU, 1, Level.UNDERGROUND, Color.ORANGE);
        final Route LCF_YVE_1 = new Route("LCF_YVE_1", LCF, YVE, 3, Level.UNDERGROUND, Color.YELLOW);
        final Route LOC_LUG_1 = new Route("LOC_LUG_1", LOC, LUG, 1, Level.UNDERGROUND, Color.VIOLET);
        final Route LUC_OLT_1 = new Route("LUC_OLT_1", LUC, OLT, 3, Level.OVERGROUND, Color.GREEN);
        final Route LUC_SCZ_1 = new Route("LUC_SCZ_1", LUC, SCZ, 1, Level.OVERGROUND, Color.BLUE);
        final Route LUC_ZOU_1 = new Route("LUC_ZOU_1", LUC, ZOU, 1, Level.OVERGROUND, Color.ORANGE);
        final Route LUC_ZOU_2 = new Route("LUC_ZOU_2", LUC, ZOU, 1, Level.OVERGROUND, Color.YELLOW);
        final Route MAR_SIO_1 = new Route("MAR_SIO_1", MAR, SIO, 2, Level.UNDERGROUND, Color.GREEN);
        final Route NEU_SOL_1 = new Route("NEU_SOL_1", NEU, SOL, 4, Level.OVERGROUND, Color.GREEN);
        final Route NEU_YVE_1 = new Route("NEU_YVE_1", NEU, YVE, 2, Level.OVERGROUND, Color.BLACK);
        final Route OLT_SOL_1 = new Route("OLT_SOL_1", OLT, SOL, 1, Level.OVERGROUND, Color.BLUE);
        final Route OLT_ZUR_1 = new Route("OLT_ZUR_1", OLT, ZUR, 3, Level.OVERGROUND, Color.WHITE);
        final Route PFA_SAR_1 = new Route("PFA_SAR_1", PFA, SAR, 3, Level.UNDERGROUND, Color.YELLOW);
        final Route PFA_SCZ_1 = new Route("PFA_SCZ_1", PFA, SCZ, 1, Level.OVERGROUND, Color.VIOLET);
        final Route PFA_STG_1 = new Route("PFA_STG_1", PFA, STG, 3, Level.OVERGROUND, Color.ORANGE);
        final Route PFA_ZUR_1 = new Route("PFA_ZUR_1", PFA, ZUR, 2, Level.OVERGROUND, Color.BLUE);
        final Route SAR_VAD_1 = new Route("SAR_VAD_1", SAR, VAD, 1, Level.UNDERGROUND, Color.ORANGE);
        final Route SCE_WIN_1 = new Route("SCE_WIN_1", SCE, WIN, 1, Level.OVERGROUND, Color.BLACK);
        final Route SCE_WIN_2 = new Route("SCE_WIN_2", SCE, WIN, 1, Level.OVERGROUND, Color.WHITE);
        final Route SCE_ZUR_1 = new Route("SCE_ZUR_1", SCE, ZUR, 3, Level.OVERGROUND, Color.ORANGE);
        final Route SCZ_WAS_1 = new Route("SCZ_WAS_1", SCZ, WAS, 2, Level.UNDERGROUND, Color.GREEN);
        final Route SCZ_WAS_2 = new Route("SCZ_WAS_2", SCZ, WAS, 2, Level.UNDERGROUND, Color.YELLOW);
        final Route SCZ_ZOU_1 = new Route("SCZ_ZOU_1", SCZ, ZOU, 1, Level.OVERGROUND, Color.BLACK);
        final Route SCZ_ZOU_2 = new Route("SCZ_ZOU_2", SCZ, ZOU, 1, Level.OVERGROUND, Color.WHITE);
        final Route STG_VAD_1 = new Route("STG_VAD_1", STG, VAD, 2, Level.UNDERGROUND, Color.BLUE);
        final Route STG_WIN_1 = new Route("STG_WIN_1", STG, WIN, 3, Level.OVERGROUND, Color.RED);
        final Route STG_ZUR_1 = new Route("STG_ZUR_1", STG, ZUR, 4, Level.OVERGROUND, Color.BLACK);
        final Route WIN_ZUR_1 = new Route("WIN_ZUR_1", WIN, ZUR, 1, Level.OVERGROUND, Color.BLUE);
        final Route WIN_ZUR_2 = new Route("WIN_ZUR_2", WIN, ZUR, 1, Level.OVERGROUND, Color.VIOLET);
        final Route ZOU_ZUR_1 = new Route("ZOU_ZUR_1", ZOU, ZUR, 1, Level.OVERGROUND, Color.GREEN);
        final Route ZOU_ZUR_2 = new Route("ZOU_ZUR_2", ZOU, ZUR, 1, Level.OVERGROUND, Color.RED);
        final List<Route> ALL_ROUTES = List.of(
                AT1_STG_1, AT2_VAD_1, BAD_BAL_1, BAD_OLT_1, BAD_ZUR_1, BAL_DE1_1,
                BAL_DEL_1, BAL_OLT_1, BEL_LOC_1, BEL_LUG_1, BEL_LUG_2, BEL_WAS_1,
                BEL_WAS_2, BER_FRI_1, BER_FRI_2, BER_INT_1, BER_LUC_1, BER_LUC_2,
                BER_NEU_1, BER_SOL_1, BRI_INT_1, BRI_IT5_1, BRI_LOC_1, BRI_SIO_1,
                BRI_WAS_1, BRU_COI_1, BRU_DAV_1, BRU_IT2_1, COI_DAV_1, COI_SAR_1,
                COI_WAS_1, DAV_AT3_1, DAV_IT1_1, DAV_SAR_1, DE2_SCE_1, DE3_KRE_1,
                DE4_KRE_1, DE5_STG_1, DEL_FR4_1, DEL_LCF_1, DEL_SOL_1, FR1_MAR_1,
                FR2_GEN_1, FR3_LCF_1, FRI_LAU_1, FRI_LAU_2, GEN_LAU_1, GEN_LAU_2,
                GEN_YVE_1, INT_LUC_1, IT3_LUG_1, IT4_LOC_1, KRE_SCE_1, KRE_STG_1,
                KRE_WIN_1, LAU_MAR_1, LAU_NEU_1, LCF_NEU_1, LCF_YVE_1, LOC_LUG_1,
                LUC_OLT_1, LUC_SCZ_1, LUC_ZOU_1, LUC_ZOU_2, MAR_SIO_1, NEU_SOL_1,
                NEU_YVE_1, OLT_SOL_1, OLT_ZUR_1, PFA_SAR_1, PFA_SCZ_1, PFA_STG_1,
                PFA_ZUR_1, SAR_VAD_1, SCE_WIN_1, SCE_WIN_2, SCE_ZUR_1, SCZ_WAS_1,
                SCZ_WAS_2, SCZ_ZOU_1, SCZ_ZOU_2, STG_VAD_1, STG_WIN_1, STG_ZUR_1,
                WIN_ZUR_1, WIN_ZUR_2, ZOU_ZUR_1, ZOU_ZUR_2);
        //endregion

        //region Tickets
        final Ticket BAL_BER = new Ticket(BAL, BER, 5);
        final Ticket BAL_BRI = new Ticket(BAL, BRI, 10);
        final Ticket BAL_STG = new Ticket(BAL, STG, 8);
        final Ticket BER_COI = new Ticket(BER, COI, 10);
        final Ticket BER_LUG = new Ticket(BER, LUG, 12);
        final Ticket BER_SCZ = new Ticket(BER, SCZ, 5);
        final Ticket BER_ZUR = new Ticket(BER, ZUR, 6);
        final Ticket FRI_LUC = new Ticket(FRI, LUC, 5);
        final Ticket GEN_BAL = new Ticket(GEN, BAL, 13);
        final Ticket GEN_BER = new Ticket(GEN, BER, 8);
        final Ticket GEN_SIO = new Ticket(GEN, SIO, 10);
        final Ticket GEN_ZUR = new Ticket(GEN, ZUR, 14);
        final Ticket INT_WIN = new Ticket(INT, WIN, 7);
        final Ticket KRE_ZUR = new Ticket(KRE, ZUR, 3);
        final Ticket LAU_INT = new Ticket(LAU, INT, 7);
        final Ticket LAU_LUC = new Ticket(LAU, LUC, 8);
        final Ticket LAU_STG = new Ticket(LAU, STG, 13);
        final Ticket LCF_BER = new Ticket(LCF, BER, 3);
        final Ticket LCF_LUC = new Ticket(LCF, LUC, 7);
        final Ticket LCF_ZUR = new Ticket(LCF, ZUR, 8);
        final Ticket LUC_VAD = new Ticket(LUC, VAD, 6);
        final Ticket LUC_ZUR = new Ticket(LUC, ZUR, 2);
        final Ticket LUG_COI = new Ticket(LUG, COI, 10);
        final Ticket NEU_WIN = new Ticket(NEU, WIN, 9);
        final Ticket OLT_SCE = new Ticket(OLT, SCE, 5);
        final Ticket SCE_MAR = new Ticket(SCE, MAR, 15);
        final Ticket SCE_STG = new Ticket(SCE, STG, 4);
        final Ticket SCE_ZOU = new Ticket(SCE, ZOU, 3);
        final Ticket STG_BRU = new Ticket(STG, BRU, 9);
        final Ticket WIN_SCZ = new Ticket(WIN, SCZ, 3);
        final Ticket ZUR_BAL = new Ticket(ZUR, BAL, 4);
        final Ticket ZUR_BRU = new Ticket(ZUR, BRU, 11);
        final Ticket ZUR_LUG = new Ticket(ZUR, LUG, 9);
        final Ticket ZUR_VAD = new Ticket(ZUR, VAD, 6);

        final Ticket BER_C = ticketToNeighbors(List.of(BER), 6, 11, 8, 5);
        final Ticket COI_C = ticketToNeighbors(List.of(COI), 6, 3, 5, 12);
        final Ticket LUG_C = ticketToNeighbors(List.of(LUG), 12, 13, 2, 14);
        final Ticket ZUR_C = ticketToNeighbors(List.of(ZUR), 3, 7, 11, 7);

        final Ticket DE_C = ticketToNeighbors(DE, 0, 5, 13, 5);
        final Ticket AT_C = ticketToNeighbors(AT, 5, 0, 6, 14);
        final Ticket IT_C = ticketToNeighbors(IT, 13, 6, 0, 11);
        final Ticket FR_C = ticketToNeighbors(FR, 5, 14, 11, 0);

        final List<Ticket> ALL_TICKETS = List.of(
                BAL_BER, BAL_BRI, BAL_STG, BER_COI, BER_LUG, BER_SCZ,
                BER_ZUR, FRI_LUC, GEN_BAL, GEN_BER, GEN_SIO, GEN_ZUR,
                INT_WIN, KRE_ZUR, LAU_INT, LAU_LUC, LAU_STG, LCF_BER,
                LCF_LUC, LCF_ZUR, LUC_VAD, LUC_ZUR, LUG_COI, NEU_WIN,
                OLT_SCE, SCE_MAR, SCE_STG, SCE_ZOU, STG_BRU, WIN_SCZ,
                ZUR_BAL, ZUR_BRU, ZUR_LUG, ZUR_VAD,
                BER_C, COI_C, LUG_C, ZUR_C,
                DE_C, DE_C, AT_C, AT_C, IT_C, IT_C, FR_C, FR_C);

        private Ticket ticketToNeighbors(List<Station> from, int de, int at, int it, int fr) {
            var trips = new ArrayList<Trip>();
            if (de != 0) trips.addAll(Trip.all(from, DE, de));
            if (at != 0) trips.addAll(Trip.all(from, AT, at));
            if (it != 0) trips.addAll(Trip.all(from, IT, it));
            if (fr != 0) trips.addAll(Trip.all(from, FR, fr));
            return new Ticket(trips);
        }
        //endregion
    }
}
