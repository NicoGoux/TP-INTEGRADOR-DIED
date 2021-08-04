package Interfaces;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import Interfaces.Accessories.MyButton;
import Interfaces.RoutesInterface.RoutesPanel;
import Interfaces.StationsInterface.StationsPanel;
import Interfaces.TransportsInterface.TransportsPanel;
import Interfaces.buyTicketsInterface.BuyTicketsPanel;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class FirstMenu extends JPanel{

    private static FirstMenu firstMenuInstance;

    public static FirstMenu getInstance() {
        if (firstMenuInstance==null) {
            firstMenuInstance= new FirstMenu();
        }
        return firstMenuInstance;
    }

    public FirstMenu() {

        //Creacion del menu principal
        super();
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.LIGHT_GRAY);
        this.setForeground(Color.DARK_GRAY);

        //Creacion del boton que encamina al menu de estaciones.
        MyButton stationButton = new MyButton("Estaciones",24);

        //Creacion del boton que encamina al menu de rutas
        MyButton transportButton = new MyButton("Transportes",24);

        //Creacion del boton que encamina al historial de mantenimientos
        MyButton routeButton = new MyButton("Rutas",24);

        //Creacion del boton que encamina la compra de boletos
        MyButton buyTicketsButton = new MyButton("Obtener pasaje",24);

        //Creacion del boton de salida
        MyButton exitButton = new MyButton("Salir",24);

        //Se añaden los botones con sus posiciones especificas
        GridBagConstraints cts = new GridBagConstraints();
        cts.insets=new Insets(10,10,10,10);
        cts.fill=GridBagConstraints.HORIZONTAL;
        cts.gridx=0;
        cts.gridy=0;
        this.add(stationButton,cts);
        
        cts.gridy=1;
        this.add(transportButton,cts);
        
        cts.gridy=2;
        this.add(routeButton,cts);
        
        cts.gridy=3;
        this.add(buyTicketsButton,cts);
        
        cts.gridy=4;
        this.add(exitButton,cts);

        //Creacion del Frame general
        JFrame frame = WindowManager.getMainWindow();
        frame.setContentPane(this);
        frame.pack();
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //Creacion de listeners
        stationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                StationsPanel stationsPanel = StationsPanel.getInstance();
                frame.setContentPane(stationsPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        transportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                TransportsPanel transportPanel = TransportsPanel.getInstance();
                frame.setContentPane(transportPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        routeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                RoutesPanel routePanel = RoutesPanel.getInstance();
                frame.setContentPane(routePanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

         buyTicketsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                BuyTicketsPanel ticketsPanel = BuyTicketsPanel.getInstance();
                frame.setContentPane(ticketsPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JPanel contentPane = new JPanel();
                int n = JOptionPane.showConfirmDialog(contentPane,"¿Desea salir?","Salir", JOptionPane.YES_OPTION);
                if(n == 0) {
                    System.exit(0);
                }
            }
        });
    }
}