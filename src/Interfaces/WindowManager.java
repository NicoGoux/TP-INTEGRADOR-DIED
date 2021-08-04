package Interfaces;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class WindowManager { //Clase gestora de ventanas JFrame

    private static JFrame instanceWindow;

    public static JFrame getMainWindow() {
        if (instanceWindow==null) {
            //Creacion de JFrame
            instanceWindow = new JFrame();
            instanceWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            instanceWindow.setTitle("Sistema de Gestion Transporte Multimodal");
            instanceWindow.setVisible(true);

            //Pop-up de confirmacion de salida
            instanceWindow.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    JPanel contentPane = new JPanel();
                    int n = JOptionPane.showConfirmDialog(contentPane,"¿Desea salir?","Salir", JOptionPane.YES_OPTION);
                    if(n == 0) {
                        System.exit(0);
                    }
                }
            });
        }
        return instanceWindow;
    }

    public static JDialog getPopUpWindow (String titleString) {
        JDialog popUp = new JDialog();
        popUp.setTitle(titleString);
        popUp.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                instanceWindow.setEnabled(true);
                instanceWindow.setVisible(true);
            }
        });
        popUp.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        popUp.setLocationRelativeTo(null);
        return popUp;
    }
}
