public class Group {

    private int id;
    private String name;
    private Pilgrim leader;
    private Pilgrim[] party;

    public Group(Pilgrim leader, Pilgrim[] party) {
        this.leader = leader;
        this.party = party;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Pilgrim getLeader() {
        return leader;
    }

    public Pilgrim[] getParty() {
        return party;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(Pilgrim leader) {
        this.leader = leader;
    }

    public void setParty(Pilgrim[] party) {
        this.party = party;
    }
}