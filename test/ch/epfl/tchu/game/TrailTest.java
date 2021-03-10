package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrailTest {
    @Test
    void trailLongestWorksOnGivenExample() {
        var s1 = new Station(1, "Yverdon");
        var s2 = new Station(2, "Fribourg");
        var s3 = new Station(3, "Neuchâtel");
        var s4 = new Station(4, "Berne");
        var s5 = new Station(5, "Lucerne");
        var s6 = new Station(6, "Soleure");

        var routes = List.of(
                new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
                new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
                new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED),
                new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
                new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
                new Route("F", s4, s2, 1, Route.Level.OVERGROUND, Color.ORANGE));

        var longest = Trail.longest(routes);
        assertEquals(13, longest.length());
        if (s2.equals(longest.station1()))
            assertEquals(s5, longest.station2());
        else if (s2.equals(longest.station2()))
            assertEquals(s5, longest.station1());
        else
            fail("Unexpected start station: " + longest.station1());
    }

    @Test
    void trailLongestWorksWithEmptyRoutes() {
        var longest = Trail.longest(List.of());
        assertEquals(0, longest.length());
        assertNull(longest.station1());
        assertNull(longest.station2());
    }

    @Test
    void trailLongestWorksWithDisconnectedRoutes() {
        var chRoutes = new ChRoutes();
        var routes = List.of(
                chRoutes.SAR_VAD_1,
                chRoutes.BER_LUC_1,
                chRoutes.GEN_YVE_1,
                chRoutes.IT3_LUG_1);
        var longest = Trail.longest(routes);
        assertEquals(6, longest.length());
        if (longest.station1().equals(chRoutes.GEN))
            assertEquals(longest.station2(), chRoutes.YVE);
        else if (longest.station1().equals(chRoutes.YVE))
            assertEquals(longest.station1(), chRoutes.GEN);
        else
            fail("Unexpected start station: " + longest.station1());
    }

    @Test
    void trailLongestWorksWithSingleCycle() {
        var chRoutes = new ChRoutes();
        var routes = List.of(
                chRoutes.FRI_LAU_1,
                chRoutes.BER_FRI_1,
                chRoutes.BER_LUC_1,
                chRoutes.INT_LUC_1,
                chRoutes.BRI_INT_1,
                chRoutes.BRI_SIO_1,
                chRoutes.MAR_SIO_1,
                chRoutes.LAU_MAR_1);
        var longest = Trail.longest(routes);
        assertEquals(23, longest.length());
    }

    @Test
    void trailLongestWorksWithDoubleCycle() {
        var chRoutes = new ChRoutes();
        var routes = List.of(
                chRoutes.BER_SOL_1,
                chRoutes.OLT_SOL_1,
                chRoutes.LUC_OLT_1,
                chRoutes.INT_LUC_1,
                chRoutes.BER_INT_1,
                chRoutes.BER_LUC_1);
        var longest = Trail.longest(routes);
        assertEquals(17, longest.length());
    }

    @Test
    void trailLongestWorksWithTripleCycle() {
        var chRoutes = new ChRoutes();
        var routes = List.of(
                chRoutes.SCE_ZUR_1,
                chRoutes.SCE_WIN_1,
                chRoutes.WIN_ZUR_1,
                chRoutes.STG_ZUR_1,
                chRoutes.PFA_STG_1,
                chRoutes.PFA_ZUR_1,
                chRoutes.ZOU_ZUR_1,
                chRoutes.LUC_ZOU_1,
                chRoutes.LUC_OLT_1,
                chRoutes.OLT_ZUR_1,
                chRoutes.BAD_ZUR_1);
        var longest = Trail.longest(routes);
        assertEquals(23, longest.length());
    }

    @Test
    void trailLongestTrailWorksOnKnownExample1() {
        var routes = new ChRoutes();

        var routesR = List.of(
                routes.FR2_GEN_1,
                routes.GEN_LAU_1,
                routes.LAU_MAR_1,
                routes.MAR_SIO_1,
                routes.BRI_SIO_1,
                routes.BRI_IT5_1,
                routes.LAU_NEU_1,
                routes.FRI_LAU_1,
                routes.BER_FRI_1,
                routes.BER_LUC_1,
                routes.LUC_ZOU_1,
                routes.ZOU_ZUR_1,
                routes.STG_ZUR_1,
                routes.DE5_STG_1,
                routes.STG_VAD_1);

        var routesG = List.of(
                routes.BER_NEU_1,
                routes.BER_SOL_1,
                routes.OLT_SOL_1,
                routes.BAD_OLT_1,
                routes.BAD_ZUR_1,
                routes.WIN_ZUR_1,
                routes.PFA_ZUR_1,
                routes.LUC_OLT_1,
                routes.LUC_SCZ_1,
                routes.SCZ_WAS_1,
                routes.BEL_WAS_1,
                routes.BEL_LUG_1,
                routes.COI_WAS_1,
                routes.COI_SAR_1,
                routes.COI_DAV_1,
                routes.DAV_AT3_1,
                routes.BRU_COI_1);

        var routesB = List.of(
                routes.FRI_LAU_2,
                routes.BER_FRI_2,
                routes.BER_LUC_2,
                routes.LUC_ZOU_2,
                routes.ZOU_ZUR_2,
                routes.OLT_ZUR_1,
                routes.BAL_OLT_1,
                routes.BAL_DE1_1,
                routes.BAL_DEL_1,
                routes.SCZ_ZOU_2,
                routes.SCZ_WAS_2,
                routes.BEL_WAS_2,
                routes.BEL_LUG_2,
                routes.PFA_SCZ_1,
                routes.PFA_SAR_1,
                routes.SAR_VAD_1,
                routes.AT2_VAD_1);

        var longestR = Trail.longest(routesR);
        var longestG = Trail.longest(routesG);
        var longestB = Trail.longest(routesB);

        assertEquals(28, longestR.length());
        assertEquals(21, longestG.length());
        assertEquals(17, longestB.length());
    }

    @Test
    void trailLongestWorksOnKnownExample2() {
        var routes = new ChRoutes();

        var routesR = List.of(
                routes.BRI_LOC_1,
                routes.BRI_SIO_1,
                routes.MAR_SIO_1,
                routes.LAU_MAR_1,
                routes.GEN_LAU_1,
                routes.GEN_YVE_1,
                routes.LCF_YVE_1,
                routes.DEL_LCF_1,
                routes.DEL_SOL_1,
                routes.OLT_SOL_1,
                routes.BAL_OLT_1,
                routes.BER_LUC_1,
                routes.SCE_WIN_1);

        var routesG = List.of(
                routes.DE2_SCE_1,
                routes.SCE_WIN_2,
                routes.WIN_ZUR_1,
                routes.ZOU_ZUR_1,
                routes.SCZ_ZOU_1,
                routes.SCZ_WAS_1,
                routes.BEL_WAS_1,
                routes.BEL_LUG_1,
                routes.COI_WAS_1,
                routes.COI_SAR_1,
                routes.SAR_VAD_1,
                routes.AT2_VAD_1,
                routes.BRU_COI_1,
                routes.BRU_IT2_1);

        var routesB = List.of(
                routes.DE4_KRE_1,
                routes.KRE_WIN_1,
                routes.WIN_ZUR_2,
                routes.BAD_ZUR_1,
                routes.ZOU_ZUR_2,
                routes.LUC_ZOU_2,
                routes.INT_LUC_1,
                routes.BRI_INT_1,
                routes.BER_INT_1,
                routes.BER_FRI_1,
                routes.BER_NEU_1,
                routes.LCF_NEU_1,
                routes.FR3_LCF_1,
                routes.BER_SOL_1,
                routes.BAL_DEL_1,
                routes.BAL_DE1_1);

        var longestR = Trail.longest(routesR);
        var longestG = Trail.longest(routesG);
        var longestB = Trail.longest(routesB);

        assertEquals(35, longestR.length());
        assertEquals(19, longestG.length());
        assertEquals(18, longestB.length());
    }

    @Test
    void trailLongestWorksOnKnownExample4() {
        var routes = new ChRoutes();

        var routesR = List.of(
                routes.FRI_LAU_1,
                routes.BER_FRI_1,
                routes.BER_LUC_1,
                routes.BER_INT_1,
                routes.INT_LUC_1,
                routes.LUC_ZOU_1,
                routes.ZOU_ZUR_1,
                routes.STG_ZUR_1,
                routes.DE5_STG_1,
                routes.AT1_STG_1,
                routes.LUC_SCZ_1,
                routes.SCZ_WAS_1,
                routes.BEL_WAS_1,
                routes.BEL_LOC_1,
                routes.IT4_LOC_1,
                routes.IT3_LUG_1);

        var routesG = List.of(
                routes.SCE_WIN_1,
                routes.WIN_ZUR_1,
                routes.OLT_ZUR_1,
                routes.LUC_OLT_1,
                routes.STG_WIN_1,
                routes.STG_VAD_1,
                routes.SAR_VAD_1,
                routes.COI_SAR_1,
                routes.BRU_COI_1,
                routes.COI_WAS_1,
                routes.BRI_WAS_1,
                routes.BRI_INT_1,
                routes.BRI_LOC_1,
                routes.LOC_LUG_1,
                routes.BEL_LUG_1);

        var longestR = Trail.longest(routesR);
        var longestG = Trail.longest(routesG);

        assertEquals(31, longestR.length());
        assertEquals(31, longestG.length());
    }

    @Test
    void trailLongestWorksOnKnownExample5() {
        var routes = new ChRoutes();

        var routesR = List.of(
                routes.DE2_SCE_1,
                routes.SCE_WIN_1,
                routes.SCE_ZUR_1,
                routes.ZOU_ZUR_1,
                routes.LUC_ZOU_1,
                routes.LUC_OLT_1,
                routes.BAL_OLT_1,
                routes.OLT_SOL_1,
                routes.BER_SOL_1,
                routes.SCZ_ZOU_1,
                routes.SCZ_WAS_1,
                routes.BRI_WAS_1,
                routes.COI_WAS_1,
                routes.COI_SAR_1,
                routes.SAR_VAD_1,
                routes.BRU_COI_1,
                routes.BRU_IT2_1);

        var routesG = List.of(
                routes.BAL_DE1_1,
                routes.BAL_DEL_1,
                routes.DEL_LCF_1,
                routes.FR3_LCF_1,
                routes.LCF_YVE_1,
                routes.GEN_YVE_1,
                routes.LCF_NEU_1,
                routes.BER_NEU_1,
                routes.BER_FRI_1,
                routes.BER_LUC_1,
                routes.LUC_SCZ_1,
                routes.PFA_SCZ_1,
                routes.PFA_ZUR_1,
                routes.WIN_ZUR_1,
                routes.STG_ZUR_1,
                routes.KRE_STG_1,
                routes.AT1_STG_1,
                routes.AT2_VAD_1);

        var longestR = Trail.longest(routesR);
        var longestG = Trail.longest(routesG);

        assertEquals(22, longestR.length());
        assertEquals(28, longestG.length());
    }

    private static final class ChRoutes {
        // Stations
        final Station BAD = new Station(0, "Baden");
        final Station BAL = new Station(1, "Bâle");
        final Station BEL = new Station(2, "Bellinzone");
        final Station BER = new Station(3, "Berne");
        final Station BRI = new Station(4, "Brigue");
        final Station BRU = new Station(5, "Brusio");
        final Station COI = new Station(6, "Coire");
        final Station DAV = new Station(7, "Davos");
        final Station DEL = new Station(8, "Delémont");
        final Station FRI = new Station(9, "Fribourg");
        final Station GEN = new Station(10, "Genève");
        final Station INT = new Station(11, "Interlaken");
        final Station KRE = new Station(12, "Kreuzlingen");
        final Station LAU = new Station(13, "Lausanne");
        final Station LCF = new Station(14, "La Chaux-de-Fonds");
        final Station LOC = new Station(15, "Locarno");
        final Station LUC = new Station(16, "Lucerne");
        final Station LUG = new Station(17, "Lugano");
        final Station MAR = new Station(18, "Martigny");
        final Station NEU = new Station(19, "Neuchâtel");
        final Station OLT = new Station(20, "Olten");
        final Station PFA = new Station(21, "Pfäffikon");
        final Station SAR = new Station(22, "Sargans");
        final Station SCE = new Station(23, "Schaffhouse");
        final Station SCZ = new Station(24, "Schwyz");
        final Station SIO = new Station(25, "Sion");
        final Station SOL = new Station(26, "Soleure");
        final Station STG = new Station(27, "Saint-Gall");
        final Station VAD = new Station(28, "Vaduz");
        final Station WAS = new Station(29, "Wassen");
        final Station WIN = new Station(30, "Winterthour");
        final Station YVE = new Station(31, "Yverdon");
        final Station ZOU = new Station(32, "Zoug");
        final Station ZUR = new Station(33, "Zürich");
        final Station DE1 = new Station(34, "Allemagne");
        final Station DE2 = new Station(35, "Allemagne");
        final Station DE3 = new Station(36, "Allemagne");
        final Station DE4 = new Station(37, "Allemagne");
        final Station DE5 = new Station(38, "Allemagne");
        final Station AT1 = new Station(39, "Autriche");
        final Station AT2 = new Station(40, "Autriche");
        final Station AT3 = new Station(41, "Autriche");
        final Station IT1 = new Station(42, "Italie");
        final Station IT2 = new Station(43, "Italie");
        final Station IT3 = new Station(44, "Italie");
        final Station IT4 = new Station(45, "Italie");
        final Station IT5 = new Station(46, "Italie");
        final Station FR1 = new Station(47, "France");
        final Station FR2 = new Station(48, "France");
        final Station FR3 = new Station(49, "France");
        final Station FR4 = new Station(50, "France");

        // Routes
        final Route AT1_STG_1 = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null);
        final Route AT2_VAD_1 = new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED);
        final Route BAD_BAL_1 = new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED);
        final Route BAD_OLT_1 = new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET);
        final Route BAD_ZUR_1 = new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route BAL_DE1_1 = new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE);
        final Route BAL_DEL_1 = new Route("BAL_DEL_1", BAL, DEL, 2, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route BAL_OLT_1 = new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route BEL_LOC_1 = new Route("BEL_LOC_1", BEL, LOC, 1, Route.Level.UNDERGROUND, Color.BLACK);
        final Route BEL_LUG_1 = new Route("BEL_LUG_1", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.RED);
        final Route BEL_LUG_2 = new Route("BEL_LUG_2", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route BEL_WAS_1 = new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
        final Route BEL_WAS_2 = new Route("BEL_WAS_2", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
        final Route BER_FRI_1 = new Route("BER_FRI_1", BER, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE);
        final Route BER_FRI_2 = new Route("BER_FRI_2", BER, FRI, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route BER_INT_1 = new Route("BER_INT_1", BER, INT, 3, Route.Level.OVERGROUND, Color.BLUE);
        final Route BER_LUC_1 = new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null);
        final Route BER_LUC_2 = new Route("BER_LUC_2", BER, LUC, 4, Route.Level.OVERGROUND, null);
        final Route BER_NEU_1 = new Route("BER_NEU_1", BER, NEU, 2, Route.Level.OVERGROUND, Color.RED);
        final Route BER_SOL_1 = new Route("BER_SOL_1", BER, SOL, 2, Route.Level.OVERGROUND, Color.BLACK);
        final Route BRI_INT_1 = new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE);
        final Route BRI_IT5_1 = new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN);
        final Route BRI_LOC_1 = new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null);
        final Route BRI_SIO_1 = new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK);
        final Route BRI_WAS_1 = new Route("BRI_WAS_1", BRI, WAS, 4, Route.Level.UNDERGROUND, Color.RED);
        final Route BRU_COI_1 = new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null);
        final Route BRU_DAV_1 = new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE);
        final Route BRU_IT2_1 = new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route COI_DAV_1 = new Route("COI_DAV_1", COI, DAV, 2, Route.Level.UNDERGROUND, Color.VIOLET);
        final Route COI_SAR_1 = new Route("COI_SAR_1", COI, SAR, 1, Route.Level.UNDERGROUND, Color.WHITE);
        final Route COI_WAS_1 = new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null);
        final Route DAV_AT3_1 = new Route("DAV_AT3_1", DAV, AT3, 3, Route.Level.UNDERGROUND, null);
        final Route DAV_IT1_1 = new Route("DAV_IT1_1", DAV, IT1, 3, Route.Level.UNDERGROUND, null);
        final Route DAV_SAR_1 = new Route("DAV_SAR_1", DAV, SAR, 3, Route.Level.UNDERGROUND, Color.BLACK);
        final Route DE2_SCE_1 = new Route("DE2_SCE_1", DE2, SCE, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route DE3_KRE_1 = new Route("DE3_KRE_1", DE3, KRE, 1, Route.Level.OVERGROUND, Color.ORANGE);
        final Route DE4_KRE_1 = new Route("DE4_KRE_1", DE4, KRE, 1, Route.Level.OVERGROUND, Color.WHITE);
        final Route DE5_STG_1 = new Route("DE5_STG_1", DE5, STG, 2, Route.Level.OVERGROUND, null);
        final Route DEL_FR4_1 = new Route("DEL_FR4_1", DEL, FR4, 2, Route.Level.UNDERGROUND, Color.BLACK);
        final Route DEL_LCF_1 = new Route("DEL_LCF_1", DEL, LCF, 3, Route.Level.UNDERGROUND, Color.WHITE);
        final Route DEL_SOL_1 = new Route("DEL_SOL_1", DEL, SOL, 1, Route.Level.UNDERGROUND, Color.VIOLET);
        final Route FR1_MAR_1 = new Route("FR1_MAR_1", FR1, MAR, 2, Route.Level.UNDERGROUND, null);
        final Route FR2_GEN_1 = new Route("FR2_GEN_1", FR2, GEN, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route FR3_LCF_1 = new Route("FR3_LCF_1", FR3, LCF, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route FRI_LAU_1 = new Route("FRI_LAU_1", FRI, LAU, 3, Route.Level.OVERGROUND, Color.RED);
        final Route FRI_LAU_2 = new Route("FRI_LAU_2", FRI, LAU, 3, Route.Level.OVERGROUND, Color.VIOLET);
        final Route GEN_LAU_1 = new Route("GEN_LAU_1", GEN, LAU, 4, Route.Level.OVERGROUND, Color.BLUE);
        final Route GEN_LAU_2 = new Route("GEN_LAU_2", GEN, LAU, 4, Route.Level.OVERGROUND, Color.WHITE);
        final Route GEN_YVE_1 = new Route("GEN_YVE_1", GEN, YVE, 6, Route.Level.OVERGROUND, null);
        final Route INT_LUC_1 = new Route("INT_LUC_1", INT, LUC, 4, Route.Level.OVERGROUND, Color.VIOLET);
        final Route IT3_LUG_1 = new Route("IT3_LUG_1", IT3, LUG, 2, Route.Level.UNDERGROUND, Color.WHITE);
        final Route IT4_LOC_1 = new Route("IT4_LOC_1", IT4, LOC, 2, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route KRE_SCE_1 = new Route("KRE_SCE_1", KRE, SCE, 3, Route.Level.OVERGROUND, Color.VIOLET);
        final Route KRE_STG_1 = new Route("KRE_STG_1", KRE, STG, 1, Route.Level.OVERGROUND, Color.GREEN);
        final Route KRE_WIN_1 = new Route("KRE_WIN_1", KRE, WIN, 2, Route.Level.OVERGROUND, Color.YELLOW);
        final Route LAU_MAR_1 = new Route("LAU_MAR_1", LAU, MAR, 4, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route LAU_NEU_1 = new Route("LAU_NEU_1", LAU, NEU, 4, Route.Level.OVERGROUND, null);
        final Route LCF_NEU_1 = new Route("LCF_NEU_1", LCF, NEU, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route LCF_YVE_1 = new Route("LCF_YVE_1", LCF, YVE, 3, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route LOC_LUG_1 = new Route("LOC_LUG_1", LOC, LUG, 1, Route.Level.UNDERGROUND, Color.VIOLET);
        final Route LUC_OLT_1 = new Route("LUC_OLT_1", LUC, OLT, 3, Route.Level.OVERGROUND, Color.GREEN);
        final Route LUC_SCZ_1 = new Route("LUC_SCZ_1", LUC, SCZ, 1, Route.Level.OVERGROUND, Color.BLUE);
        final Route LUC_ZOU_1 = new Route("LUC_ZOU_1", LUC, ZOU, 1, Route.Level.OVERGROUND, Color.ORANGE);
        final Route LUC_ZOU_2 = new Route("LUC_ZOU_2", LUC, ZOU, 1, Route.Level.OVERGROUND, Color.YELLOW);
        final Route MAR_SIO_1 = new Route("MAR_SIO_1", MAR, SIO, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route NEU_SOL_1 = new Route("NEU_SOL_1", NEU, SOL, 4, Route.Level.OVERGROUND, Color.GREEN);
        final Route NEU_YVE_1 = new Route("NEU_YVE_1", NEU, YVE, 2, Route.Level.OVERGROUND, Color.BLACK);
        final Route OLT_SOL_1 = new Route("OLT_SOL_1", OLT, SOL, 1, Route.Level.OVERGROUND, Color.BLUE);
        final Route OLT_ZUR_1 = new Route("OLT_ZUR_1", OLT, ZUR, 3, Route.Level.OVERGROUND, Color.WHITE);
        final Route PFA_SAR_1 = new Route("PFA_SAR_1", PFA, SAR, 3, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route PFA_SCZ_1 = new Route("PFA_SCZ_1", PFA, SCZ, 1, Route.Level.OVERGROUND, Color.VIOLET);
        final Route PFA_STG_1 = new Route("PFA_STG_1", PFA, STG, 3, Route.Level.OVERGROUND, Color.ORANGE);
        final Route PFA_ZUR_1 = new Route("PFA_ZUR_1", PFA, ZUR, 2, Route.Level.OVERGROUND, Color.BLUE);
        final Route SAR_VAD_1 = new Route("SAR_VAD_1", SAR, VAD, 1, Route.Level.UNDERGROUND, Color.ORANGE);
        final Route SCE_WIN_1 = new Route("SCE_WIN_1", SCE, WIN, 1, Route.Level.OVERGROUND, Color.BLACK);
        final Route SCE_WIN_2 = new Route("SCE_WIN_2", SCE, WIN, 1, Route.Level.OVERGROUND, Color.WHITE);
        final Route SCE_ZUR_1 = new Route("SCE_ZUR_1", SCE, ZUR, 3, Route.Level.OVERGROUND, Color.ORANGE);
        final Route SCZ_WAS_1 = new Route("SCZ_WAS_1", SCZ, WAS, 2, Route.Level.UNDERGROUND, Color.GREEN);
        final Route SCZ_WAS_2 = new Route("SCZ_WAS_2", SCZ, WAS, 2, Route.Level.UNDERGROUND, Color.YELLOW);
        final Route SCZ_ZOU_1 = new Route("SCZ_ZOU_1", SCZ, ZOU, 1, Route.Level.OVERGROUND, Color.BLACK);
        final Route SCZ_ZOU_2 = new Route("SCZ_ZOU_2", SCZ, ZOU, 1, Route.Level.OVERGROUND, Color.WHITE);
        final Route STG_VAD_1 = new Route("STG_VAD_1", STG, VAD, 2, Route.Level.UNDERGROUND, Color.BLUE);
        final Route STG_WIN_1 = new Route("STG_WIN_1", STG, WIN, 3, Route.Level.OVERGROUND, Color.RED);
        final Route STG_ZUR_1 = new Route("STG_ZUR_1", STG, ZUR, 4, Route.Level.OVERGROUND, Color.BLACK);
        final Route WIN_ZUR_1 = new Route("WIN_ZUR_1", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.BLUE);
        final Route WIN_ZUR_2 = new Route("WIN_ZUR_2", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.VIOLET);
        final Route ZOU_ZUR_1 = new Route("ZOU_ZUR_1", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.GREEN);
        final Route ZOU_ZUR_2 = new Route("ZOU_ZUR_2", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.RED);
    }
}
