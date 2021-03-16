package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        this.tickets = tickets;
        this.cards = cards;
        this.routes = routes;
    }

    public PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument( initialCards.size()==4);
        SortedBag.Builder <Ticket> tickets = new SortedBag.Builder<>();
        List<Route> routes = new ArrayList<>();
        return new PlayerState(tickets.build(), initialCards, routes);
    }

    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        List<Ticket> listWithTickets = tickets.toList();
        for (Ticket ticket : newTickets) {
            listWithTickets.add(ticket);
        }
        SortedBag.Builder<Ticket> new_Builder = new SortedBag.Builder<Ticket>();
        for (Ticket ticket : listWithTickets){
            new_Builder.add(ticket);
        }
        return new PlayerState(new_Builder.build(), cards, routes);
    }

    public SortedBag<Card> cards(){
        return cards;
    }

    public PlayerState withAddedCard(Card card){
        List<Card> listWithCard = cards.toList();
        listWithCard.add(card);
        SortedBag.Builder<Card> new_Builder = new SortedBag.Builder<Card>();
        for (Card single_card : listWithCard){
            new_Builder.add(single_card);
        }
        return new PlayerState(tickets, new_Builder.build(), routes);
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        List<Card> listWithCard = cards.toList();
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
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<SortedBag<Card>>();
        for (SortedBag<Card> SB : route.possibleClaimCards()){
            if (cards.contains(SB)){
                possibleClaimCards.add(SB);
            }
        }
        return possibleClaimCards;
    }

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(additionalCardsCount>=1 && additionalCardsCount<=3 && drawnCards.size()==3
                && initialCards != null);
        Card firstCard = initialCards.get(0);
        for ( Card card : initialCards){
            if (firstCard.equals(Card.LOCOMOTIVE)){
                firstCard = card;
            }
            else{
                Preconditions.checkArgument(card.equals(firstCard) || card.equals(Card.LOCOMOTIVE));
            } // NE PAS OUBLIER PRECONDITIONS COULEURS
        }

        SortedBag<Card> newCards = cards.difference(initialCards);
        for(Card card : drawnCards){
            SortedBag.Builder<Card> dC = new SortedBag.Builder<>();
            for(Card initial_card : initialCards){
                if (card.equals(initial_card) || card.equals(Card.LOCOMOTIVE)){
                    break;
                }
            }
            dC.add(card);
            SortedBag<Card> uselessCard = dC.build();
            drawnCards.difference(uselessCard);
        }
        SortedBag<Card> allUsableCards = allUsableCards(newCards,drawnCards);
        List<SortedBag<Card>> possibleAdditionalCards = new ArrayList<>(allUsableCards.subsetsOfSize(additionalCardsCount));
        possibleAdditionalCards.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return possibleAdditionalCards;
    }

    private static SortedBag<Card> allUsableCards(SortedBag<Card> deck,SortedBag<Card> drawnCards){
        SortedBag.Builder<Card> temp = new SortedBag.Builder<>();
        for (Card my_card : deck) {
            for (Card additional_card : drawnCards){
                if(my_card.equals(additional_card) || my_card.equals(Card.LOCOMOTIVE)){
                    temp.add(my_card);
                    break;
                }
            }
        }
        return temp.build();
    }

    PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> routesWithoutClaimedRoute =new ArrayList<>(routes) ;
        routesWithoutClaimedRoute.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), routesWithoutClaimedRoute);
    }

    public int ticketPoints(){

        return 0;
    }

    public int finalPoints(){
        return ticketPoints()+claimPoints();
    }
}