package Interfaces.buyTicketsInterface;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import Graph.Route;
import Graph.Graph;

import java.util.List;
import java.time.LocalDate;

import java.sql.Date;

import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;

import DataBase.DBConnection;
import Interfaces.WindowManager;
import Interfaces.FirstMenu;
import Interfaces.Accessories.MyButton;
import Interfaces.Accessories.StationsComboBox;
import Interfaces.Accessories.EmailVerification;

public class BuyTicketsButtonPanel extends JPanel {
    private static BuyTicketsButtonPanel BuyTicketsButtonPanelInstance;

    public static BuyTicketsButtonPanel getInstance() {
        if(BuyTicketsButtonPanelInstance==null) {
            BuyTicketsButtonPanelInstance = new BuyTicketsButtonPanel();
        }
        return BuyTicketsButtonPanelInstance;
    }

    public BuyTicketsButtonPanel() {
        super();
        PrintedGraphPanel graphPanel = PrintedGraphPanel.getInstance();
        JFrame mainFrame = WindowManager.getMainWindow();
        Graph graphInstance = Graph.getInstance();
        
        this.setLayout(new GridBagLayout());
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(Color.LIGHT_GRAY);

        GridBagConstraints cts = new GridBagConstraints();

        cts.weightx=1.0;
        cts.weighty=1.0;
        cts.anchor=GridBagConstraints.WEST;
        cts.insets=new Insets(10,10,5,10);
        cts.gridx=0;
        cts.gridy=0;
        //Creamos los labels de origen y destino
        this.add(new JLabel("Estación origen"),cts);

        cts.gridy=1;
        this.add(new JLabel("Estación destino"),cts);

        //Creamos el textField de nombre y el ComboBox de colores
        StationsComboBox originStationsBox = new StationsComboBox("---");
        StationsComboBox endStationsBox = new StationsComboBox("---");

        //Input verifier para los campos de texto y los combobox
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

        //Añadimos los comboBox
        cts.gridy=0;
        cts.gridx=2;
        this.add(originStationsBox,cts);

        cts.gridy=1;
        this.add(endStationsBox,cts);

        //Creamos los botones de busqueda, camino mas corto, camino mas barato, camino mas rapido y flujo maximo
        MyButton searchButton = new MyButton("Buscar", 14);
        MyButton shortestRouteButton = new MyButton("Camino mas corto", 14);
        MyButton fastestRouteButton = new MyButton("Camino mas rapido", 14);
        MyButton cheaperRouteButton = new MyButton("Camino mas barato", 14);
        MyButton backButton = new MyButton("Volver", 14);
        MyButton buyButton = new MyButton("Comprar Boleto", 14);
        MyButton maxFlowButton = new MyButton("Calcular flujo maximo", 14);
        
        
        cts.gridx=0;
        cts.gridy=2;
        this.add(searchButton,cts);

        cts.gridy=5;
        this.add(shortestRouteButton,cts);

        cts.gridy=6;
        this.add(fastestRouteButton,cts);

        cts.gridy=7;
        this.add(cheaperRouteButton,cts);

        cts.gridy=8;
        this.add(maxFlowButton,cts);

        cts.gridy=9;
        this.add(buyButton,cts);
        
        cts.gridy=10;
        this.add(backButton,cts);


        //Los botones especificos comenzaran bloqueados
        shortestRouteButton.setEnabled(false);
        fastestRouteButton.setEnabled(false);
        cheaperRouteButton.setEnabled(false);
        maxFlowButton.setEnabled(false);
        buyButton.setEnabled(false);
        

        //Action listener de las seleccion de estaciones (bloquea los botones de filtrado en al presionar un combobox de seleccion de estaciones)
        ActionListener selectStation = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                graphPanel.clearRoutesComboBox();
                shortestRouteButton.setEnabled(false);
                fastestRouteButton.setEnabled(false);
                cheaperRouteButton.setEnabled(false);
                maxFlowButton.setEnabled(false);
                buyButton.setEnabled(false);
                
            }
        };
        originStationsBox.addActionListener(selectStation);
        endStationsBox.addActionListener(selectStation);

        //Listeners de los botones
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                mainFrame.setSize(1200,600);
                
                shortestRouteButton.setEnabled(false);
                fastestRouteButton.setEnabled(false);
                cheaperRouteButton.setEnabled(false);
                maxFlowButton.setEnabled(false);
                buyButton.setEnabled(false);

                int origin = (Integer)originStationsBox.getSelectedItem();
                int end = (Integer)endStationsBox.getSelectedItem();
                //Se verifica que se hayan seleccionado las estaciones de origen y destino
                if (origin==0 || end==0) {
                    JOptionPane.showMessageDialog(null,"Error. Seleccione las estaciones de origen y de destino","Buscar ruta",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                else if (!graphInstance.getStation(origin).getStatus() || !graphInstance.getStation(end).getStatus() ||
                         !graphInstance.getStation(origin).getTimeStatus() || !graphInstance.getStation(end).getTimeStatus()) {
                    JOptionPane.showMessageDialog(null,"Error. Una de las estaciones seleccionadas se encuentra cerrada","Buscar ruta",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Se obtiene la lista de caminos
                List<List<Route>> pathList = graphInstance.possiblePaths(origin,end);
                
                //Si no existen caminos se le informa al usuario
                if (pathList.isEmpty()) {
                    JOptionPane.showMessageDialog(null,"No existen caminos que vayan desde la estación "+origin+" hasta la estación "+end,"Buscar ruta",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                graphPanel.refreshRoutesComboBox(pathList);
                shortestRouteButton.setEnabled(true);
                fastestRouteButton.setEnabled(true);
                cheaperRouteButton.setEnabled(true);
                buyButton.setEnabled(true);
                maxFlowButton.setEnabled(true);
            }
        });

        shortestRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                int origin = (Integer)originStationsBox.getSelectedItem();
                int end = (Integer)endStationsBox.getSelectedItem();

                //Se obtiene la lista de caminos
                List<List<Route>> pathList = graphInstance.possiblePaths(origin,end);
                graphPanel.setRoutesComboBox(graphInstance.getShortestRoute(pathList));
            }
        });

        fastestRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int origin = (Integer)originStationsBox.getSelectedItem();
                int end = (Integer)endStationsBox.getSelectedItem();

                //Se obtiene la lista de caminos
                List<List<Route>> pathList = graphInstance.possiblePaths(origin,end);
                graphPanel.setRoutesComboBox(graphInstance.getFastestRoute(pathList));
            }
        });

        cheaperRouteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int origin = (Integer)originStationsBox.getSelectedItem();
                int end = (Integer)endStationsBox.getSelectedItem();

                //Se obtiene la lista de caminos
                List<List<Route>> pathList = graphInstance.possiblePaths(origin,end);
                graphPanel.setRoutesComboBox(graphInstance.getCheaperRoute(pathList));

            }
        });
        
        buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Se bloquea el frame principal
                mainFrame.setEnabled(false);
                
                //Se crea un popUp donde se permitira ingresar los datos del comprador
                JDialog popUp = WindowManager.getPopUpWindow("Ingrese los datos del pasajero");
                JPanel addShopPanel = new JPanel();
                addShopPanel.setLayout(new GridBagLayout());
                GridBagConstraints cts = new GridBagConstraints();

                //Creamos y añadimos los labels al panel
                cts.weightx=1.0;
                cts.weighty=1.0;
                cts.anchor=GridBagConstraints.WEST;
                cts.insets=new Insets(10,10,5,10);
                cts.gridx=0;
                cts.gridy=0;
                addShopPanel.add(new JLabel("Ingrese el nombre del pasajero"), cts);

                cts.gridy=1;
                addShopPanel.add(new JLabel("Ingrese el Email del pasajero"),cts);
                
                //Creamos el textField de nombre y el ComboBox de colores
                JTextField clientNameField = new JTextField(20);
                JTextField emailField = new JTextField(20);

                //Añadimos el textfield y el combobox
                cts.insets=new Insets(10,5,10,10);
                cts.fill=GridBagConstraints.HORIZONTAL; 
                cts.gridx=2;
                cts.gridwidth=1;
                cts.gridy=0;
                addShopPanel.add(clientNameField, cts);

                cts.gridy=1;
                addShopPanel.add(emailField, cts);

                //Añadimos botones de confirmacion
                MyButton nextButton = new MyButton("Siguiente", 14);
                MyButton cancelButton = new MyButton("Cancelar compra", 14);

                //Añadimos los botones al panel
                cts.gridy=2;
                cts.gridx=0;
                cts.anchor=GridBagConstraints.CENTER;
                cts.fill=GridBagConstraints.HORIZONTAL;
                addShopPanel.add(cancelButton,cts);

                cts.gridx=2;
                addShopPanel.add(nextButton,cts);
                
                popUp.setContentPane(addShopPanel);
                popUp.pack();
                mainFrame.setLocationRelativeTo(null);
                popUp.setVisible(true);

                nextButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        if (clientNameField.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(null,"Error. Por favor ingrese un nombre valido","Ingrese sus datos",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        else if (clientNameField.getText().length() > 20) {
                            JOptionPane.showMessageDialog(null,"Error. El nombre posee mas de 20 caracteres","Ingrese sus datos",JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (emailField.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(null,"Error. Por favor ingrese un email valido","Ingrese sus datos",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        else if (emailField.getText().length() > 40) {
                            JOptionPane.showMessageDialog(null,"Error. El email posee mas de 40 caracteres","Ingrese sus datos",JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        else if (!EmailVerification.verifyEmail(emailField.getText())) {
                            return;
                        }

                        JPanel dataPanel = new JPanel();
                        dataPanel.setLayout(new GridBagLayout());
                        GridBagConstraints cts = new GridBagConstraints();
                        
                        //Label informativo
                        cts.insets=new Insets(10,5,10,5);
                        cts.gridx=0;
                        cts.gridy=0;
                        cts.gridwidth=2;
                        dataPanel.add(new JLabel("DATOS DE LA COMPRA"),cts);

                        //Añadimos los botones
                        MyButton finishBuyButton = new MyButton("Realizar compra", 14);
                        cts.weightx=1.0;
                        cts.weighty=1.0;
                        cts.insets=new Insets(5,5,5,5);
                        cts.fill=GridBagConstraints.HORIZONTAL;
                        cts.gridwidth=1;
                        cts.gridy=9;
                        dataPanel.add(cancelButton,cts);
                        cts.gridx=1;
                        dataPanel.add(finishBuyButton,cts);

                        // Definimos un nuevo hilo y buscamos el ultimo id de boleto almacenado en la base de datos
                        Runnable r1 = () -> {
                            int idTicket=0;
                            ResultSet rs = null;
                            DBConnection.establishConnection();
                            PreparedStatement pstm = null;
                            try {
                                pstm = DBConnection.getConnection().prepareStatement("SELECT MAX(id)"+
                                                                                     "FROM ticket ");
                                rs = pstm.executeQuery();
                                while(rs.next()){
                                    idTicket=rs.getInt(1)+1;
                                }

                                //Creamos los labels
                                JLabel idTicketLabel = new JLabel("Nro de boleto: " + idTicket);
                                JLabel emailLabel = new JLabel("Email: " + emailField.getText());
                                JLabel clientNameLabel = new JLabel("Nombre: " + clientNameField.getText());
                                JLabel sellDateLabel = new JLabel("Fecha de la venta: " + LocalDate.now());
                                JLabel originStationLabel = new JLabel("Estacion origen: " + originStationsBox.getSelectedItem());
                                JLabel endStationLabel = new JLabel("Estacion destino: " + endStationsBox.getSelectedItem());
                                JLabel pathLabel = new JLabel("Camino a seguir: " + graphPanel.getRouteSelected());
                                JLabel ticketCostLabel = new JLabel("Costo del boleto: " + graphInstance.getPathCost(graphPanel.getRouteSelected()));

                                //Añadimos los labels
                                cts.gridwidth=2;
                                cts.insets=new Insets(5,10,5,10);
                                cts.gridx=0;
                                cts.gridy=1;
                                dataPanel.add(idTicketLabel,cts);
                                cts.gridy=2;
                                dataPanel.add(emailLabel,cts);
                                cts.gridy=3;
                                dataPanel.add(clientNameLabel,cts);
                                cts.gridy=4;
                                dataPanel.add(sellDateLabel,cts);
                                cts.gridy=5;
                                dataPanel.add(originStationLabel,cts);
                                cts.gridy=6;
                                dataPanel.add(endStationLabel,cts);
                                cts.gridy=7;
                                dataPanel.add(pathLabel,cts);
                                cts.gridy=8;
                                dataPanel.add(ticketCostLabel,cts);

                                popUp.setContentPane(dataPanel);
                                popUp.pack();
                                mainFrame.setLocationRelativeTo(null);
                                popUp.setVisible(true);
                            }
                            catch (SQLException exception) {
                                    exception.printStackTrace();
                            }
                            finally {
                                DBConnection.closeConnection(pstm, rs);
                            }
                        };
                        new Thread(r1,"ShowInformation").start();

                        //Boton de finalizar compra
                        finishBuyButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                //Mostramos mensaje de compra realizada
                                JOptionPane.showMessageDialog(null,"Compra realizada!","Realizar compra",JOptionPane.INFORMATION_MESSAGE);
                                // Definimos un nuevo hilo, buscamos el id maximo de boleto e insertamos los datos
                                // de la compra realizada por el cliente en la base de datos 
                                Runnable r = () -> {

                                    int idTicket=0;
                                    DBConnection.establishConnection();
                                    PreparedStatement pstmInsert = null;
                                    PreparedStatement pstmID = null;
                                    ResultSet rsID = null;
                                    int rsInsert = 0;
                                    try {
                                        pstmID = DBConnection.getConnection().prepareStatement("SELECT MAX(id)"+
                                                                                               "FROM ticket ");
                                        rsID = pstmID.executeQuery();
                                        // Aumentamos el numero de boleto
                                        while(rsID.next()){
                                            idTicket=rsID.getInt(1)+1;
                                        }
                                        
                                        pstmInsert = DBConnection.getConnection().prepareStatement("INSERT INTO ticket VALUES (?,?,?,?,?,?,?,?)");
                                        pstmInsert.setInt(1, idTicket);
                                        pstmInsert.setString(2, emailField.getText());
                                        pstmInsert.setString(3, clientNameField.getText());
                                        pstmInsert.setDate(4, Date.valueOf(LocalDate.now()));
                                        pstmInsert.setInt(5, (Integer)originStationsBox.getSelectedItem());
                                        pstmInsert.setInt(6, (Integer)endStationsBox.getSelectedItem());
                                        pstmInsert.setString(7, graphPanel.getRouteSelected().toString());
                                        pstmInsert.setDouble(8, graphInstance.getPathCost(graphPanel.getRouteSelected()));
                                        rsInsert = pstmInsert.executeUpdate();
                                    }
                                    catch (SQLException exception) {
                                        exception.printStackTrace();
                                    }
                                    finally {
                                        DBConnection.closeConnection(pstmInsert, rsInsert);
                                        DBConnection.closeConnection(pstmID, rsID);
                                    }
                                };
                                new Thread(r,"DB insert ticket").start();
                                popUp.setVisible(false);
                                popUp.dispose();
                                mainFrame.setEnabled(true);
                                mainFrame.setVisible(true);
                            }
                        });
                    }
                });

                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        popUp.setVisible(false);
                        popUp.dispose();
                        mainFrame.setEnabled(true);
                        mainFrame.setVisible(true);
                    }
                });
            }
        });
        
        maxFlowButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                Integer origin = (Integer)originStationsBox.getSelectedItem();
                Integer end = (Integer)endStationsBox.getSelectedItem();
                List<List<Route>> pathList = graphInstance.possiblePaths(origin,end);
                
                Integer flow = graphInstance.getMaxFlow(origin, end, pathList);
                String message = "El flujo maximo desde: \n"+
                                 "• Estación de origen: "+origin+"\n"+
                                 "• Estación de destino: "+end+"\n"+
                                 "En este momento es: " +flow+" pasajeros";
                JOptionPane.showMessageDialog(null,message,"Flujo maximo entre estaciones",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                graphPanel.clearRoutesComboBox();
                originStationsBox.setSelectedIndex(0);
                endStationsBox.setSelectedIndex(0);

                mainFrame.setContentPane(FirstMenu.getInstance());
                mainFrame.pack();
                mainFrame.setSize(800,600);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            }
        });
    }
}
