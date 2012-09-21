package it.av.es.web.converter;

import it.av.es.model.ClosingRange;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;

public class ClosingRangeConverter implements IConverter<ClosingRange>{

    @Override
    public ClosingRange convertToObject(String value, Locale locale) {
        return ClosingRange.valueOf(value);
    }

    @Override
    public String convertToString(ClosingRange value, Locale locale) {
        return value.name();
    }
    
}