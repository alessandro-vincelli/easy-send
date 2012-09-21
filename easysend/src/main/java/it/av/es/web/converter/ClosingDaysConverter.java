package it.av.es.web.converter;

import it.av.es.model.ClosingDays;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

public class ClosingDaysConverter implements IConverter<ClosingDays>{

    @Override
    public ClosingDays convertToObject(String value, Locale locale) {
        return ClosingDays.valueOf(value);
    }

    @Override
    public String convertToString(ClosingDays value, Locale locale) {
        return value.name();
    }
    
}