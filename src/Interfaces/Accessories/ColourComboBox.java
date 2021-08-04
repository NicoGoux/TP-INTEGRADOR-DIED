package Interfaces.Accessories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JComboBox;
import DataBase.DBConnection;

public class ColourComboBox extends JComboBox<String> {
    public ColourComboBox() {
        super();
        this.addItem("Seleccione un color");
        Runnable colourT = () -> {
            ResultSet rs =null;
            DBConnection.establishConnection();
            PreparedStatement pstm = null;
            try {
                pstm = DBConnection.getConnection().prepareStatement("SELECT * FROM COLOUR");
                rs = pstm.executeQuery();
                while (rs.next()) { 
                    this.addItem(rs.getString(1));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            } finally {
                DBConnection.closeConnection(pstm, rs);
            }
        };
        new Thread(colourT,"colourComboBox").start();
        this.setRenderer(new ColourSelectorRender(this));
    }
}
