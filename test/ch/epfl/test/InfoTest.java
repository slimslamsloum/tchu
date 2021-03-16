package ch.epfl.test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoTest {


    @Test
    void infoCardNameWorks() {
        var actualK1 = Info.cardName(Card.BLACK, 1);
        var expectedK1 = "noire";
        assertEquals(expectedK1, actualK1);
        var actualK9 = Info.cardName(Card.BLACK, 9);
        var expectedK9 = "noires";
        assertEquals(expectedK9, actualK9);

        var actualB1 = Info.cardName(Card.BLUE, 1);
        var expectedB1 = "bleue";
        assertEquals(expectedB1, actualB1);
        var actualB9 = Info.cardName(Card.BLUE, 9);
        var expectedB9 = "bleues";
        assertEquals(expectedB9, actualB9);

        var actualG1 = Info.cardName(Card.GREEN, 1);
        var expectedG1 = "verte";
        assertEquals(expectedG1, actualG1);
        var actualG9 = Info.cardName(Card.GREEN, 9);
        var expectedG9 = "vertes";
        assertEquals(expectedG9, actualG9);

        var actualO1 = Info.cardName(Card.ORANGE, 1);
        var expectedO1 = "orange";
        assertEquals(expectedO1, actualO1);
        var actualO9 = Info.cardName(Card.ORANGE, 9);
        var expectedO9 = "oranges";
        assertEquals(expectedO9, actualO9);

        var actualR1 = Info.cardName(Card.RED, 1);
        var expectedR1 = "rouge";
        assertEquals(expectedR1, actualR1);
        var actualR9 = Info.cardName(Card.RED, 9);
        var expectedR9 = "rouges";
        assertEquals(expectedR9, actualR9);

        var actualV1 = Info.cardName(Card.VIOLET, 1);
        var expectedV1 = "violette";
        assertEquals(expectedV1, actualV1);
        var actualV9 = Info.cardName(Card.VIOLET, 9);
        var expectedV9 = "violettes";
        assertEquals(expectedV9, actualV9);

        var actualW1 = Info.cardName(Card.WHITE, 1);
        var expectedW1 = "blanche";
        assertEquals(expectedW1, actualW1);
        var actualW9 = Info.cardName(Card.WHITE, 9);
        var expectedW9 = "blanches";
        assertEquals(expectedW9, actualW9);

        var actualY1 = Info.cardName(Card.YELLOW, 1);
        var expectedY1 = "jaune";
        assertEquals(expectedY1, actualY1);
        var actualY9 = Info.cardName(Card.YELLOW, 9);
        var expectedY9 = "jaunes";
        assertEquals(expectedY9, actualY9);

        var actualL1 = Info.cardName(Card.LOCOMOTIVE, 1);
        var expectedL1 = "locomotive";
        assertEquals(expectedL1, actualL1);
        var actualL9 = Info.cardName(Card.LOCOMOTIVE, 9);
        var expectedL9 = "locomotives";
        assertEquals(expectedL9, actualL9);
    }

    @Test
    void infoDrawWorks() {
        var actual = Info.draw(List.of("Ada", "Ada"), 17);
        var expected = "\nAda et Ada sont ex æqo avec 17 points !\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoWillPlayFirstWorks() {
        var info = new Info("Niklaus");
        var actual = info.willPlayFirst();
        var expected = "Niklaus jouera en premier.\n\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoKeptTicketsWorks() {
        var info = new Info("Edsger");

        var actual1 = info.keptTickets(1);
        var expected1 = "Edsger a gardé 1 billet.\n";
        assertEquals(expected1, actual1);

        var actual5 = info.keptTickets(5);
        var expected5 = "Edsger a gardé 5 billets.\n";
        assertEquals(expected5, actual5);
    }

    @Test
    void infoCanPlayWorks() {
        var info = new Info("Charles");

        var actual = info.canPlay();
        var expected = "\nC'est à Charles de jouer.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoDrewTicketsWorks() {
        var info = new Info("Linus");

        var actual1 = info.drewTickets(1);
        var expected1 = "Linus a tiré 1 billet...\n";
        assertEquals(expected1, actual1);

        var actual5 = info.drewTickets(5);
        var expected5 = "Linus a tiré 5 billets...\n";
        assertEquals(expected5, actual5);
    }

    @Test
    void infoDrewBlindCardWorks() {
        var info = new Info("Alan");

        var actual = info.drewBlindCard();
        var expected = "Alan a tiré une carte de la pioche.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoDrewVisibleCardWorks() {
        var info = new Info("John");

        var actual = info.drewVisibleCard(Card.GREEN);
        var expected = "John a tiré une carte verte visible.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoClaimedRouteWorks() {
        var info = new Info("Brian");

        var s1 = new Station(0, "Neuchâtel");
        var s2 = new Station(1, "Lausanne");

        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.ORANGE);
        var actual1 = info.claimedRoute(route1, SortedBag.of(Card.ORANGE));
        var expected1 = "Brian a pris possession de la route Neuchâtel – Lausanne au moyen de 1 orange.\n";
        assertEquals(expected1, actual1);

        var route2 = new Route("1", s1, s2, 2, Route.Level.OVERGROUND, null);
        var actual2 = info.claimedRoute(route2, SortedBag.of(2, Card.RED));
        var expected2 = "Brian a pris possession de la route Neuchâtel – Lausanne au moyen de 2 rouges.\n";
        assertEquals(expected2, actual2);

        var route3 = new Route("1", s1, s2, 4, Route.Level.UNDERGROUND, null);
        var actual3 = info.claimedRoute(route3, SortedBag.of(4, Card.BLUE, 2, Card.LOCOMOTIVE));
        var expected3 = "Brian a pris possession de la route Neuchâtel – Lausanne au moyen de 4 bleues et 2 locomotives.\n";
        assertEquals(expected3, actual3);
    }

    @Test
    void infoAttemptsTunnelClaimWorks() {
        var info = new Info("Grace");

        var s1 = new Station(0, "Wassen");
        var s2 = new Station(1, "Coire");

        var route1 = new Route("1", s1, s2, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        var actual1 = info.attemptsTunnelClaim(route1, SortedBag.of(Card.ORANGE));
        var expected1 = "Grace tente de s'emparer du tunnel Wassen – Coire au moyen de 1 orange !\n";
        assertEquals(expected1, actual1);

        var route2 = new Route("1", s1, s2, 2, Route.Level.UNDERGROUND, null);
        var actual2 = info.attemptsTunnelClaim(route2, SortedBag.of(2, Card.RED));
        var expected2 = "Grace tente de s'emparer du tunnel Wassen – Coire au moyen de 2 rouges !\n";
        assertEquals(expected2, actual2);

        var route3 = new Route("1", s1, s2, 4, Route.Level.UNDERGROUND, null);
        var actual3 = info.attemptsTunnelClaim(route3, SortedBag.of(4, Card.BLUE, 2, Card.LOCOMOTIVE));
        var expected3 = "Grace tente de s'emparer du tunnel Wassen – Coire au moyen de 4 bleues et 2 locomotives !\n";
        assertEquals(expected3, actual3);
    }

    @Test
    void infoDrewAdditionalCardsWorks() {
        var info = new Info("Margaret");

        var actual1 = info.drewAdditionalCards(SortedBag.of(3, Card.ORANGE), 0);
        var expected1 = "Les cartes supplémentaires sont 3 oranges. Elles n'impliquent aucun coût additionnel.\n";
        assertEquals(expected1, actual1);

        var actual2 = info.drewAdditionalCards(SortedBag.of(1, Card.WHITE, 2, Card.RED), 1);
        var expected2 = "Les cartes supplémentaires sont 2 rouges et 1 blanche. Elles impliquent un coût additionnel de 1 carte.\n";
        assertEquals(expected2, actual2);

        var actual3 = info.drewAdditionalCards(SortedBag.of(1, Card.YELLOW, 2, Card.GREEN), 2);
        var expected3 = "Les cartes supplémentaires sont 2 vertes et 1 jaune. Elles impliquent un coût additionnel de 2 cartes.\n";
        assertEquals(expected3, actual3);

        var actual4 = info.drewAdditionalCards(SortedBag.of(1, Card.VIOLET, 2, Card.LOCOMOTIVE), 3);
        var expected4 = "Les cartes supplémentaires sont 1 violette et 2 locomotives. Elles impliquent un coût additionnel de 3 cartes.\n";
        assertEquals(expected4, actual4);
    }

    @Test
    void infoDidNotClaimRouteWorks() {
        var info = new Info("Guido");
        var s1 = new Station(0, "Zernez");
        var s2 = new Station(1, "Klosters");

        var route = new Route("1", s1, s2, 4, Route.Level.UNDERGROUND, Color.ORANGE);
        var actual = info.didNotClaimRoute(route);
        var expected = "Guido n'a pas pu (ou voulu) s'emparer de la route Zernez – Klosters.\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoLastTurnBeginsWorks() {
        var info = new Info("Martin");

        var actual1 = info.lastTurnBegins(0);
        var expected1 = "\nMartin n'a plus que 0 wagons, le dernier tour commence !\n";
        assertEquals(expected1, actual1);

        var actual2 = info.lastTurnBegins(1);
        var expected2 = "\nMartin n'a plus que 1 wagon, le dernier tour commence !\n";
        assertEquals(expected2, actual2);

        var actual3 = info.lastTurnBegins(2);
        var expected3 = "\nMartin n'a plus que 2 wagons, le dernier tour commence !\n";
        assertEquals(expected3, actual3);
    }

    @Test
    void infoGetsLongestTrailBonusWorks() {
        var info = new Info("Larry");

        var s1 = new Station(0, "Montreux");
        var s2 = new Station(1, "Montreux");

        var route = new Route("1", s1, s2, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        var trail = Trail.longest(List.of(route));

        var actual = info.getsLongestTrailBonus(trail);
        var expected = "\nLarry reçoit un bonus de 10 points pour le plus long trajet (Montreux – Montreux).\n";
        assertEquals(expected, actual);
    }

    @Test
    void infoWonWorks() {
        var info = new Info("Bjarne");

        var actual1 = info.won(2, 1);
        var expected1 = "\nBjarne remporte la victoire avec 2 points, contre 1 point !\n";
        assertEquals(expected1, actual1);

        var actual2 = info.won(3, 2);
        var expected2 = "\nBjarne remporte la victoire avec 3 points, contre 2 points !\n";
        assertEquals(expected2, actual2);
    }
}
