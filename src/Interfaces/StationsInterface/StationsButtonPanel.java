package Interfaces.StationsInterface;

import Interfaces.FirstMenu;
import Interfaces.WindowManager;
import Interfaces.Accessories.MyButton;
import Interfaces.Accessories.TimeVerification;
import Interfaces.Tables.StationsTable;
import DataBase.DBConnection;
import Exceptions.TimeFormatException;
import Graph.Station;
import Graph.Graph;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.MaskFormatter;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;

public class StationsButtonPanel extends JPanel {

    private static StationsButtonPanel ButtonPanelInstance;

    public static StationsButtonPanel getInstance() {
        if(ButtonPanelInstance==null) {
            ButtonPanelInstance = new StationsButtonPanel();
        }
        return ButtonPanelInstance;
    }

    public StationsButtonPanel() {
        
        //Obtenemos la tabla
        StationsTable stationsTable = StationsTable.getInstance();
        

        JFrame mainFrame = WindowManager.getMainWindow();
        this.setLayout(new GridBagLayout());
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(Color.LIGHT_GRAY);

        MyButton addStationButton = new MyButton("Añadir estación", 14);
        MyButton maintenanceButton = new MyButton("Modificar mantenimiento", 14);
        MyButton editStationButton = new MyButton("Editar estación", 14);
        MyButton finishEditButton = new MyButton("Finalizar Edicion", 14);
        MyButton cancelEditButton = new MyButton("Cancelar Edicion", 14);
        MyButton deleteStationButton = new MyButton("Eliminar estación", 14);
        MyButton backButton = new MyButton("Volver", 14);

        //Creacion de Listeners

        //Listener del boton añadir
        addStationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Se bloquea el frame principal
                mainFrame.setEnabled(false);

                //Se crea un popUp donde se permitira ingresar los datos de la nueva estacion
                JDialog popUp = WindowManager.getPopUpWindow("Ingrese los datos de la estación");
                JPanel addStationPanel = new JPanel();
                addStationPanel.setLayout(new GridBagLayout());
                GridBagConstraints cts = new GridBagConstraints();
                
                //Creamos y agregamos los labels al panel
                cts.weightx=1.0;
                cts.weighty=1.0;
                cts.anchor=GridBagConstraints.WEST;
                cts.insets=new Insets(10,10,5,10);
                cts.gridx=0;
                cts.gridy=0;
                addStationPanel.add(new JLabel("Ingrese el nombre de la estación"), cts);

                cts.gridy=1;
                addStationPanel.add(new JLabel("Ingrese el horario de apertura"),cts);

                cts.gridy=2;
                addStationPanel.add(new JLabel("Ingrese el horario de cierre"),cts);
                
                //Se crean los textField
                JTextField stationNameField = new JTextField(20);

                MaskFormatter mask=null;
                try { //Se crean una mascara para las horas
                    mask = new MaskFormatter("##:##");
                } 
                catch (ParseException e) {
                    e.printStackTrace();
                }
                JFormattedTextField stationOpeningField = new JFormattedTextField(mask);
                JFormattedTextField stationClosingField = new JFormattedTextField(mask);

                //Input verifier para los campos de texto
                stationNameField.setInputVerifier(new InputVerifier() {
                    public boolean verify(JComponent input) {
                        JTextField tf = (JTextField) input;
                        String nameString = tf.getText();
                        // verificamos si la cadena es correcta y devolvemos
                        if (nameString.isEmpty()) {
                            JOptionPane.showMessageDialog(null,"Error. Ingrese un nombre valido","Ingrese los datos de la nueva estación",JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        else if (nameString.length()>20) {
                            JOptionPane.showMessageDialog(null,"Error. El nombre indicado tiene mas de 20 caracteres","Ingrese los datos de la nueva estación",JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        else {
                            return true;
                        }
                    }
                });

                InputVerifier timeInputVerifier = new InputVerifier() {
                    public boolean verify(JComponent input) {
                        JTextField tf = (JTextField) input;
                        String timeString = tf.getText();
                        // verificamos si la cadena es correcta y devolvemos
                        return TimeVerification.verifyTime(timeString);
                    }
                };

                stationOpeningField.setInputVerifier(timeInputVerifier);
                stationClosingField.setInputVerifier(timeInputVerifier);

                //Se añaden los textfield al panel
                cts.insets=new Insets(10,5,10,10);
                cts.fill=GridBagConstraints.HORIZONTAL; 
                cts.gridx=2;
                cts.gridwidth=1;
                cts.gridy=0;
                addStationPanel.add(stationNameField, cts);

                stationOpeningField.setHorizontalAlignment(JTextField.CENTER);
                cts.gridy=1;
                cts.gridwidth=2;
                stationOpeningField.setHorizontalAlignment(JTextField.CENTER);
                addStationPanel.add(stationOpeningField,cts);

                cts.gridy=2;
                stationClosingField.setHorizontalAlignment(JTextField.CENTER);
                addStationPanel.add(stationClosingField,cts);

                //Añadimos botones de confirmacion al popUp
                MyButton okButton = new MyButton("Añadir estacion", 14);
                cts.gridy=3;
                cts.gridx=2;
                cts.anchor=GridBagConstraints.CENTER;
                cts.fill=GridBagConstraints.HORIZONTAL;
                addStationPanel.add(okButton,cts);
                
                popUp.setContentPane(addStationPanel);
                popUp.pack();
                popUp.setVisible(true);

                //Action listener del boton de confirmacion
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        //Si no se ingreso ningun dato se indica un error que solicita el ingreso de los datos

                        /* AÑADIDO DESPUES DE LA ENTREGA se cambio la verificacion de stationOpeningField y stationClosingField ya que consultabamos si 
                           el campo era vacio pero nunca iba a ser vacio debido a la mascara que poseen
                         */
                        if (stationNameField.getText().isEmpty() || stationOpeningField.getText().equals("  :  ") || stationClosingField.getText().equals("  :  ")) {
                            JOptionPane.showMessageDialog(null,"Error. No se ingresaron los datos de la estación a agregar","Ingrese los datos de la nueva estación",JOptionPane.ERROR_MESSAGE);
                        }
                        //Se almacena la nueva estacion en la base de datos, se agrega la estacion al grafo y se actualiza la tabla
                        else {
                            Station aStation=new Station(stationNameField.getText(),stationOpeningField.getText(),stationClosingField.getText());
                            Runnable tRunnable = () -> {
                                //Se añade la estacion a la base de datos
                                DBConnection.establishConnection();
                                PreparedStatement pstm = null;
                                int ps=0;
                                try { 
                                    pstm = DBConnection.getConnection().prepareStatement("INSERT INTO STATION VALUES (?,?,?,?,?)");
                                    pstm.setInt(1, aStation.getIdStation());
                                    pstm.setString(2, aStation.getName());
                                    pstm.setString(3, aStation.getOpening());
                                    pstm.setString(4, aStation.getClosing());
                                    pstm.setBoolean(5, aStation.getStatus());
                                    ps = pstm.executeUpdate();

                                    /* AÑADIDO DESPUES DE LA ENTREGA */
                                    //Se añade la estacion a la lista de estaciones del grafo
                                    Graph.getInstance().addStation(aStation);
                                    stationsTable.addStation(aStation);
                                    /* AÑADIDO DESPUES DE LA ENTREGA */


                                } 
                                catch (SQLException e){
                                    e.printStackTrace();
                                    System.out.println("ERROR SQL IN addStationButton:" + e.getSQLState());
                                } 
                                finally {
                                    DBConnection.closeConnection(pstm, ps);
                                    
                                    /*
                                    Se quito despues de la entrega y se coloco en el bloque try ya que si se detecta una excepcion y no se almacena en base de datos
                                    se estaria agregando una estacion no almacenada en la base de datos 
                                      
                                    //Se añade la estacion a la lista de estaciones del grafo
                                    Graph.getInstance().addStation(aStation);
                                    stationsTable.addStation(aStation);
                                     */
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
            }
        });

        editStationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Habilitamos edicion para la fila seleccionada, por default sera la primer fila
                if (stationsTable.editTable()) {
                    //Sacamos la visibilidad de los botones de añadir, editar y volver. Añadimos botones de finalizar edicion, cancelar edicion y eliminar estacion
                    editStationButton.setVisible(false);
                    addStationButton.setVisible(false);
                    maintenanceButton.setVisible(false);
                    backButton.setVisible(false);
                    StationsFilterPanel.getInstance().blockSearch();

                    //Seteamos como visibles los botones de edicion
                    finishEditButton.setVisible(true);
                    cancelEditButton.setVisible(true);
                    deleteStationButton.setVisible(true);  
                }
                else {
                    JOptionPane.showMessageDialog(null,"Error. No se selecciono ninguna fila para editar","Edición de datos",JOptionPane.ERROR_MESSAGE);
                }    
            }
        });

        //Añadimos listener para los botones generados por el boton de edición
        //Finalizamos la edicion de la fila y almacenamos los datos
        finishEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                //Obtenemos el identificador del objeto que se actualizara en la base de datos
                int identifyObject=stationsTable.getIdentifier();
                //Obtenemos la ultima fila editada
                int row = stationsTable.getLastEditRow();
                //Fijamos los cambios en la tabla retirando el foco de los campos editables
                stationsTable.stopEditingCell();

                //Verificamos si algun valor se fijo en vacio, en caso de hacerlo se retorna a su valor anterior
                for (int i = 0;i<stationsTable.getColumnCount();i++) {
                    if (stationsTable.getValueAt(row, i)==null) {
                        stationsTable.restoreCell(row,i);
                    }
                    else if (stationsTable.getValueAt(row, i) instanceof String && ((String)stationsTable.getValueAt(row, i)).isEmpty()) {
                        stationsTable.restoreCell(row,i);
                    }
                }

                /*
                 * En este caso el acceso a la base de datos no fue planteado en un hilo secundario debido a que
                 * las acciones posteriores a la actualizacion dependian de la correcta realizacion de la misma
                 * y de la cantidad de datos que se actualizaron.
                 */

                int update=0;
                DBConnection.establishConnection();
                PreparedStatement pstm = null;
                try {

                    //Verifica si las horas ingresadas cumplen con el formato correspondiente, de lo contrario, tira una excepcion.
                    TimeVerification.tryVerifyTime((String) stationsTable.getValueAt(row, 2));
                    TimeVerification.tryVerifyTime((String) stationsTable.getValueAt(row, 3));

                    pstm = DBConnection.getConnection().prepareStatement("UPDATE STATION SET name=?, opening=?, closing=? WHERE id="+identifyObject);
                    pstm.setString(1, (String) stationsTable.getValueAt(row, 1));
                    pstm.setString(2, (String) stationsTable.getValueAt(row, 2));
                    pstm.setString(3, (String) stationsTable.getValueAt(row, 3));

                    //No incluimos la actualizacion del ID y el estado de la estacion ya que los mismos no estan modificados
                    //pstm.setInt(1,(int) stationsTable.getValueAt(row, 0));
                    //pstm.setBoolean(5, (Boolean) stationsTable.getValueAt(row, 4));

                    update = pstm.executeUpdate();
                } 
                catch (SQLException e) {
                    if (e.getSQLState().equals("22001")){
                        JOptionPane.showMessageDialog(null,"Error. El nombre indicado tiene mas de 20 caracteres","Ingrese los datos de la nueva estación",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    e.printStackTrace();
                    System.out.println("ERROR SQL IN addStationButton:" + e.getSQLState());
                    System.out.println("ERROR SQL IN addStationButton:" + e.getSQLState());
                    return;
                } 
                catch (TimeFormatException e) {
                    JOptionPane.showMessageDialog(null,"Error. Respete el formato indicado (hh:mm, considerando formato de 24 horas)","Ingrese los datos de la estación",JOptionPane.ERROR_MESSAGE);
                    return;

                } 
                finally {
                    DBConnection.closeConnection(pstm, update);
                }
                
                //Una vez que la base de datos acepta los datos actualizados deshabilitamos la edicion de la fila seleccionada
                stationsTable.finishRowEdit();

                /*Buscamos la estacion actualizada en la lista del grafo para actualizarle sus atributos. 
                 *El proceso solo se realiza si se realizo algun cambio en la base de datos,
                 *ya que las estaciones estaran creadas a partir de la misma.
                 */

                if (update>0) {
                    Station aStationUpdated = Graph.getInstance().getStation(identifyObject);
                    aStationUpdated.setName((String)stationsTable.getValueAt(row, 1));
                    aStationUpdated.setOpening((String)stationsTable.getValueAt(row, 2));
                    aStationUpdated.setClosing((String)stationsTable.getValueAt(row, 3));

                    //aStationUpdated.setIdStation((int)stationsTable.getValueAt(row, 0));
                    //aStationUpdated.setStatus((boolean)stationsTable.getValueAt(row, 4));
                }

                //Ocultamos los botones de edicion
                finishEditButton.setVisible(false);
                cancelEditButton.setVisible(false);
                deleteStationButton.setVisible(false);

                //Habilitamos los botones bloqueados con la edicion
                editStationButton.setVisible(true);
                addStationButton.setVisible(true);
                maintenanceButton.setVisible(true);
                backButton.setVisible(true);
                StationsFilterPanel.getInstance().enableSearch();

                mainFrame.pack();
            }
        });

        //Se cancela la edicion de la fila y se restablecen los valores anteriores
        cancelEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                //Finalizamos la edicion de la fila y almacenamos los datos
                stationsTable.clearSelection();
                stationsTable.restoreRow();
                stationsTable.finishRowEdit();

                //Ocultamos los botones de edicion
                finishEditButton.setVisible(false);
                cancelEditButton.setVisible(false);
                deleteStationButton.setVisible(false);

                //Habilitamos los botones bloqueados con la edicion
                editStationButton.setVisible(true);
                addStationButton.setVisible(true);
                maintenanceButton.setVisible(true);
                backButton.setVisible(true);
                StationsFilterPanel.getInstance().enableSearch();

                mainFrame.pack();
            }
        });

        //Se elimina la estacion seleccionada del sistema
        deleteStationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                /* AÑADIDO DESPUES DE LA ENTREGA */
                stationsTable.stopEditingCell();
                /* AÑADIDO DESPUES DE LA ENTREGA 
                   Si se iniciaba la edicion de una celda y se eliminaba la fila, la celda continuaba en edicion y no permitia salir de ella
                 */

                int confirm = JOptionPane.showConfirmDialog(null,"Desea eliminar la estación "+stationsTable.getIdentifier()+"?","Eliminar estación",JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    Runnable t = () -> {
                        DBConnection.establishConnection();
                        PreparedStatement pstm = null;
                        int ps=0;
                        try { 
                            pstm = DBConnection.getConnection().prepareStatement("DELETE FROM STATION WHERE id="+stationsTable.getIdentifier());
                            ps = pstm.executeUpdate();
                            Graph.getInstance().removeStation(stationsTable.getIdentifier());
                            stationsTable.refreshData();
                            
                        } 
                        catch (SQLException e) {
                            JOptionPane.showMessageDialog(null,"Error. La estacion no puede ser eliminada","Eliminar estación",JOptionPane.OK_OPTION);
                            e.printStackTrace();
                            return;
                        }
                        finally {
                            DBConnection.closeConnection(pstm, ps);
                        }
                    };
                    new Thread(t,"DB delete thread").start();
                }

                //Finalizamos la edicion de la fila y almacenamos los datos
                stationsTable.clearSelection();
                stationsTable.finishRowEdit();

                //Ocultamos los botones de edicion
                finishEditButton.setVisible(false);
                cancelEditButton.setVisible(false);
                deleteStationButton.setVisible(false);

                //Habilitamos los botones bloqueados con la edicion
                editStationButton.setVisible(true);
                addStationButton.setVisible(true);
                maintenanceButton.setVisible(true);
                backButton.setVisible(true);
                StationsFilterPanel.getInstance().enableSearch();
            }
        });

        maintenanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                //Habilitamos edicion para la fila seleccionada, por default sera la primer fila
                if (stationsTable.getSelectedRow()!=-1) {
                    //Se bloquea el frame principal
                    mainFrame.setEnabled(false);

                    //Se crea un popUp donde se permitira seleccionar añadir o finalizar mantenimientos segun el estado de la estacion
                    JDialog popUp = WindowManager.getPopUpWindow("Mantenimiento");
                    JPanel maintenancePanel = new JPanel();
                    maintenancePanel.setLayout(new GridBagLayout());
                    GridBagConstraints cts = new GridBagConstraints();
                    
                    //Creamos los botones
                    MyButton addMaintenanceButton = new MyButton("Iniciar mantenimiento", 14);
                    MyButton finishMaintenanceButton = new MyButton("Finalizar mantenimiento", 14);

                    //Realizamos los listeners de cada boton
                    addMaintenanceButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            //Hilo que se encarga de cargar el mantenimiento en la base de datos
                            stationsTable.editMaintenanceTable();
                            Runnable t1 = () -> {
                                DBConnection.establishConnection();
                                int a=0, b=0;
                                PreparedStatement pstmInsert = null;
                                PreparedStatement pstmUpdate = null;
                                try {
                                    pstmInsert = DBConnection.getConnection().prepareStatement("INSERT INTO MAINTENANCE VALUES (?,?)");
                                    pstmInsert.setInt(1, stationsTable.getIdentifier());
                                    pstmInsert.setDate(2, Date.valueOf(LocalDate.now()));
                                    a = pstmInsert.executeUpdate();

                                    pstmUpdate = DBConnection.getConnection().prepareStatement("UPDATE STATION SET operative=? WHERE id="+stationsTable.getIdentifier());
                                    pstmUpdate.setBoolean(1, false);
                                    b = pstmUpdate.executeUpdate();

                                    //Seteamos los valores en la tabla
                                    stationsTable.setValueAt(false, stationsTable.getLastEditRow(), 4);
                                    stationsTable.finishRowEdit();

                                    //Actualizamos el estado de la estacion
                                    Station aStation = Graph.getInstance().getStation(stationsTable.getIdentifier());
                                    aStation.setStatus(false);

                                } catch (SQLException e) {
                                    if (e.getSQLState().equals("23505")) {
                                        JOptionPane.showMessageDialog(null,"Error. Ya existe registrado un mantenimiento con esta fecha de inicio","Iniciar mantenimiento",JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    e.printStackTrace();
                                } finally {
                                    DBConnection.closeConnection(pstmInsert, a);
                                    DBConnection.closeConnection(pstmUpdate, b);

                                }
                            };
                            new Thread(t1,"Thread DBTable").start();

                            popUp.setVisible(false);
                            popUp.dispose();
                            mainFrame.setEnabled(true);
                            mainFrame.setVisible(true);
                        }
                    });

                    finishMaintenanceButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            stationsTable.editMaintenanceTable();
                            String comment = JOptionPane.showInputDialog(null,"¿Desea ingresar un comentario?", "Comentario",JOptionPane.QUESTION_MESSAGE);
                            
                            while (comment!=null && comment.length()>200) {
                                comment = (String)JOptionPane.showInputDialog(null,"El comentario posee mas de 200 caracteres", "Comentario",JOptionPane.QUESTION_MESSAGE, null,null, comment);
                            }
                            if (comment == null) {
                                comment = "";
                            }
                            final String finalComment=comment;
                            Runnable t1 = () -> {
                                DBConnection.establishConnection();
                                int a=0, b=0;
                                PreparedStatement pstmUpdateMaintenance = null;
                                PreparedStatement pstmUpdateStation = null;
                                try {
                                    pstmUpdateMaintenance = DBConnection.getConnection().prepareStatement("UPDATE MAINTENANCE SET end_date=?, comment=? WHERE id="+stationsTable.getIdentifier()+
                                                                                                                             " AND end_date IS NULL");
                                    pstmUpdateMaintenance.setDate(1, Date.valueOf(LocalDate.now()));
                                    pstmUpdateMaintenance.setString(2, finalComment);
                                    a = pstmUpdateMaintenance.executeUpdate();

                                    pstmUpdateStation = DBConnection.getConnection().prepareStatement("UPDATE STATION SET operative=? WHERE id="+stationsTable.getIdentifier());
                                    pstmUpdateStation.setBoolean(1, true);
                                    a = pstmUpdateStation.executeUpdate();

                                    //Seteamos los valores en la tabla
                                    stationsTable.setValueAt(true, stationsTable.getLastEditRow(), 4);
                                    stationsTable.finishRowEdit();

                                    //Actualizamos el estado de la estacion
                                    Station aStation = Graph.getInstance().getStation(stationsTable.getIdentifier());
                                    aStation.setStatus(true);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } finally {
                                    DBConnection.closeConnection(pstmUpdateMaintenance, a);
                                    DBConnection.closeConnection(pstmUpdateStation, b);
                                }
                            };
                            new Thread(t1,"Thread DBTable").start();

                            popUp.setVisible(false);
                            popUp.dispose();
                            mainFrame.setEnabled(true);
                            mainFrame.setVisible(true);
                        }
                    });

                    //Añadimos los botones al panel
                    cts.fill=GridBagConstraints.HORIZONTAL;
                    cts.insets=new Insets(15,15,15,15);
                    cts.gridx=0;
                    cts.gridy=0;
                    maintenancePanel.add(addMaintenanceButton,cts);

                    cts.gridy=2;
                    maintenancePanel.add(finishMaintenanceButton,cts);

                    //Obtenemos la ultima fila seleccionada y el estado de la misma. Se habilitara un boton u otro segun el estado actual
                    if ((Boolean)stationsTable.getValueAt(stationsTable.getSelectedRow(), 4)) {
                        finishMaintenanceButton.setEnabled(false);
                    }
                    else {
                        addMaintenanceButton.setEnabled(false);
                    }

                    popUp.setContentPane(maintenancePanel);
                    popUp.pack();
                    popUp.setVisible(true);

                }
                else {
                    JOptionPane.showMessageDialog(null,"Error. No se selecciono ninguna fila para editar","Edición de datos",JOptionPane.ERROR_MESSAGE);
                }    
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Realizamos este nuevo thread para que se resetee la tabla y los filtros al volver al menu principal
                Runnable r1 = () -> {
                    StationsFilterPanel.getInstance().resetFilters();
                    stationsTable.refreshData();
                };
                new Thread(r1,"Reset Table").start();

                mainFrame.setContentPane(FirstMenu.getInstance());
                mainFrame.pack();
                mainFrame.setSize(800,600);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            }
        });

        finishEditButton.setVisible(false);
        cancelEditButton.setVisible(false);
        deleteStationButton.setVisible(false);
        
        //Se añaden los botones al stationsButtonPanel
        GridBagConstraints cts = new GridBagConstraints();
        cts.insets=new Insets(10,10,10,10);
        cts.fill=GridBagConstraints.HORIZONTAL;
        cts.gridx=0;
        cts.gridy=0;

        this.add(addStationButton,cts);

        cts.gridx=1;

        this.add(editStationButton, cts);

        cts.gridx=1;
        this.add(finishEditButton,cts);
        finishEditButton.setVisible(false);            
        
        cts.gridx=2;
        this.add(cancelEditButton,cts);
        cancelEditButton.setVisible(false);

        cts.gridx=3;
        this.add(deleteStationButton,cts);
        deleteStationButton.setVisible(false);

        cts.anchor=GridBagConstraints.EAST;
        cts.gridx=4;

        this.add(maintenanceButton,cts);

        cts.gridx=5;
        this.add(backButton, cts);
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
