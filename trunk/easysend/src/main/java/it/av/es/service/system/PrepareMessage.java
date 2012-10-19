/**
 * 
 */
package it.av.es.service.system;

import it.av.es.model.Order;
import it.av.es.model.ProductOrdered;
import it.av.es.model.User;
import it.av.es.util.DateUtil;
import it.av.es.util.NumberUtil;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Utility class to create e-mail message
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class PrepareMessage {

    @Autowired
    private MessageSource messageSource;

    public String mailTextPasswordRecover(User user, String newPassword, Locale locale) {
        StringBuffer textBody = new StringBuffer();
        textBody.append("\n\n");
        String[] params = { user.getFirstname() };
        textBody.append(messageSource.getMessage("pwdRecover.message.startMailBody", params, locale));
        textBody.append("\n\n");
        textBody.append(newPassword);
        textBody.append("\n\n");
        textBody.append("http://easytrack.eurocargo.com");
        textBody.append("\n");
        return textBody.toString();
    }

    public String mailTextNotifyNewOrder(Order order, Locale locale) {
        StringBuffer textBody = new StringBuffer();
        textBody.append("\n\n");
        String[] params = { order.getUser().getFirstname() + " " + order.getUser().getLastname() };
        textBody.append(messageSource.getMessage("notification.newOrder.startMailBody", params, locale));
        textBody.append("\n\n");
        textBody.append("Data inserimento: ");
        textBody.append(DateUtil.SDF2SHOW.print(order.getCreationTime().getTime()));
        textBody.append("\n");
        textBody.append("Numero di riferimento: ");
        textBody.append(order.getReferenceNumber().intValue());
        textBody.append("\n\n");
        textBody.append("Cliente ed indirizzo di spedizione: ");
        textBody.append("\n");
        textBody.append(order.getCustomerAddressForDisplay());
        textBody.append("\n\n");
        textBody.append("Prodotti:");
        textBody.append("\n");
        List<ProductOrdered> ordered = order.getProductsOrdered();
        for (ProductOrdered productOrdered : ordered) {
            textBody.append("- ");
            textBody.append(productOrdered.getNumber());
            textBody.append(" ");
            textBody.append(productOrdered.getProduct().getName());
            textBody.append("\n");
        }        
        textBody.append("\n\n");
        textBody.append("Importo totale: ");
        textBody.append(NumberUtil.italianCurrency.format(order.getTotalAmount()));
        textBody.append("\n");
        textBody.append("\n\n");
        textBody.append("http://easytrack.eurocargo.com");
        textBody.append("\n");
        return textBody.toString();
    }
}
