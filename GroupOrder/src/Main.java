import java.sql.*;

public class Main {

    public static void main(String[] args) {
        MainInterface main = new MainInterface();
        try {
            main.launchMainInterface();
            while (true) {
                main.mainMenu();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
