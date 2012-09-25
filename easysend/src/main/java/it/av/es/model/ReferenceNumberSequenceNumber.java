package it.av.es.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ReferenceNumberSequenceNumber {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
    @javax.persistence.SequenceGenerator(
        name="SEQ_GEN",
        sequenceName="order_reference_number_sequence"
    )
    private Integer number;
}


