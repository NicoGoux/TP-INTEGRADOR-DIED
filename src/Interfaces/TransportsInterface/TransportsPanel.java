package Interfaces.TransportsInterface;

import Interfaces.Tables.TransportsTable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;

public class TransportsPanel extends JPanel {
    private static TransportsPanel transportPanelInstance;
    
    public static TransportsPanel getInstance() {
        if(transportPanelInstance==null) {
            transportPanelInstance = new TransportsPanel();
        }
        return transportPanelInstance;
    }

    public TransportsPanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setForeground(Color.DARK_GRAY);

        // Creamos la tabla y el scroll
        TransportsTable transportsTable = TransportsTable.getInstance();
        JScrollPane scrollPane = new JScrollPane(transportsTable);
        scrollPane.setSize(50,50);

        // Creamos el panel de botones
        TransportsButtonPanel buttonPanel = TransportsButtonPanel.getInstance();

        //Creamos el panel de filtrado

        TransportsFilterPanel filterPanel = TransportsFilterPanel.getInstance();
        //Añadimos y acomodamos los paneles
        
        this.add(filterPanel,BorderLayout.WEST);
        this.add(scrollPane,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.SOUTH);
        this.setVisible(true);
    }
}
