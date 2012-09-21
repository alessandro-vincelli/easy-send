package it.av.es.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * sigle delle provincie
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
public class Provincia {
    @Id
    private String sigla;

    public Provincia() {
        super();
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

}