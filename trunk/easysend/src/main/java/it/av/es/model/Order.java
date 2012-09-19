package it.av.es.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "orders")
public class Order extends BasicEntity {
    
    public static final String NAME_FIELD = "name";
    public static final String USER_FIELD = "user";
    public static final String PROJECT_FIELD = "project";
    public static final String PRODUCT_FIELD = "product";
    public static final String CREATIONTIME_FIELD = "creationTime";
    public static final String PRODUCTNUMBER_FIELD = "productNumber";

    @ManyToOne
    @JoinColumn(name = "customer_fk")
    private Customer customer;
    private String name;
    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;
    @ManyToOne
    @JoinColumn(name = "project_fk")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "product_fk")
    private Product product;
    private int productNumber;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationTime;

    public Order() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(int productNumber) {
        this.productNumber = productNumber;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


}
