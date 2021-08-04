package Interfaces.Accessories;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import Graph.Graph;

public class StationSelectorRender extends JLabel implements ListCellRenderer<Integer> {
    private String initialString;
    public StationSelectorRender(JComboBox<Integer> comboBox, String initialString) {
        super();
        this.initialString=initialString;
        this.setOpaque(true);
        this.setAlignmentX(CENTER_ALIGNMENT);
        this.setAlignmentY(CENTER_ALIGNMENT);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value==0) {
            setText(initialString);
        }
        else {
            if (!Graph.getInstance().getStation(value).getStatus() || !Graph.getInstance().getStation(value).getTimeStatus()) {
                setForeground(Color.GRAY);
                setText(""+value+("(Cerrada)"));
            }
            else {
                setForeground(Color.black);
                setText(""+value);
            }
            
        }
        return this;
    }
}
