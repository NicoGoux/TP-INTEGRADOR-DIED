package Interfaces.RoutesInterface;

import Interfaces.Tables.RoutesTable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;

public class RoutesPanel extends JPanel {
    private static RoutesPanel transportPanelInstance;
    
    public static RoutesPanel getInstance() {
        if(transportPanelInstance==null) {
            transportPanelInstance = new RoutesPanel();
        }
        return transportPanelInstance;
    }

    public RoutesPanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setForeground(Color.DARK_GRAY);

        // Creamos la tabla y el scroll
        RoutesTable routesTable = RoutesTable.getInstance();
        JScrollPane scrollPane = new JScrollPane(routesTable);
        scrollPane.setSize(50,50);

        // Creamos el panel de botones
        RoutesButtonPanel buttonPanel = RoutesButtonPanel.getInstance();

        //Añadimos y acomodamos los paneles
        this.add(scrollPane,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.SOUTH);
        this.setVisible(true);
    }
}
