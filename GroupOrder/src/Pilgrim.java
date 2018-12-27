import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pilgrim {

    private int id;
    private String name;
    private Language nativeLanguage;
    private Set<Language> otherLanguages;
    private int groupId;
    private boolean isLeader;

    public Pilgrim(int id, String name, Language nativeLanguage, boolean isLeader) {
        this.id = id;
        this.name = name;
        this.nativeLanguage = nativeLanguage;
        otherLanguages = new HashSet<>();
        groupId = -1;
        this.isLeader = isLeader;
    }

    public Pilgrim(int id, String name, Language nativeLanguage, int groupId, boolean isLeader) {
        this.id = id;
        this.name = name;
        this.nativeLanguage = nativeLanguage;
        otherLanguages = new HashSet<>();
        this.groupId = groupId;
        this.isLeader = isLeader;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Language getNativeLanguage() {
        return nativeLanguage;
    }

    public void setNativeLanguage(Language nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }

    public Set<Language> getOtherLanguages() {
        return otherLanguages;
    }

    public void setOtherLanguages(Set<Language> otherLanguages) {
        this.otherLanguages = otherLanguages;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    @Override
    public String toString() {
        String start = "Pilgrim with id " + id + ", named " + name + ".\n" +
                "Talks " + nativeLanguage;
        for (Language e : otherLanguages) {
            start.concat(", " + e.toString());
        }
        start = start.concat(groupId == -1 ? "\nStill has no group." : "\nWorks in group " + groupId + ".");
        start = start.concat("\nThis pilgrim is " + (isLeader ? "" : "not ") + "a leader.");
        return start;
    }
}
