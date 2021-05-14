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

import java.util.*;

/**
 * Creation of the visual and playable interface of the map
 *
 * @author Alexandre Kambiz Gunter (324268)
 * @author Selim Jerad (327529)
 */

class MapViewCreator {
    //all needed dimensions for the cars on the map
    private static final int RECTANGLE_LENGTH = 36;
    private static final int RECTANGLE_HEIGHT = 12;
    private static final int CIRCLE_RADIUS = 3;
    private static final int CIRCLE_SPACING = 6;
    private static final int CIRCLE_CENTER = 12;

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
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            routeGroup.getStyleClass().add("route");

            //modification of the routes visual appearance according to its level and color
            if (route.level().equals(Route.Level.UNDERGROUND)){
                routeGroup.getStyleClass().add("UNDERGROUND");
            }

            if(route.color() == null){
                routeGroup.getStyleClass().add("NEUTRAL");
            }
            else {
                routeGroup.getStyleClass().addAll( route.color().toString());
            }

            // iteration on the length of the route to produce each box that makes up the route
            for (int i = 0; i < route.length(); ++i) {

                //Creation of the visual representation of a routes box
                Group caseGroup = new Group();
                caseGroup.setId(route.id() + "_" + (i + 1));

                //Creation of the visual representation of a taken routes box, called cars
                Group carGroup = new Group();
                carGroup.getStyleClass().add("car");

                //Creation of the shapes that make up a car or a box
                Rectangle caseRectangle = new Rectangle(RECTANGLE_LENGTH, RECTANGLE_HEIGHT);
                caseRectangle.getStyleClass().addAll("track", "filled");
                caseGroup.getChildren().add(caseRectangle);

                Rectangle carRectangle = new Rectangle(RECTANGLE_LENGTH, RECTANGLE_HEIGHT);
                carRectangle.getStyleClass().add("filled");

                Circle c1 = new Circle(CIRCLE_CENTER, CIRCLE_SPACING, CIRCLE_RADIUS);
                Circle c2 = new Circle(CIRCLE_CENTER * 2, CIRCLE_SPACING, CIRCLE_RADIUS);

                //the car group is now completed, then added as a children of the case group,
                // which is finally added to the routeGroup
                carGroup.getChildren().addAll(carRectangle, c1, c2);
                caseGroup.getChildren().add(carGroup);
                routeGroup.getChildren().add(caseGroup);
            }
            //The route group is added as a children of the map
            mapView.getChildren().add(routeGroup);

            //The use of a routes property if the player doesn't want to take the route,
            //or if he cannot take the route
            routeGroup.disableProperty()
                    .bind(claimRouteHandlerProperty.isNull().
                            or(observableGameState.booleanForEachRoute(route).not()));

            //Allows the route to change visually if the route is taken by a new owner
            observableGameState.routePlayerId(route).addListener((property, oldVal, newVal)
                    -> routeGroup.getStyleClass().add(newVal.name())
            );

            //Allow the players to take the route with their mouse
            routeGroup.setOnMouseClicked(
                    e -> {
                        List<SortedBag<Card>> possibleClaimCards = observableGameState.playerstate(route);
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
}