package Interfaces.StationsInterface;

import Interfaces.Tables.StationsTable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;

public class StationsPanel extends JPanel {
    private static StationsPanel stationsPanelInstance;
    
    public static StationsPanel getInstance() {
        if(stationsPanelInstance==null) {
            stationsPanelInstance = new StationsPanel();
        }
        return stationsPanelInstance;
    }

    public StationsPanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setForeground(Color.DARK_GRAY);

        // Creamos la tabla y el scroll
        StationsTable stationsTable = StationsTable.getInstance();
        JScrollPane scrollPane = new JScrollPane(stationsTable);
        scrollPane.setSize(50,50);

        // Creamos el panel de botones
        StationsButtonPanel buttonPanel = StationsButtonPanel.getInstance();

        //Creamos el panel de filtrado

        StationsFilterPanel filterPanel = StationsFilterPanel.getInstance();
        //Añadimos y acomodamos los paneles
        
        this.add(filterPanel,BorderLayout.WEST);
        this.add(scrollPane,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.SOUTH);
        this.setVisible(true);
    }
}
