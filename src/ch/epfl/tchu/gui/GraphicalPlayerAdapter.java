package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Adapter of the class GraphicalPlayer
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class GraphicalPlayerAdapter implements Player {

    //attributes needed for the graphical player adapter: several blocking queues (for cards, tickets routes, etc)
    //and a graphical player
    GraphicalPlayer graphicalPlayer;
    BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    BlockingQueue<TurnKind> turnKindQueue;
    BlockingQueue<Route> routesQueue;
    BlockingQueue<SortedBag<Card>> cardsQueue;
    BlockingQueue<Integer> cardPlacementsQueue;

    /**
     *Graphical player adapter constructor, which initiates all blocking queues to array blocking queues of size 1
     */
    public GraphicalPlayerAdapter(){
        ticketsQueue = new ArrayBlockingQueue<>(1);
        turnKindQueue = new ArrayBlockingQueue<>(1);
        routesQueue = new ArrayBlockingQueue<>(1);
        cardsQueue = new ArrayBlockingQueue<>(1);
        cardPlacementsQueue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        BlockingQueue<GraphicalPlayer> graphicalPlayerQueue = new ArrayBlockingQueue<>(1);
        runLater(() -> graphicalPlayerQueue.add(new GraphicalPlayer(ownId, playerNames)));
        this.graphicalPlayer = retrieveFromQueue(graphicalPlayerQueue);
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
        return retrieveFromQueue(ticketsQueue);
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
            putInQueue(turnKindQueue, TurnKind.DRAW_CARDS);
            putInQueue(cardPlacementsQueue, i);
        };

        runLater(() -> graphicalPlayer.startTurn(drawTicketsHandler, drawCardHandler, claimRouteHandler));

        return retrieveFromQueue(turnKindQueue);
    }

    @Override
    public int drawSlot() {
        if (!cardPlacementsQueue.isEmpty()){
            return cardPlacementsQueue.remove();
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
        return retrieveFromQueue(cardsQueue);
    }

    /**
     * Method returning the element in a queue
     * @param blockingQueue queue in argument
     * @param <T> type parameter used in the queue
     * @return only element in the queue, while also removing it from the queue
     * @throws Error if an InterruptedException is caught
     */
    private <T> T retrieveFromQueue(BlockingQueue<T> blockingQueue){
        try{
            return blockingQueue.take();
        }
        catch (InterruptedException e){
            throw new Error();
        }
    }

    /**
     * Puts element in a blocking queue
     * @param blockingQueue queue where element will be added
     * @param element element to be added
     * @param <T> type parameter of the element
     * @throws Error if an Interrupted exception is caught
     */
    private <T> void putInQueue(BlockingQueue<T> blockingQueue, T element){
        try{
            blockingQueue.put(element);
        }
        catch (InterruptedException e){
            throw new Error();
        }
    }
}
