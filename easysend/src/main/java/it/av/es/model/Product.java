package it.av.es.model;

import javax.persistence.Entity;

@Entity
public class Product extends BasicEntity {

    private String name;

    public Product() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
