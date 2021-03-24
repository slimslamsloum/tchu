package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {


    public static final Random NON_RANDOM = new Random() {
        @Override
        public int nextInt(int i) {
            return i-1;
        }
    };

    @Test
    /*void InitialWorksForNonRandomDeck(){
        var tickets = SortedBag.of(new ChMaps().ALL_TICKETS);
        var gameState = GameState.initial(tickets,NON_RANDOM);
        var ticketsDeck = Deck.of(tickets,NON_RANDOM);
        var playerTickets = ticketsDeck.topCards(5);
        ticketsDeck.withoutTopCards(5);
        var cardDeck = Deck.of(Constants.ALL_CARDS, NON_RANDOM);
        var playerCards = cardDeck.topCards(Constants.INITIAL_CARDS_COUNT);
        cardDeck.withoutTopCards(4);
        var currentPlayerId=PlayerId.PLAYER_2;
        var finalPlayerID=PlayerId.PLAYER_1;
        var playerStateMap = new EnumMap<>(PlayerId.class);
        PlayerState playerState= new PlayerState(playerTickets,playerCards,new ArrayList<>());
        playerStateMap.put(currentPlayerId,playerState);
        playerStateMap.put(finalPlayerID,playerState);
        assertEquals(gameState, new GameState(tickets.size(),ticketsDeck,CardState.of(cardDeck),currentPlayerId,playerStateMap,finalPlayerID ));
    }*/
    void InitialWorksForNonRandomDeck(){
        var tickets = SortedBag.of(new ChMaps().ALL_TICKETS);
        var gameState1 = GameState.initial(tickets,NON_RANDOM);
        var gameState2 = GameState.initial(tickets,NON_RANDOM);
        assertEquals(PlayerId.PLAYER_2,gameState1.currentPlayerId());
        assertEquals(PlayerId.PLAYER_1,gameState2.lastPlayer());
    }
    @Test
    void InitialWorksForRandomDeck(){
        var tickets = SortedBag.of(new ChMaps().ALL_TICKETS);
        var gameState1 = GameState.initial(tickets,new Random());
        var gameState2 = GameState.initial(tickets,new Random());
        assertNotEquals(gameState2, gameState1);
        assertNotEquals(gameState1.currentPlayerId(), gameState1.lastPlayer());
    }
    @Test
    void topTicketsWorksOrThrowsException(){
        var tickets = SortedBag.of(new ChMaps().ALL_TICKETS);
        var gameState = GameState.initial(tickets,NON_RANDOM);
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(tickets.size()+1);
        });
        SortedBag.Builder<Ticket> reducedBag= new SortedBag.Builder<>();
        int cmp=0;
        for(Ticket ticket : tickets){
            if(cmp<10){
                cmp+=1;
            }
            else {
                reducedBag.add(ticket);
            }
        }
        assertEquals(gameState.topTickets(tickets.size()-10),reducedBag.build());
        assertTrue(gameState.topTickets(tickets.size()-10).size()+10==tickets.size());
        assertTrue(gameState.topTickets(0).equals(reducedBag.build().difference(reducedBag.build())));
    }

    @Test
    void withoutTopTicketsThrowsException(){
        var tickets = SortedBag.of(new ChMaps().ALL_TICKETS);
        var gameState = GameState.initial(tickets,NON_RANDOM);
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(tickets.size()+1);
        });
    }

    @Test
    void withDrawnFaceUpCardFails(){
        GameState GS = generator();
        while (!GS.cardState().isDeckEmpty()){
            GS=GS.withoutTopCard();
        }
        GameState finalGS = GS;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGS.withDrawnFaceUpCard(3);
        });
    }

    @Test
    void withDrawnFaceUpCardWorks(){
        GameState GS = generator();
        Card topcard = GS.topCard();
        Card faceup = GS.cardState().faceUpCard(3);
        GS=GS.withDrawnFaceUpCard(3);
        assertTrue(GS.currentPlayerState().cards().contains(faceup));
        assertTrue(GS.cardState().faceUpCard(3)==topcard);
    }

    @Test
    void withBlindlyDrawnCardFails(){
        GameState GS = generator();
        while (!GS.cardState().isDeckEmpty()){
            GS=GS.withoutTopCard();
        }
        GameState finalGS = GS;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGS.withBlindlyDrawnCard();
        });
    }

    @Test
    void withBlindlyDrawnCardWorks(){
        GameState GS = generator();
        Card topcard = GS.topCard();
        GS=GS.withBlindlyDrawnCard();
        assertTrue(GS.currentPlayerState().cards().contains(topcard));
    }

    @Test
    void withInitiallyChosenTicketsFails(){
        GameState GS = generator();
        GS = GS.withChosenAdditionalTickets(SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,3)), SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,2)));
        GameState finalGS = GS;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGS.withInitiallyChosenTickets(finalGS.currentPlayerId(), SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,2)) );
       });
    }

    @Test
    void withInitiallyChosenTicketsWorks(){
        GameState GS = generator();
        GS = GS.withInitiallyChosenTickets(GS.currentPlayerId(), SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,3)));
        assertEquals(SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,3)), GS.currentPlayerState().tickets());
    }

    @Test
    void withClaimedRouteWorks(){
        GameState GS = generator();
        List<Route> routes = new ArrayList<>(new ChMaps().ALL_ROUTES.subList(0, 17));
        int i = 0;
        for (Route route : routes){
            GS=GS.withClaimedRoute(route, SortedBag.of(Card.RED));
            assertTrue(GS.currentPlayerState().routes().contains(route));
        }
    }

    @Test
    void withChosenAdditionalTicketsFails(){
        GameState GS = generator();
        GameState finalGS = GS;
        assertThrows(IllegalArgumentException.class, () -> {
            finalGS.withChosenAdditionalTickets(SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,3)), SortedBag.of(new ChMaps().ALL_TICKETS.subList(3,6)));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            finalGS.withChosenAdditionalTickets(SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,1)), SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,6)));
        });
    }

    @Test
    void withChosenAdditionalTicketsWorks(){
        GameState GS = generator();
        GS = GS.withChosenAdditionalTickets(SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,3)), SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,2)));
        assertTrue(SortedBag.of(new ChMaps().ALL_TICKETS.subList(0,2)).equals(GS.currentPlayerState().tickets()));
        System.out.println(GS.ticketDeck().size());
        System.out.println(new ChMaps().ALL_TICKETS.size()-2);
        assertTrue(GS.ticketDeck().size() == new ChMaps().ALL_TICKETS.size()-3);
    }

    @Test
    void LastTurnBeginsWorks(){
        GameState GS = generator();
        List<Route> routes = new ArrayList<>(new ChMaps().ALL_ROUTES.subList(0, 17));
        routes.add(new ChMaps().ALL_ROUTES.get(18));
        for(Route route: routes) {
            GS=GS.withClaimedRoute(route, SortedBag.of(Card.RED));
        }
        assertTrue(GS.lastTurnBegins());
    }

    @Test
    void ForNextTurnWorks(){
        GameState GS = generator();
        List<Route> routes = new ArrayList<>(new ChMaps().ALL_ROUTES.subList(0, 17));
        routes.add(new ChMaps().ALL_ROUTES.get(18));
        for(Route route: routes) {
            GS=GS.withClaimedRoute(route, SortedBag.of(Card.RED));
        }
        assertEquals(GS.currentPlayerId(), GS.forNextTurn().lastPlayer());
    }

    private static final class ChMaps {
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
        final Route AT1_STG_1 = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null);
        final Route AT2_VAD_1 = new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED);
        final Route BAD_BAL_1 = new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED);
        final Route BAD_OLT_1 = new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET);
        final Route BAD_ZUR_1 = new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route BAL_DE1_1 = new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE);
        final Route BAL_DEL_1 = new Route("BAL_DEL_1", BAL, DEL, 2, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route BAL_OLT_1 = new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route BEL_LOC_1 = new Route("BEL_LOC_1", BEL, LOC, 1, Route.Level.UNDERGROUND, Color.BLACK);
        final Route BEL_LUG_1 = new Route("BEL_LUG_1", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.RED);
        final Route BEL_LUG_2 = new Route("BEL_LUG_2", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route BEL_WAS_1 = new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
        final Route BEL_WAS_2 = new Route("BEL_WAS_2", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
        final Route BER_FRI_1 = new Route("BER_FRI_1", BER, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE);
        final Route BER_FRI_2 = new Route("BER_FRI_2", BER, FRI, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route BER_INT_1 = new Route("BER_INT_1", BER, INT, 3, Route.Level.OVERGROUND, Color.BLUE);
        final Route BER_LUC_1 = new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null);

        final Route BER_LUC_2 = new Route("BER_LUC_2", BER, LUC, 4, Route.Level.OVERGROUND, null);
        final Route BER_NEU_1 = new Route("BER_NEU_1", BER, NEU, 2, Route.Level.OVERGROUND, Color.RED);
        final Route BER_SOL_1 = new Route("BER_SOL_1", BER, SOL, 2, Route.Level.OVERGROUND, Color.BLACK);
        final Route BRI_INT_1 = new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE);
        final Route BRI_IT5_1 = new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN);
        final Route BRI_LOC_1 = new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null);
        final Route BRI_SIO_1 = new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK);
        final Route BRI_WAS_1 = new Route("BRI_WAS_1", BRI, WAS, 4, Route.Level.UNDERGROUND, Color.RED);
        final Route BRU_COI_1 = new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null);
        final Route BRU_DAV_1 = new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE);
        final Route BRU_IT2_1 = new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route COI_DAV_1 = new Route("COI_DAV_1", COI, DAV, 2, Route.Level.UNDERGROUND, Color.VIOLET);
        final Route COI_SAR_1 = new Route("COI_SAR_1", COI, SAR, 1, Route.Level.UNDERGROUND, Color.WHITE);
        final Route COI_WAS_1 = new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null);
        final Route DAV_AT3_1 = new Route("DAV_AT3_1", DAV, AT3, 3, Route.Level.UNDERGROUND, null);
        final Route DAV_IT1_1 = new Route("DAV_IT1_1", DAV, IT1, 3, Route.Level.UNDERGROUND, null);
        final Route DAV_SAR_1 = new Route("DAV_SAR_1", DAV, SAR, 3, Route.Level.UNDERGROUND, Color.BLACK);
        final Route DE2_SCE_1 = new Route("DE2_SCE_1", DE2, SCE, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route DE3_KRE_1 = new Route("DE3_KRE_1", DE3, KRE, 1, Route.Level.OVERGROUND, Color.ORANGE);
        final Route DE4_KRE_1 = new Route("DE4_KRE_1", DE4, KRE, 1, Route.Level.OVERGROUND, Color.WHITE);
        final Route DE5_STG_1 = new Route("DE5_STG_1", DE5, STG, 2, Route.Level.OVERGROUND, null);
        final Route DEL_FR4_1 = new Route("DEL_FR4_1", DEL, FR4, 2, Route.Level.UNDERGROUND, Color.BLACK);
        final Route DEL_LCF_1 = new Route("DEL_LCF_1", DEL, LCF, 3, Route.Level.UNDERGROUND, Color.WHITE);
        final Route DEL_SOL_1 = new Route("DEL_SOL_1", DEL, SOL, 1, Route.Level.UNDERGROUND, Color.VIOLET);
        final Route FR1_MAR_1 = new Route("FR1_MAR_1", FR1, MAR, 2, Route.Level.UNDERGROUND, null);
        final Route FR2_GEN_1 = new Route("FR2_GEN_1", FR2, GEN, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route FR3_LCF_1 = new Route("FR3_LCF_1", FR3, LCF, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route FRI_LAU_1 = new Route("FRI_LAU_1", FRI, LAU, 3, Route.Level.OVERGROUND, Color.RED);
        final Route FRI_LAU_2 = new Route("FRI_LAU_2", FRI, LAU, 3, Route.Level.OVERGROUND, Color.VIOLET);
        final Route GEN_LAU_1 = new Route("GEN_LAU_1", GEN, LAU, 4, Route.Level.OVERGROUND, Color.BLUE);
        final Route GEN_LAU_2 = new Route("GEN_LAU_2", GEN, LAU, 4, Route.Level.OVERGROUND, Color.WHITE);
        final Route GEN_YVE_1 = new Route("GEN_YVE_1", GEN, YVE, 6, Route.Level.OVERGROUND, null);
        final Route INT_LUC_1 = new Route("INT_LUC_1", INT, LUC, 4, Route.Level.OVERGROUND, Color.VIOLET);
        final Route IT3_LUG_1 = new Route("IT3_LUG_1", IT3, LUG, 2, Route.Level.UNDERGROUND, Color.WHITE);
        final Route IT4_LOC_1 = new Route("IT4_LOC_1", IT4, LOC, 2, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route KRE_SCE_1 = new Route("KRE_SCE_1", KRE, SCE, 3, Route.Level.OVERGROUND, Color.VIOLET);
        final Route KRE_STG_1 = new Route("KRE_STG_1", KRE, STG, 1, Route.Level.OVERGROUND, Color.GREEN);
        final Route KRE_WIN_1 = new Route("KRE_WIN_1", KRE, WIN, 2, Route.Level.OVERGROUND, Color.YELLOW);
        final Route LAU_MAR_1 = new Route("LAU_MAR_1", LAU, MAR, 4, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route LAU_NEU_1 = new Route("LAU_NEU_1", LAU, NEU, 4, Route.Level.OVERGROUND, null);
        final Route LCF_NEU_1 = new Route("LCF_NEU_1", LCF, NEU, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route LCF_YVE_1 = new Route("LCF_YVE_1", LCF, YVE, 3, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route LOC_LUG_1 = new Route("LOC_LUG_1", LOC, LUG, 1, Route.Level.UNDERGROUND, Color.VIOLET);
        final Route LUC_OLT_1 = new Route("LUC_OLT_1", LUC, OLT, 3, Route.Level.OVERGROUND, Color.GREEN);
        final Route LUC_SCZ_1 = new Route("LUC_SCZ_1", LUC, SCZ, 1, Route.Level.OVERGROUND, Color.BLUE);
        final Route LUC_ZOU_1 = new Route("LUC_ZOU_1", LUC, ZOU, 1, Route.Level.OVERGROUND, Color.ORANGE);
        final Route LUC_ZOU_2 = new Route("LUC_ZOU_2", LUC, ZOU, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route MAR_SIO_1 = new Route("MAR_SIO_1", MAR, SIO, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route NEU_SOL_1 = new Route("NEU_SOL_1", NEU, SOL, 4, Route.Level.OVERGROUND, Color.GREEN);
        final Route NEU_YVE_1 = new Route("NEU_YVE_1", NEU, YVE, 2, Route.Level.OVERGROUND, Color.BLACK);
        final Route OLT_SOL_1 = new Route("OLT_SOL_1", OLT, SOL, 1, Route.Level.OVERGROUND, Color.BLUE);
        final Route OLT_ZUR_1 = new Route("OLT_ZUR_1", OLT, ZUR, 3, Route.Level.OVERGROUND, Color.WHITE);
        final Route PFA_SAR_1 = new Route("PFA_SAR_1", PFA, SAR, 3, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route PFA_SCZ_1 = new Route("PFA_SCZ_1", PFA, SCZ, 1, Route.Level.OVERGROUND, Color.VIOLET);
        final Route PFA_STG_1 = new Route("PFA_STG_1", PFA, STG, 3, Route.Level.OVERGROUND, Color.ORANGE);
        final Route PFA_ZUR_1 = new Route("PFA_ZUR_1", PFA, ZUR, 2, Route.Level.OVERGROUND, Color.BLUE);
        final Route SAR_VAD_1 = new Route("SAR_VAD_1", SAR, VAD, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route SCE_WIN_1 = new Route("SCE_WIN_1", SCE, WIN, 1, Route.Level.OVERGROUND, Color.BLACK);
        final Route SCE_WIN_2 = new Route("SCE_WIN_2", SCE, WIN, 1, Route.Level.OVERGROUND, Color.WHITE);
        final Route SCE_ZUR_1 = new Route("SCE_ZUR_1", SCE, ZUR, 3, Route.Level.OVERGROUND, Color.ORANGE);
        final Route SCZ_WAS_1 = new Route("SCZ_WAS_1", SCZ, WAS, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route SCZ_WAS_2 = new Route("SCZ_WAS_2", SCZ, WAS, 2, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route SCZ_ZOU_1 = new Route("SCZ_ZOU_1", SCZ, ZOU, 1, Route.Level.OVERGROUND, Color.BLACK);
        final Route SCZ_ZOU_2 = new Route("SCZ_ZOU_2", SCZ, ZOU, 1, Route.Level.OVERGROUND, Color.WHITE);
        final Route STG_VAD_1 = new Route("STG_VAD_1", STG, VAD, 2, Route.Level.UNDERGROUND, Color.BLUE);
        final Route STG_WIN_1 = new Route("STG_WIN_1", STG, WIN, 3, Route.Level.OVERGROUND, Color.RED);
        final Route STG_ZUR_1 = new Route("STG_ZUR_1", STG, ZUR, 4, Route.Level.OVERGROUND, Color.BLACK);
        final Route WIN_ZUR_1 = new Route("WIN_ZUR_1", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.BLUE);
        final Route WIN_ZUR_2 = new Route("WIN_ZUR_2", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.VIOLET);
        final Route ZOU_ZUR_1 = new Route("ZOU_ZUR_1", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.GREEN);
        final Route ZOU_ZUR_2 = new Route("ZOU_ZUR_2", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.RED);
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
    private static GameState generator(){
        var tickets = SortedBag.of(new ChMaps().ALL_TICKETS);
        GameState GS = GameState.initial(tickets, new Random(1));
        return GS;
    }
}