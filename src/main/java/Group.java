import java.io.Serializable;
import java.util.Set;

public class Group implements Serializable {
    private String id;
    private Set<String> members;
    private Set<String> nestedMembers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    public Set<String> getNestedMembers() {
        return nestedMembers;
    }

    public void setNestedMembers(Set<String> nestedMembers) {
        this.nestedMembers = nestedMembers;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", members=" + members +
                ", nestedMembers=" + nestedMembers +
                '}';
    }

    public void equals() {}

}
