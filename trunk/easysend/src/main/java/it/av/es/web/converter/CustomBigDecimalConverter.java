package it.av.es.web.converter;

import it.av.es.util.NumberUtil;

import java.util.Locale;

import org.apache.wicket.util.convert.converter.BigDecimalConverter;

public class CustomBigDecimalConverter extends BigDecimalConverter {

    public CustomBigDecimalConverter() {
        super();
        setNumberFormat(Locale.ITALIAN, NumberUtil.getItalian());
    }

}
