package Interfaces.Accessories;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColourSelectorRender extends JLabel implements ListCellRenderer<String> {
    private JComboBox<String> comboBox;
    public ColourSelectorRender(JComboBox<String> comboBox) {
        super();
        this.comboBox=comboBox;
        this.setOpaque(true);
        this.setAlignmentX(CENTER_ALIGNMENT);
        this.setAlignmentY(CENTER_ALIGNMENT);

    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.equals("Seleccione un color")) {
            setText("Seleccione un color");
            comboBox.setBackground(Color.WHITE);
            setBackground(Color.WHITE);
        }
        else {
            setText("Color");
            comboBox.setBackground(Color.decode(value));
            setForeground(Color.decode(value));
            setBackground(Color.decode(value));
        } 
        return this;
    }
}
