package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import ch.epfl.tchu.gui.ActionHandlers.*;

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
    BlockingQueue<GraphicalPlayer> graphicalPlayerQueue;


    /**
     *Graphical player adapter constructor, which initiates all blocking queues to array blocking queues of size 1
     */
    public GraphicalPlayerAdapter(){
        ticketsQueue = new ArrayBlockingQueue<>(1);
        turnKindQueue = new ArrayBlockingQueue<>(1);
        routesQueue = new ArrayBlockingQueue<>(1);
        cardsQueue = new ArrayBlockingQueue<>(1);
        cardPlacementsQueue = new ArrayBlockingQueue<>(1);
        graphicalPlayerQueue = new ArrayBlockingQueue<>(1);
    }

    /**
     * Method to instantiate the visual interface of the player
     * @param ownId id of player on which method is called
     * @param playerNames map of Player Ids linked to player names
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayerQueue.add(new GraphicalPlayer(ownId, playerNames)));
        this.graphicalPlayer = retrieveFromQueue(graphicalPlayerQueue);
    }

    /**
     * Method that allows the information to be seen by the player
     * @param info info communicated to the player
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * Method that updates the state of the player
     * @param newState a new gamestate
     * @param ownState player's player state
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(()-> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * Method that sets the initial possible choice of ticket of the player
     * @param tickets 5 tickets distributed at the beginning of the game to a player
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        ChooseTicketsHandler chooseTicketsHandler = (ticketsToChoose) -> {
            putInQueue(ticketsQueue, ticketsToChoose);
        };
        runLater(() -> graphicalPlayer.chooseTickets(chooseTicketsHandler,tickets));
    }

    /**
     * Method that returns the initial choice of Tickets of the player
     * @return the initial choice of Tickets of the player
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return retrieveFromQueue(ticketsQueue);
    }

    /**
     * Method that allows the next turn of the player to start and to play one of the options he has
     * @return the action he decided to play, which is removed from the queue
     */
    @Override
    public TurnKind nextTurn() {

        DrawTicketsHandler drawTicketsHandler = () -> {
            putInQueue(turnKindQueue, TurnKind.DRAW_TICKETS);
        };

        ClaimRouteHandler claimRouteHandler = (route,cards) -> {
            putInQueue(turnKindQueue, TurnKind.CLAIM_ROUTE);
            putInQueue(routesQueue, route);
            putInQueue(cardsQueue, cards);
        };

        DrawCardHandler drawCardHandler = (i) -> {
            putInQueue(turnKindQueue, TurnKind.DRAW_CARDS);
            putInQueue(cardPlacementsQueue, i);
        };

        runLater(() -> graphicalPlayer.startTurn(drawTicketsHandler, drawCardHandler, claimRouteHandler));

        return retrieveFromQueue(turnKindQueue);
    }

    /**
     * Method that allows a player to draw cards from a specific slot
     * @return the card that was drown and removed to be placed in the player's hand
     */
    @Override
    public int drawSlot() {
        if (!cardPlacementsQueue.isEmpty()){
            return cardPlacementsQueue.remove();
        }
        else{
            DrawCardHandler drawCardHandler = (i) -> {
                putInQueue(cardPlacementsQueue, i);
            };
            runLater(() -> graphicalPlayer.drawCard(drawCardHandler));
            return retrieveFromQueue(cardPlacementsQueue);
        }
    }

    /**
     * Method that removes a claimed route from the possible routes that can be claimed
     * @return the route that has been claimed, which is removed from the queue
     */
    @Override
    public Route claimedRoute() {
        return retrieveFromQueue(routesQueue);
    }

    /**
     * Method that allows the player to choose amongst 3 tickets
     * @param options tickets the player has drawn from the ticket pile
     * @return the choice of the player, which is removed from the queue
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    /**
     * Method that removes from the queue the cards initially used by the player to claim a route
     * @return the card used to claim the route initially, which are removed from the queue
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return retrieveFromQueue(cardsQueue);
    }

    /**
     * Method that allows a player to choose the cards he wants to use additionally to obtain a tunnel
     * @param options possible SortedBags that can be used to claim the tunnel
     * @return the additional cards used to claim the tunnel, which are removed from the queue
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        ActionHandlers.ChooseCardsHandler chooseCardsHandler = (cards) ->{
            putInQueue(cardsQueue, cards); };

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