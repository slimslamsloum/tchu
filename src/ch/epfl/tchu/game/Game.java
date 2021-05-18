package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * How a game of tChu unfolds
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Game {
    private Game() {}

    /**
     * Method that describes how a tChu game should be played
     * @param players map of the 2 players that will play the game
     * @param playerNames map of the player names
     * @param tickets pile of tickets
     * @param rng used to shuffle decks, randomly choose who will play first, etc
     * @throws IllegalArgumentException if there are not 2 players and 2 player names in each map
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size()==2 && playerNames.size()==2);

        //here, method initPlayers is used to create the 2 players that will play the game and also
        //to give each one a name
        for(Map.Entry<PlayerId, Player> playerEntry: players.entrySet()){
            playerEntry.getValue().initPlayers(playerEntry.getKey(),playerNames);
        }

        //game state is initialized
        GameState gameState = GameState.initial(tickets,rng);

        //players get the info of who will play first
        Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst(), players);

        //in the following order, for both players: initial tickets are presented, states are updated, players choose
        //tickets, info are sent
        for (Map.Entry<PlayerId, Player> playerEntry : players.entrySet()){
            playerEntry.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState=gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }
        for (Map.Entry<PlayerId, Player> playerEntry : players.entrySet()){
            playerEntry.getValue().updateState(gameState, gameState.playerState(playerEntry.getKey()));
        }
        for (Map.Entry<PlayerId, Player> playerEntry : players.entrySet()){
            gameState= gameState.withInitiallyChosenTickets(playerEntry.getKey(),players.get(playerEntry.getKey()).chooseInitialTickets());
        }
        for (Map.Entry<PlayerId, Player> playerEntry : players.entrySet()){
            Game.allInfo(new Info(playerNames.get(playerEntry.getKey())).keptTickets(gameState.playerState(playerEntry.getKey()).ticketCount()), players);
        }

        //number of turns after a player has less than 2 cars
        int lastTurns = 1;

        //loop that defines what happens in a round. A player has 3 choices, and in the next
        //loop the next player will play.
        //The loop continues until a player has less than 2 cars (lastTurnBegins is true), in which
        //case each player plays once and then the game ends.
        while (!gameState.lastTurnBegins() || lastTurns != 0) {
            //once we know a player has less than 2 cars, both players get that information.
            //lastTurns then decrements by 1, and when it is equal to 0 (both players will have played
            //once more) the loop will end
            if (gameState.lastTurnBegins()){
                allInfo(new Info(playerNames.get(gameState.currentPlayerId())).lastTurnBegins(gameState.currentPlayerState().carCount()),players);
                lastTurns -= 1;
            }

            //Player variable containing the current player playing
            Player currentPlayer = players.get(gameState.currentPlayerId());

            //both players get the info of who is going to play first
            Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).canPlay(),players);

            //current player's gamestate is updated
            Game.updateAll(gameState,players);

            //current player gets to choose which of the 3 actions he is going to perform
            Player.TurnKind playerChoice = currentPlayer.nextTurn();


            //if the current player has chosen to draw tickets and if the player can draw tickets,
            //the following block of code runs
            if (playerChoice == Player.TurnKind.DRAW_TICKETS) {
                //the player chooses amongst the top 3 tickets in the ticket pile
                SortedBag<Ticket> topTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(topTickets);
                //both players get the info that the current player has drawn tickets and has kept a certain amount
                Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).drewTickets(Constants.IN_GAME_TICKETS_COUNT),players);
                Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).keptTickets(chosenTickets.size()),players);
                //the player keeps the tickets he chose, the other are discarded
                gameState = gameState.withChosenAdditionalTickets(topTickets, chosenTickets);
            }

            //if the current player chooses to draw cards, the following
            //block of code runs
            else if (playerChoice == Player.TurnKind.DRAW_CARDS) {
                //the player gets to pick 2 times a card, explaining the following for loop
                for (int i = 0; i < 2; i++) {
                    //current player can only pick a card if discard size + deck size is bigger than 5
                    if (gameState.canDrawCards()) {
                        //deck is recreated if needed
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        //state is update before second draw slot
                        if (i == 1) {
                            Game.updateAll(gameState,players);
                        }
                        //slot = card slot that the player wants to pick
                        int slot = currentPlayer.drawSlot();
                        //if player wants to pick a card from pile
                        if (slot == Constants.DECK_SLOT) {
                            //deck is recreated from discard pile if needed
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            //player blindly draws a card from deck
                            gameState = gameState.withBlindlyDrawnCard();
                            //players get info that current player has blindly drawn a card
                            Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).drewBlindCard(),players);
                        }
                        //if current player wants to draw a face up card
                        if (slot >= Constants.FACE_UP_CARD_SLOTS.get(0) && slot <= Constants.FACE_UP_CARD_SLOTS.get(4)) {
                            //players receive info that current player will draw the face up card at index slot
                            Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).drewVisibleCard(gameState.cardState().faceUpCard(slot)),players);
                            //player draws card at index slot
                            gameState = gameState.withDrawnFaceUpCard(slot);
                        }
                    }
                }
            }
            //if the current player wants to claim a route, the following
            //block of code runs
            else if (playerChoice == Player.TurnKind.CLAIM_ROUTE) {
                Route route = currentPlayer.claimedRoute();
                SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                SortedBag.Builder<Card> drawnCardsSB = new SortedBag.Builder<>();
                boolean canClaimRoute = gameState.currentPlayerState().canClaimRoute(route);

                //if the player can claim the route with the cards he has, the following block of code runs
                if (canClaimRoute) {
                    //if the player tries to claim and underground route, the following block of code runs
                    if (route.level() == Route.Level.UNDERGROUND) {
                        //both players receive the info the the current player is attempting to claim
                        //an underground route
                        Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).attemptsTunnelClaim(route,initialClaimCards),players);
                        //the top 3 cards of the pile are revealed, deck is recreated each time we pick a card from
                        //the pile
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsSB.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        //number of cards the player has to play is computed
                        int cardsToPlay = route.additionalClaimCardsCount(initialClaimCards, SortedBag.of(drawnCardsSB.build()));
                        //players get the info that the top 3 cards of the pile were drawn
                        Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).drewAdditionalCards(drawnCardsSB.build(),cardsToPlay),players);
                        //list of possible additional cards the player needs to add is computed
                        if (cardsToPlay!=0){
                            List<SortedBag<Card>> possibleAdditionalCards =
                                    gameState.currentPlayerState().possibleAdditionalCards(cardsToPlay, initialClaimCards,
                                            drawnCardsSB.build());

                            //if possibleAdditionalCards's size isn't null (i.e player has different options to take the tunnel)
                            if (possibleAdditionalCards.size() != 0) {
                                //SB of the chosen cards of the player is created
                                SortedBag<Card> chosenCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);
                                //if the chosen cards size is equal to the var cardsToPlay (i.e player has correct cards
                                //to claim the underground route), the following block runs
                                if (chosenCards.size()== cardsToPlay){
                                    //SB of the total cards played to claim the route is created
                                    SortedBag<Card> cardsPlayedForTunnel = initialClaimCards.union(chosenCards);
                                    //Both players receive the info that the current player has claimed the underground tunnel
                                    Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).claimedRoute(route,cardsPlayedForTunnel),players);
                                    //player claims the undergound route
                                    gameState = gameState.withClaimedRoute(route, cardsPlayedForTunnel);
                                }
                                //in this case, the current player doesn't want to play the additional cards to claim
                                //the route and both players receive the info that the current player didn't
                                //claim the route
                                else {
                                    Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).didNotClaimRoute(route),players);
                                }
                            }
                            //in this case, the current player cannot afford to take the tunnel
                            //the route and both players receive the info that the current player didn't
                            //claim the route
                            else {
                                Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).didNotClaimRoute(route),players);
                            }
                        }
                        //if  cardsToPlay equals 0 (i.e player doesn't have to play additional cards)
                        else{
                            //current player claims the route
                            gameState = gameState.withClaimedRoute(route, initialClaimCards);
                            //both players get the info that the current player has claimed the route
                            Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).claimedRoute(route,initialClaimCards),players);
                        }
                        //the gamestate has the previous top 3 cards of the pile added to the discard pile
                        gameState = gameState.withMoreDiscardedCards(SortedBag.of(drawnCardsSB.build()));
                    }
                    //if the route's level is overground, the following block of code runs
                    if (route.level() == Route.Level.OVERGROUND) {
                        //the player claims the route with initialClaimCards
                        gameState = gameState.withClaimedRoute(route, initialClaimCards);
                        //both players get the info that this route was claimed by the current player
                        Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).claimedRoute(route,initialClaimCards),players);
                    }
                }
                else {
                    //if the player can't claim the route, both players are made aware of it
                    Game.allInfo(new Info(playerNames.get(gameState.currentPlayerId())).didNotClaimRoute(route),players);
                }
            }
            //the current player becomes the next player
            gameState = gameState.forNextTurn();
        }

        //both player have their gamestates updated at the end of the game
        Game.updateAll(gameState,players);
        //each player's number of points is computed in terms of claim points and ticket points
        int player1points = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
        int player2points = gameState.playerState(PlayerId.PLAYER_2).finalPoints();

        //longest trail is computed
        Trail longestTrailP1 = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());
        Trail longestTrailP2 = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());

        //10 extra points is given to the player that has claimed the longest route
        if (longestTrailP1.length() > longestTrailP2.length()){
            player1points += 10;
            Game.allInfo(new Info(playerNames.get(PlayerId.PLAYER_1)).getsLongestTrailBonus(longestTrailP1),players);
        }
        else if (longestTrailP1.length() < longestTrailP2.length()){
            player2points += 10;
            Game.allInfo(new Info(playerNames.get(PlayerId.PLAYER_1)).getsLongestTrailBonus(longestTrailP2),players);
        }
        else {
            player1points += 10;
            player2points += 10;
            Game.allInfo(new Info(playerNames.get(PlayerId.PLAYER_1)).getsLongestTrailBonus(longestTrailP2),players);
            Game.allInfo(new Info(playerNames.get(PlayerId.PLAYER_1)).getsLongestTrailBonus(longestTrailP1),players);
        }

        //both players get the info of who was won or if there a tie
        if (player1points > player2points) {
            Game.allInfo(new Info(playerNames.get(PlayerId.PLAYER_1)).won(player1points,player2points),players);
        }
        if (player1points < player2points) {
            Game.allInfo(new Info(playerNames.get(PlayerId.PLAYER_2)).won(player2points,player1points),players);
        }
        if (player1points == player2points) {Game.allInfo(Info.draw(List.of(playerNames.get(PlayerId.PLAYER_1),playerNames.get(PlayerId.PLAYER_2)),player1points),players);}
    }

    /**
     * Method that gives both players a certain info given as an argument
     * @param info info that will be given to the players
     * @param players map of the players that are playing
     */
    private static void allInfo(String info,Map<PlayerId, Player> players){
        players.forEach(((playerId, player) -> players.get(playerId).receiveInfo(info)));
    }

    /**
     * Method that updates the gamestate for both players
     * @param gameState state of the game
     * @param players map of player Ids linked to players
     */
    private static void updateAll(GameState gameState,Map<PlayerId, Player> players ){
        players.forEach(((playerId, player) -> players.get(playerId).updateState(gameState,gameState.playerState(playerId))));
    }
}