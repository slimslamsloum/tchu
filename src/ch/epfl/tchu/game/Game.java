package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

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

        GameState gameState =GameState.initial(tickets,rng);
        players.get(gameState.currentPlayerId()).receiveInfo(new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst());

        for(PlayerId playerId : PlayerId.ALL){
            players.get(playerId).setInitialTicketChoice(gameState.topTickets(5));
        }
        for(PlayerId playerId : PlayerId.ALL){
            players.get(playerId).chooseInitialTickets();
        }
        for(PlayerId playerId : PlayerId.ALL){
            players.get(playerId).receiveInfo(new Info(playerNames.get(playerId)).keptTickets(gameState.playerState(playerId).ticketCount()));
        }

        while (!gameState.lastTurnBegins()) {
            boolean turnDone = false;

            while (turnDone = false) {
                Player.TurnKind playerChoice = players.get(gameState.currentPlayerId()).nextTurn();

                if (playerChoice == Player.TurnKind.CLAIM_ROUTE) {
                    players.get(gameState.currentPlayerId()).claimedRoute();
                    players.get(gameState.currentPlayerId()).initialClaimCards();
                    if (players.get(gameState.currentPlayerId()).claimedRoute().level() == Route.Level.UNDERGROUND) {
                        players.get(gameState.currentPlayerId()).chooseAdditionalCards
                                (gameState.currentPlayerState().possibleAdditionalCards());
                    }
                //cette partie c'est un enorme bordel, a finir
                }

                if (playerChoice == Player.TurnKind.DRAW_CARDS) {
                    gameState=gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int slot =  players.get(gameState.currentPlayerId()).drawSlot();
                    if (slot == Constants.DECK_SLOT){
                        gameState=gameState.withBlindlyDrawnCard();
                    }
                    if (slot >0 && slot <4){
                        gameState=gameState.withDrawnFaceUpCard(slot);
                    }
                    turnDone = true;
                }

                if (playerChoice == Player.TurnKind.DRAW_TICKETS && gameState.canDrawTickets()) {
                    tickets = players.get(gameState.currentPlayerId()).chooseTickets(gameState.topTickets(3));
                    gameState=gameState.withChosenAdditionalTickets(gameState.topTickets(3),tickets);
                    turnDone = true;
                }

                if(turnDone==true){
                    gameState = gameState.forNextTurn();
                }
            }
            for(int i = 0; i<2; i++){
                Player.TurnKind playerChoice = players.get(gameState.currentPlayerId()).nextTurn();
                if (playerChoice == Player.TurnKind.CLAIM_ROUTE) {
                    players.get(gameState.currentPlayerId()).claimedRoute();
                    players.get(gameState.currentPlayerId()).initialClaimCards();
                    if (players.get(gameState.currentPlayerId()).claimedRoute().level() == Route.Level.UNDERGROUND) {
                        players.get(gameState.currentPlayerId()).chooseAdditionalCards
                                (gameState.currentPlayerState().possibleAdditionalCards());
                    }
                }
                if (playerChoice == Player.TurnKind.DRAW_CARDS) {
                    gameState=gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int slot =  players.get(gameState.currentPlayerId()).drawSlot();
                    if (slot == Constants.DECK_SLOT){
                        gameState=gameState.withBlindlyDrawnCard();
                    }
                    if (slot >0 && slot <4){
                        gameState=gameState.withDrawnFaceUpCard(slot);
                    }
                    turnDone = true;
                }
                if (playerChoice == Player.TurnKind.DRAW_TICKETS && gameState.canDrawTickets()) {
                    tickets = players.get(gameState.currentPlayerId()).chooseTickets(gameState.topTickets(3));
                    gameState=gameState.withChosenAdditionalTickets(gameState.topTickets(3),tickets);
                    turnDone = true;
                }
                if(turnDone==true){
                    gameState = gameState.forNextTurn();
                }
            }

            int player1points = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
            int player2points = gameState.playerState(PlayerId.PLAYER_2).finalPoints();

            if (player1points > player2points) {}
            if (player1points < player2points) {}
            if (player1points == player2points) {}
        }
    }
}