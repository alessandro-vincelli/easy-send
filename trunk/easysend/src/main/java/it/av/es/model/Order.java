package it.av.es.model;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity

@Table(name = "orders")
@XmlRootElement
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
    public static final String REFERENCENUMBER_FIELD = "referenceNumber";
    public static final String ISINCHARGE_FIELD = "isInCharge";
    public static final String ISCANCELLED_FIELD = "isCancelled";

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "customer_fk")
    private Customer customer;
    @ManyToOne//(optional = false)
    private Address shippingAddress;
    @ManyToOne
    @JoinColumn(name = "user_fk")
    @XmlTransient
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
    @Enumerated(EnumType.STRING)
    private DeliveryTimeRequiredType deliveryTimeRequiredType;
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
    @XmlTransient
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

    @XmlTransient
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

    public Date getDeliveryTimeRequired() {
        return deliveryTimeRequired;
    }

    public void setDeliveryTimeRequired(Date deliveryTimeRequired) {
        this.deliveryTimeRequired = deliveryTimeRequired;
    }

    public DeliveryTimeRequiredType getDeliveryTimeRequiredType() {
        return deliveryTimeRequiredType;
    }

    public void setDeliveryTimeRequiredType(DeliveryTimeRequiredType deliveryTimeRequiredType) {
        this.deliveryTimeRequiredType = deliveryTimeRequiredType;
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
    
    public BigDecimal getTotalAmount(){
        BigDecimal n = BigDecimal.ZERO;
        for (ProductOrdered p : productsOrdered) {
            n = n.add(p.getAmount());
        }
        n = n.add(getShippingCost());
        return n;
    }
    
    /**
     * return the total number of pack for the given product 
     * 
     * @param p product
     * @return
     */
    public int getTotalProductforGivenProduct(Product p){
        int n = 0;
        for (ProductOrdered po : getProductsOrdered()) {
            if(po.getProduct().equals(p)){
                n = n + po.getNumber();    
            }
        }
        return n;
    }
    
    /**
     * return the total number of pack for the given product 
     * 
     * @param p product
     * @return
     */
    public int getTotalProductforGivenProductFamily(ProductFamily pf){
        int n = 0;
        for (ProductOrdered po : getProductsOrdered()) {
            if(po.getProduct().getProductFamily() != null && pf != null &&po.getProduct().getProductFamily().equals(pf)){
                n = n + po.getNumber();    
            }
        }
        return n;
    }
    
    /**
     * check if this order can be cancelled
     * @return
     */
    public boolean canBeCancelled(){
        if(isInCharge || isCancelled) return false;
        return true;
    }
    
    /**
     * return true if the order permits an extra free item 
     * 
     * @return
     */
    public boolean isAllowedFreeItem(){
        int items = 0;
        for (ProductOrdered p : productsOrdered) {
            if(p.getProduct().getConcursOnFreePack()){
                items = items + p.getNumber();
            }
        }
        if(items >= getProject().getNumberOfItemsPerFreeProduct()){
            return true;    
        }
        return false;
    }
    
    /**
     * return true if the order permits a pre payment discount
     * 
     * @return
     */
    public boolean isPrePaymentDiscountApplicable(){
        if (this.getPaymentType().equals(PaymentType.PREPAYMENT) && this.getProject().getPrePaymentDiscount() > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * return true if the order permits a free shipping cost
     * 
     * @return
     */
    public boolean isFreeShippingCostApplicable(){
        if (this.getNumberOfItemsInProductOrdered() >= this.getProject().getFreeShippingNumber()) {
            return true;
        }
        return false;
    }
    

    /**
     * return true if the order already contains a free product
     * 
     * @return
     */
    public boolean containsFreeOrder() {
        for (ProductOrdered p : getProductsOrdered()) {
            if (p.getProduct().getFree()) {
                return true;
            }
        }
        return false;
    }

    
    public String getCustomerAddressForDisplay(){
        StringBuffer buffer = new StringBuffer();
        if(getShippingAddress() != null){
            buffer.append(getShippingAddress().getName());
            buffer.append("\n");
            buffer.append(getShippingAddress().getAddress());
            buffer.append("\n");
            buffer.append(getShippingAddress().getZipcode());
            buffer.append(" ");
            buffer.append(getShippingAddress().getCity());
            buffer.append("\n");
            buffer.append(StringUtils.isNotBlank(getShippingAddress().getPhoneNumber())?getShippingAddress().getPhoneNumber():"");
            buffer.append("\n");
        }
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
        buffer.append(" ");
        buffer.append(user.getCity());
        buffer.append("\n");
        buffer.append(user.getPhoneNumber());
        buffer.append("\n");
        return buffer.toString();
    }
    
}
