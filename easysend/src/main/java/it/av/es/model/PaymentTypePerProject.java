package it.av.es.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Alessandro Vincelli
 *
 */
@Entity
@Table
public class PaymentTypePerProject extends BasicEntity {

    public static final String NAME_FIELD = "name";
    public static final String DISCOUNT_FIELD = "discount";

    /**
     * payment type name
     */
    private String name;
    /**
     * discount in percentage
     */
    private Integer discount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
}