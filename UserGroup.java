import java.util.*;

public class UserGroup {
    private String groupID;
    private List<Object> members;

    public UserGroup(String groupID) {
        this.groupID = groupID;
        this.members = new ArrayList<>();
    }

    public String getGroupID() {
        return this.groupID;
    }

    public List<Object> getMembers() {
        return this.members;
    }


    public void addMember(Object member) {
        if(!this.members.contains(member)) {
            this.members.add(member);
        }
    }

    public List<Object> getMember() {
        return this.members;
    }
}
