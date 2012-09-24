package it.av.es.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "orders")
public class Order extends BasicEntity {

    
    public static final String USER_FIELD = "user";
    public static final String PROJECT_FIELD = "project";
    public static final String PRODUCTSORDERED_FIELD = "productsOrdered";
    public static final String CREATIONTIME_FIELD = "creationTime";
    public static final String NOTES_FIELD = "notes";
    public static final String CUSTOMER_FIELD = "customer";

    @ManyToOne
    @JoinColumn(name = "customer_fk")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;
    @ManyToOne
    @JoinColumn(name = "project_fk")
    private Project project;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<ProductOrdered> productsOrdered;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationTime;
    private String notes;
    private Boolean isPrePayment;
    /**
     * % of discount for prePayment, applied From parent Project
     */
    private Integer prePaymentDiscount;
    /**
     *  applied From parent Project
     */
    private BigDecimal shippingCost;
    /**
     * the number of product for free shipping,  applied From parent Project
     */
    private Integer freeShippingNumber;

    public Order() {
        super();
        customer = new Customer();
    }
    
    /**
     * Creates order and set fields from Project 
     * 
     * @param project
     */
    public Order(Project project) {
        this();
        setFreeShippingNumber(project.getFreeShippingNumber());
        setPrePaymentDiscount(project.getPrePaymentDiscount());
        setShippingCost(project.getShippingCost());
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

    public void addProductOrdered(ProductOrdered productOrdered) {
        if (productsOrdered == null) {
            productsOrdered = new ArrayList<ProductOrdered>();
        }
        productsOrdered.add(productOrdered);
    }

    public void removeProductOrdered(ProductOrdered productOrdered) {
        productsOrdered.remove(productOrdered);
    }

    public List<ProductOrdered> getProductsOrdered() {
        return productsOrdered;
    }

    public void setProductsOrdered(List<ProductOrdered> productsOrdered) {
        this.productsOrdered = productsOrdered;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsPrePayment() {
        return isPrePayment;
    }

    public void setIsPrePayment(Boolean isPrePayment) {
        this.isPrePayment = isPrePayment;
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
