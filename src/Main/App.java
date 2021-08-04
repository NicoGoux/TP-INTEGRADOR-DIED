package Main;

import DataBase.DBConnection;
import Interfaces.*;

public class App {
    public static void main(String[] args) {
        Runnable t = () -> {
            DBConnection.refreshGraphData();
        };
        new Thread(t,"Thread DBApp (Test)").start();
        new FirstMenu();
        System.out.println();
    }
}