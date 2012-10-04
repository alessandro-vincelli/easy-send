/**
 * 
 */
package it.av.es.service.system;

import it.av.es.model.Order;
import it.av.es.model.User;

/**
 * Creates and sends email from EasyTrack
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface MailService {

    /**
     * Send an email to notify a new {@link Order}
     * 
     * @param Order the order to be notified
     * @param page
     * 
     */
    void sendNewOrderNotification(Order order);

    /**
     * Send the the given password to the given user by email
     * 
     * @param eater
     * @param newPassword
     */
    void sendPassword(User user, String newPassword);


}
