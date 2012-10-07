package it.av.es.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * 
 */
public class MessageDialogPanel extends Panel {

    private Label label;
    private final MessageDialog messageDialog;

    /**
     * Constructor.
     * @param id the markupId, an html div suffice to host a dialog.
     * @param title the title of the dialog 
     * @param string the message to be displayed
     * @param icon the predefined icon to display
     */
    public MessageDialogPanel(String id, final MessageDialog messageDialog, String title, String string) {

        super(id);
        this.messageDialog = messageDialog;

        WebMarkupContainer container = new WebMarkupContainer("container");
        this.add(container);

        this.label = new Label("message", string);
        container.add(this.label.setOutputMarkupId(true));

        AjaxFallbackLink<String> okButton = new AjaxFallbackLink<String>("ok") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                messageDialog.onCloseDialog(target, ButtonName.BUTTON_YES);
                messageDialog.close(target);
            }
        };
        container.add(okButton);

        AjaxFallbackLink<String> cancelButton = new AjaxFallbackLink<String>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                messageDialog.onCloseDialog(target, ButtonName.BUTTON_NO);
                messageDialog.close(target);
            }
        };
        container.add(cancelButton);
    }

}
