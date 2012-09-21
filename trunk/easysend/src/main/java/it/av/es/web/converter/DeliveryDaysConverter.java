package it.av.es.web.converter;

import it.av.es.model.DeliveryDays;
import it.av.es.model.DeliveryType;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

public class DeliveryDaysConverter implements IConverter<DeliveryDays>{

    @Override
    public DeliveryDays convertToObject(String value, Locale locale) {
        return DeliveryDays.valueOf(value);
    }

    @Override
    public String convertToString(DeliveryDays value, Locale locale) {
        return value.name();
    }
    
}