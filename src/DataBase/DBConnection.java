package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Graph.Graph;
import Graph.Station;
import Graph.Transport;
import Graph.Route;

public class DBConnection {
    static private Connection connection;
    static public Connection establishConnection () {
        Connection conn = null;
        try { 
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/", "postgres", "died2021");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        connection=conn;
        return conn;
    }

    static public void closeConnection(PreparedStatement pstm, Object resultObject) {
        if(resultObject!=null) { 
            try {
                if (resultObject instanceof ResultSet) {
                    ((ResultSet)resultObject).close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(pstm!=null) {
            try { 
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace(); 
            }
        } 
        
        if(connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) { 
                e.printStackTrace(); 
            }
        }
        connection = null;
    }

    static public Connection getConnection() {
        return connection;
    }

    public static void refreshGraphData() {
        Graph graphInstance = Graph.getInstance();
        ResultSet rsStations = null;
        ResultSet rsTransports = null;
        ResultSet rsRoutes = null;
        DBConnection.establishConnection();
        PreparedStatement pstmStations = null;
        PreparedStatement pstmTransports = null;
        PreparedStatement pstmRoutes = null;
        try {
            pstmStations = DBConnection.getConnection().prepareStatement("SELECT * FROM STATION ORDER BY id");
            rsStations = pstmStations.executeQuery();
            int idScounter=0;
            while (rsStations.next()) {
                graphInstance.addStation(new Station(rsStations.getInt(1), rsStations.getString(2), rsStations.getString(3), rsStations.getString(4), rsStations.getBoolean(5)));
                idScounter = rsStations.getInt(1);
            }
            Station.setIdSCounter(idScounter);

            pstmTransports = DBConnection.getConnection().prepareStatement("SELECT * FROM TRANSPORT ORDER BY id");
            rsTransports = pstmTransports.executeQuery();
            int idTCounter=0;
            while (rsTransports.next()) {
                graphInstance.addTransport(new Transport(rsTransports.getInt(1), rsTransports.getString(2), rsTransports.getString(3), rsTransports.getBoolean(4)));
                idTCounter = rsTransports.getInt(1);
            }
            Transport.setIdTCounter(idTCounter);

            pstmRoutes = DBConnection.getConnection().prepareStatement("SELECT * FROM ROUTE");
            rsRoutes = pstmRoutes.executeQuery();
            while (rsRoutes.next()) {
                graphInstance.connect(new Route(rsRoutes.getInt(1), rsRoutes.getInt(2), rsRoutes.getString(3),rsRoutes.getInt(4),rsRoutes.getInt(5),rsRoutes.getInt(6),rsRoutes.getBoolean(7), rsRoutes.getDouble(8)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(pstmStations, rsStations);
            DBConnection.closeConnection(pstmTransports, rsTransports);
            DBConnection.closeConnection(pstmRoutes, rsRoutes);
        }
    }
}
