package it.av.es.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    @OneToMany
    public Set<Recipient> recipients;

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
    
    public Set<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<Recipient> recipients) {
        this.recipients = recipients;
    }
    
    public void addRecipient(Recipient recipient){
        if(recipients ==null){
            recipients = new HashSet<Recipient>();
        }
        recipients.add(recipient);
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
