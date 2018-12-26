import java.sql.*;

public class Main {

//    public static void main(String[] args) {
//        ConnectionManager manager = new ConnectionManager();
//        System.out.println(manager.connectDb());
//        manager.disconnect();
//    }

    public static void main(String[] args) {
        System.out.println("Conectando a la base de datos...");
        ConnectionManager connection = new ConnectionManager();

        try {
            connection.connectDb();

            connection.savePilgrims(new Pilgrim[]{new Pilgrim("Pedro", Language.FRENCH), new Pilgrim(1, "Bien", Language.POLISH)});

            for (Pilgrim p: connection.loadPilgrimResources()) {
                System.out.println(p);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            connection.onError();

        } finally {
            try {
                connection.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }




    }
}
