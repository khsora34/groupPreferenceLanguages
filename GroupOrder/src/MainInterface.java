import java.sql.SQLException;
import java.util.*;

public class MainInterface {

    private ConnectionManager connection;
    private StandardInput input;

    private Map<Integer, Pilgrim> pilgrims;
    private Map<Integer, Group> groups;

    private boolean groupsNeedSaving = false;
    private boolean pilgrimsNeedSaving = false;

    public MainInterface() {
        connection = new ConnectionManager();
        input = new StandardInput();
    }

    private void launchMainInterface() throws SQLException {
        System.out.println("WELCOME TO GROUP ORDER.");
        System.out.println("LAUNCHING DATABASE...");

        connection.connectDb();

        System.out.println("SUCCESFULLY CONNECTED.");

        System.out.println("LOADING PILGRIMS INTO PROGRAM.");

        pilgrims = connection.loadPilgrimResources();

        if (pilgrims.values().isEmpty()) {
            System.out.println("STILL NO PILGRIMS IN THE DATABASE.");
        } else {
            System.out.println("PILGRIMS LOADED SUCCESFULLY.");
        }

        System.out.println("LOADING AVAILABLE ROOMS.");

        groups = connection.loadGroupsResources();

        if (groups.values().isEmpty()) {
            System.out.println("STILL NO GROUPS IN THE DATABASE.");
        } else {
            System.out.println("ROOMS LOADED SUCCESFULLY.");
        }

        System.out.println("PROGRAM STARTED SUCCESFULLY.");

        connection.disconnect();
    }


    private void mainMenu() throws SQLException {
        System.out.println("CHOOSE AN OPTION");
        for (Action a : Action.values()) {
            System.out.println(a.toString());
        }

        switch (Action.valueOf(input.readNumber())) {
            case ENTER_PILGRIM:
                addPilgrim();
                break;
            case ENTER_GROUP:
                addGroup();
                break;
            case SHOW_AVAILABLE_PILGRIMS:
                showPilgrims();
                break;
            case SHOW_AVAILABLE_GROUP:
                showGroups();
                break;
            case MIX_GROUP:
                mixAlgorithm();
                break;
            case SAVE_CHANGES:
                saveChanges();
                break;
            case RELOAD_DATABASE:
                reloadDatabase();
                break;
            case EXIT:
                endSession();
                break;


        }
    }

    private boolean askForSaving() {
        if (pilgrimsNeedSaving || groupsNeedSaving) {
            System.out.println("DO YOU WANT TO SAVE YOUR CHANGES BEFORE RELOADING? NO -> -1 | OTHERWISE YES");
            if (-1 != input.readNumber()) {
                try {
                    saveChanges();
                } catch (SQLException e) {
                    System.out.println("UNABLE TO SAVE DATA ON DATABASE, ERROR OUTPUT:");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    private void addPilgrim() {
        System.out.println("IF YOU WANT TO UPDATE A PILGRIM, ENTER THE ID, ELSE -1: ");

        int id = input.readNumber();

        if (id == -1) {
            id = connection.getLastPilgrimId();
        }

        System.out.println("ENTER PILGRIM NAME: ");

        String name = input.readString();

        System.out.println("IS THE PILGRIM A LEADER? Yes -> 1, OTHERWISE NO): ");

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

        Pilgrim p = new Pilgrim(id, name, language, isLeader);

        Set<Language> otherLanguages = new HashSet<>();

        String inputString = "";

        do {
            System.out.println("ENTER ANY OTHER LANGUAGE THE PILGRIM KNOWS (TYPE '0' TO END): ");

            inputString = input.readString();

            if (!"0".equals(inputString)) {
                Language newLanguage = Utils.getLanguage(inputString);

                if (newLanguage == null) {
                    System.out.println("LANGUAGE NOT SUPPORTED, TRY AGAIN. ");
                } else {
                    otherLanguages.add(newLanguage);
                    System.out.println("ADDED " + newLanguage.name() + " TO THIS PILGRIM.");
                }
            }

        } while (!"0".equals(inputString));

        p.setOtherLanguages(otherLanguages);

        pilgrims.put(id, p);
    }

    private void addGroup() {
        System.out.println("IF YOU WANT TO UPDATE A GROUP, ENTER THE ID, ELSE -1: ");

        int id = input.readNumber();

        if (id == -1) {
            id = connection.getLastPilgrimId();
        }

        System.out.println("ENTER GROUP's ROOM NAME: ");

        String name = input.readString();

        Group g = new Group(id, name);

        groups.put(id, g);
    }

    private void showPilgrims() {
        for (Pilgrim p: pilgrims.values()) {
            System.out.println(p.toString());
        }
    }

    private void showGroups() {
        for (Group g: groups.values()) {
            System.out.println(g.toString());
        }
    }

    private void mixAlgorithm() {
        MixAlgorithm alg = new MixAlgorithm(pilgrims, groups);

        System.out.println("THE ALGORITHM IS LOADING...");

        System.out.println("WOULD YOU LIKE TO START FROM ZERO OR CONTINUE WITH ACTUAL SELECTION? -1 -> ZERO | OTHERWISE -> ACTUAL");
        System.out.println("IF YOU START FROM ZERO, YOUR LAST SELECTION WILL BE DELETED.");

        boolean useFilter = -1 != input.readNumber();

        System.out.println("WOULD YOU LIKE TO SHUFFLE THE PILGRIMS BEFORE MIXING? 1 -> YES | OTHERWISE -> NO");
        System.out.println("IF SHUFFLING IS ENABLED, THE PROGRAM MAY TAKE MORE TIME TO ASSIGN GROUPS.");

        boolean useShuffle = -1 != input.readNumber();

        alg.startAlgorithm(useFilter, useShuffle);

        System.out.println("ASSIGNMENT SUCCESFULLY ENDED.");
    }

    private void saveChanges() throws SQLException {
        connection.savePilgrims(pilgrims.values().toArray(new Pilgrim[0]));

        for (Group g: groups.values()) {
            connection.saveGroup(g);
        }

        System.out.println("SAVED INFO SUCCESSFULLY.");
    }

    private void reloadDatabase() throws SQLException {
        if (!askForSaving()) {
            System.out.println("\nOPERATION CANCELED");
            return;
        }

        System.out.println("LOADING PILGRIMS INTO PROGRAM.");

        pilgrims = connection.loadPilgrimResources();

        if (pilgrims.values().isEmpty()) {
            System.out.println("STILL NO PILGRIMS IN THE DATABASE.");
        } else {
            System.out.println("PILGRIMS LOADED SUCCESFULLY.");
        }

        System.out.println("LOADING AVAILABLE ROOMS.");

        groups = connection.loadGroupsResources();

        if (groups.values().isEmpty()) {
            System.out.println("STILL NO GROUPS IN THE DATABASE.");
        } else {
            System.out.println("ROOMS LOADED SUCCESFULLY.");
        }

        System.out.println("\nPROGRAM RELOADED SUCCESSFULLY.\n");

    }

    private void endSession() throws SQLException {
        if (!askForSaving()) {
            System.out.println("\nOPERATION CANCELED");
            return;
        }

        System.out.println("\nSHUTTING DOWN THE DATABASE.");

        connection.disconnect();

        System.out.println("SHUTDOWN COMPLETE, THANKS FOR USING OUR SOFTWARE!");
        System.out.println("PRESS ENTER TO EXIT THE PROGRAM.");
        input.nextLine();
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            new MainInterface().mainMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
