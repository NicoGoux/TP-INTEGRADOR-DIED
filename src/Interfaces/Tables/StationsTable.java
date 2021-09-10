package Interfaces.Tables;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import DataBase.DBConnection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Graph.Station;

public class StationsTable extends JTable {

    private static StationsTable stationsTableInstance;

    private TableModel model;
    private int lastEditRow;

    public static StationsTable getInstance() {
        if (stationsTableInstance==null) {
            stationsTableInstance=new StationsTable();
        }
        return stationsTableInstance;
    }

    public StationsTable () {
        super();
        this.setBackground(Color.WHITE);
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));

        //Creamos los datos que ingresaremos en la tabla
        String[] columnNames = {"ID", "NOMBRE", "HORARIO APERTURA", "HORARIO CIERRE", "OPERATIVA"};

        //Creamos el modelo de tabla
        this.model = new TableModel(null,columnNames);
        this.setModel(this.model);

        //Agregamos parametros para el header
        JTableHeader header = this.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        
        //Agregamos parametros de columnas
        TableColumnModel column = this.getColumnModel();
        DefaultTableCellRenderer centerAlignment = new DefaultTableCellRenderer();
        centerAlignment.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i=0;i<this.getColumnCount();i++) {
            if (i!=column.getColumnIndex("OPERATIVA")) {
                column.getColumn(i).setCellRenderer(centerAlignment);
            }
        }
        column.getColumn(0).setPreferredWidth(20);
        column.getColumn(1).setPreferredWidth(50);
        column.getColumn(2).setPreferredWidth(120);
        column.getColumn(3).setPreferredWidth(100);
        column.getColumn(4).setPreferredWidth(70);

        //Parametros a las filas
        this.setRowHeight(30);

        //Parametros generales
        this.setPreferredScrollableViewportSize(new Dimension(700, 450));
        this.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        this.setFillsViewportHeight(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Listener de pulsación
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	JTable table = (JTable)e.getSource();
                TableModel model = (TableModel)((JTable)e.getSource()).getModel();
                int row = table.rowAtPoint( e.getPoint() );
                int column = table.columnAtPoint( e.getPoint());
                model.setLastPoint(row, column);
            }
        });
        refreshData();
    }

    public void refreshData() {
        //Cargamos los datos desde la base de datos
        this.clearTable();
        Runnable t1 = () -> {
            ResultSet rs =null;
            DBConnection.establishConnection();
            PreparedStatement pstm = null;
            try {
                pstm = DBConnection.getConnection().prepareStatement("SELECT * FROM STATION ORDER BY id"); //Order by agregado despues de la entrega
                rs = pstm.executeQuery();
                Object [] data = new Object[5];
                while (rs.next()) { 
                    data[0] = rs.getInt(1);
                    data[1] = rs.getString(2);
                    data[2] = rs.getString(3);
                    data[3] = rs.getString(4);
                    data[4] = rs.getBoolean(5);
                    this.model.addRow(data); 
                } 
            } 
            catch (SQLException e) {
                e.printStackTrace();
            } 
            finally {
                DBConnection.closeConnection(pstm, rs);
            }
        };
        new Thread(t1,"Thread DBTable").start(); 
    }

    public void addStation(Station newStation){
        Object [] data = new Object[5];
        
        //Se almacenan los atributos de la estacion en un arreglo
        data[0] = newStation.getIdStation();
        data[1] = newStation.getName();
        data[2] = newStation.getOpening();
        data[3] = newStation.getClosing();
        data[4] = newStation.getStatus();

        this.model.addRow(data);
    }

    public boolean editTable() {
        //Si no se selecciono una fila se devolvera un valor false
        if (this.getSelectedRow()==-1) {
            return false;
        }
        else {
            //Realizamos un backUp de la fila actual antes de realizar modificaciones para el caso de que el usuario cancele la modificacion.
            this.lastEditRow= this.getSelectedRow();
            this.model.setBackUpRow(this.lastEditRow);
            /*
             * Hicimos que la columna del ID no sea editable para que los ID sean generados automaticamente por el software.
             * Generamos un arrayList para poder generalizar el codigo presentado en el TableModel y poder utilizarlo en otras tablas que realicemos.
             */
            ArrayList<Integer> notEditableColumn = new ArrayList<>();
            notEditableColumn.add(0);
            notEditableColumn.add(4);
            this.model.setEditableCells(notEditableColumn);
            return true;
        }
    }

    public boolean editMaintenanceTable() {
        //Si no se selecciono una fila se devolvera un valor false
        if (this.getSelectedRow()==-1) {
            return false;
        }
        else {
            this.lastEditRow= this.getSelectedRow();
            this.model.setEditableCell(this.lastEditRow, 4);
            return true;
        }
    }

    public Integer getIdentifier() {
        return (Integer)this.getValueAt(this.getLastEditRow(), 0);
    }

    public void restoreRow(){
        this.stopEditingCell();
        int row = this.lastEditRow;
        for (int i=0; i<this.getColumnCount();i++) {
            this.setValueAt(this.model.getBackUpRow()[i], row, i);
        }
    }

    public void restoreCell(int row, int i) {
        this.model.restoreCell(row,i);
    }
    
    public void stopEditingCell() {
        if (this.getCellEditor()!=null) {
            this.getCellEditor().stopCellEditing();
        }
    }

    public void finishRowEdit() {
        this.model.clearEditableCells();
    }

    public int getLastEditRow() {
        return this.lastEditRow;
    }

    public void clearTable() {
        this.model.setRowCount(0);
    }

    public void addRow(Object[] data) {
        model.addRow(data);
    }
}

