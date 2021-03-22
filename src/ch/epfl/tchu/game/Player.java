package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

public interface Player {

    public enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }

    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);
    void receiveInfo(String info);
    void updateState(PublicGameState newState, PlayerState ownState);
    void setInitialTicketChoice(SortedBag<Ticket> tickets);
    SortedBag<Ticket> chooseInitialTickets();
    TurnKind nextTurn();
    int drawSlot();
    Route claimedRoute();
    SortedBag<Card> initialClaimCards();
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

}
