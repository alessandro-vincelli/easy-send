package it.av.es.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Project extends BasicEntity {

    private String name;
    @ManyToMany(targetEntity = User.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch=FetchType.EAGER)
    //@JoinTable(joinColumns = @JoinColumn(name = "EMPER_ID"), inverseJoinColumns = @JoinColumn(name = "EMPEE_ID"))
    private Set<User> users;
    @OneToMany(mappedBy="orders")
    public Set<Order> orders;
    @OneToMany(mappedBy="products")
    public Set<Product> products;

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

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
    
    public void addOrder(Order order) {
        if (orders == null) {
            orders = new HashSet<Order>();
        }
        this.orders.add(order);
    }

    public void addUser(User user) {
        if (users == null) {
            users = new HashSet<User>();
        }
        this.users.add(user);
    }

}
