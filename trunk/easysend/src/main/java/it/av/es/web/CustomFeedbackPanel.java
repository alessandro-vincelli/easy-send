/**
 * 
 */
package it.av.es.web;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * FeedPanel that specialize some graphic beahavior
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class CustomFeedbackPanel extends FeedbackPanel {

    /**
     * {@inheritDoc}
     */
    public CustomFeedbackPanel(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    /**
     * {@inheritDoc}
     */
    public CustomFeedbackPanel(String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCSSClass(FeedbackMessage message) {
        if(message.getLevel() == 250){
            return "message-green";    
        }
        else if(message.getLevel() < 250){
            return "message-blue";    
        }
        else if(message.getLevel() >= 300 && message.getLevel() < 400){
            return "message-yellow";    
        }
        else{
            return "message-red";
        }
    }
}