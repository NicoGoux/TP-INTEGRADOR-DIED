package Interfaces.buyTicketsInterface;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;

public class BuyTicketsPanel extends JPanel{
    private static BuyTicketsPanel buyTicketsPanelInstance;
    
    public static BuyTicketsPanel getInstance() {
        if(buyTicketsPanelInstance==null) {
            buyTicketsPanelInstance = new BuyTicketsPanel();
        }
        return buyTicketsPanelInstance;
    }

    public BuyTicketsPanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setForeground(Color.DARK_GRAY);

        // Creamos el panel del grafo
        PrintedGraphPanel graphPanel = PrintedGraphPanel.getInstance();

        // Creamos el panel de botones
        BuyTicketsButtonPanel buttonPanel = BuyTicketsButtonPanel.getInstance();

        //Añadimos y acomodamos los paneles
        this.add(graphPanel,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.WEST);
        
    }
}
