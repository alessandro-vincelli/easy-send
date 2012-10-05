package it.av.es.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Project extends BasicEntity {

    public static final String NAME_FIELD = "name";
    public static final String USERS_FIELD = "users";
    public static final String ORDERS_FIELD = "order";
    public static final String PRODUCTS_FIELD = "products";

    private String name;
    @ManyToMany(targetEntity = User.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    //@JoinTable(joinColumns = @JoinColumn(name = "EMPER_ID"), inverseJoinColumns = @JoinColumn(name = "EMPEE_ID"))
    private Set<User> users;
    @ManyToMany(targetEntity = User.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Set<User> coordinators;
    @OneToMany
    public Set<Order> orders;
    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Product> products;
    /**
     * % of discount for prePayment
     */
    private Integer prePaymentDiscount;
    private BigDecimal shippingCost;
    /**
     * the number of product for free shipping
     */
    private Integer freeShippingNumber;

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

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void addProduct(Product prj) {
        if (products == null) {
            products = new HashSet<Product>();
        }
        this.products.add(prj);
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

    public Integer getPrePaymentDiscount() {
        return prePaymentDiscount;
    }

    public void setPrePaymentDiscount(Integer prePaymentDiscount) {
        this.prePaymentDiscount = prePaymentDiscount;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Integer getFreeShippingNumber() {
        return freeShippingNumber;
    }

    public void setFreeShippingNumber(Integer freeShippingNumber) {
        this.freeShippingNumber = freeShippingNumber;
    }

}
