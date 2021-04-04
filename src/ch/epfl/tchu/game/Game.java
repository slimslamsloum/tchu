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

        int lastTurns = 2;

        while (!gameState.lastTurnBegins() || lastTurns != 0) {

            if (gameState.lastTurnBegins()){
                lastTurns -= 1;
            }

            Player currentPlayer = players.get(gameState.currentPlayerId());
            currentPlayer.updateState(gameState, gameState.currentPlayerState());
            Player.TurnKind playerChoice = currentPlayer.nextTurn();

            if (playerChoice == Player.TurnKind.CLAIM_ROUTE) {
                Route route = currentPlayer.claimedRoute();
                SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                SortedBag.Builder<Card> drawnCardsSB = new SortedBag.Builder<>();
                boolean canClaimRoute = gameState.currentPlayerState().canClaimRoute(route);

                if (canClaimRoute) {
                    if (route.level() == Route.Level.UNDERGROUND) {
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsSB.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }

                        int cardsToPlay = route.additionalClaimCardsCount(initialClaimCards, SortedBag.of(drawnCardsSB.build()));
                        List<SortedBag<Card>> possibleAdditionalCards =
                                gameState.currentPlayerState().possibleAdditionalCards(cardsToPlay, initialClaimCards,
                                        drawnCardsSB.build());

                        //what if player can't claim route??
                        if (possibleAdditionalCards.size() != 0) {
                            SortedBag<Card> chosenCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);
                            SortedBag<Card> cardsPlayedForTunnel = initialClaimCards.union(chosenCards);
                            gameState = gameState.withClaimedRoute(route, cardsPlayedForTunnel);
                        }
                        else{
                            gameState = gameState.withClaimedRoute(route, initialClaimCards);
                        }
                        gameState = gameState.withMoreDiscardedCards(SortedBag.of(drawnCardsSB.build()));
                    }
                    if (route.level() == Route.Level.OVERGROUND) {
                        gameState = gameState.withClaimedRoute(route, initialClaimCards);
                    }
                    gameState = gameState.forNextTurn();
                }
            }

            if (playerChoice == Player.TurnKind.DRAW_CARDS) {
                for (int i = 0; i < 2; i++) {
                    if (i == 1) {
                        currentPlayer.updateState(gameState, gameState.currentPlayerState());
                    }
                    if (gameState.canDrawCards()) {
                        int slot = currentPlayer.drawSlot();
                        if (slot == Constants.DECK_SLOT) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            gameState = gameState.withBlindlyDrawnCard();
                        }
                        if (slot >= 0 && slot <= 4) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            gameState = gameState.withDrawnFaceUpCard(slot);

                        }
                    }
                }
                gameState = gameState.forNextTurn();
            }

            if (playerChoice == Player.TurnKind.DRAW_TICKETS && gameState.canDrawTickets()) {
                tickets = currentPlayer.chooseTickets(gameState.topTickets(3));
                gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(3), tickets);
                gameState = gameState.forNextTurn();
            }
        }

        for(PlayerId playerId : PlayerId.ALL){
            players.get(playerId).updateState(gameState, gameState.currentPlayerState());
        }

        int player1points = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
        int player2points = gameState.playerState(PlayerId.PLAYER_2).finalPoints();

        Trail longestTrailP1 = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());
        Trail longestTrailP2 = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());

        if (longestTrailP1.length() > longestTrailP2.length()){
              player1points += 10;
        }
        if (longestTrailP1.length() < longestTrailP2.length()){
            player2points += 10;
        }

        if (player1points > player2points) {}
        if (player1points < player2points) {}
        if (player1points == player2points) {}
    }
}