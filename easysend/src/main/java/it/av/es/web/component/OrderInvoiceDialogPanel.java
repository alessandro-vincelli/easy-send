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
public class OrderInvoiceDialogPanel extends Panel {

    private Label label;
    @SpringBean
    private OrderService orderService;
    private final Date invoiceDate = new Date();
    private final Date invoiceDueDate = new Date();

    /**
     * Constructor.
     * @param id the markupId, an html div suffice to host a dialog.
     * @param dialog parent
     * @param order 
     * @param customFeedbackPanel 
     */
    public OrderInvoiceDialogPanel(String id, final OrderInvoiceDialog dialog, final Order order, final CustomFeedbackPanel customFeedbackPanel) {
        super(id);
        Injector.get().inject(this);
        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        this.add(container);
        this.label = new Label("message", new ResourceModel("invoiceDialog.desc").getObject());
        container.add(this.label.setOutputMarkupId(true));
        final CustomFeedbackPanel feedbackPanel = new CustomFeedbackPanel("feedBackPanel");
        container.add(feedbackPanel);

        Form<OrderInvoiceDialogPanel> form =  new Form<OrderInvoiceDialogPanel>("form", new CompoundPropertyModel<OrderInvoiceDialogPanel>(this));
        container.add(form);
        final DateField invoiceDate = new DateField("invoiceDate");
        invoiceDate.setRequired(true);
        form.add(invoiceDate);
        final DateField invoiceDueDate = new DateField("invoiceDueDate");
        invoiceDueDate.setRequired(true);
        form.add(invoiceDueDate);
        
        AjaxButton okButton = new AjaxButton("ok", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                orderService.setInvoiceApprovedStatus(order, ((SecuritySession)getSession()).getLoggedInUser(), invoiceDate.getModelObject(), invoiceDueDate.getModelObject());
                customFeedbackPanel.info(new ResourceModel("info.order.approvedInvoice").getObject());
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
