package Interfaces.buyTicketsInterface.GraphPainter;

import javax.swing.JPanel;

import Graph.Graph;
import Graph.Route;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Map;

public class GraphPainter extends JPanel {
    
    private List<List<Route>> pathList;
    
    public GraphPainter(List<List<Route>> pathList) {
        super();
        this.pathList=pathList;
    }

    public void paintComponent(Graphics g) {
        if (this.pathList==null) {
            return;
        }
        super.paintComponent(g);
        this.setBackground(Color.DARK_GRAY);
        this.setForeground(Color.lightGray);
        Graphics2D graphGraphic = (Graphics2D) g;

        //Obtenemos la lista de estaciones de una lista de trayectos
        List<Integer> stationsList = Graph.getInstance().getStationsFromPathList(this.pathList);

        //Pintamos las estaciones y obtenemos un HashMap con sus posiciones
        Map<Integer, Point> stationsPosition = StationsPainter.stationsGraphic(graphGraphic,stationsList);

        //Pintamos las rutas mediante el pathList
        RoutesPainter.routesGraphic(graphGraphic, this.pathList, stationsPosition);
    }
}
