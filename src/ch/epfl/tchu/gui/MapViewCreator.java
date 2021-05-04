package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

    public void createMapView(ObservableGameState observableGameState,
                              ObjectProperty<ClaimRouteHandler> claimRouteHandlerProperty,
                              CardChooser cardChooser){
        Pane mapPane = new Pane();
        mapPane.getStylesheets().addAll("map.css", "colors.css");
        mapPane.getChildren().add(new ImageView());
        Map<Route, Group> routeNodeMap = new HashMap<>();

        for (Route route : ChMap.routes()){ // est on sur de devoir utiliser CHMAP???
            Group carGroup = nodeForCar();
            Group caseGroup = nodeForCase(carGroup);
            Group routeGroup = nodeForRoute(route,caseGroup);
            routeGroup.setId(route.toString());
            routeGroup.getStyleClass().addAll("route",route.level().toString(),"NEUTRAL");
            routeNodeMap.put(route,routeGroup);
            ReadOnlyObjectProperty<PlayerId> routePlayerId = observableGameState.routePlayerId(route);
            if (routePlayerId != null){
                routeGroup.getStyleClass().addAll("route",route.level().toString(), routePlayerId.getName()); // pas sur du get name
            }
            else{
                routeGroup.disableProperty().bind(routePlayerId.isNull().or(observableGameState.booleanForEachRoute(route).not()));
            } // asbolument pas certain de la méthode booleanForEachRoute, il faut que tu m'expliques les différents properties

        }

        mapPane.getChildren().addAll(routeNodeMap.values());

    }

    private Group nodeForRoute(Route route, Group c){
        List<Node> children = new ArrayList<>();
        for (int i=0; i<route.length(); ++i){
            c.setId(route.toString()+"_"+(i+1));
            children.add(c);
        }
        return new Group(children);
    }

    private Group nodeForCase(Group car){
        Rectangle r = new Rectangle(RECTANGLE_LENGTH,RECTANGLE_HEIGHT);
        r.getStyleClass().addAll("track", "filled");
        return new Group(r, car);
    }

    private Group nodeForCar(){
        Rectangle rectangle = new Rectangle(RECTANGLE_LENGTH,RECTANGLE_HEIGHT);
        Circle c1 = new Circle(CIRCLE_CENTER, CIRCLE_SPACING,CIRCLE_RADIUS);
        Circle c2 = new Circle(CIRCLE_CENTER*2, CIRCLE_SPACING,CIRCLE_RADIUS);
        Group carGroup =new Group(rectangle, c1, c2);
        carGroup.getStyleClass().addAll("car", "filled");
        return carGroup;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}