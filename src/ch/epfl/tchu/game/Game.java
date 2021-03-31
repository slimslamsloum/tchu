package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Game {

    private Game() {}

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size()==2 && playerNames.size()==2);

        for(Map.Entry<PlayerId, Player> playerEntry: players.entrySet()){
            playerEntry.getValue().initPlayers(playerEntry.getKey(),playerNames);
        }

        GameState gameState = GameState.initial(tickets,rng);
        players.get(gameState.currentPlayerId()).receiveInfo(new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst());

        for(PlayerId playerId : PlayerId.ALL){
            players.get(playerId).setInitialTicketChoice(gameState.topTickets(5));
        }
        for(PlayerId playerId : PlayerId.ALL){
            players.get(playerId).updateState(gameState, gameState.playerState(playerId));
            players.get(playerId).chooseInitialTickets();
        }
        for(PlayerId playerId : PlayerId.ALL){
            players.get(playerId).receiveInfo(new Info(playerNames.get(playerId)).keptTickets(gameState.playerState(playerId).ticketCount()));
        }

        while (!gameState.lastTurnBegins()) {
            Player currentPlayer =  players.get(gameState.currentPlayerId());
            currentPlayer.updateState(gameState, gameState.currentPlayerState());
            Player.TurnKind playerChoice = currentPlayer.nextTurn();

            //pas sur de cette partie
            if (playerChoice == Player.TurnKind.CLAIM_ROUTE) {
                Route route = currentPlayer.claimedRoute();
                SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                List<Card> drawnCards=  new ArrayList<>();
                boolean canClaimRoute = gameState.currentPlayerState().canClaimRoute(route);

                if (canClaimRoute) {
                    if (route.level() == Route.Level.UNDERGROUND) {
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++){
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCards.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }

                        //implement fact that locomotives can always be used
                        SortedBag.Builder<Card> AdditionalCardsToPlayBuilder = new SortedBag.Builder<>();
                        for (Card card: initialClaimCards){
                            for (Card drawnCard: drawnCards){
                                if (drawnCard.equals(card) && card != Card.LOCOMOTIVE){
                                    AdditionalCardsToPlayBuilder.add(card);
                                }
                                if(drawnCard==Card.LOCOMOTIVE){
                                    AdditionalCardsToPlayBuilder.add(Card.LOCOMOTIVE);
                                }
                            }
                        }
                        SortedBag<Card> AdditionalCardsToPlay = AdditionalCardsToPlayBuilder.build();


                        if(AdditionalCardsToPlay.isEmpty()){
                            gameState=gameState.withMoreDiscardedCards(SortedBag.of(drawnCards));
                        }
                        else{
                            int cardsToPlay = route.additionalClaimCardsCount(initialClaimCards, SortedBag.of(drawnCards));
                            SortedBag<Card> cardsPlayedForTunnel = initialClaimCards.union(AdditionalCardsToPlay);
                            gameState = gameState.withClaimedRoute(route, cardsPlayedForTunnel);
                        }
                    }
                    if (route.level() == Route.Level.OVERGROUND) {
                        gameState = gameState.withClaimedRoute(route, initialClaimCards);
                    }
                    gameState=gameState.forNextTurn();
                }
            }

            if (playerChoice == Player.TurnKind.DRAW_CARDS) {
                for(int i = 0; i<2; i++) {
                    if (i==1) {currentPlayer.updateState(gameState, gameState.currentPlayerState());}
                    if (gameState.canDrawCards()){
                        int slot = currentPlayer.drawSlot();
                        if (slot == Constants.DECK_SLOT) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            gameState = gameState.withBlindlyDrawnCard();
                        }
                        if (slot >= 0 && slot <= 4) {
                            gameState = gameState.withDrawnFaceUpCard(slot);
                        }
                    }
                }
                gameState=gameState.forNextTurn();
            }

            if (playerChoice == Player.TurnKind.DRAW_TICKETS && gameState.canDrawTickets()) {
                tickets = currentPlayer.chooseTickets(gameState.topTickets(3));
                gameState=gameState.withChosenAdditionalTickets(gameState.topTickets(3),tickets);
                gameState = gameState.forNextTurn();
            }

            for(int i = 0; i<2; i++){

            }

            for(PlayerId playerId : PlayerId.ALL){
                players.get(playerId).updateState(gameState, gameState.currentPlayerState());
            }
            currentPlayer.updateState(gameState, gameState.currentPlayerState());
            int player1points = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
            int player2points = gameState.playerState(PlayerId.PLAYER_2).finalPoints();

            if (player1points > player2points) {}
            if (player1points < player2points) {}
            if (player1points == player2points) {}
        }
    }
}