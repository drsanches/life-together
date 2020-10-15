package ru.drsanches.user_service.data.user;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import ru.drsanches.user_service.data.friends.Friends;
import javax.persistence.Column;
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

    @Column(unique = true)
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private boolean enabled;

    @OneToMany(mappedBy = "fromUser", fetch= FetchType.EAGER)
    private Set<Friends> outgoingRequests;

    @OneToMany(mappedBy = "toUser", fetch= FetchType.EAGER)
    private Set<Friends> incomingRequests;

    public User() {}

    public User(String id) {
        this.id = id;
        this.enabled = true;
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

    public boolean isEnabled() {
        return enabled;
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

    public void setEnable(boolean enabled) {
        this.enabled = enabled;
    }
}