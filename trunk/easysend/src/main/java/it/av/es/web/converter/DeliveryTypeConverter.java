package it.av.es.web.converter;

import it.av.es.model.DeliveryType;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

public class DeliveryTypeConverter implements IConverter<DeliveryType>{

    @Override
    public DeliveryType convertToObject(String value, Locale locale) {
        return DeliveryType.valueOf(value);
    }

    @Override
    public String convertToString(DeliveryType value, Locale locale) {
        return value.name();
    }
    
}