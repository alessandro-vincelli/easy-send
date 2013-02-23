package it.av.es.web.converter;

import it.av.es.model.PaymentTypePerProject;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

public class PaymentTypePerProjectConverter implements IChoiceRenderer<PaymentTypePerProject> {

    @Override
    public Object getDisplayValue(PaymentTypePerProject object) {
        return object.getName();
    }

    @Override
    public String getIdValue(PaymentTypePerProject object, int index) {
        return object.getId();
    }

}