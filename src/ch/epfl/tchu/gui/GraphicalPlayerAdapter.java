package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

public class GraphicalPlayerAdapter implements Player {

    GraphicalPlayer graphicalPlayer;
    BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    BlockingQueue<TurnKind> turnKindQueue;
    BlockingQueue<Route> routesQueue;
    BlockingQueue<SortedBag<Card>> cardsQueue;
    BlockingQueue<Integer> cardPlacementsQueue;

    public GraphicalPlayerAdapter(){
        ticketsQueue = new ArrayBlockingQueue<>(1);
        turnKindQueue = new ArrayBlockingQueue<>(1);
        routesQueue = new ArrayBlockingQueue<>(1);
        cardsQueue = new ArrayBlockingQueue<>(1);
        cardPlacementsQueue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer=new GraphicalPlayer(ownId, playerNames));
    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(()-> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        ActionHandlers.ChooseTicketsHandler chooseTicketsHandler = (ticketsToChoose) -> {
            putInQueue(ticketsQueue, ticketsToChoose);
        };
        runLater(() -> graphicalPlayer.chooseTickets(chooseTicketsHandler,tickets));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return ticketsQueue.peek();
    }

    @Override
    public TurnKind nextTurn() {

        ActionHandlers.DrawTicketsHandler drawTicketsHandler = () -> {
            putInQueue(turnKindQueue, TurnKind.DRAW_TICKETS);
        };

        ActionHandlers.ClaimRouteHandler claimRouteHandler = (route,cards) -> {
            putInQueue(turnKindQueue, TurnKind.CLAIM_ROUTE);
            putInQueue(routesQueue, route);
            putInQueue(cardsQueue, cards);
        };

        ActionHandlers.DrawCardHandler drawCardHandler = (i) -> {
            putInQueue(cardPlacementsQueue, i);
        };

        runLater(() -> graphicalPlayer.startTurn(drawTicketsHandler, drawCardHandler, claimRouteHandler));

        return retrieveFromQueue(turnKindQueue);
    }

    @Override
    public int drawSlot() {
        if (cardPlacementsQueue.peek() != null){
            return cardPlacementsQueue.peek();
        }
        else{
            ActionHandlers.DrawCardHandler drawCardHandler = (i) -> {
                putInQueue(cardPlacementsQueue, i);
            };
            runLater(() -> graphicalPlayer.drawCard(drawCardHandler));
            return retrieveFromQueue(cardPlacementsQueue);
        }
    }

    @Override
    public Route claimedRoute() {
        return retrieveFromQueue(routesQueue);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return retrieveFromQueue(cardsQueue);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        ActionHandlers.ChooseCardsHandler chooseCardsHandler = (cards) ->{
            try{cardsQueue.put(cards);}
            catch(InterruptedException e){ throw new Error(); }
        };

        runLater(() -> graphicalPlayer.chooseAdditionalCards(chooseCardsHandler, options));
        return cardsQueue.peek();
    }

    private <T> T retrieveFromQueue(BlockingQueue<T> blockingQueue){
        try{
            return blockingQueue.take();
        }
        catch (InterruptedException e){
            throw new Error();
        }
    }

    private <T> void putInQueue(BlockingQueue<T> blockingQueue, T element){
        try{
            blockingQueue.put(element);
        }
        catch (InterruptedException e){
            throw new Error();
        }
    }
}
