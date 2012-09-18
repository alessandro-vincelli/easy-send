package it.av.es.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Recipient extends BasicEntity {

    public static final String NAME_FIELD = "name";
    public static final String ADDRES_FIELD = "address";
    public static final String ZIPCODE_FIELD = "zipcode";
    public static final String PROVINCE_FIELD = "province";
    public static final String CITY_FIELD = "city";
    public static final String COUNTRY_FIELD = "country";
    public static final String USER_FIELD = "user";

    private String name;
    private String address;
    private String zipcode;
    private String province;
    @ManyToOne
    private City city;
    @ManyToOne
    private Country country;
    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;

    public Recipient() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
