package it.av.es.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ProductOrdered extends BasicEntity {

    public static final String NAME_FIELD = "name";

    private String name;
    @ManyToOne
    @JoinColumn(name = "product_fk")
    private Product product;
    private int number;
    private BigDecimal amount;
    private int discount;

    public ProductOrdered() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public BigDecimal getTotalWeight(){
        return getProduct().getKilos().multiply(BigDecimal.valueOf(number));
    }
    
    public BigDecimal getTotalVolume(){
        return getProduct().getVolume().multiply(BigDecimal.valueOf(number));
    }
    
    public int getTotalItemsInside(){
        return getProduct().getItemsInside() * number;
    }
}
