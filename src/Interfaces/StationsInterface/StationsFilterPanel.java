package Interfaces.StationsInterface;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import DataBase.DBConnection;
import Graph.Graph;
import Interfaces.Accessories.MyButton;
import Interfaces.Tables.StationsTable;

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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class StationsFilterPanel extends JPanel {
    private static StationsFilterPanel stationsFilterPanelInstance;
    
    public static StationsFilterPanel getInstance() {
        if(stationsFilterPanelInstance==null) {
            stationsFilterPanelInstance = new StationsFilterPanel();
        }
        return stationsFilterPanelInstance;
    }

    public StationsFilterPanel() {
        super();
        this.setLayout(new GridBagLayout());
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(Color.LIGHT_GRAY);

        StationsTable table = StationsTable.getInstance();

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
        this.add(new JLabel("Hora apertura"),cts);

        cts.gridy=4;
        this.add(new JLabel("Hora cierre"),cts);

        cts.gridy=5;
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

        //Opening field
        JTextField openingField = new JTextField(10);
        openingField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char car = e.getKeyChar();
                if((car<'0' || car>'9' || car==':')) e.consume();
            }
        });

        //Closing field
        JTextField closingField = new JTextField(10);
        closingField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char car = e.getKeyChar();
                if((car<'0' || car>'9' || car==':')) e.consume();
            }
        });

        //Status dropList
        JComboBox<String> statusComboBox = new JComboBox<String>();
        statusComboBox.addItem("");
        statusComboBox.addItem("Operativa");
        statusComboBox.addItem("En mantenimiento");
        
        //Añadimos los campos al panel
        
        cts.fill=GridBagConstraints.HORIZONTAL;
        cts.gridx=2;
        cts.gridy=1;
        this.add(idField,cts); 

        cts.gridy=2;
        this.add(nameField, cts);
        
        cts.gridwidth=1;
        cts.gridy=3;
        this.add(openingField,cts);
        
        cts.gridy=4;
        this.add(closingField,cts);

        cts.gridy=5;
        this.add(statusComboBox,cts);

        //Creamos un boton de busqueda y un boton de filtro por importancia (page rank)
        MyButton searchButton = new MyButton("Buscar", 14);
        MyButton priorityStationButton = new MyButton("Importancia",14);
        MyButton priorityMaintenanceButton = new MyButton("Proximo mantenimiento",14);

        //Añadimos los botones
        cts.insets=new Insets(5,5,5,5);
        cts.gridx=0;
        cts.gridy=6;
        this.add(searchButton,cts);

        cts.gridy=7;
        this.add(new JLabel("Ordenar por:"),cts);

        cts.gridy=8;
        this.add(priorityStationButton,cts);

        cts.gridx=2;
        this.add(priorityMaintenanceButton,cts);

        //Añadimos label de busqueda
        cts.gridx=2;
        JLabel searchingLabel = new JLabel("Buscando.."); 
        this.add(searchingLabel, cts);
        searchingLabel.setVisible(false);

        //Añadimos listener de boton de busqueda
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Deshabilitamos todos los botones y la seleccion de la tabla
                searchingLabel.setVisible(true);
                searchButton.setEnabled(false);
                StationsButtonPanel.getInstance().disableButtons();
                StationsTable.getInstance().setRowSelectionAllowed(false);

                //Busqueda en base de datos
                Runnable t1 = () -> {
                    //Armamos la consulta segun los parametros que se ingresen en los campos de texto
                    String consult = "SELECT * FROM STATION";
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
                    if (!openingField.getText().isEmpty()) {
                        if (quantityFilters>0) {
                            where = where.concat(" AND ");
                        }
                        where = where.concat("opening LIKE '"+openingField.getText()+"%'");
                        quantityFilters++;
                    }
                    if (!closingField.getText().isEmpty()) {
                        if (quantityFilters>0) {
                            where = where.concat(" AND ");
                        }
                        where = where.concat("closing LIKE '"+closingField.getText()+"%'");
                        quantityFilters++;
                    }

                    if (!((String)statusComboBox.getSelectedItem()).isEmpty()) {
                        if (quantityFilters>0) {
                            where = where.concat(" AND ");
                        }
                        if (((String)statusComboBox.getSelectedItem()).equals("Operativa")) {
                            where = where.concat("operative=true");
                            quantityFilters++;
                        }
                        else {
                            where = where.concat("operative=false");
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
                        Object [] data = new Object[5];
                        //Limpiamos la tabla
                        table.clearTable();
                        while (rs.next()) { 
                            data[0] = rs.getInt(1);
                            data[1] = rs.getString(2);
                            data[2] = rs.getString(3);
                            data[3] = rs.getString(4);
                            data[4] = rs.getBoolean(5);
                            table.addRow(data);
                        }
                    } 
                    catch (SQLException exception) {
                        exception.printStackTrace();
                    } 
                    finally {
                        DBConnection.closeConnection(pstm, rs);
                        searchButton.setEnabled(true);
                        searchingLabel.setVisible(false);
                        StationsButtonPanel.getInstance().enableButtons();
                        StationsTable.getInstance().setRowSelectionAllowed(true);
                    }
                };

                Thread dbThread = new Thread(t1,"DBThread");
                dbThread.start();
            }
        });
    
        priorityStationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //Se vacia la tabla
                table.clearTable();

                //Se obtiene la instancia del grafo
                Graph graphInstance = Graph.getInstance();

                //Se consulta si existen estaciones en el grafo. En caso de que no existan detiene la ejecucion del metodo
                if (graphInstance.getAllStations().isEmpty()) {
                    return;
                }
                HashMap<Integer,Double> pageRank = graphInstance.calculatePageRank();

                //Se ordena el hashMap de pageRank
                List<Map.Entry<Integer,Double>> sortedPageRank = pageRank.entrySet().stream()
                                                                                    .sorted(Map.Entry.comparingByValue((d1,d2)->d2.compareTo(d1)))
                                                                                    .collect(Collectors.toList());
                //Se actualiza la tabla con los datos obtenidos
                for (Map.Entry<Integer,Double> aStationPair : sortedPageRank) {
                    table.addStation(graphInstance.getStation(aStationPair.getKey()));
                }
                
            }
        });
    
        priorityMaintenanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Se vacia la tabla
                table.clearTable();

                //Se obtiene la instancia del grafo
                Graph graphInstance = Graph.getInstance();

                //Se consulta a la base de datos sobre el ultimo mantenimiento de cada estacion
                Runnable r1 = () -> {
                    ResultSet rsNotMaintenance = null;
                    ResultSet rsCurrentMaintenance = null;
                    DBConnection.establishConnection();
                    PreparedStatement pstmNotMaintenance = null;
                    PreparedStatement pstmCurrentMaintenance = null;
                    try {
                        pstmNotMaintenance = DBConnection.getConnection().prepareStatement("SELECT M.id, MAX(end_date) as last_maintenance "+
                                                                             "FROM maintenance M "+
                                                                             "WHERE M.id NOT IN (SELECT M2.id "+
                                                                                                "FROM maintenance M2 "+
                                                                                                "WHERE end_date IS NULL) "+
                                                                             "GROUP BY M.id");
                        rsNotMaintenance = pstmNotMaintenance.executeQuery();

                        //Se crea un hashmap que almacena cada estacion con su ultimo mantenimiento
                        HashMap<Integer,LocalDate> lastMaintenance = new HashMap<Integer,LocalDate>();
                        while (rsNotMaintenance.next()) { 
                            lastMaintenance.put(rsNotMaintenance.getInt(1), rsNotMaintenance.getDate(2).toLocalDate());
                        }

                        //Se agregan las estaciones a la tabla en el orden representado por la priorityQueue
                        PriorityQueue<Map.Entry<Integer,LocalDate>> priorityPair = graphInstance.nextMaintenance(lastMaintenance);
                        while (!priorityPair.isEmpty()) {
                            table.addStation(graphInstance.getStation(priorityPair.poll().getKey()));
                        }

                        //Consultamos por las estaciones que se encuentran en mantenimiento actualmente para agregarlas al final de la tabla
                        pstmCurrentMaintenance = DBConnection.getConnection().prepareStatement("SELECT M2.id "+
                                                                                               "FROM maintenance M2 "+
                                                                                               "WHERE end_date IS NULL "+
                                                                                               "GROUP BY M2.id");
                        rsCurrentMaintenance = pstmCurrentMaintenance.executeQuery();
                        //Agregamos las estaciones con un mantenimiento en curso a la tabla
                        while (rsCurrentMaintenance.next()) {
                            table.addStation(graphInstance.getStation(rsCurrentMaintenance.getInt(1)));
                        }

                    }
                    catch (SQLException exception) {
                            exception.printStackTrace();
                    }
                    finally {
                        DBConnection.closeConnection(pstmNotMaintenance, rsNotMaintenance);
                        DBConnection.closeConnection(pstmCurrentMaintenance, rsCurrentMaintenance);
                        searchButton.setEnabled(true);
                        searchingLabel.setVisible(false);
                        StationsButtonPanel.getInstance().enableButtons();
                        StationsTable.getInstance().setRowSelectionAllowed(true);
                    }
                };
                new Thread(r1,"nextMaintenance Thread").start();
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
