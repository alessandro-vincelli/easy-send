package it.av.es.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;




/**
 * countries
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
@org.hibernate.annotations.Table(appliesTo = "citta", indexes = { @org.hibernate.annotations.Index(name = "idx_citta_comune", columnNames = { "comune" }), @org.hibernate.annotations.Index(name = "idx_citta_provincia", columnNames = { "provincia" }), @org.hibernate.annotations.Index(name = "idx_citta_cap", columnNames = { "cap" }) })
public class Citta{
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    @javax.persistence.SequenceGenerator(
        name="SEQ_GEN",
        sequenceName="citta_id_sequence"
    )
    //@Column(columnDefinition=" DEFAULT nextval('citta_id_sequence'::regclass)")
    private String id;
    private String provincia;
    private String comune;
    private String comune2;
    private String frazione;
    private String frazione2;
    private String topo;
    private String topo2;
    private String dugt;
    private String numeroCivicio;
    private String cap;
    

    public Citta() {
        super();
    }


    public String getProvincia() {
        return provincia;
    }


    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }


    public String getComune() {
        return comune;
    }


    public void setComune(String comune) {
        this.comune = comune;
    }


    public String getComune2() {
        return comune2;
    }


    public void setComune2(String comune2) {
        this.comune2 = comune2;
    }


    public String getFrazione() {
        return frazione;
    }


    public void setFrazione(String frazione) {
        this.frazione = frazione;
    }


    public String getFrazione2() {
        return frazione2;
    }


    public void setFrazione2(String frazione2) {
        this.frazione2 = frazione2;
    }


    public String getTopo() {
        return topo;
    }


    public void setTopo(String topo) {
        this.topo = topo;
    }


    public String getTopo2() {
        return topo2;
    }


    public void setTopo2(String topo2) {
        this.topo2 = topo2;
    }


    public String getDugt() {
        return dugt;
    }


    public void setDugt(String dugt) {
        this.dugt = dugt;
    }


    public String getNumeroCivicio() {
        return numeroCivicio;
    }


    public void setNumeroCivicio(String numeroCivicio) {
        this.numeroCivicio = numeroCivicio;
    }


    public String getCap() {
        return cap;
    }


    public void setCap(String cap) {
        this.cap = cap;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    
}