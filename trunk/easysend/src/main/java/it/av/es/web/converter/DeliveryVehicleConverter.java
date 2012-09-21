package it.av.es.web.converter;

import it.av.es.model.DeliveryVehicle;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

public class DeliveryVehicleConverter implements IConverter<DeliveryVehicle>{

    @Override
    public DeliveryVehicle convertToObject(String value, Locale locale) {
        return DeliveryVehicle.valueOf(value);
    }

    @Override
    public String convertToString(DeliveryVehicle value, Locale locale) {
        return value.name();
    }
    
}