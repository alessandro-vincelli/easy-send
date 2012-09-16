package it.av.es.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Project extends BasicEntity {

    private String name;
    @OneToMany
    @JoinColumn(name="project_fk") //we need to duplicate the physical information
    private Set<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUser(User user){
        if (users == null) {
            users = new HashSet<User>();
        }
        this.users.add(user);        
    }

}
