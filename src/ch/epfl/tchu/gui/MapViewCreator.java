package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static ch.epfl.tchu.gui.GuiConstants.*;

import java.util.*;

/**
 * Creation of the visual and playable interface of the map
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

class MapViewCreator {

    /**
     * private constructor
     */
    private MapViewCreator() {
    }

    /**
     * Method that creates a new map view
     * @param observableGameState the game observer
     * @param claimRouteHandlerProperty the player's property to claim a route
     * @param cardChooser the interface to choose cards
     * @return the view of the map (instance of Pane)
     */
    public static Pane createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty,
                                     CardChooser cardChooser) {
        //creation of the maps view
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll("map.css", "colors.css");
        mapView.getChildren().add(new ImageView());

        //iteration on all existing routes
        for (Route route : ChMap.routes()) {

            // creation of a group proper to each route
            Group routeGroup = groupForRoute(route);
            // iteration on the length of the route to produce each box that makes up the route
            for (int index = 0; index < route.length(); ++index) {

                //Creation of the visual representation of a routes box
                Group caseGroup = groupForCase(index, route);
                Group carGroup = groupForCar();
                caseGroup.getChildren().add(carGroup);
                routeGroup.getChildren().add(caseGroup);
            }
            //The route group is added as a children of the map
            mapView.getChildren().add(routeGroup);

            //The use of a routes property if the player doesn't want to take the route,
            //or if he cannot take the route
            routeGroup.disableProperty()
                    .bind(claimRouteHandlerProperty.isNull().
                            or(observableGameState.canClaimRoute(route).not()));

            //Allows the route to change visually if the route is taken by a new owner
            observableGameState.routePlayerId(route).addListener((property, oldVal, newVal)
                    -> routeGroup.getStyleClass().add(newVal.name())
            );

            //Allow the players to take the route with their mouse
            routeGroup.setOnMouseClicked(
                    e -> {
                        List<SortedBag<Card>> possibleClaimCards = observableGameState.playerState(route);
                        ClaimRouteHandler claimRouteH = claimRouteHandlerProperty.get();
                        //If the player has only one choice, the route is automatically taken with these cards
                        if (possibleClaimCards.size() == 1) {
                            claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                        } //Else, the player can choose amongst different possibilities to take the route
                        else {
                            ChooseCardsHandler chooseCardsH =
                                    chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                            cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                        }
                    }
            );
        }
        return mapView;
    }
    // functional interface to choose cards
    @FunctionalInterface
    interface CardChooser {
        /**
         * Method of the interface to choose cards
         * @param options the list of all combinations of card amongst which the player can choose
         * @param handler the action handler to choose cards
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }

    /**
     * Method that creates the visual group representing a whole tCHu route
     * @param route the route we want to represent
     * @return a new group representing the route
     */
    private static Group groupForRoute(Route route){
        Group routeGroup = new Group();
        routeGroup.setId(route.id());
        routeGroup.getStyleClass().add("route");

        //modification of the routes visual appearance according to its level and color
        if (route.level().equals(Route.Level.UNDERGROUND)){
            routeGroup.getStyleClass().add("UNDERGROUND");
        }
        String color = route.color() == null ? "NEUTRAL" : route.color().toString();
        routeGroup.getStyleClass().add(color);
        return routeGroup;
    }


    /**
     * Method that creates the box for one part of the route
     * @param index the box index,
     * @param route the route we want to represent
     * @return a new group representing the route
     */
    private static Group groupForCase(int index, Route route){
        Group caseGroup = new Group();
        caseGroup.setId(route.id() + "_" + (index + 1));

        //Creation of the shapes that make up a box
        Rectangle caseRectangle = new Rectangle(MVC_RECTANGLE_LENGTH, MVC_RECTANGLE_HEIGHT);
        caseRectangle.getStyleClass().addAll("track", "filled");
        caseGroup.getChildren().add(caseRectangle);
        return caseGroup;
    }


    /**
     * Method that creates the visual representation of a routes box, called cars
     * @return a new group representing the car
     */
    private static Group groupForCar() {
        Group carGroup = new Group();
        carGroup.getStyleClass().add("car");

        Rectangle carRectangle = new Rectangle(MVC_RECTANGLE_LENGTH, MVC_RECTANGLE_HEIGHT);
        carRectangle.getStyleClass().add("filled");

        Circle c1 = new Circle(MVC_CIRCLE_CENTER, MVC_CIRCLE_SPACING, MVC_CIRCLE_RADIUS);
        Circle c2 = new Circle(MVC_CIRCLE_CENTER * 2, MVC_CIRCLE_SPACING, MVC_CIRCLE_RADIUS);

        //the car group is now completed, then added as a children of the case group,
        // which is finally added to the routeGroup
        carGroup.getChildren().addAll(carRectangle, c1, c2);
        return carGroup;
    }
}