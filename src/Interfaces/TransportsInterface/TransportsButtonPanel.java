package Interfaces.TransportsInterface;

import Interfaces.FirstMenu;
import Interfaces.WindowManager;
import Interfaces.Accessories.ColourComboBox;
import Interfaces.Accessories.MyButton;
import Interfaces.Tables.TransportsTable;
import DataBase.DBConnection;
import Graph.Transport;
import Graph.Graph;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransportsButtonPanel extends JPanel {

    private static TransportsButtonPanel ButtonPanelInstance;

    public static TransportsButtonPanel getInstance() {
        if(ButtonPanelInstance==null) {
            ButtonPanelInstance = new TransportsButtonPanel();
        }
        return ButtonPanelInstance;
    }

    public TransportsButtonPanel() {
        
        JFrame mainFrame = WindowManager.getMainWindow();
        TransportsTable transportsTable = TransportsTable.getInstance();

        this.setLayout(new GridBagLayout());
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(Color.LIGHT_GRAY);

        MyButton addTransportButton = new MyButton("Añadir transporte", 14);
        MyButton editTransportButton = new MyButton("Editar transporte", 14);
        MyButton finishEditButton = new MyButton("Finalizar Edicion", 14);
        MyButton cancelEditButton = new MyButton("Cancelar Edicion", 14);
        MyButton deleteTransportButton = new MyButton("Eliminar transporte", 14);
        MyButton backButton = new MyButton("Volver", 14);

        //Creacion de Listeners

        //Listener del boton añadir
        addTransportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Se bloquea el frame principal
                mainFrame.setEnabled(false);
                
                //Se crea un popUp donde se permitira ingresar los datos de la nueva linea de transporte
                JDialog popUp = WindowManager.getPopUpWindow("Ingrese los datos de la linea de transporte");
                JPanel addTransportPanel = new JPanel();
                addTransportPanel.setLayout(new GridBagLayout());
                GridBagConstraints cts = new GridBagConstraints();

                //Creamos y añadimos los labels al panel
                cts.weightx=1.0;
                cts.weighty=1.0;
                cts.anchor=GridBagConstraints.WEST;
                cts.insets=new Insets(10,10,5,10);
                cts.gridx=0;
                cts.gridy=0;
                addTransportPanel.add(new JLabel("Ingrese el nombre del transporte"), cts);

                cts.gridy=1;
                addTransportPanel.add(new JLabel("Seleccione el color del transporte"),cts);
                
                //Creamos el textField de nombre y el ComboBox de colores
                JTextField transportNameField = new JTextField(20);
                ColourComboBox colourComboBox = new ColourComboBox();

                //Añadimos el textfield y el combobox
                cts.insets=new Insets(10,5,10,10);
                cts.fill=GridBagConstraints.HORIZONTAL; 
                cts.gridx=2;
                cts.gridwidth=1;
                cts.gridy=0;
                addTransportPanel.add(transportNameField, cts);

                cts.gridy=1;
                addTransportPanel.add(colourComboBox, cts);

                //Input verifier para los campos de texto
                transportNameField.setInputVerifier(new InputVerifier() {
                    public boolean verify(JComponent input) {
                        JTextField tf = (JTextField) input;
                        String nameString = tf.getText();
                        // verificamos si la cadena es correcta
                        if (nameString.length()>20) {
                            JOptionPane.showMessageDialog(null,"Error. El nombre indicado tiene mas de 20 caracteres","Ingrese los datos de la nueva linea de transporte",JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        else {
                            return true;
                        }
                    }
                });

                //Añadimos boton de confirmacion
                MyButton okButton = new MyButton("Añadir transporte", 14); 
                cts.gridy=2;
                cts.gridx=2;
                cts.anchor=GridBagConstraints.CENTER;
                cts.fill=GridBagConstraints.HORIZONTAL;
                addTransportPanel.add(okButton,cts);
                
                popUp.setContentPane(addTransportPanel);
                popUp.pack();
                popUp.setVisible(true);

                //Action listener del boton de confirmacion
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        System.out.println(colourComboBox.getSelectedItem());
                        //Si no se ingreso ningun dato se indica un error que solicita el ingreso de los datos
                        if (transportNameField.getText().isEmpty() || colourComboBox.getSelectedItem().equals("Seleccione un color")) {
                            JOptionPane.showMessageDialog(null,"Error. No se ingresaron los datos de la linea de transporte a agregar","Ingrese los datos de la nueva linea de transporte",JOptionPane.ERROR_MESSAGE);
                        }
                        //Se almacena la nueva linea de transporte en la base de datos, se agrega al grafo y se actualiza la tabla
                        else {
                            Transport aTransport=new Transport(transportNameField.getText(),(String)colourComboBox.getSelectedItem());
                            Runnable tRunnable = () -> {
                                //Se añade la nueva linea de transporte a la base de datos
                                DBConnection.establishConnection();
                                PreparedStatement pstm = null;
                                int ps=0;
                                try { 
                                    pstm = DBConnection.getConnection().prepareStatement("INSERT INTO TRANSPORT VALUES (?,?,?,?)");
                                    pstm.setInt(1, aTransport.getId());
                                    pstm.setString(2, aTransport.getName());
                                    pstm.setString(3, aTransport.getColourString());
                                    pstm.setBoolean(4, aTransport.getStatus());
                                    ps = pstm.executeUpdate();

                                    //Se añade la nueva linea de transporte a la lista de transportes del grafo
                                    Graph.getInstance().addTransport(aTransport);
                                    transportsTable.addTransport(aTransport);
                                } catch (SQLException e){
                                    e.printStackTrace();
                                    System.out.println("ERROR SQL IN addTransportButton:" + e.getSQLState());
                                } 
                                finally {
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
            }
        });

        editTransportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Habilitamos edicion para la fila seleccionada, por default sera la primer fila
                if (transportsTable.editTable()) {
                    //Bloqueamos los botones de añadir, editar y volver.
                    editTransportButton.setVisible(false);
                    addTransportButton.setVisible(false);
                    backButton.setVisible(false);
                    TransportsFilterPanel.getInstance().blockSearch();

                    //Seteamos como visibles los botones de edicion
                    finishEditButton.setVisible(true);
                    cancelEditButton.setVisible(true);
                    deleteTransportButton.setVisible(true);  
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
                int identifyObject=transportsTable.getIdentifier();
                //Obtenemos la ultima fila editada
                int row = transportsTable.getLastEditRow();
                //Fijamos los cambios en la tabla retirando el foco de los campos editables
                transportsTable.stopEditingCell();

                //Verificamos si algun valor se fijo en vacio, en caso de hacerlo se retorna a su valor anterior
                for (int i = 0;i<transportsTable.getColumnCount();i++) {
                    if (transportsTable.getValueAt(row, i)==null) {
                        transportsTable.restoreCell(row,i);
                    }
                    else if (transportsTable.getValueAt(row, i) instanceof String && ((String)transportsTable.getValueAt(row, i)).isEmpty()) {
                        transportsTable.restoreCell(row,i);
                    }
                }

                //Verificamos que se haya seleccionado un color
                if (transportsTable.getValueAt(row, 2).equals("Seleccione un color")) {
                    JOptionPane.showMessageDialog(null,"Error. Seleccione un color valido","Edición de datos",JOptionPane.ERROR_MESSAGE);
                    return;
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
                    pstm = DBConnection.getConnection().prepareStatement("UPDATE TRANSPORT SET name=?, colour=?, status=? WHERE id="+identifyObject);
                    pstm.setString(1, (String) transportsTable.getValueAt(row, 1));
                    pstm.setString(2, (String) transportsTable.getValueAt(row, 2));
                    pstm.setBoolean(3, (Boolean) transportsTable.getValueAt(row, 3));
                    update = pstm.executeUpdate();
                } catch (SQLException e) {
                    if (e.getSQLState().equals("22001")){
                        JOptionPane.showMessageDialog(null,"Error. El nombre indicado tiene mas de 20 caracteres","Ingrese los datos de la nueva estación",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    e.printStackTrace();
                    System.out.println("ERROR SQL IN addTransportButton:" + e.getSQLState());
                    System.out.println("ERROR SQL IN addTransportButton:" + e.getSQLState());
                    return;   
                } 
                finally {
                    DBConnection.closeConnection(pstm, update);
                }
                
                //Una vez que la base de datos acepta los datos actualizados deshabilitamos la edicion de la fila seleccionada
                transportsTable.finishRowEdit();

                /*Buscamos la estacion actualizada en la lista del grafo para actualizarle sus atributos. 
                 *El proceso solo se realiza si se realizo algun cambio en la base de datos,
                 *ya que las estaciones estaran creadas a partir de la misma.
                 */

                if (update>0) {
                    Transport aTransportUpdated = Graph.getInstance().getTransport(identifyObject);
                    aTransportUpdated.setName((String)transportsTable.getValueAt(row, 1));
                    aTransportUpdated.setColour((String)transportsTable.getValueAt(row, 2));
                    aTransportUpdated.setStatus((Boolean)transportsTable.getValueAt(row, 3));
                }

                //Ocultamos los botones de edicion
                finishEditButton.setVisible(false);
                cancelEditButton.setVisible(false);
                deleteTransportButton.setVisible(false);

                //Habilitamos los botones bloqueados con la edicion
                editTransportButton.setVisible(true);
                addTransportButton.setVisible(true);
                backButton.setVisible(true);
                TransportsFilterPanel.getInstance().enableSearch();

                mainFrame.pack();
            }
        });

        //Se cancela la edicion de la fila y se restablecen los valores anteriores
        cancelEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                //Finalizamos la edicion de la fila y almacenamos los datos
                TransportsTable table = TransportsTable.getInstance();
                table.clearSelection();
                table.restoreRow();
                table.finishRowEdit();

                //Ocultamos los botones de edicion
                finishEditButton.setVisible(false);
                cancelEditButton.setVisible(false);
                deleteTransportButton.setVisible(false);

                //Habilitamos los botones bloqueados con la edicion
                editTransportButton.setVisible(true);
                addTransportButton.setVisible(true);
                backButton.setVisible(true);
                TransportsFilterPanel.getInstance().enableSearch();

                mainFrame.pack();
            }
        });

         //Se elimina la linea de transporte seleccionada del sistema
         deleteTransportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                /* AÑADIDO DESPUES DE LA ENTREGA */
                transportsTable.stopEditingCell();
                /* AÑADIDO DESPUES DE LA ENTREGA 
                   Si se iniciaba la edicion de una celda y se eliminaba la fila, la celda continuaba en edicion y no permitia salir de ella
                 */

                int confirm = JOptionPane.showConfirmDialog(null,"Desea eliminar la linea de transporte "+transportsTable.getIdentifier()+"?","Eliminar linea de transporte",JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    Runnable t = () -> {
                        DBConnection.establishConnection();
                        PreparedStatement pstm = null;
                        int ps=0;
                        try { 
                            pstm = DBConnection.getConnection().prepareStatement("DELETE FROM TRANSPORT WHERE id="+transportsTable.getIdentifier());
                            ps = pstm.executeUpdate();
                            Graph.getInstance().removeTransport(transportsTable.getIdentifier());
                            transportsTable.refreshData();
                        } 
                        catch (SQLException e) {
                            JOptionPane.showMessageDialog(null,"Error. La linea de transporte no puede ser eliminada","Eliminar linea de transporte",JOptionPane.OK_OPTION);
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
                transportsTable.clearSelection();
                transportsTable.finishRowEdit();

                //Ocultamos los botones de edicion
                finishEditButton.setVisible(false);
                cancelEditButton.setVisible(false);
                deleteTransportButton.setVisible(false);

                //Habilitamos los botones bloqueados con la edicion
                editTransportButton.setVisible(true);
                addTransportButton.setVisible(true);
                backButton.setVisible(true);
                TransportsFilterPanel.getInstance().enableSearch();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //Realizamos este nuevo thread para que se resetee la tabla y los filtros al volver al menu principal
                Runnable r1 = () -> {
                    TransportsFilterPanel.getInstance().resetFilters();
                    transportsTable.refreshData();
                };
                new Thread(r1,"Reset Table").start();
                
                mainFrame.setContentPane(FirstMenu.getInstance());
                mainFrame.pack();
                mainFrame.setSize(800,600);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            }
        });
        
        //Se añaden los botones al stationsButtonPanel
        GridBagConstraints cts = new GridBagConstraints();
        cts.insets=new Insets(10,10,10,10);
        cts.fill=GridBagConstraints.HORIZONTAL;
        cts.gridx=0;
        cts.gridy=0;

        this.add(addTransportButton,cts);

        cts.gridx=1;

        this.add(editTransportButton, cts);

        cts.gridx=1;
        this.add(finishEditButton,cts);
        finishEditButton.setVisible(false); 
                    
        cts.gridx=2;
        this.add(cancelEditButton,cts);
        cancelEditButton.setVisible(false);

        cts.gridx=3;
        this.add(deleteTransportButton,cts);
        deleteTransportButton.setVisible(false);

        cts.anchor=GridBagConstraints.EAST;

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
