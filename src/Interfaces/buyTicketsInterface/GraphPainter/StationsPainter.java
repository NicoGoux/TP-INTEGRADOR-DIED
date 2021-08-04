package Interfaces.buyTicketsInterface.GraphPainter;

import java.lang.Math;

import java.awt.geom.*;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationsPainter {
    public static Map<Integer, Point> stationsGraphic(Graphics2D graphGraphic, List<Integer> stationsList) {

        //Creamos un map que almacene las posiciones de las estaciones
        Map<Integer, Point> stationsPosition = new HashMap<Integer, Point>();

        //VSI = (50,50); VID = (100,100) --> x+= 100 y+= 125 <-- genera un gap de 50 (50-100+100) y uno de 75 (50-100+125)
        int a=50,b=50,c=50,d=50;
        boolean flag = true;
        for (Integer station : stationsList) {
            
            // Creamos un rectangulo con posicion (a,b) y ancho c y alto d
            Rectangle2D rectangle = new Rectangle2D.Double(a,b,c,d);

            //Añadimos antialiasing
            graphGraphic.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            
            // Buscamos el centro de los rectangulos, que nos devuelve un doble
            Double xPos = rectangle.getCenterX();
            Double yPos = rectangle.getCenterY();
            
            // Como necesitamos posiciones enteras para el "drawString" hacemos la conversion de double a long y de long a int
            int xPosI = (int)Math.round(xPos);
            int yPosI = (int)Math.round(yPos);
            graphGraphic.drawString(station.toString(), xPosI-2, yPosI);
            stationsPosition.put(station, new Point(xPosI,yPosI));
            

            Ellipse2D elipse = new Ellipse2D.Double();
            // Hacemos que la elipse se encuentre contenida en el rectangulo
            elipse.setFrame(rectangle);
            graphGraphic.draw(elipse);
            // Vamos alternando el dibujo, pintando estaciones una arriba y otra abajo mientras nos desplazamos en x
            // Cuando llegamos a cierto valor de x nos movemos hacia abajo  
            if (flag){
                if(a == 550){
                    a=50;
                    b+=125;
                }
                else{
                    b+=125;
                    a+=100;
                }
                flag = false;
            }
            else{
                if(a == 550){
                    a=50;
                    b+=125;
                }
                else{
                    b-=125;
                    a+=100;
                }
                flag = true;
            }
        }
        return stationsPosition;
    }
}
