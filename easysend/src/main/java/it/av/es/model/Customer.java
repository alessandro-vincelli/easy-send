package it.av.es.model;

import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Customer extends BasicEntity {

    public static final String CORPORATENAME_FIELD = "corporateName";
    public static final String ADDRES_FIELD = "address";
    public static final String ZIPCODE_FIELD = "zipcode";
    public static final String PROVINCE_FIELD = "province";
    public static final String CITY_FIELD = "city";
    public static final String COUNTRY_FIELD = "country";
    public static final String USER_FIELD = "user";

    private String corporateName;
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
    private String email;
    private String phoneNumber;
    private String faxNumber;
    
    private String codiceFiscaleNumber;
    private String partitaIvaNumber;
    private String paymentType;
    private String iban;
    private String bankName;
    
    private ClosingDays closingDay;
    private ClosingRange closingRange;
    private DeploingType deployngType;
    private String loadDateTime;
    private String deliveryNote;
    @ElementCollection
    private Set<DeliveryDays> deliveryDay;
    private DeliveryType deliveryType;
    private DeliveryVehicle deliveryVehicle;
    private boolean phoneForewarning;
    

    public Customer() {
        super();
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


    public String getCorporateName() {
        return corporateName;
    }


    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
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


    public String getPaymentType() {
        return paymentType;
    }


    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }


    public String getIban() {
        return iban;
    }


    public void setIban(String iban) {
        this.iban = iban;
    }


    public String getBankName() {
        return bankName;
    }


    public void setBankName(String bankName) {
        this.bankName = bankName;
    }


    public ClosingDays getClosingDay() {
        return closingDay;
    }


    public void setClosingDay(ClosingDays closingDay) {
        this.closingDay = closingDay;
    }


    public ClosingRange getClosingRange() {
        return closingRange;
    }


    public void setClosingRange(ClosingRange closingRange) {
        this.closingRange = closingRange;
    }


    public DeploingType getDeployngType() {
        return deployngType;
    }


    public void setDeployngType(DeploingType deployngType) {
        this.deployngType = deployngType;
    }


    public String getLoadDateTime() {
        return loadDateTime;
    }


    public void setLoadDateTime(String loadDateTime) {
        this.loadDateTime = loadDateTime;
    }


    public String getDeliveryNote() {
        return deliveryNote;
    }


    public void setDeliveryNote(String deliveryNote) {
        this.deliveryNote = deliveryNote;
    }


    public Set<DeliveryDays> getDeliveryDay() {
        return deliveryDay;
    }


    public void setDeliveryDay(Set<DeliveryDays> deliveryDay) {
        this.deliveryDay = deliveryDay;
    }


    public DeliveryType getDeliveryType() {
        return deliveryType;
    }


    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }


    public DeliveryVehicle getDeliveryVehicle() {
        return deliveryVehicle;
    }


    public void setDeliveryVehicle(DeliveryVehicle deliveryVehicle) {
        this.deliveryVehicle = deliveryVehicle;
    }


    public boolean isPhoneForewarning() {
        return phoneForewarning;
    }


    public void setPhoneForewarning(boolean phoneForewarning) {
        this.phoneForewarning = phoneForewarning;
    }

}
