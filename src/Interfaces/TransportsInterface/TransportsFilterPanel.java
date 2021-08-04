package Interfaces.TransportsInterface;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import DataBase.DBConnection;
import Interfaces.Accessories.ColourComboBox;
import Interfaces.Accessories.MyButton;
import Interfaces.Tables.TransportsTable;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransportsFilterPanel extends JPanel {
    private static TransportsFilterPanel transportsFilterPanelInstance;
    
    public static TransportsFilterPanel getInstance() {
        if(transportsFilterPanelInstance==null) {
            transportsFilterPanelInstance = new TransportsFilterPanel();
        }
        return transportsFilterPanelInstance;
    }

    public TransportsFilterPanel() {
        super();
        this.setLayout(new GridBagLayout());
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(Color.LIGHT_GRAY);
        
        TransportsTable transportsTable = TransportsTable.getInstance();

        //Creamos los labels y los textField
        GridBagConstraints cts = new GridBagConstraints();

        //Creamos y añadimos los labels
        cts.anchor=GridBagConstraints.NORTHWEST;
        cts.insets=new Insets(10,5,10,5);
        cts.weightx=0.5;
        cts.weighty=0.5;
        cts.gridx=0;
        cts.gridy=0;

        this.add(new JLabel("Filtros:"), cts);
        
        cts.gridx=0;
        cts.gridy=1;
        this.add(new JLabel("Id: "), cts);

        cts.gridy=2;
        this.add(new JLabel("Nombre: "), cts);

        cts.gridy=3;
        this.add(new JLabel("Color"),cts);

        cts.gridy=4;
        this.add(new JLabel("Estado"),cts);

        //Creamos los text Fields
        //id Field
        JTextField idField = new JTextField();
        //Este metodo permite que solo se puedan escribir valores enteros
        idField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char car = e.getKeyChar();
                if((car<'0' || car>'9')) e.consume();
            }
        });
        
        //name field
        JTextField nameField = new JTextField(10);

        //Color combobox
        ColourComboBox colourComboBox = new ColourComboBox();

        //Status dropList
        JComboBox<String> statusComboBox = new JComboBox<String>();
        statusComboBox.addItem("");
        statusComboBox.addItem("Activa");
        statusComboBox.addItem("No activa");
        
        //Añadimos los campos al panel
        
        cts.fill=GridBagConstraints.HORIZONTAL;
        cts.gridx=2;
        cts.gridy=1;
        this.add(idField,cts); 

        cts.gridy=2;
        this.add(nameField, cts);
        
        cts.gridwidth=1;
        cts.gridy=3;
        this.add(colourComboBox,cts);
        
        cts.gridy=4;
        this.add(statusComboBox,cts);

        //Creamos un boton de busqueda
        MyButton searchButton = new MyButton("Buscar", 14);

        //Añadimos el boton
        cts.gridx=0;
        cts.gridy=5;
        this.add(searchButton,cts);

        //Añadimos label de busqueda
        cts.gridx=2;
        JLabel searchingLabel = new JLabel("Buscando.."); 
        this.add(searchingLabel, cts);
        searchingLabel.setVisible(false);

        //Añadimos listener de boton de busqueda
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Deshabilitamos todos los botones y la seleccion de la tabla
                searchButton.setEnabled(false);
                searchingLabel.setVisible(true);
                TransportsButtonPanel.getInstance().disableButtons();
                TransportsTable.getInstance().setRowSelectionAllowed(false);

                //Busqueda en base de datos
                Runnable t1 = () -> {
                    //Armamos la consulta segun los parametros que se ingresen en los campos de texto
                    String consult = "SELECT * FROM TRANSPORT";
                    String where = " WHERE ";
                    int quantityFilters = 0;
                    if (!idField.getText().isEmpty()) {
                        where = where.concat("id="+idField.getText());
                        quantityFilters++;
                    }
                    if (!nameField.getText().isEmpty()) {
                        if (quantityFilters>0) {
                            where = where.concat(" AND ");
                        }
                        //Esta funcion permite que se busquen similares a pesar de que se presenten mayusculas, minusculas o acentos
                        where = where.concat("unaccent(lower(name)) LIKE '"+nameField.getText().toLowerCase()+"%'");
                        quantityFilters++;
                    }

                    if (!((String)colourComboBox.getSelectedItem()).equals("Seleccione un color")) {
                        if (quantityFilters>0) {
                            where = where.concat(" AND ");
                        }
                        where = where.concat("colour='"+colourComboBox.getSelectedItem()+"'");
                        quantityFilters++;
                    }

                    if (!((String)statusComboBox.getSelectedItem()).isEmpty()) {
                        if (quantityFilters>0) {
                            where = where.concat(" AND ");
                        }
                        if (((String)statusComboBox.getSelectedItem()).equals("Activa")) {
                            where = where.concat("status=true");
                            quantityFilters++;
                        }
                        else {
                            where = where.concat("status=false");
                            quantityFilters++;
                        }  
                    }
                    if (quantityFilters>0) {
                        consult = consult.concat(where);
                    }
                    consult = consult.concat(" ORDER BY id");

                    ResultSet rs =null;
                    DBConnection.establishConnection();
                    PreparedStatement pstm = null;
                    try {
                        System.out.println(consult);
                        pstm = DBConnection.getConnection().prepareStatement(consult);
                        rs = pstm.executeQuery();
                        Object [] data = new Object[4];
                        //Limpiamos la tabla
                        transportsTable.clearTable();
                        while (rs.next()) { 
                            try {
                                data[0] = rs.getInt(1);
                                data[1] = rs.getString(2);
                                data[2] = rs.getString(3);
                                data[3] = rs.getBoolean(4);
                                transportsTable.addRow(data);
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    } finally {
                        DBConnection.closeConnection(pstm, rs);
                        searchButton.setEnabled(true);
                        searchingLabel.setVisible(false);
                        TransportsButtonPanel.getInstance().enableButtons();
                        TransportsTable.getInstance().setRowSelectionAllowed(true);
                    }
                };

                Thread dbThread = new Thread(t1,"DBThread");
                dbThread.start();
            }
        });
    }

    public void blockSearch() {
        for (java.awt.Component component : this.getComponents()) {
            if (component instanceof MyButton) {
                component.setEnabled(false);
            }
        }
    }

    public void enableSearch() {
        for (java.awt.Component component : this.getComponents()) {
            if (component instanceof MyButton) {
                component.setEnabled(true);
            }
        }
    }

    public void resetFilters() {
        for (java.awt.Component component : this.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            }
            if (component instanceof JComboBox) {
                ((JComboBox)component).setSelectedIndex(0);
            }
        }
    }
}
