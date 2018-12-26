import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {

    private boolean autoCommit = true;
    private int lastPilgrimId = 0;
    private int lastGroupId = 0;
    private Connection conn = null;

    private Connection initDbConnection() throws SQLException {

        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/test.db";

        conn = DriverManager.getConnection(url);
        if (conn != null) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("A new database has been created.");

            this.autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

        }

        return conn;
    }

    private void createTable() throws SQLException {
        String pilgrimQuery = "CREATE TABLE IF NOT EXISTS pilgrim (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "nativeLanguage TEXT NOT NULL, " +
                "isLeader INTEGER NOT NULL" +
                "groupId INTEGER" +
                ");";

        String groupQuery = "CREATE TABLE IF NOT EXISTS groupRoom (" +
                "id INTEGER PRIMARY KEY, " +
                "roomId TEXT NOT NULL, " +
                ");";

        String languagesQuery = "CREATE TABLE IF NOT EXISTS spokenLanguages (" +
                "pilgrimId INTEGER NOT NULL, " +
                "language TEXT NOT NULL, " +
                "PRIMARY KEY (pilgrimId, language), " +
                "FOREIGN KEY (pilgrimId) REFERENCES pilgrim(id) ON DELETE RESTRICT ON UPDATE CASCADE " +
                ");";

        Statement st = conn.createStatement();
        st.execute(pilgrimQuery);

        st = conn.createStatement();
        st.execute(groupQuery);

        st = conn.createStatement();
        st.execute(languagesQuery);

        conn.commit();

    }

    public Map<Integer, Group> loadGroupsResources() throws SQLException {
        String query = "SELECT * FROM groupRoom";
        PreparedStatement prepSt = conn.prepareStatement(query);

        ResultSet rs = prepSt.executeQuery();

        Map<Integer, Group> map = new HashMap<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("roomId");

            lastGroupId = id > lastGroupId ? id : lastGroupId;

            map.put(id, new Group(id, name));
        }

        return map;
    }

    public Map<Integer, Pilgrim> loadPilgrimResources() throws SQLException {
        String query = "SELECT id, name, nativeLanguage, isLeader AS maxId FROM pilgrim";
        PreparedStatement prepSt = conn.prepareStatement(query);

        ResultSet rs = prepSt.executeQuery();

        Map<Integer, Pilgrim> map = new HashMap<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String languageString = rs.getString("nativeLanguage");
            boolean isLeader = rs.getBoolean("isLeader");
            lastPilgrimId = id > lastPilgrimId ? id : lastPilgrimId;

            Language language = Utils.getLanguage(languageString);

            if (language == null) {
                System.out.println("THE PILGRIM WITH ID " + id + " HAS A PROBLEM WITH HIS NATIVE LANGUAGE " + languageString);
            }

            Pilgrim newPilgrim = new Pilgrim(id, name, language, isLeader);

            map.put(id, newPilgrim);
        }

        query = "SELECT * FROM spokenLanguages";

        prepSt = conn.prepareStatement(query);

        rs = prepSt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("pilgrimId");
            Pilgrim pilgrim = map.get(id);

            if (pilgrim == null) {
                System.out.println("THERE IS A PILGRIM IN SPOKENLANGUAGES THAT DOES NOT EXIST. ID = " + id);
                continue;
            }

            String languageString = rs.getString("language");

            try {
                Language language = Language.valueOf(languageString);
                if (language != null) {
                    List<Language> otherLanguage = pilgrim.getOtherLanguages();
                    otherLanguage.add(language);
                    pilgrim.setOtherLanguage(otherLanguage);
                }

            } catch (IllegalArgumentException e) {
                System.out.println("THE PILGRIM WITH ID " + id + " HAS A PROBLEM WITH HIS NATIVE LANGUAGE " + languageString);
            }

            map.put(id, pilgrim);
        }


        return map;
    }

    private void insertPilgrim(Pilgrim p) throws SQLException {
        String pilgrimQuery = "INSERT INTO pilgrim (id, name, nativeLanguage, isLeader, groupId) VALUES (?,?,?,?,?)";
        String languagesQuery = "INSERT INTO spokenLanguages (pilgrimId, language) VALUES (?,?)";

        PreparedStatement pilgrimSt = conn.prepareStatement(pilgrimQuery);
        PreparedStatement langSt = conn.prepareStatement(languagesQuery);

        pilgrimSt.setInt(1, p.getId());
        pilgrimSt.setString(2, p.getName());
        pilgrimSt.setString(3, p.getNativeLanguage().getShortValue());
        pilgrimSt.setInt(4, p.isLeader() ? 1 : 0);
        pilgrimSt.setInt(5, p.getGroupId());
        pilgrimSt.executeUpdate();

        langSt.setInt(1, p.getId());
        for (Language l : p.getOtherLanguages()) {
            langSt.setString(2, l.getShortValue());
            langSt.executeUpdate();
        }
    }

    private void updatePilgrim(Pilgrim p) throws SQLException {
        String pilgrimQuery = "UPDATE pilgrim SET name = ?, nativeLanguage = ?, groupId = ? WHERE id = ?";
        String languagesQuery = "INSERT INTO spokenLanguages (pilgrimId, language) VALUES (?,?)";
        String deleteQuery = "DELETE FROM spokenLanguages WHERE pilgrimId = ?";

        PreparedStatement pilgrimSt = conn.prepareStatement(pilgrimQuery);
        PreparedStatement langSt = conn.prepareStatement(languagesQuery);
        PreparedStatement deleteSt = conn.prepareStatement(deleteQuery);

        pilgrimSt.setString(1, p.getName());
        pilgrimSt.setString(2, p.getNativeLanguage().getShortValue());
        pilgrimSt.setInt(3, p.getGroupId());
        pilgrimSt.setInt(4, p.getId());
        pilgrimSt.executeUpdate();

        deleteSt.setInt(1, p.getId());
        deleteSt.executeUpdate();

        langSt.setInt(1, p.getId());
        for (Language l : p.getOtherLanguages()) {
            langSt.setString(2, l.getShortValue());
            langSt.executeUpdate();
        }
    }

    public void savePilgrims(Pilgrim[] pilgrims) throws SQLException {
        for (Pilgrim p : pilgrims) {
            if (p.getId() <= lastPilgrimId) {
                updatePilgrim(p);
            } else {
                insertPilgrim(p);
            }
        }
    }

    private void insertGroup(Group group) throws SQLException {
        String query = "INSERT INTO groupRoom (id, roomId) VALUES (?,?)";
        PreparedStatement prepSt = conn.prepareStatement(query);

        prepSt.setInt(1, group.getId());
        prepSt.setString(2, group.getRoomName());

        prepSt.executeUpdate();
    }

    private void updateGroup(Group group) throws SQLException {
        String query = "UPDATE groupRoom SET roomId = ? WHERE id = ?";
        PreparedStatement prepSt = conn.prepareStatement(query);

        prepSt.setString(1, group.getRoomName());
        prepSt.setInt(2, group.getId());

        prepSt.executeUpdate();
    }

    public void saveGroup(Group group) throws SQLException {
        if (group.getId() <= lastGroupId) {
            updateGroup(group);
        } else {
            insertGroup(group);
        }
    }

    public boolean connectDb() throws SQLException {
        try {
            conn = initDbConnection();

            if (conn == null) {
                return false;
            }

            createTable();

        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }

        return true;
    }

    public void onError() {
        try {
            conn.rollback();
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws SQLException {
        conn.commit();
        conn.close();
    }

    public int getLastPilgrimId() {
        return lastPilgrimId;
    }

    public void setLastPilgrimId(int lastPilgrimId) {
        this.lastPilgrimId = lastPilgrimId;
    }

    public int getLastGroupId() {
        return lastGroupId;
    }

    public void setLastGroupId(int lastGroupId) {
        this.lastGroupId = lastGroupId;
    }
}
