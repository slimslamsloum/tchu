package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static ch.epfl.tchu.gui.ActionHandlers.*;

import java.util.*;

class MapViewCreator {
    private static final int RECTANGLE_LENGTH = 36;
    private static final int RECTANGLE_HEIGHT = 12;
    private static final int CIRCLE_RADIUS = 3;
    private static final int CIRCLE_SPACING = 6;
    private static final int CIRCLE_CENTER = 12;

    private MapViewCreator() {
    }

    public static Pane createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty,
                                     CardChooser cardChooser) {
        Pane mapPane = new Pane();
        mapPane.getStylesheets().addAll("map.css", "colors.css");
        mapPane.getChildren().add(new ImageView());

        for (Route route : ChMap.routes()) {

            Group routeGroup = new Group();
            routeGroup.setId(route.toString());
            routeGroup.getStyleClass().addAll("route", route.level().toString(), "NEUTRAL");

            for (int i = 0; i < route.length(); ++i) {

                Group caseGroup = new Group();
                caseGroup.setId(route.toString() + "_" + (i + 1));

                Group carGroup = new Group();
                carGroup.getStyleClass().add("car");

                Rectangle caseRectangle = new Rectangle(RECTANGLE_LENGTH, RECTANGLE_HEIGHT);
                caseRectangle.getStyleClass().addAll("track", "filled");
                caseGroup.getChildren().add(caseRectangle);

                Rectangle carRectangle = new Rectangle(RECTANGLE_LENGTH, RECTANGLE_HEIGHT);
                carRectangle.getStyleClass().add("filled");

                Circle c1 = new Circle(CIRCLE_CENTER, CIRCLE_SPACING, CIRCLE_RADIUS);
                Circle c2 = new Circle(CIRCLE_CENTER * 2, CIRCLE_SPACING, CIRCLE_RADIUS);

                carGroup.getChildren().addAll(carRectangle, c1, c2);
                caseGroup.getChildren().add(carGroup);
                routeGroup.getChildren().add(caseGroup);
            }
            mapPane.getChildren().add(routeGroup);

            routeGroup.disableProperty()
                    .bind(claimRouteHandlerProperty.isNull().
                            or(observableGameState.booleanForEachRoute(route).not()));

            observableGameState.routePlayerId(route).addListener((property, oldVal, newVal)
                            -> {
                        routeGroup.getStyleClass().add(newVal.name());
                    }
            );


            routeGroup.setOnMouseClicked(
                    e -> {
                        List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();
                        ClaimRouteHandler claimRouteH = claimRouteHandlerProperty.get();
                        if (possibleClaimCards.size() == 1) {
                            claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                        } else {
                            ChooseCardsHandler chooseCardsH =
                                    chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                            cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                        }
                    }
            );
        }
        return mapPane;
    }

    private static Group nodeForRoute(Route route, Group c) {
        List<Node> children = new ArrayList<>();
        for (int i = 0; i < route.length(); ++i) {
            c.setId(route.toString() + "_" + (i + 1));
            children.add(c);
        }
        return new Group(children);
    }

    private static Group nodeForCase(Group car) {
        Rectangle r = new Rectangle(RECTANGLE_LENGTH, RECTANGLE_HEIGHT);
        r.getStyleClass().addAll("track", "filled");
        return new Group(r, car);
    }

    private static Group nodeForCar() {
        Rectangle rectangle = new Rectangle(RECTANGLE_LENGTH, RECTANGLE_HEIGHT);
        rectangle.getStyleClass().add("filled");
        Circle c1 = new Circle(CIRCLE_CENTER, CIRCLE_SPACING, CIRCLE_RADIUS);
        Circle c2 = new Circle(CIRCLE_CENTER * 2, CIRCLE_SPACING, CIRCLE_RADIUS);
        Group carGroup = new Group(rectangle, c1, c2);
        carGroup.getStyleClass().add("car");
        return carGroup;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}