package it.av.es.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
@Indexed
@org.hibernate.annotations.Table(appliesTo = "users", indexes = { @org.hibernate.annotations.Index(name = "idx_user_id", columnNames = { "id" }) })
public class User extends BasicEntity implements Comparable<User> {

    public static final String ID = "id";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String EMAIL = "email";
    public static final String USERPROFILE = "userProfile";
    public static final String COUNTRY = "country";
    public static final String LANGUAGE = "language";
    public static final String AVATAR = "avatar";
    public static final String SOCIALTYPE = "socialType";
    public static final String SOCIALUID = "socialUID";
    public static final String CREATIONTIME = "creationTime";
    public static final String SEX = "sex";

    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String passwordSalt;
    @Field(store = Store.YES)
    @Column(nullable = false)
    private String firstname;
    @Field(store = Store.YES)
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String email;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationTime;
    private String address;
    private String zipcode;
    private String province;
    @ManyToOne
    private City city;
    @ManyToOne
    private Country country;
    private String phoneNumber;
    private String faxNumber;
    
    private String codiceFiscaleNumber;
    private String partitaIvaNumber;

    /**
     * used in sign up confirmation
     */
    private boolean confirmed;
    /**
     * used for security reason
     */
    private boolean blocked;

    @ManyToOne(optional = false)
    @ForeignKey(name = "user_to_language_fk")
    private Language language;
    @ManyToOne(optional = false)
    @ForeignKey(name = "user_to_profile_fk")
    @IndexedEmbedded
    private UserProfile userProfile;
    @Version
    private int version;
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Project.class, fetch=FetchType.EAGER )
    @Fetch(FetchMode.SUBSELECT)
    private Set<Project> projects;
    @OneToMany
    public Set<Order> orders;
    @OneToMany(fetch=FetchType.EAGER )
    @Fetch(FetchMode.SUBSELECT)
    public Set<Customer> customers;

    public User() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
        
    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getCodiceFiscaleNumber() {
        return codiceFiscaleNumber;
    }

    public void setCodiceFiscaleNumber(String codiceFiscaleNumber) {
        this.codiceFiscaleNumber = codiceFiscaleNumber;
    }

    public String getPartitaIvaNumber() {
        return partitaIvaNumber;
    }

    public void setPartitaIvaNumber(String partitaIvaNumber) {
        this.partitaIvaNumber = partitaIvaNumber;
    }

    public void addCustomer(Customer customer){
        if(customers ==null){
            customers = new HashSet<Customer>();
        }
        customers.add(customer);
    }
    
    public void addProject(Project prj){
        if(projects ==null){
            projects = new HashSet<Project>();
        }
        projects.add(prj);
    }

    public void addOrder(Order order) {
        if (orders == null) {
            orders = new HashSet<Order>();
        }
        this.orders.add(order);
    }

    @Override
    public int compareTo(User o) {
        if (o.getFirstname().equalsIgnoreCase(getFirstname())) {
            return o.getLastname().compareToIgnoreCase(getLastname());
        } else {
            return o.getFirstname().compareToIgnoreCase(getFirstname());
        }
    }
}
