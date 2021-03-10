package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Trail
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

public final class Trail {

    //Attributes for a trail: a starting station, an ending station, and  a list of routes
    private final Station station1;
    private final Station station2;
    private final List<Route> routes;

    /**
     * Private constructor for a route
     * @param station1 starting station
     * @param station2 ending station
     * @param routes list of routes
     */
    private Trail(Station station1, Station  station2, List<Route> routes){
        this.station1=station1;
        this.station2=station2;
        this.routes=routes;
    }

    /**
     * Method that returns a player's longest trail
     * @param routes takes as an argument the player's routes
     * @return the longest trail the player has. If a player has several longest trails of the same size,
     * returns any of them
     */
    public static Trail longest(List<Route> routes){
        if (routes.size() == 0){
            return new Trail(null, null, new ArrayList<Route>());
        }
        Trail longest_trail = new Trail (routes.get(0).station1(), routes.get(0).station2(), new ArrayList<Route>(List.of(routes.get(0))));
        List<Trail> all_trails = new ArrayList<Trail>();
        for (Route route: routes){
            all_trails.add(new Trail(route.station1(), route.station2(), new ArrayList<Route>(List.of(route))));
        }
        for (Trail trail : all_trails){
            if (trail.length()>longest_trail.length()){
                longest_trail= new Trail(trail.station1(), trail.station2(), trail.routes);
            }
        }
        while (all_trails.size()!=0){
            List<Trail> all_trails_bis = new ArrayList<>();
            for(Trail trail : all_trails){
                for (Route route: routes){
                    if (!trail.routes.contains(route)){
                        List<Route> arbitrary_list = new ArrayList<Route>(trail.routes);
                        arbitrary_list.add(route);
                        if(trail.station2().equals(route.station1())){
                            Trail arbitrary_trail = new Trail(trail.station1, route.station2(), arbitrary_list);
                            all_trails_bis.add(arbitrary_trail);
                        }
                        if(trail.station2().equals(route.station2())){
                            Trail arbitrary_trail = new Trail(trail.station1, route.station1(), arbitrary_list);
                            all_trails_bis.add(arbitrary_trail);
                        }
                        if(trail.station1().equals(route.station1())){
                            Trail arbitrary_trail = new Trail(route.station2(), trail.station2(), arbitrary_list);
                            all_trails_bis.add(arbitrary_trail);
                        }
                        if(trail.station1().equals(route.station2())){
                            Trail arbitrary_trail = new Trail(route.station1(), trail.station2(), arbitrary_list);
                            all_trails_bis.add(arbitrary_trail);
                        }
                    }
                }
            }
            all_trails=all_trails_bis;
            if (all_trails.size() != 0){
                for (Trail trail : all_trails){
                    if (trail.length()>longest_trail.length()){
                        longest_trail= new Trail(trail.station1(), trail.station2(), trail.routes);
                    }
                }
            }
        }
        return longest_trail;
    }

    /**
     * @return a trails length, defined by the sum of the lengths of each of its routes
     */
    public int length(){
        int length=0;
        for (Route route: routes){
            length += route.length();
        }
        return length;
    }

    /**
     * @return null if length of trail is 0, else returns starting station
     */
    public Station station1(){
        if (length()==0){return null;}
        else {return station1;}
    }

    /**
     * @return null if length of trail is 0, else returns ending station
     */
    public Station station2(){
        if (length()==0){return null;}
        else {return station2;}
    }

    /**
     * Method that returns textual representation of a trail
     * @return textual representation of a trail, composed of its different station and at the end its length in parenthisis
     * @throws IllegalArgumentException if the routes aren't all connected to each  other
     */
    @Override
    public String toString(){
        if (this.station1 ==  null && this.station2 == null && this.routes.size()==0){
            return "empty trail!";
        }
        else {
            String trail_text = "";
            for (Route route : routes) {
                trail_text += route.station1().toString() + " - ";
                if (routes.indexOf(route) != routes.size() - 1) {
                    Preconditions.checkArgument(route.station2() == routes.get(routes.indexOf(route) + 1).station1());
                }
            }
            trail_text += station2.toString();
            trail_text += " " + "(" + length() + ")";
            return trail_text;
        }
    }
}