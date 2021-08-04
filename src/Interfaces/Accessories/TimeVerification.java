package Interfaces.Accessories;

import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import Exceptions.TimeFormatException;

public class TimeVerification {
    private static final Pattern firstTimeFormat=Pattern.compile("2[0-3]{1}:[0-5]{1}[0-9]{1}");
    private static final Pattern secondTimeFormat = Pattern.compile("[0-1]{1}[0-9]{1}:[0-5]{1}[0-9]{1}");
    
    public static boolean verifyTime(String timeString) {
        if (firstTimeFormat.matcher(timeString).matches() || secondTimeFormat.matcher(timeString).matches()) {
            return true;
        }
        else {
            JOptionPane.showMessageDialog(null,"Error. Respete el formato indicado (hh:mm, considerando formato de 24 horas)","Ingrese los datos de la estación",JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static String verifyTimeString(String timeString) {
        while (timeString != null) {
            if (firstTimeFormat.matcher(timeString).matches() || secondTimeFormat.matcher(timeString).matches()) {
                return timeString;
            }
            else {
                timeString = JOptionPane.showInputDialog(null,"Error. Respete el formato indicado (hh:mm): ","Ingrese los datos de la estación",JOptionPane.QUESTION_MESSAGE);
            }
        }
        return null;
    }

    public static void tryVerifyTime(String timeString) throws TimeFormatException {
        if (firstTimeFormat.matcher(timeString).matches() || secondTimeFormat.matcher(timeString).matches()) {
            return;
        }
        else {
            throw new TimeFormatException();
        }
    }
}
