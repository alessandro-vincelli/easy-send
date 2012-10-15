/**
 * 
 */
package it.av.es.service.system;

import it.av.es.model.Order;
import it.av.es.model.User;

import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SimpleMailMessage notificationTemplateMessage;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PrepareMessage prepareMessage;

    /**
     * Send a notification mail to the given recipient mail address
     * 
     * @param subject subject of the e-mail
     * @param message message to notify
     * @param recipient a valid email address
     */
    private void sendNotificationMail(String subject, String message, String recipient) {
        SimpleMailMessage m = new SimpleMailMessage(notificationTemplateMessage);
        String[] re = {"a.vincelli@gmail.com" };
        m.setTo(re);
        m.setCc("acoppola@eurocargo.com");
        m.setBcc("alessandro.vincelli@me.com");
        m.setSubject(subject);
        m.setText(message);
        m.setSentDate(new Date(System.currentTimeMillis()));
        javaMailSender.send(m);
    }
    
    /**
     * Send an email to the given recipient mail address
     * 
     * @param subject subject of the e-mail
     * @param message message to notify
     * @param recipient a valid email address
     */
    private void sendMail(String subject, String message, String recipient) {
        SimpleMailMessage m = new SimpleMailMessage(notificationTemplateMessage);
        m.setTo(recipient);
        m.setSubject(subject);
        m.setText(message);
        m.setSentDate(new Date(System.currentTimeMillis()));
        javaMailSender.send(m);
    }

    @Override
    public void sendNewOrderNotification(Order order) {
        Locale locale = Locale.ITALIAN;
        Object[] params = { order.getUser().getFirstname() + " " + order.getUser().getLastname() };
        String subject = messageSource.getMessage("notification.newOrder.mailSubject", params, locale);
        String body = prepareMessage.mailTextNotifyNewOrder(order, locale);
        sendNotificationMail(subject, body, "");
    }

    @Override
    public void sendPassword(User user, String newPassword) {
        Locale locale = Locale.ITALIAN;
        Object[] params = { user.getFirstname() + " " + user.getLastname() };
        String subject = (messageSource.getMessage("pwdRecover.message.subject", params, locale));
        String message = prepareMessage.mailTextPasswordRecover(user, newPassword, locale);
        sendMail(subject, message, user.getEmail());

    }
}