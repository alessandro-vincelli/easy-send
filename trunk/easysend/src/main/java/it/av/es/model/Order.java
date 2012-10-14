package it.av.es.model;

import it.av.es.EasySendException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity

@Table(name = "orders")
public class Order extends BasicEntity {

    public static final String USER_FIELD = "user";
    public static final String PROJECT_FIELD = "project";
    public static final String PRODUCTSORDERED_FIELD = "productsOrdered";
    public static final String CREATIONTIME_FIELD = "creationTime";
    public static final String NOTES_FIELD = "notes";
    public static final String CUSTOMER_FIELD = "customer";
    @Deprecated
    public static final String ISPREPAYMENT_FIELD = "isPrePayment";
    public static final String PAYMENTTYPE_FIELD = "paymentType";
    public static final String SHIPPINGCOST_FIELD = "shippingCost";
    public static final String REFERNCENUMBER_FIELD = "referenceNumber";
    public static final String ISINCHARGE_FIELD = "isInCharge";
    public static final String ISCANCELLED_FIELD = "isCancelled";

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "customer_fk")
    private Customer customer;
    @ManyToOne//(optional = false)
    private Address shippingAddress;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date deliveryTimeRequired;
    private String notes;
    /**
     * usaere tipo pagamento
     */
    @Deprecated
    private boolean isPrePayment;
    private boolean isInCharge;
    private boolean isCancelled;
    @Column(columnDefinition = "serial")
    @Generated(GenerationTime.INSERT)
    private Integer referenceNumber;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
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
        productsOrdered = new ArrayList<ProductOrdered>();
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
        setProject(project);
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
        setPaymentType(customer.getPaymentType());
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

    @Deprecated
    public Boolean getIsPrePayment() {
        return isPrePayment;
    }

    @Deprecated
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

    public Boolean getIsInCharge() {
        return isInCharge;
    }

    public void setIsInCharge(Boolean isInCharge) {
        this.isInCharge = isInCharge;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
    @Deprecated
    public void setPrePayment(boolean isPrePayment) {
        this.isPrePayment = isPrePayment;
    }

    public Integer getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(Integer referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setInCharge(boolean isInCharge) {
        this.isInCharge = isInCharge;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getNumberOfItemsInProductOrdered() {
        int n = 0;
        for (ProductOrdered p : productsOrdered) {
            n = n + p.getNumber();
        }
        return n;
    }

    public BigDecimal getTotalWeightInProductOrdered(){
        BigDecimal n = BigDecimal.ZERO;
        for (ProductOrdered p : productsOrdered) {
            n = n.add(p.getTotalWeight());
        }
        return n;
    }
    
    public BigDecimal getTotalVolumeInProductOrdered(){
        BigDecimal n = BigDecimal.ZERO;
        for (ProductOrdered p : productsOrdered) {
            n = n.add(p.getTotalVolume());
        }
        return n;
    }
    
    public int getTotalItemsInsideInProductOrdered(){
        int n = 0;
        for (ProductOrdered p : productsOrdered) {
            n = n + p.getTotalItemsInside();
        }
        return n;
    }
    
    public void applyDiscountIfApplicable() {
        ArrayList<ProductOrdered> newList = new ArrayList<ProductOrdered>(productsOrdered.size());
        for (ProductOrdered p : productsOrdered) {
            newList.add(addProductOrdered(p.getProduct(), p.getNumber()));
        }
        setProductsOrdered(newList);
    }

    public void applyFreeShippingCostIfApplicable() {
        if (getNumberOfItemsInProductOrdered() >= getProject().getFreeShippingNumber()) {
            setShippingCost(BigDecimal.ZERO);
        } else {
            setShippingCost(project.getShippingCost());
        }
    }
    
    public ProductOrdered addProductOrdered(Product product, int numberOfProds) {
        ProductOrdered ordered = new ProductOrdered();
        ordered.setProduct(product);
        ordered.setNumber(numberOfProds);
        BigDecimal amount = new BigDecimal(0);
        Currency currency;
        int percentDiscount = 0;
        List<Price> prices = product.getPrices();
        for (Price price : prices) {
            if(numberOfProds >= price.getFromNumber() && numberOfProds <= price.getToNumber()){
                amount= price.getAmount();
                currency = price.getCurrency();
                percentDiscount = price.getPercentDiscount();
            }
        }
        if(amount == BigDecimal.ZERO){
            throw new EasySendException("Price not available");
        }
        ordered.setAmount(amount.multiply(BigDecimal.valueOf(numberOfProds)));
        //apply discount if isPrepayment
        if(getPaymentType().equals(PaymentType.PREPAYMENT) && getProject().getPrePaymentDiscount() > 0){
            BigDecimal discount = ((ordered.getAmount().divide(BigDecimal.valueOf(100))).multiply(BigDecimal.valueOf(getProject().getPrePaymentDiscount())));
            ordered.setAmount(ordered.getAmount().subtract(discount)); 
            percentDiscount = percentDiscount + prePaymentDiscount; 
        }
        ordered.setDiscount(percentDiscount);
        return ordered;
    }
        
    public BigDecimal getTotalAmount(){
        BigDecimal n = BigDecimal.ZERO;
        for (ProductOrdered p : productsOrdered) {
            n = n.add(p.getAmount());
        }
        n = n.add(getShippingCost());
        return n;
    }
    
    public String getCustomerAddressForDisplay(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(customer.getDefaultShippingAddresses().getName());
        buffer.append("\n");
        buffer.append(customer.getDefaultShippingAddresses().getAddress());
        buffer.append("\n");
        buffer.append(customer.getDefaultShippingAddresses().getZipcode());
        buffer.append(customer.getDefaultShippingAddresses().getCity());
        buffer.append("\n");
        buffer.append(customer.getDefaultShippingAddresses().getPhoneNumber());
        buffer.append("\n");
        return buffer.toString();
    }
    
    public String getUserAddressForDisplay(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(user.getFirstname());
        buffer.append(" ");
        buffer.append(user.getLastname());
        buffer.append("\n");
        buffer.append(user.getAddress());
        buffer.append("\n");
        buffer.append(user.getZipcode());
        buffer.append(user.getCity());
        buffer.append("\n");
        buffer.append(user.getPhoneNumber());
        buffer.append("\n");
        return buffer.toString();
    }
    
}
