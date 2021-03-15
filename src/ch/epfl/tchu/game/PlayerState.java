package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        this.tickets = tickets;
        this.cards = cards;
        this.routes = routes;
    }

    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    public SortedBag<Card> cards(){
        return cards;
    }

    public PlayerState withAddedCard(Card card){
        ArrayList<Card> listWithCard = (ArrayList<Card>) SortedBag.of(cards).toList();
        listWithCard.add(card);
        SortedBag.Builder<Card> new_Builder = new SortedBag.Builder<Card>();
        for (Card single_card : listWithCard){
            new_Builder.add(single_card);
        }
        return new PlayerState(tickets, new_Builder.build(), routes);
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        ArrayList<Card> listWithCard = (ArrayList<Card>) SortedBag.of(cards).toList();
        for (Card card: additionalCards){
            listWithCard.add(card);
        }
        SortedBag.Builder<Card> new_Builder = new SortedBag.Builder<Card>();
        for (Card single_card : listWithCard){
            new_Builder.add(single_card);
        }
        return new PlayerState(tickets, new_Builder.build(), routes);
    }

    public boolean canClaimRoute(Route route){
        boolean canClaim = false;
        if (this.carCount() >= route.length() ){
            for (SortedBag<Card> SB : possibleClaimCards(route)){
                if (cards.contains(SB)){
                    canClaim = true;
                }
            }
        }
        return canClaim;
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(this.carCount() >= route.length());
        List<SortedBag<Card>> possibleClaimCards = List.of();
        for (SortedBag<Card> SB : possibleClaimCards(route)){
            if (cards.contains(SB)){
                possibleClaimCards.add(SB);
            }
        }
        return possibleClaimCards;
    }

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){

    }

    PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> routesWithoutClaimedRoute = routes;
        routesWithoutClaimedRoute.remove(route);
        return new PlayerState(tickets, cards.difference(claimCards), routesWithoutClaimedRoute);
        //? wont this modify routes?
    }

    int ticketPoints(){
    }

    int finalPoints(){
        return ticketPoints()+claimPoints();
    }
}
