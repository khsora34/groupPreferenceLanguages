import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

public class MainInterface {

    private ConnectionManager connection;
    private StandardInput input;

    private Map<Integer, Pilgrim> pilgrims;
    private Map<Integer, Group> groups;

    private boolean needsSaving = false;

    private int newIdForPilgrim = 1;
    private int newIdForGroup = 1;

    public MainInterface() {
        connection = new ConnectionManager();
        input = new StandardInput();
    }

    private void loadDBData() throws SQLException {
        pilgrims = null;
        groups = null;

        System.out.println("LOADING PILGRIMS INTO PROGRAM.");

        if (!connection.connectDb()) {
            System.out.println("UNABLE TO CONNECT TO DATABASE.");
            System.out.println("PRESS ENTER TO EXIT THE PROGRAM..");
            input.nextLine();
            System.exit(-1);
        }

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

        newIdForPilgrim = connection.getLastPilgrimId();
        newIdForGroup = connection.getLastGroupId();

        connection.disconnect();
    }

    public void launchMainInterface() throws SQLException {
        System.out.println("WELCOME TO GROUP ORDER.\n");
        System.out.println("LAUNCHING DATABASE...\n");

        loadDBData();

        System.out.println("PROGRAM STARTED SUCCESFULLY.");
    }

    public void mainMenu() throws SQLException {
        System.out.println("\nACTIONS AVAILABLE: \n");
        for (Action a : Action.values()) {
            System.out.println(a.toString());
        }

        System.out.print(" \nCHOOSE AN ACTION: ");

        int option = input.readNumber();

        System.out.print(" \n---------------------------------------------------------------------------------------------------\n");

        Action action = Action.valueOf(option);

        if (action == null) {
            System.out.println("OPTION NOT SUPPORTED. PLEASE, TRY AGAIN.");
            return;
        }

        switch (action) {
            case ENTER_PILGRIM:
                addPilgrim(-1);
                break;
            case ENTER_GROUP:
                addGroup(-1);
                break;
            case MODIFY_PILGRIM:
                modifyPilgrim();
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
            case SHOW_ACTUAL_GROUPS:
                showMixedGroups();
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
        if (needsSaving) {
            System.out.print("DO YOU WANT TO SAVE YOUR CHANGES BEFORE RELOADING? YES -> 1 | NO -> -1");
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

    private void addPilgrim(int id) {
        if (id == -1) {
            id = newIdForPilgrim;
            newIdForPilgrim++;
        }

        System.out.print("ENTER PILGRIM NAME: ");

        String name = input.readString();

        System.out.print("\nIS THE PILGRIM A LEADER? YES -> 1 | NO -> -1): ");

        boolean isLeader = input.readNumber() == 1;

        System.out.println("\nAVAILABLE LANGUAGES: ");

        for (Language l : Language.values()) {
            System.out.print(l.toString() + ", ");
        }

        Language language = null;

        do {
            System.out.print("ENTER THE PILGRIM'S NATIVE LANGUAGE: ");
            language = Utils.getLanguage(input.readString());
            if (language == null) {
                System.out.println("LANGUAGE NOT SUPPORTED, TRY AGAIN. ");
            }
        } while (language == null);

        Pilgrim p = new Pilgrim(id, name, language, isLeader);

        Set<Language> otherLanguages = new HashSet<>();

        String inputString = "";

        do {
            System.out.print("ENTER ANY OTHER LANGUAGE THE PILGRIM KNOWS (TYPE '0' TO END): ");

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

        needsSaving = true;
    }

    private void addGroup(int id) {
        if (id == -1) {
            id = newIdForGroup;
            newIdForGroup++;
        }

        System.out.println("ENTER GROUP's ROOM NAME: ");

        String name = input.readString();

        Group g = new Group(id, name);

        groups.put(id, g);

        needsSaving = true;
    }

    private void modifyPilgrim() {
        System.out.print("ENTER THE ID TO MODIFY A PILGRIM: ");

        int id = input.readNumber();

        addPilgrim(id);

    }

    private void modifyGroup() {
        System.out.print("ENTER THE ID TO MODIFY A GROUP: ");

        int id = input.readNumber();

        addGroup(id);
    }

    private void showPilgrims() {
        if (pilgrims.values().isEmpty()) {
            System.out.println("THERE ARE STILL NO PILGRIMS LOADED.\n");
        } else {
            System.out.println("HERE IS THE PILGRIM'S LIST:\n ");
        }


        for (Pilgrim p: pilgrims.values()) {
            System.out.println(p.toString());
            System.out.println("----------------------");
        }
    }

    private void showGroups() {
        if (groups.values().isEmpty()) {
            System.out.println("THERE ARE STILL NO GROUPS LOADED.|n");
        } else {
            System.out.println("HERE IS THE GROUP'S LIST:\n ");
        }

        for (Group g: groups.values()) {
            System.out.println(g.toString());
            System.out.println("----------------------");
        }
    }

    private void mixAlgorithm() {
        if (pilgrims.values().isEmpty() || groups.values().isEmpty()) {
            System.out.println("THERE AREN'T ENOUGH PILGRIMS/GROUPS FOR STARTING TO MIX.");
            return;
        }

        MixAlgorithm alg = new MixAlgorithm(pilgrims, groups);

        System.out.println("THE ALGORITHM IS LOADING...");

        System.out.println("WOULD YOU LIKE TO START FROM ZERO OR CONTINUE WITH ACTUAL SELECTION? NEW MIX -> 1 | USE ACTUAL -> -1");
        System.out.println("IF YOU START FROM ZERO, YOUR LAST SELECTION WILL BE DELETED.");

        boolean useFilter = 1 != input.readNumber();

        System.out.println("WOULD YOU LIKE TO SHUFFLE THE PILGRIMS BEFORE MIXING? YES -> 1 | NO -> -1");
        System.out.println("IF SHUFFLING IS ENABLED, THE PROGRAM MAY TAKE MORE TIME TO ASSIGN GROUPS.");

        boolean useShuffle = -1 != input.readNumber();

        alg.startAlgorithm(useFilter, useShuffle);

        pilgrims = alg.getPilgrimsMap();

        groups = alg.getGroupsMap();

        System.out.println("ASSIGNMENT SUCCESFULLY ENDED.");

        needsSaving = true;
    }

    private void showMixedGroups() {
        if (pilgrims.values().isEmpty() || groups.values().isEmpty()) {
            System.out.println("THERE AREN'T ENOUGH PILGRIMS/GROUPS FOR STARTING TO MIX.");
            return;
        }

        System.out.print("SHOWING THE ACTUAL LIST OF PILGRIMS: \n");

        int groupsNumber = groups.values().size();

        List<Pilgrim>[] pilgrimsGroups = new LinkedList[groupsNumber];

        for (int i = 0; i < pilgrimsGroups.length; i++) {
            pilgrimsGroups[i] = new LinkedList<>();
        }

        for (Pilgrim p: pilgrims.values()) {
            if (p.getGroupId() == -1) {
                continue;
            }
            pilgrimsGroups[p.getGroupId() - 1].add(p);
        }

        for (int i = 0; i < pilgrimsGroups.length; i++) {
            System.out.println("GROUP NUMBER " + (i+1) + ": has " + pilgrimsGroups[i].size() + " participants");
            for (Pilgrim p: pilgrimsGroups[i]) {
                System.out.print(" - " + p.getName() + (p.isLeader()? ", is leader and" : ",") +  " speaks " + p.getNativeLanguage().name());

                for(Language l: p.getOtherLanguages()) {
                    System.out.print(", " + l.name());
                }
                System.out.println("");
            }
            System.out.println("\n------------------------------------\n");
        }
    }

    private void saveChanges() throws SQLException {
        if (!connection.connectDb()) {
            System.out.println("UNABLE TO CONNECT TO DATABASE.");
            System.out.println("PRESS ENTER TO EXIT THE PROGRAM..");
            input.nextLine();
            System.exit(-1);
        }

        connection.savePilgrims(pilgrims.values());
        connection.saveGroups(groups.values());

        connection.setLastPilgrimId(newIdForPilgrim);
        connection.setLastGroupId(newIdForGroup);

        System.out.println("SAVED INFO SUCCESSFULLY.\n");

        needsSaving = false;

        connection.disconnect();
    }

    private void reloadDatabase() throws SQLException {
        if (!askForSaving()) {
            System.out.println("\nOPERATION CANCELED");
            return;
        }

        loadDBData();

        System.out.println("\nPROGRAM RELOADED SUCCESSFULLY.\n");
    }

    private void endSession() throws SQLException {
        if (!askForSaving()) {
            System.out.println("\nOPERATION CANCELED");
            return;
        }

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
