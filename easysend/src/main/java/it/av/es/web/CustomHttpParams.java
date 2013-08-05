package it.av.es.web;

import it.av.es.model.Order;

import org.apache.wicket.request.mapper.parameter.PageParameters;

public class CustomHttpParams {

    public static final String CUSTOMER_ID = "customerId";
    public static final String USER_ID = "userId";
    public static final String ORDER_ID = "orderId";

    
    /**
     * Creates parameters for the given order.
     * 
     * @param order
     * @return parameters
     */
    public static PageParameters createParamsForOrder(Order o) {
        PageParameters parameters = new PageParameters();
        parameters.add(ORDER_ID, o.getId());
        return parameters;
    }
}
