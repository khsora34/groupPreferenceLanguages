import java.util.HashSet;
import java.util.Set;

public class Group {

    private int id;
    private String roomName;
    private int numberOfLeaders;
    private int numberOfParticipants;
    private Set<Language> allLanguages;

    public Group(int id, String roomName) {
        this.id = id;
        this.roomName = roomName;
        numberOfParticipants = 0;
        numberOfLeaders = 0;
        allLanguages = new HashSet<>();
    }

    public Group(int id, String roomName, int numberOfParticipants, int numberOfLeaders) {
        this.id = id;
        this.roomName = roomName;
        this.numberOfParticipants = numberOfParticipants;
        this.numberOfLeaders = numberOfLeaders;
        allLanguages = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public int getNumberOfLeaders() {
        return numberOfLeaders;
    }

    public Set<Language> getAllLanguages() {
        return allLanguages;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public void setNumberOfLeaders(int numberOfLeaders) {
        this.numberOfLeaders = numberOfLeaders;
    }

    public void setAllLanguages(Set<Language> allLanguages) {
        this.allLanguages = allLanguages;
    }

    @Override
    public String toString() {
        String start = "Room with id " + id + " named " + roomName + ".\n" +
                "On the moment, it has " + numberOfParticipants + " participants, where " + numberOfLeaders + " of them are leaders.\n";

        if (allLanguages == null || allLanguages.isEmpty()) {
            start = start.concat("Still no languages spoken here.");
        } else {
            start = start.concat("The languages spoken in this room are ");
            for (Language l: allLanguages) {
                start = start.concat(l.name() + ", ");
            }
        }

        return start;
    }
}