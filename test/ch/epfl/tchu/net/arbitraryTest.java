package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class arbitraryTest {

    static class Server implements Runnable {
        private PlayerId expectedPlayerId;
        private Map<PlayerId,String> expectedPlayerNames;
        private String expectedInfos;
        private PublicGameState expectedPGS;
        private PlayerState expectedOwnState;
        private Player.TurnKind nextTurn;
        private int drawSlot;
        private Route claimedRoute;
        private SortedBag<Card> initialClaimCards;
        public boolean hasFinished = false;

        public Server(PlayerId expectedPlayerId, Map<PlayerId, String> expectedPlayerNames, String expectedInfos, PublicGameState expectedPGS, PlayerState expectedOwnState, Player.TurnKind nextTurn, int drawSlot, Route claimedRoute, SortedBag<Card> initialClaimCard) {
            this.expectedPlayerId = expectedPlayerId;
            this.expectedPlayerNames = expectedPlayerNames;
            this.expectedInfos = expectedInfos;
            this.expectedPGS = expectedPGS;
            this.expectedOwnState = expectedOwnState;
            this.nextTurn = nextTurn;
            this.drawSlot = drawSlot;
            this.claimedRoute = claimedRoute;
            this.initialClaimCards = initialClaimCard;
        }

        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(5108); Socket socket = serverSocket.accept()) {
                Player playerProxy = new RemotePlayerProxy(socket);
                playerProxy.initPlayers(expectedPlayerId, expectedPlayerNames);
                playerProxy.receiveInfo(expectedInfos);
                playerProxy.updateState(expectedPGS, expectedOwnState);
                playerProxy.setInitialTicketChoice(SortedBag.of(ChMap.tickets().subList(0, 5)));
                assertEquals(nextTurn, playerProxy.nextTurn());
                assertEquals(drawSlot, playerProxy.drawSlot());
                assertEquals(3, playerProxy.chooseTickets(SortedBag.of(ChMap.tickets().subList(0, 5))).size());
                assertEquals(claimedRoute, playerProxy.claimedRoute());
                assertEquals(initialClaimCards, playerProxy.initialClaimCards());
                assertEquals(2, playerProxy.chooseInitialTickets().size());
                this.hasFinished = true;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    static class Client implements Runnable {
        private PlayerId expectedPlayerId;
        private Map<PlayerId,String> expectedPlayerNames;
        private String expectedInfos;
        private PublicGameState expectedPGS;
        private PlayerState expectedOwnState;
        private Player.TurnKind nextTurn;
        private int drawSlot;
        private Route claimedRoute;
        private SortedBag<Card> initialClaimCards;
        public boolean hasFinished = false;

        Client(PlayerId expectedPlayerId, Map<PlayerId, String> expectedPlayerNames, String expectedInfos, PublicGameState expectedPGS, PlayerState expectedOwnState, Player.TurnKind nextTurn, int drawSlot, Route claimedRoute, SortedBag<Card> initialClaimCards) {
            this.expectedPlayerId = expectedPlayerId;
            this.expectedPlayerNames = expectedPlayerNames;
            this.expectedInfos = expectedInfos;
            this.expectedPGS = expectedPGS;
            this.expectedOwnState = expectedOwnState;
            this.nextTurn = nextTurn;
            this.drawSlot = drawSlot;
            this.claimedRoute = claimedRoute;
            this.initialClaimCards = initialClaimCards;
        }
        public void run() {
            RemotePlayerClient playerClient = new RemotePlayerClient(new DummyPlayer(expectedPlayerId, expectedPlayerNames, expectedInfos, expectedPGS, expectedOwnState, nextTurn, drawSlot, claimedRoute, initialClaimCards), "localhost", 5108);
            playerClient.run();
            this.hasFinished = true;
        }
    }

    static class DummyPlayer implements Player {
        private PlayerId expectedPlayerId;
        private Map<PlayerId, String> expectedPlayerNames;
        private String expectedInfos;
        private PublicGameState expectedPGS;
        private PlayerState expectedOwnState;
        private SortedBag<Ticket> initialTicketsChoice;
        private TurnKind nextTurn;
        private int drawSlot;
        private Route claimedRoute;
        private SortedBag<Card> initialClaimCards;

        public DummyPlayer(PlayerId expectedPlayerId, Map<PlayerId, String> expectedPlayerNames, String expectedInfos, PublicGameState expectedPGS, PlayerState expectedOwnState, TurnKind nextTurn, int drawSlot, Route claimedRoute, SortedBag<Card> initialClaimCards) {
            this.expectedPlayerId = expectedPlayerId;
            this.expectedPlayerNames = expectedPlayerNames;
            this.expectedInfos = expectedInfos;
            this.expectedPGS = expectedPGS;
            this.expectedOwnState = expectedOwnState;
            this.nextTurn = nextTurn;
            this.drawSlot = drawSlot;
            this.claimedRoute = claimedRoute;
            this.initialClaimCards = initialClaimCards;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            assertEquals(expectedPlayerId, ownId);
            assertEquals(expectedPlayerNames, playerNames);

        }

        @Override
        public void receiveInfo(String info) {
            assertEquals(expectedInfos, info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            arePublicGameStatesEquals(expectedPGS, newState);
            arePlayerStateEquals(expectedOwnState, ownState);
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initialTicketsChoice = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return SortedBag.of(2, this.initialTicketsChoice.get(0));
        }

        @Override
        public TurnKind nextTurn() {
            return nextTurn;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return SortedBag.of(3, options.get(0));
        }

        @Override
        public int drawSlot() {
            return drawSlot;
        }

        @Override
        public Route claimedRoute() {
            return claimedRoute;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }
    }

    @Test
    public void testServer() throws InterruptedException {
        PublicPlayerState player1State = new PublicPlayerState(4, 2, ChMap.routes().subList(0, 3));
        PlayerState player2State = new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 4)), SortedBag.of(Card.ALL), ChMap.routes().subList(0, 2));
        PublicCardState cardState = new PublicCardState(Card.ALL.subList(0, 5), 2, 3);
        PublicGameState pgs = new PublicGameState(2, cardState, PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, player1State, PlayerId.PLAYER_2, player2State), null);
        Server servTestInstance = new Server(PlayerId.PLAYER_2, Map.of(PlayerId.PLAYER_1, "mdr", PlayerId.PLAYER_2, "lol"), "salut", pgs, player2State, Player.TurnKind.CLAIM_ROUTE, 2, ChMap.routes().get(5), SortedBag.of(Card.ALL));
        Client clientTestInstance = new Client(PlayerId.PLAYER_2, Map.of(PlayerId.PLAYER_1, "mdr", PlayerId.PLAYER_2, "lol"), "salut", pgs, player2State, Player.TurnKind.CLAIM_ROUTE, 2, ChMap.routes().get(5), SortedBag.of(Card.ALL));
        Thread servThread = new Thread(servTestInstance);
        Thread clientThread = new Thread(clientTestInstance);
        servThread.start();
        clientThread.start();
        servThread.join();
        clientThread.join();

        assertTrue(servTestInstance.hasFinished);
        assertTrue(clientTestInstance.hasFinished);
    }

    private static void arePublicGameStatesEquals(PublicGameState expected, PublicGameState given) {
        assertEquals(expected.currentPlayerId(), given.currentPlayerId());
        assertEquals(expected.lastPlayer(), given.lastPlayer());
        assertEquals(expected.ticketsCount(), given.ticketsCount());
        arePublicCardStatesEquals(expected.cardState(), given.cardState());

        for(PlayerId pid : PlayerId.ALL) {
            arePublicPlayerStateEquals(expected.playerState(pid), given.playerState(pid));
        }
    }

    private static void arePublicCardStatesEquals(PublicCardState expected, PublicCardState given) {
        assertEquals(expected.faceUpCards(), given.faceUpCards());
        assertEquals(expected.deckSize(), given.deckSize());
        assertEquals(expected.discardsSize(), given.discardsSize());
    }

    private static void arePublicPlayerStateEquals(PublicPlayerState expected, PublicPlayerState given) {
        assertEquals(expected.ticketCount(), given.ticketCount());
        assertEquals(expected.cardCount(), given.cardCount());
        assertEquals(expected.routes(), given.routes());
    }

    private static void arePlayerStateEquals(PlayerState expected, PlayerState given) {
        arePublicPlayerStateEquals(expected, given);
        assertEquals(expected.tickets(), given.tickets());
        assertEquals(expected.cards(), given.cards());
    }
}