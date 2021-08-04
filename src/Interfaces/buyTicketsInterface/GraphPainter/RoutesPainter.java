package Interfaces.buyTicketsInterface.GraphPainter;

import java.lang.Math;

import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Graph.Route;
import Interfaces.buyTicketsInterface.PrintedGraphPanel;

public class RoutesPainter {
    public static void routesGraphic(Graphics2D graphGraphic, List<List<Route>> pathList,Map<Integer,Point> stationPosition) {

        //Creamos una lista de puntos, donde cada punto representa la estacion origen en X y la estacion destino en Y
        //Esto nos permite que no se dibujen muchas lineas entre dos estaciones adyacentes
        List<Point> drawed = new ArrayList<Point>();

        for (List<Route> aPath : pathList) {
            
            //Pintamos la ruta seleccionada
            //Consultamos si la ruta es la ruta seleccionada en el comboBox
            if (aPath == PrintedGraphPanel.getInstance().getRouteSelected()) {
                //Limpiamos las rutas pintadas anteriormente para que nos permita pintar la ruta seleccionada en un color especifico
                drawed.clear();
                graphGraphic.setColor(Color.RED);
            }
            else {
                graphGraphic.setColor(Color.WHITE);
            }
            
            for(Route aRoute : aPath) {
                if (!drawed.contains(new Point(aRoute.getOrigin(),aRoute.getEnd()))) {
                    //Añadimos antialiasing
                    graphGraphic.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                    
                    //Obtenemos la posicion de la estacion de origen
                    double originDoubleX = stationPosition.get(aRoute.getOrigin()).getX();
                    double originDoubleY = stationPosition.get(aRoute.getOrigin()).getY();

                    double endDoubleX = stationPosition.get(aRoute.getEnd()).getX();
                    double endDoubleY = stationPosition.get(aRoute.getEnd()).getY();

                    // Como necesitamos posiciones enteras para el "drawLine" hacemos la conversion de double a long y de long a int
                    int originX = (int)Math.round(originDoubleX);
                    int originY = (int)Math.round(originDoubleY);
                    int endX = (int)Math.round(endDoubleX);
                    int endY = (int)Math.round(endDoubleY);
                    
                    //Planeteamos una calculo matematico referido a las posiciones de los centros de cada nodo para representar el trayecto de las lineas dibujadas
                    if (originY<endY) {
                        originY+=25;
                        endY-=25;       
                    }
                    else if (originY>endY){
                        originY-=25;
                        endY+=25;  
                    }

                    if (originX<endX) {
                        graphGraphic.drawLine(originX+25, originY, endX-25, endY);
                    }
                    else if (originX>endX){
                        graphGraphic.drawLine(originX-25, originY, endX+25, endY);
                    }
                    else {
                        graphGraphic.drawLine(originX, originY, endX, endY);
                    }

                    //Añadimos el punto de origen y final a la lista de lineas pintadas
                    drawed.add(new Point(aRoute.getOrigin(),aRoute.getEnd()));
                }  
            }
        }
    }
}
