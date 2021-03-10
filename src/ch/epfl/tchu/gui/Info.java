package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * In game informations
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public class Info {
    //Attribute for Info : player whom informations are shared
    private final String playerName;

    /**
     *public constructor for informations
     * @param playerName the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     *Method that returns the french name of a card
     * @param card the cards type
     * @param count the cards multiplicity
     * @return the french name of a card according to its type and its multiplicity, if there is more than one card.
     */
    public static String cardName(Card card, int count){
        Preconditions.checkArgument( count!=0);
        if(card.equals(Card.BLACK)){
            return StringsFr.BLACK_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.VIOLET)){
            return StringsFr.VIOLET_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.BLUE)){
            return StringsFr.BLUE_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.GREEN)){
            return StringsFr.GREEN_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.YELLOW)){
            return StringsFr.YELLOW_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.ORANGE)){
            return StringsFr.ORANGE_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.RED)){
            return StringsFr.RED_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.WHITE)){
            return StringsFr.WHITE_CARD+StringsFr.plural(count);
        }
        else if(card.equals(Card.LOCOMOTIVE)){
            return StringsFr.LOCOMOTIVE_CARD+StringsFr.plural(count);
        }

        return null;
    }

    /**
     * @param playerNames the names of all players
     * @param points the number of points all players obtained
     * @return a message announcing that the game ended in a draw, if there is more than one player
     */
    public static String draw(List<String> playerNames, int points){
        Preconditions.checkArgument( playerNames.size()>1);
        String players="";
        int cmp = 1;
        for(String player : playerNames){
            if (cmp== playerNames.size()-1){
                players += player+StringsFr.AND_SEPARATOR;
            }
            else if(cmp== playerNames.size()){
                players += player;
            }
            else{
                players += player+", ";
            }
            cmp+=1;
        }
        return String.format(StringsFr.DRAW,players,points);
    }

    /**
     * @return a message announcing which player plays first
     */
    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST,playerName);
    }

    /**
     * @param count the number of tickets
     * @return a message announcing how many tickets were kept by the player
     */
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS,playerName,count,StringsFr.plural(count));
    }

    /**
     * @return a message announcing the player can play
     */
    public String canPlay(){return String.format(StringsFr.CAN_PLAY,playerName);}

    /**
     * @param count the number of tickets
     * @return a message announcing how many tickets were drawn by the player
     */
    public String drewTickets(int count){return String.format(StringsFr.DREW_TICKETS,playerName,count,
            StringsFr.plural(count));}

    /**
     * @return a message announcing that the player drew a card blindfolded from the stock
     */
    public String drewBlindCard(){return String.format(StringsFr.DREW_BLIND_CARD,playerName);}

    /**
     * @param card the cards type
     * @return a message announcing that the player drew one of the five visible cards, and which card it is
     */
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD,playerName,cardName(card,1));
    }

    /**
     * @param route the claimed road
     * @param cards the cards used to claim the road
     * @return a message announcing that the player acquired a new road, and with which cards he did
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(StringsFr.CLAIMED_ROUTE,playerName,roadName(route),setContent(cards));
    }

    /**
     * @param route the claimed road
     * @param initialCards the cards used to claim the tunnel
     * @return a message announcing that the player acquired a new tunnel, and with which cards he did
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM,playerName,roadName(route),setContent(initialCards));
    }

    /**
     * @param drawnCards the drawn cards
     * @param additionalCost the number of cards that need to be played additionally
     * @return a message announcing which cards were drawn by the player and how many additional cards he has to play,
     * after trying to acquire a tunnel
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String s=String.format(StringsFr.ADDITIONAL_CARDS_ARE,setContent(drawnCards));
        if(additionalCost==0){
            s+=StringsFr.NO_ADDITIONAL_COST;
            return s;
        }
        else {
           s+=String.format(StringsFr.SOME_ADDITIONAL_COST,additionalCost,StringsFr.plural(additionalCost));
           return s;
        }
    }

    /**
     * @param route a road
     * @return a message announcing that the player couldn't or wouldn't acquire a new tunnel
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE,playerName,roadName(route));
    }

    /**
     * @param carCount the number of cars remaining to the player
     * @return a message announcing that the last round begins if the player has less than three cars remaining
     */
    public String lastTurnBegins(int carCount){
        Preconditions.checkArgument(carCount <=2);
        return String.format(StringsFr.LAST_TURN_BEGINS,playerName,carCount,StringsFr.plural(carCount));
    }

    /**
     * @param longestTrail the longest trail, or one of them
     * @return a message announcing that the player acquires a bonus at the end of the game, for having built the
     * longest trail
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        Preconditions.checkArgument(longestTrail.station1()!=null && longestTrail.station2()!=null);
        String s = longestTrail.station1().toString() + StringsFr.EN_DASH_SEPARATOR+ longestTrail.station2().toString();
        return String.format(StringsFr.GETS_BONUS,playerName,s);
    }

    /**
     * @param points the points of the winner (here the player)
     * @param loserPoints the points of the loser
     * @return a message announcing by how may points the player won, and by how many points the other player lost
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS,playerName,points,StringsFr.plural(points),loserPoints,
                StringsFr.plural(loserPoints));
    }

    /**
     * @param route a road
     * @return the conventional textual representation of a road according to its departure and arrival stations
     */
    private static String roadName(Route route){
        String departure = route.station1().toString();
        String arrival = route.station2().toString();
        return departure+StringsFr.EN_DASH_SEPARATOR+arrival;
    }
    /**
     * @param cards a set of cards
     * @return the conventional textual representation of a set of cards according to their multiplicity
     */
    private static String setContent(SortedBag<Card> cards){
        String setContent="";
        int cmp = 1;
        for (Card card: cards.toSet()) {
            int n = cards.countOf(card);
            if (cmp== cards.toSet().size()-1){
                setContent += n + " "+ cardName(card,n) +StringsFr.AND_SEPARATOR;
            }
            else if(cmp== cards.toSet().size()){
                setContent += n + " "+ cardName(card,n);
            }
            else{
                setContent += n + " "+ cardName(card,n) +", ";
            }
            cmp+=1;
        }
        return setContent;
    }

    public static void main(String[] args){
        SortedBag.Builder<Card> s= new SortedBag.Builder<>();
        s.add(Card.WHITE).add(Card.GREEN).add(Card.BLACK).add(Card.WHITE);
        SortedBag<Card> cards =s.build();

        List<String> players1= new ArrayList<>();
        players1.add("Michelle");
        players1.add("Marin");
        players1.add("Gabonga");

        List<String> players2= new ArrayList<>();
        players2.add("Michelle");
        Info info = new Info("Michelle");
        Route route = ChMap.routes().get(2);
        //Trail trail = Trail.longest(ChMap.ALL_ROUTES);

        //System.out.println(cardName(Card.LOCOMOTIVE,4));
        //System.out.println(draw(players2,0));
        //System.out.println(info.drewBlindCard());
        //System.out.println(roadName(route));
        //System.out.println(setContent(cards));
        //System.out.println(info.drewAdditionalCards(cards,2));
        //System.out.println(info.drewAdditionalCards(cards,0));
        //System.out.println(info.attemptsTunnelClaim(route,cards));
        //System.out.println(info.claimedRoute(route,cards));
        //System.out.println(info.didNotClaimRoute(route));
        //System.out.println(info.drewVisibleCard(Card.BLUE));
        //System.out.println(info.drewTickets(4));
        //System.out.println(info.keptTickets(4));
        //System.out.println(info.won(4,7));
        //System.out.println(info.getsLongestTrailBonus(trail));
    }
}
