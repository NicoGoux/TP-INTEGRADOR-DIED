package Interfaces.Accessories;

import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class EmailVerification {
    private static final Pattern emailFormat=Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    
    public static boolean verifyEmail(String email) {
        if (emailFormat.matcher(email).matches()) {
            return true;
        }
        else {
            JOptionPane.showMessageDialog(null,"Error. Respete el formato de email (xxxxx@xxx.xxx)","Ingrese los datos del cliente",JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
