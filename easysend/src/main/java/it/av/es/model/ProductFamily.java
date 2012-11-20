package it.av.es.model;

import javax.persistence.Entity;

@Entity
public class ProductFamily extends BasicEntity {

    public static final String NAME_FIELD = "name";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
