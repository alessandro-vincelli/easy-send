/**
 * 
 */
package it.av.es.web.component;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxRequestTarget.IJavaScriptResponse;
import org.apache.wicket.ajax.AjaxRequestTarget.IListener;
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
        setOutputMarkupId(true);
    }
    
    public void publishWithEffects(AjaxRequestTarget target){
        target.addListener(new IListener() {
            
            @Override
            public void onBeforeRespond(Map<String, Component> map, AjaxRequestTarget target) {}
            @Override
            public void onAfterRespond(Map<String, Component> map, IJavaScriptResponse response) {
                //response.addJavaScript("$('#" + getMarkupId() + "').delay(0).fadeTo(0, 0, function() {})");
                response.addJavaScript("$('#" + getMarkupId() + "').hide().delay(200).fadeTo(500, 1, function() {})");
                //response.addJavaScript("alert('" + getMarkupId() + "')");
                //response.addJavaScript("$('#" + getFeedbackPanel().getMarkupId() + "').fadeTo(300, 0.5, function() {})");
                response.addJavaScript("$('#" + getMarkupId() + "').delay(10000).fadeTo(2000, 0.25, function() {})");
            }
        });
        target.add(this);
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