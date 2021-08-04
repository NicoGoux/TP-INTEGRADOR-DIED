package Interfaces.Tables;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Color;
import java.awt.Component;

public class ColourCellRender extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table.getValueAt(row, column).equals("Seleccione un color")) {
            super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
        }
        else {
            Color c = Color.decode((String)table.getValueAt(row, column));
            super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
            this.setOpaque(true);
            this.setBackground(c);
            this.setForeground(c);
        }
        return this;
    }
}
