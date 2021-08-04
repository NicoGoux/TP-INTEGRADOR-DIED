package Interfaces.buyTicketsInterface;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import Graph.Route;
import Interfaces.WindowManager;
import Interfaces.buyTicketsInterface.GraphPainter.GraphPainter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class PrintedGraphPanel extends JPanel {

    private static PrintedGraphPanel PrintedGraphPanelInstance;

    public List<List<Route>> pathList;

    public static PrintedGraphPanel getInstance() {
        if(PrintedGraphPanelInstance==null) {
            PrintedGraphPanelInstance = new PrintedGraphPanel();
        }
        return PrintedGraphPanelInstance;
    }

    public PrintedGraphPanel() {
        super();
        this.pathList = new ArrayList<List<Route>>();
        this.setLayout(new BorderLayout());
        this.setBackground(Color.DARK_GRAY);

        //Creamos un comboBox donde se almacenaran las rutas
        JComboBox<List<Route>> routes = new JComboBox<List<Route>>();
        routes.setPreferredSize(new Dimension(600,20));
        this.add(routes,BorderLayout.NORTH);
        routes.setEnabled(false);

        //ActionListener para comboBox
        ActionListener selectRoute = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                refreshGraph();
            }
        };
        routes.addActionListener(selectRoute);
    }

    public void refreshRoutesComboBox(List<List<Route>> pathList) {

        //Seteamos el pathList
        this.pathList=pathList;

        //Limpiamos el combo box
        this.clearRoutesComboBox();

        //Buscamos la instancia de comboBox y de GraphPainter. Removemos la instancia anterior de graphPainter ya que no lo hicimos tipo Singleton
        JComboBox<List<Route>> routesComboBox = null;
        for (java.awt.Component c : this.getComponents()) {
            if (c instanceof JComboBox) {
                routesComboBox = (JComboBox<List<Route>>)c;
            }
            if (c instanceof GraphPainter) {
                this.remove(c);
            }
        }

        //Añadimos los elementos del PathList al comboBox
        for (List<Route> aPath : pathList) {
            routesComboBox.addItem(aPath);           
        }

        //Habilitamos la seleccion del comboBox
        routesComboBox.setEnabled(true);
        
        //Creamos el lienzo del grafo
        GraphPainter graphPainter = new GraphPainter(pathList);
        this.add(graphPainter,BorderLayout.CENTER);
    }
    
    public void setRoutesComboBox(List<Route> routeList) {
        JComboBox<List<Route>> routesComboBox = null;
        for (java.awt.Component c : this.getComponents()) {
            if (c instanceof JComboBox) 
                routesComboBox =  (JComboBox<List<Route>>)c;   
        }
        routesComboBox.setSelectedItem(routeList);
    }

    public void clearRoutesComboBox() {

        //Buscamos la instancia de comboBox y de GraphPainter. Removemos la instancia anterior de graphPainter
        for (java.awt.Component c : this.getComponents()) {
            if (c instanceof JComboBox) {
                ((JComboBox)c).removeAllItems();
                ((JComboBox)c).setEnabled(false);
            }
            if (c instanceof GraphPainter) {
                this.remove(c);
            }
        }
        this.repaint();
    }

    public List<Route> getRouteSelected() {
        JComboBox<List<Route>> routesComboBox = null;
        for (java.awt.Component c : this.getComponents()) {
            if (c instanceof JComboBox) {
                routesComboBox =  (JComboBox<List<Route>>)c;
            } 
        }
        return (List<Route>)routesComboBox.getSelectedItem();
    }

    public void refreshGraph() {
        //Removemos el objeto graphPainter
        for (java.awt.Component c : this.getComponents()) {
            if (c instanceof GraphPainter) {
                this.remove(c);
            }
        }
        //Creamos el lienzo del grafo
        GraphPainter graphPainter = new GraphPainter(pathList);
        add(graphPainter,BorderLayout.CENTER);

        WindowManager.getMainWindow().pack();
        WindowManager.getMainWindow().setSize(1200,600);
    }
}