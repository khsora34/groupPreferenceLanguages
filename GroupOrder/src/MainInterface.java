import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainInterface {

    private ConnectionManager connection;
    private StandardInput input;

    private boolean needsSaving = false;
    private Map<Integer, Pilgrim> pilgrims;
    private Map<Integer, Group> rooms;

    public MainInterface() {
        connection = new ConnectionManager();
        input = new StandardInput();
    }

    private void start() throws SQLException {
        System.out.println("WELCOME TO GROUP ORDER.");
        System.out.println("LAUNCHING DATABASE...");

        connection.connectDb();

        System.out.println("SUCCESFULLY CONNECTED.");

        System.out.println("LOADING PILGRIMS INTO PROGRAM.");

        pilgrims = connection.loadPilgrimResources();

        System.out.println("PILGRIMS LOADED SUCCESFULLY.");

        System.out.println("LOADING AVAILABLE ROOMS.");

        rooms = connection.loadGroupsResources();

        System.out.println("ROOMS LOADED SUCCESFULLY.");

        System.out.println("PROGRAM STARTED SUCCESFULLY.");

        connection.disconnect();
    }


    private void mainMenu() {
        System.out.println("WHAT DO YOU WANT TO DO?");
        for (Action a : Action.values()) {
            System.out.println(a.toString());
        }

        switch (input.readNumber()) {
            case 1:

            case 2:
            case 3:
            case 4:
            case 5:
            case 6:

        }
    }

    private void addPilgrim() {
        System.out.println("ENTER PILGRIM NAME: ");

        String name = input.readString();

        System.out.println("IS THE PILGRIM A LEADER? (1 IF YES, OTHERWISE NO): ");

        boolean isLeader = input.readNumber() == 1;

        System.out.println("AVAILABLE LANGUAGES: ");

        for (Language l : Language.values()) {
            System.out.print(l.toString() + ", ");
        }

        Language language = null;

        do {
            System.out.println("ENTER THE PILGRIM'S NATIVE LANGUAGE: ");
            language = Utils.getLanguage(input.readString());
            if (language == null) {
                System.out.println("LANGUAGE NOT SUPPORTED, TRY AGAIN. ");
            }

        } while (language == null);

        Pilgrim p = new Pilgrim(name, language, isLeader);

        List<Language> otherLanguages = new ArrayList<>();

        String inputString = "";

        do {
            System.out.println("ENTER ANY OTHER LANGUAGE THE PILGRIM KNOWS (TYPE '0' TO END): ");

            inputString = input.readString();

            if (!inputString.equals("0")) {
                Language newLanguage = Utils.getLanguage(inputString);

                if (newLanguage == null) {
                    System.out.println("LANGUAGE NOT SUPPORTED, TRY AGAIN. ");
                } else {
                    System.out.println("ADDED " + newLanguage.name() + " TO THIS PILGRIM.");
                }
            }

        } while (!inputString.equals("0"));

        p.setOtherLanguage(otherLanguages);

        //pilgrims.put()
    }

    public static void main(String[] args) {
        new MainInterface().mainMenu();
    }


}
