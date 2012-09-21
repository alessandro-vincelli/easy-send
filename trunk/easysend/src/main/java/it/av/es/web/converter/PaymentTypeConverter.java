package it.av.es.web.converter;

import it.av.es.model.PaymentType;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

public class PaymentTypeConverter implements IConverter<PaymentType>{

    @Override
    public PaymentType convertToObject(String value, Locale locale) {
        return PaymentType.valueOf(value);
    }

    @Override
    public String convertToString(PaymentType value, Locale locale) {
        return value.name();
    }
    
}