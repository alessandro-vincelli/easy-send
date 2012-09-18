package it.av.es.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

/**
 * countries
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
@org.hibernate.annotations.Table(appliesTo = "city", indexes = { @org.hibernate.annotations.Index(name = "idx_city_country", columnNames = { "country" }), @org.hibernate.annotations.Index(name = "idx_city_name", columnNames = { "name" }) })
public class City extends BasicEntity implements Comparable<City> {

    public final static String NAME_FIELD = "name";

    public static final String COUNTRY_FIELD = "country";

    @ManyToOne(optional = false)
    @ForeignKey(name = "city_to_country_fk")
    private Country country;
    private String nameSimplified;
    private String name;
    private String region;
    private String latitude;
    private String longitude;

    public City() {
        super();
    }

    public City(String name) {
        super();
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getNameSimplified() {
        return nameSimplified;
    }

    public void setNameSimplified(String nameSimplified) {
        this.nameSimplified = nameSimplified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(City o) {
        return this.getName().compareTo(o.getName());
    }
}