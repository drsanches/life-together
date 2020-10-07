package ru.drsanches.user_service.data.user;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import ru.drsanches.user_service.data.friends.Friends;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

//TODO: do something with entity name
@Entity(name = "ru.drsanches.user_service.data.user.User")
@Table(name="user_profile")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    private String id;

    private String username;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "fromUser", fetch= FetchType.EAGER)
    private Set<Friends> outgoingRequests;

    @OneToMany(mappedBy = "toUser", fetch= FetchType.EAGER)
    private Set<Friends> incomingRequests;

    public User() {}

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Set<Friends> getOutgoingRequests() {
        return outgoingRequests;
    }

    public Set<Friends> getIncomingRequests() {
        return incomingRequests;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}