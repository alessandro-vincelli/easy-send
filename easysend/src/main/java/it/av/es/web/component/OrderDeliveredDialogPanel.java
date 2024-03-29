package it.av.es.web.component;

import it.av.es.model.Order;
import it.av.es.service.OrderService;
import it.av.es.web.security.SecuritySession;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 */
public class OrderDeliveredDialogPanel extends Panel {

    private Label label;
    @SpringBean
    private OrderService orderService;
    private final Date deliveredTime = new Date();

    /**
     * Constructor.
     * @param id the markupId, an html div suffice to host a dialog.
     * @param dialog parent
     * @param order 
     * @param customFeedbackPanel 
     */
    public OrderDeliveredDialogPanel(String id, final OrderDeliveredDialog dialog, final Order order, final CustomFeedbackPanel customFeedbackPanel) {
        super(id);
        Injector.get().inject(this);
        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        this.add(container);
        this.label = new Label("message", new ResourceModel("orderDeliveredDialog.desc").getObject());
        container.add(this.label.setOutputMarkupId(true));
        final CustomFeedbackPanel feedbackPanel = new CustomFeedbackPanel("feedBackPanel");
        container.add(feedbackPanel);

        Form<OrderDeliveredDialogPanel> form =  new Form<OrderDeliveredDialogPanel>("form", new CompoundPropertyModel<OrderDeliveredDialogPanel>(this));
        container.add(form);
        final DateField deliveredTime = new DateField("deliveredTime");
        deliveredTime.setRequired(true);
        form.add(deliveredTime);
        
        AjaxButton okButton = new AjaxButton("ok", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                orderService.setDeliveredStatus(order, ((SecuritySession)getSession()).getLoggedInUser(), deliveredTime.getModelObject());
                customFeedbackPanel.info(new ResourceModel("info.order.approvedDeliveredStatus").getObject());
                dialog.onCloseDialog(target, ButtonName.BUTTON_YES);
                dialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(feedbackPanel);
            }

        };
        form.add(okButton);

        AjaxFallbackLink<String> cancelButton = new AjaxFallbackLink<String>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.onCloseDialog(target, ButtonName.BUTTON_NO);
                dialog.close(target);
            }
        };
        form.add(cancelButton);
    }

}
