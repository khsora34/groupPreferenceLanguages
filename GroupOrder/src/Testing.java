import java.sql.SQLException;

public class Testing {

    public static void main(String[] args) {
        System.out.println("Conectando a la base de datos...");
        ConnectionManager connection = new ConnectionManager();

        try {
            connection.connectDb();

            connection.loadPilgrimResources();
            connection.loadGroupsResources();

           // connection.savePilgrims(new Pilgrim[]{new Pilgrim(1, "Pedro", Language.FRENCH, true), new Pilgrim(3, "Bien", Language.POLISH, false), new Pilgrim(29, "ISI", Language.FRENCH, true)});

            for (Pilgrim p: connection.loadPilgrimResources().values()) {
                System.out.println(p + "\n");
            }

            connection.saveGroup(new Group(2, "LALALAND"));
            connection.saveGroup(new Group(34, "NOCASA"));

            for (Group g: connection.loadGroupsResources().values()) {
                System.out.println(g + "\n");
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
