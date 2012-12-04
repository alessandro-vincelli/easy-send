package it.av.es.model;


/**
 * @author Alessandro Vincelli
 *
 */
public enum OrderStatus {

    CREATED,
    INCHARGE ,    
    CANCELLED,
    SENT,
    DELIVERED,
    INVOICE_APPROVED

//    private String value;
//
//    DeliveryType(String value) {
//        this.value = value;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public static List<String> getStringValues()
//    {
//        List<String> stringValues = new ArrayList<String>();
//        for (DeliveryType test : values()) {
//            stringValues.add(test.getValue());
//        }
//
//        return stringValues;
//    }

}