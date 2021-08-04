package Interfaces.Accessories;

import java.awt.Font;

import javax.swing.JButton;
//Esta clase se creo para poder modificar los tamaños de fuentes de los botones.
public class MyButton extends JButton {
    public MyButton(String text, int size) {
        super(text);
        this.setFont(new Font("Calibri",Font.BOLD,size));
    }
}
