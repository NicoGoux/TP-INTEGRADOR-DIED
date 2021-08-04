package Interfaces.Tables;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel {
    // private String[] columnsName;
    // private Object[][] data;
    private List<Point> editableCells;
    private Object[] backUpRow;
    private int lastRow;
    private int lastColumn;

    public TableModel(Object[][] data, String[] columnsName){
        super();
        this.editableCells=new ArrayList<Point>();
        setDataVector(data, columnsName);
    }

    public boolean isCellEditable(int row, int column) {
        return this.editableCells.contains(new Point(row,column));
    }

    //PROBAR SI FUNCIONA ASI
    public Class<?> getColumnClass(int index) {
        //for (int i=0;i<this.getColumnCount();i++) {
           // if (this.getValueAt(0, index)!=null) {
                return this.getValueAt(0, index).getClass();
           // }
        //}
        //return null;
    }

    //SI NO SE USA LA COLUMNA PUEDE REEMPLAZARSE POR EL lastSelectedRow de la tabla mediante paso de parametros
    public void setLastPoint(int row, int column) {
        if (row !=-1 && column!=-1) {
            this.lastRow=row;
            this.lastColumn=column;
            // solo para probar System.out.println(this.lastRow + " , " + this.lastColumn);
        }
    }

    public void setEditableCells(List<Integer> notEditableColumns) {
        /*
         *Si hay elementos en la lista de columnas no editables se analizara la lista y se incluiran en la fila editable todos los elementos distintos a los que
         *se encuentran en la lista 
         */
        if(!notEditableColumns.isEmpty()) {
            for (int i = 0; i<this.getColumnCount(); i++) {
                if(!notEditableColumns.contains(i)) {
                    this.editableCells.add(new Point(this.lastRow,i));
                }
            }
        }
        // Si la lista esta vacia añadira todas las columnas a la fila editable
        else {
            for (int i = 0; i<this.getColumnCount(); i++) {
                this.editableCells.add(new Point(this.lastRow,i));
            }
        }
    }

    public void clearEditableCells() {
        this.editableCells.clear();
    }
    
    public void setBackUpRow(int lastEditRow) {
        Object[] backUpRow = new Object[this.getColumnCount()];
        for (int i=0;i<this.getColumnCount();i++) {
            backUpRow[i] = this.getValueAt(lastEditRow, i);
        }
        this.backUpRow=backUpRow;
    }

    public Object[] getBackUpRow() {
        return this.backUpRow;
    }

    public void restoreRow() {
        int row = this.editableCells.get(0).x;
        for (int i=0; i<this.getColumnCount();i++) {
            this.setValueAt(this.backUpRow[i], row, i);
        }
    }

    public void restoreCell(int row,int column) {
        this.setValueAt(this.backUpRow[column], row, column);
    }

    public void setEditableCell(int row, int column) {
        this.editableCells.add(new Point(row,column));
    }

    //Para que no salte la advertencia
    public int getLastColumn() {
        return lastColumn;
    }

    public void setLastColumn(int lastColumn) {
        this.lastColumn = lastColumn;
    }
}
