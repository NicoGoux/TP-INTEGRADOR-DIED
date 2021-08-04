package Interfaces.RoutesInterface;

import Interfaces.FirstMenu;
import Interfaces.WindowManager;
import Interfaces.Accessories.ColourComboBox;
import Interfaces.Accessories.MyButton;
import Interfaces.Accessories.StationsComboBox;
import Interfaces.Tables.RoutesTable;
import DataBase.DBConnection;
import Graph.Graph;
import Graph.Route;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RoutesButtonPanel extends JPanel {

    private static RoutesButtonPanel RoutePanelInstance;

    public static RoutesButtonPanel getInstance() {
        if(RoutePanelInstance==null) {
            RoutePanelInstance = new RoutesButtonPanel();
        }
        return RoutePanelInstance;
    }

    public RoutesButtonPanel() {
        super();
        JFrame mainFrame = WindowManager.getMainWindow();
        RoutesTable routesTable = RoutesTable.getInstance();

        this.setLayout(new GridBagLayout());
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(Color.LIGHT_GRAY);
        
        ColourComboBox routeColourBox = new ColourComboBox();
        MyButton addRouteButton = new MyButton("Añadir ruta", 14);
        MyButton switchStatusRouteButton = new MyButton("Habilitar/Deshabilitar ruta", 14);
        MyButton deleteRouteButton = new MyButton("Eliminar ruta", 14);
        MyButton backButton = new MyButton("Volver", 14);


        //Creacion de Listeners
        //Listener del selector de color
        routeColourBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (routeColourBox.getSelectedItem().equals("Seleccione un color")) {
                    addRouteButton.setEnabled(false);
                    switchStatusRouteButton.setEnabled(false);
                    deleteRouteButton.setEnabled(false);
                    routesTable.clearTable();
                }
                else {
                    routesTable.refreshData((String)routeColourBox.getSelectedItem());
                    addRouteButton.setEnabled(true);
                    deleteRouteButton.setEnabled(true);
                    switchStatusRouteButton.setEnabled(true);
                }
            }
        });

        //Listener del boton añadir
        addRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Como en el sistema no se aceptan los bucles se comprueba que la cantidad de estaciones visitadas no sea similar a la cantidad de estaciones disponibles en el sistema
                if (routesTable.getVisitedStations().size()==Graph.getInstance().getAllStations().size()) {
                    JOptionPane.showMessageDialog(null,"El trayecto ya incluye todas las estaciones disponibles","Añadir nuevo trayecto",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                //Se bloquea el frame principal
                mainFrame.setEnabled(false);
                
                //Se crea un popUp donde se permitira ingresar los datos de la nueva ruta
                JDialog popUp = WindowManager.getPopUpWindow("Ingrese los datos de la nueva ruta");
                JPanel addRoutePanel = new JPanel();
                addRoutePanel.setLayout(new GridBagLayout());
                GridBagConstraints cts = new GridBagConstraints();

                //Creamos los Labels
                JLabel originLabel = new JLabel("Seleccione la estacion de origen");
                JLabel endLabel = new JLabel("Seleccione la estacion de destino");
                JLabel distanceLabel = new JLabel("Indique la distancia del viaje (en kilometros)");
                JLabel durationLabel = new JLabel("Indique la duracion del viaje (en minutos)");
                JLabel maxPassengerLabel = new JLabel("Indique la cantidad maxima de pasajeros");
                JLabel priceLabel = new JLabel("Indique el precio del viaje");

                //Añadimos los labels
                cts.weightx=1.0;
                cts.weighty=1.0;
                cts.anchor=GridBagConstraints.WEST;
                cts.insets=new Insets(10,10,5,10);
                cts.gridx=0;
                cts.gridy=0;
                addRoutePanel.add(originLabel, cts);
                
                cts.gridy=1;
                addRoutePanel.add(endLabel,cts);

                cts.gridy=2;
                addRoutePanel.add(distanceLabel,cts);

                cts.gridy=3;
                addRoutePanel.add(durationLabel,cts);

                cts.gridy=4;
                addRoutePanel.add(maxPassengerLabel,cts);

                cts.gridy=5;
                addRoutePanel.add(priceLabel,cts);


                //Creamos el textField de nombre y el ComboBox de colores
                StationsComboBox originStationsBox = new StationsComboBox("Seleccione una estación");
                StationsComboBox endStationsBox = new StationsComboBox("Seleccione una estación");

                //Eliminamos las estaciones ya visitadas
                if (!routesTable.getVisitedStations().isEmpty()) {
                    for (Integer i : routesTable.getVisitedStations()) {
                        endStationsBox.removeItem(i);
                        originStationsBox.removeItem(i);
                    }
                }

                KeyAdapter integerTextField = new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char car = e.getKeyChar();
                        if((car<'0' || car>'9')) e.consume();
                    }
                };

                JTextField distanceField = new JTextField();
                distanceField.addKeyListener(integerTextField);

                JTextField durationField = new JTextField();
                durationField.addKeyListener(integerTextField);

                JTextField maxPassengerField = new JTextField();
                maxPassengerField.addKeyListener(integerTextField);

                JTextField priceField = new JTextField();

                KeyAdapter doubleTextField = new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char car = e.getKeyChar();
                        if(car!='.' && (car<'0' || car>'9')) e.consume();
                        if (car=='.') priceField.addKeyListener(integerTextField);
                    }
                };

                priceField.addKeyListener(doubleTextField);

                //Añadimos el textfield y el combobox
                cts.insets=new Insets(10,5,10,10);
                cts.fill=GridBagConstraints.HORIZONTAL; 
                cts.gridx=2;
                cts.gridwidth=1;
                cts.gridy=0;
                addRoutePanel.add(originStationsBox, cts);

                cts.gridy=1;
                addRoutePanel.add(endStationsBox, cts);

                cts.gridy=2;
                addRoutePanel.add(distanceField, cts);

                cts.gridy=3;
                addRoutePanel.add(durationField, cts);

                cts.gridy=4;
                addRoutePanel.add(maxPassengerField, cts);

                cts.gridy=5;
                addRoutePanel.add(priceField, cts);

                //Input verifier para los combobox
                originStationsBox.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange()==1) {
                            if ((Integer)e.getItem()!=0) {
                                if ((Integer)e.getItem() == (Integer)endStationsBox.getSelectedItem()) {
                                    JOptionPane.showMessageDialog(null,"Error. Seleccione una estación distinta a la estación de destino","Buscar ruta",JOptionPane.ERROR_MESSAGE);
                                    originStationsBox.setSelectedItem(0);
                                }
                            }
                        }
                    }
                });
        
                endStationsBox.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange()==1) {
                            if ((Integer)e.getItem()!=0) {
                                if ((Integer)e.getItem() == (Integer)originStationsBox.getSelectedItem()) {
                                    JOptionPane.showMessageDialog(null,"Error. Seleccione una estación distinta a la estación de origen","Buscar ruta",JOptionPane.ERROR_MESSAGE);
                                    endStationsBox.setSelectedItem(0);
                                }
                            }
                        }
                    }
                });

                //Añadimos boton de confirmacion
                MyButton okButton = new MyButton("Añadir ruta", 14); 
                cts.gridy=6;
                cts.gridx=2;
                cts.anchor=GridBagConstraints.CENTER;
                cts.fill=GridBagConstraints.HORIZONTAL;
                addRoutePanel.add(okButton,cts);

                //Action listener del boton de confirmacion
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        //Obtenemos todos los valores ingresados (Solo lo realizamos para que el if siguiente sea mas legible)
                        Integer origin = (Integer)originStationsBox.getSelectedItem();
                        Integer end = (Integer)endStationsBox.getSelectedItem();
                        String distance = distanceField.getText();
                        String duration = durationField.getText();
                        String maxPassenger = maxPassengerField.getText();
                        String price = priceField.getText();

                        //Si no se ingreso algun dato se indica un error que solicita el ingreso de los datos
                        if (origin==0 || end==0 || distance.isEmpty() || duration.isEmpty() || maxPassenger.isEmpty() || price.isEmpty()) {
                            JOptionPane.showMessageDialog(null,"Error. Por favor, ingrese todos los datos solicitados","Ingrese los datos de la nueva ruta",JOptionPane.ERROR_MESSAGE);
                        }
                        //Se almacena la nueva ruta en la base de datos, se agrega al grafo y se actualiza la tabla
                        else {
                            //Transformamos los valores ingresador a enteros
                            int intDistance = Integer.parseInt(distance);
                            int intDuration = Integer.parseInt(duration);
                            int intMaxPassenger = Integer.parseInt(maxPassenger);
                            double doublePrice = Double.parseDouble(price);
                            String routeColor = (String)routeColourBox.getSelectedItem();

                            Route aRoute=new Route(origin, end, routeColor,intDistance,intDuration,intMaxPassenger,true, doublePrice);
                            Runnable tRunnable = () -> {
                                //Se añade la estacion a la base de datos
                                DBConnection.establishConnection();
                                PreparedStatement pstm = null;
                                int ps=0;
                                try { 
                                    pstm = DBConnection.getConnection().prepareStatement("INSERT INTO ROUTE VALUES (?,?,?,?,?,?,?,?)");
                                    pstm.setInt(1, aRoute.getOrigin());
                                    pstm.setInt(2, aRoute.getEnd());
                                    pstm.setString(3, aRoute.getColourString());
                                    pstm.setInt(4, aRoute.getDistance());
                                    pstm.setInt(5, aRoute.getDuration());
                                    pstm.setInt(6, aRoute.getMaxPassengers());
                                    pstm.setBoolean(7, aRoute.getStatus());
                                    pstm.setDouble(8, aRoute.getPrice());
                                    ps = pstm.executeUpdate();

                                    //Se añade la estacion a la lista de estaciones del grafo
                                    Graph.getInstance().connect(aRoute);

                                    //Se actualiza la tabla
                                    routesTable.refreshData((String)routeColourBox.getSelectedItem());
                                } catch (SQLException e){
                                    if (e.getSQLState().equals("23505")) {
                                        String message = "Error. La ruta que se desea crear ya existe\n"+
                                                         "• Estación de origen: "+aRoute.getOrigin()+"\n"+
                                                         "• Estación de destino: "+aRoute.getEnd();
                                        JOptionPane.showMessageDialog(null,message,"Añadir ruta",JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    e.printStackTrace();
                                    System.out.println("ERROR SQL IN addTransportButton:" + e.getSQLState());
                                } finally {
                                    DBConnection.closeConnection(pstm, ps);
                                }
                            };
                            new Thread(tRunnable,"Thread AddStation").start();
                            popUp.setVisible(false);
                            popUp.dispose();
                            mainFrame.setEnabled(true);
                            mainFrame.setVisible(true);
                        }
                    }
                });

                //Seteamos todas las visibilidades a falso
                for (java.awt.Component c : addRoutePanel.getComponents()) {
                    c.setVisible(false);
                }

                popUp.setContentPane(addRoutePanel);

                //Analizamos si existe un trayecto creado, en el caso de no existir permitimos añadir el trayecto inicial
                if (routesTable.getRowCount()==0) {
                    for (java.awt.Component c : addRoutePanel.getComponents()) {
                        c.setVisible(true);
                    }
                    popUp.pack();
                    popUp.setVisible(true);
                }

                //Si no existe consultamos si deseamos agregar la nueva ruta al principio o al final
                else {
                    MyButton addNewInitialRoute = new MyButton("Nueva ruta al comienzo del trayecto", 14);
                    MyButton addNewFinalRoute = new MyButton("Nueva ruta al final del trayecto", 14);

                    cts.gridx=1;
                    cts.gridy=1;
                    addRoutePanel.add(addNewInitialRoute,cts);

                    cts.gridy=2;
                    addRoutePanel.add(addNewFinalRoute,cts);

                    popUp.pack();
                    popUp.setVisible(true);

                    //Listener para boton addNewInitialRoute
                    addNewInitialRoute.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            int origin = routesTable.getOrigin();
                            //Añadimos el origen al combo box de estaciones finales (para que nos permita hacer el setSelected)
                            endStationsBox.addItem(origin);

                            //Ponemos visibles todos los elementos excepto los que no necesitamos
                            for (java.awt.Component c : addRoutePanel.getComponents()) {
                                c.setVisible(true);
                            }
                            addNewInitialRoute.setVisible(false);
                            addNewFinalRoute.setVisible(false);
                            endLabel.setVisible(false);
                            endStationsBox.setVisible(false);
                            endStationsBox.setSelectedItem(origin);

                            popUp.pack();
                            popUp.setVisible(true);
                        }
                    });

                    addNewFinalRoute.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            int end = routesTable.getEnd();
                            //Añadimos el origen al combo box de estaciones finales (para que nos permita hacer el setSelected)
                            originStationsBox.addItem(end);

                            //Ponemos visibles todos los elementos excepto los que no necesitamos
                            for (java.awt.Component c : addRoutePanel.getComponents()) {
                                c.setVisible(true);
                            }
                            addNewInitialRoute.setVisible(false);
                            addNewFinalRoute.setVisible(false);
                            originLabel.setVisible(false);
                            originStationsBox.setVisible(false);
                            originStationsBox.setSelectedItem(end);

                            popUp.pack();
                            popUp.setVisible(true);
                        }
                    });
                }
            }
        });

        //Listener del boton deshabilitar
        switchStatusRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Si no hay un trayecto para habilitar se muestra un error
                if (routesTable.getOrigin()==-1 && routesTable.getEnd()==-1) {
                    JOptionPane.showMessageDialog(null,"No existe un trayecto para cambiar su estado","Cambiar estado del trayecto",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Mostramos al usuario un mensaje de confirmacion
                String message = "¿Desea cambiar el estado de la ruta seleccionada?\n"+
                                 "• Estación de origen: "+routesTable.getOrigin()+"\n"+
                                 "• Estación de destino: "+routesTable.getEnd();

                int confirm = JOptionPane.showConfirmDialog(null,message,"Cambiar estado del trayecto",JOptionPane.YES_NO_OPTION);
                
                //En caso afirmativo habilitamos la linea
                boolean newStatus;
                if (routesTable.getRouteStatus()) {
                    newStatus=false;
                }
                else {
                    newStatus=true;
                }
                if (confirm == 0) {
                    Runnable t = () -> {
                        DBConnection.establishConnection();
                        PreparedStatement pstm = null;
                        int ps=0;
                        String colourHex = (String)routeColourBox.getSelectedItem();
                        try { 
                            pstm = DBConnection.getConnection().prepareStatement("UPDATE ROUTE SET status=? WHERE colour='"+colourHex+"'");
                            pstm.setBoolean(1, newStatus);
                            ps = pstm.executeUpdate();

                            //Se cambia el estado de las todas las rutas del color "colourHex" al nuevo estado
                            Graph.getInstance().changeColourRoutesStatus(colourHex, newStatus);
                            routesTable.refreshData(colourHex);
                        }
                        catch (SQLException e) {
                            JOptionPane.showMessageDialog(null,"Error. No se puede cambiar el estado de la ruta","Cambiar estado de la ruta",JOptionPane.OK_OPTION);
                            e.printStackTrace();
                            return;
                        }
                        finally {
                            DBConnection.closeConnection(pstm, ps);
                        }
                    };
                    new Thread(t,"DB switchStatus thread").start();
                }
                routesTable.clearSelection();
            }
        });
        
        //Listener del boton eliminar
        deleteRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Si no hay un trayecto para eliminar se muestra un error
                if (routesTable.getOrigin()==-1 && routesTable.getEnd()==-1) {
                    JOptionPane.showMessageDialog(null,"No existe un trayecto para eliminar","Eliminar trayecto",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Mostramos al usuario un mensaje de confirmacion
                String message = "¿Desea eliminar la ruta seleccionada?\n"+
                                 "• Estación de origen: "+routesTable.getOrigin()+"\n"+
                                 "• Estación de destino: "+routesTable.getEnd();

                int confirm = JOptionPane.showConfirmDialog(null,message,"Eliminar trayecto",JOptionPane.YES_NO_OPTION);
                
                //En caso afirmativo borramos los datos de la base de datos
                if (confirm == 0) {
                    Runnable t = () -> {
                        DBConnection.establishConnection();
                        PreparedStatement pstm = null;
                        int ps=0;
                        String colourHex = (String)routeColourBox.getSelectedItem();
                        try { 
                            pstm = DBConnection.getConnection().prepareStatement("DELETE FROM ROUTE WHERE colour='"+colourHex+"'");
                            ps = pstm.executeUpdate();
                            Graph.getInstance().deleteColourRoutes(colourHex);
                            routesTable.refreshData(colourHex);
                        }
                        catch (SQLException e) {
                            JOptionPane.showMessageDialog(null,"Error. La ruta no puede ser eliminada","Eliminar ruta",JOptionPane.OK_OPTION);
                            e.printStackTrace();
                            return;
                        }
                        finally {
                            DBConnection.closeConnection(pstm, ps);
                        }
                    };
                    new Thread(t,"DB delete thread").start();
                }

                routesTable.clearSelection();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Realizamos este nuevo thread para que se resetee la tabla y los filtros al volver al menu principal
                Runnable r1 = () -> {
                    routesTable.clearTable();
                    routeColourBox.setSelectedItem("Seleccione un color");
                };
                new Thread(r1,"Reset Table").start();

                mainFrame.setContentPane(FirstMenu.getInstance());
                mainFrame.pack();
                mainFrame.setSize(800,600);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            }
        });
        
        //Se añaden los botones al RoutesButtonPanel
        GridBagConstraints cts = new GridBagConstraints();
        cts.anchor=GridBagConstraints.WEST;
        cts.insets=new Insets(10,10,10,10);
        cts.fill=GridBagConstraints.HORIZONTAL;
        cts.gridx=0;
        cts.gridy=0;
        this.add(new JLabel("Seleccione el color de la linea"),cts);

        cts.gridx=1;
        this.add(routeColourBox,cts);

        cts.gridx=3;
        cts.anchor=GridBagConstraints.EAST;
        this.add(addRouteButton,cts);

        cts.gridx=4;
        this.add(switchStatusRouteButton,cts);

        cts.gridx=5;
        this.add(deleteRouteButton,cts);

        cts.gridx=6;
        this.add(backButton, cts);

        addRouteButton.setEnabled(false);
        switchStatusRouteButton.setEnabled(false);
        deleteRouteButton.setEnabled(false);
    }

	public void disableButtons() {
        for (java.awt.Component component : this.getComponents()) {
            if (component instanceof MyButton) {
                component.setEnabled(false);
            }
        }
	}

    public void enableButtons() {
        for (java.awt.Component component : this.getComponents()) {
            if (component instanceof MyButton) {
                component.setEnabled(true);
            }
        }
    }
}
