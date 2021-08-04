package Interfaces.Tables;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
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
import java.util.List;

import Graph.Graph;
import Graph.Route;

public class RoutesTable extends JTable {

    private static RoutesTable routesTableInstance;

    private TableModel model;
    private List<Integer> visitedStations;
    private int origin;
    private int end;

    public static RoutesTable getInstance() {
        if (routesTableInstance==null) {
            routesTableInstance=new RoutesTable();
        }
        return routesTableInstance;
    }

    public RoutesTable () {
        super();
        this.origin =-1;
        this.end = -1;
        visitedStations = new ArrayList<Integer>();

        this.setBackground(Color.WHITE);
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));

        //Creamos los datos que ingresaremos en la tabla
        String[] columnNames = {"ESTACIÓN ORIGEN", "ESTACIÓN DESTINO", "COLOR", "DISTANCIA[KM]", "DURACION[m]","CANTIDAD PASAJEROS", "ESTADO", "PRECIO"};

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
            if (i!=column.getColumnIndex("ESTADO")) {
                column.getColumn(i).setCellRenderer(centerAlignment);
            }
        }
        column.getColumn(0).setPreferredWidth(50);
        column.getColumn(1).setPreferredWidth(100);
        column.getColumn(2).setPreferredWidth(50);
        column.getColumn(3).setPreferredWidth(50);
        //Parametros para la columna color
        TableColumn colour = this.getColumn("COLOR");
        colour.setCellRenderer(new ColourCellRender()); //Permitira agregarle color a la columna COLORES

        //Parametros a las filas
        this.setRowHeight(30);

        //Parametros generales
        this.setPreferredScrollableViewportSize(new Dimension(400, 450));
        this.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        this.setFillsViewportHeight(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Listener de pulsación
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	JTable table = (JTable)e.getSource();
                TableModel model = (TableModel)((JTable)e.getSource()).getModel();
                int row = table.rowAtPoint( e.getPoint() );
                int column = table.columnAtPoint( e.getPoint() );
                model.setLastPoint(row, column);
            }
        });
    }

    public void refreshData(String colour) {
        //Cargamos los datos desde la base de datos
        this.clearTable();
        Runnable t1 = () -> {
            ResultSet rs =null;
            DBConnection.establishConnection();
            PreparedStatement pstm = null;
            try {
                pstm = DBConnection.getConnection().prepareStatement("SELECT * FROM ROUTE WHERE colour='"+colour+"'");
                rs = pstm.executeQuery();
                Object [] data = new Object[8];
                while (rs.next()) { 
                    data[0] = rs.getInt(1);
                    data[1] = rs.getInt(2);
                    data[2] = rs.getString(3);
                    data[3] = rs.getInt(4);
                    data[4] = rs.getInt(5);
                    data[5] = rs.getInt(6);
                    data[6] = rs.getBoolean(7);
                    data[7] = rs.getDouble(8);
                    this.model.addRow(data);

                    //Obtenemos el inicio, el final y las estaciones recorridas del trayecto
                    this.origin = Graph.getInstance().getRouteOrigin(colour);
                    this.end = Graph.getInstance().getRouteEnd(colour);
                    if (!this.visitedStations.contains(rs.getInt(1))) {
                        this.visitedStations.add(rs.getInt(1));
                    }
                    if (!this.visitedStations.contains(rs.getInt(2))) {
                        this.visitedStations.add(rs.getInt(2));
                    }
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

    public void addRoute(Route newRoute) {
        Object [] data = new Object[8];
        //Se almacenan los atributos de la nueva ruta en un arreglo
        data[0] = newRoute.getOrigin();
        data[1] = newRoute.getEnd();
        data[2] = newRoute.getColourString();
        data[3] = newRoute.getDistance();
        data[4] = newRoute.getDuration();
        data[5] = newRoute.getMaxPassengers();
        data[6] = newRoute.getStatus();
        data[7] = newRoute.getPrice();
        this.model.addRow(data);
    }

    public Object[] getIdentifier() {
        Object [] identifier = new Object[3];
        this.getSelectedRow();
        identifier[0] = this.getValueAt(this.getSelectedRow(),0);
        identifier[1] = this.getValueAt(this.getSelectedRow(),1);
        identifier[2] = this.getValueAt(this.getSelectedRow(),2);
        return identifier;
    }

    public void clearTable() {
        this.model.setRowCount(0);
        this.origin =-1;
        this.end = -1;
        this.visitedStations.clear();
    }

    public void addRow(Object[] row){
        this.model.addRow(row);
    }

    public int getOrigin() {
        return this.origin;
    }

    public int getEnd() {
        return this.end;
    }

    public List<Integer> getVisitedStations() {
        return this.visitedStations;
    }

    //Devuelve el estado actual de la tabla
    public boolean getRouteStatus() {
        return (Boolean)this.getValueAt(0, this.getColumnModel().getColumnIndex("ESTADO"));
    }
}

