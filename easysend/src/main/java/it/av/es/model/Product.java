package it.av.es.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Product extends BasicEntity {

    public static final String NAME_FIELD = "name";

    private String name;
    private String shortName;
    @OneToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Price> prices;
    private BigDecimal kilos;
    private BigDecimal volume;
    private Integer itemsInside;
    private Boolean concursOnFreePack;
    private Boolean free;

    public Product() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public void addPrice(Price price) {
        if (prices == null) {
            prices = new ArrayList<Price>();
        }
        prices.add(price);
    }

    public BigDecimal getKilos() {
        return kilos;
    }

    public void setKilos(BigDecimal kilos) {
        this.kilos = kilos;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

   
    public Integer getItemsInside() {
        return itemsInside;
    }

    public void setItemsInside(Integer itemsInside) {
        this.itemsInside = itemsInside;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getConcursOnFreePack() {
        return concursOnFreePack;
    }

    public void setConcursOnFreePack(Boolean concursOnFreePack) {
        this.concursOnFreePack = concursOnFreePack;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

}
