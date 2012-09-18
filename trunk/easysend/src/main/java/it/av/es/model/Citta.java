package it.av.es.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * countries
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
@org.hibernate.annotations.Table(appliesTo = "citta", indexes = { @org.hibernate.annotations.Index(name = "idx_citta_comune", columnNames = { "comune" }), @org.hibernate.annotations.Index(name = "idx_citta_provincia", columnNames = { "provincia" }), @org.hibernate.annotations.Index(name = "idx_citta_cap", columnNames = { "cap" }) })
public class Citta{
    
    @Id
    private String istat;
    private String comune;
    private String provincia;
    private String regione;
    private String prefisso;
    private String cap;
    private String codFisco;
    private String abitanti;
    private String link;

    public Citta() {
        super();
    }

    public String getIstat() {
        return istat;
    }

    public void setIstat(String istat) {
        this.istat = istat;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getPrefisso() {
        return prefisso;
    }

    public void setPrefisso(String prefisso) {
        this.prefisso = prefisso;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }


    public String getCodFisco() {
        return codFisco;
    }

    public void setCodFisco(String codFisco) {
        this.codFisco = codFisco;
    }

    public String getAbitanti() {
        return abitanti;
    }

    public void setAbitanti(String abitanti) {
        this.abitanti = abitanti;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    
}