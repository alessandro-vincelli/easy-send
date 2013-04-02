package it.av.es.web.component;

import it.av.es.model.Order;
import it.av.es.service.OrderService;

import java.math.BigDecimal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 */
public class OrderForceCostDialogPanel extends Panel {

    private Label label;
    @SpringBean
    private OrderService orderService;
    private final BigDecimal cost = BigDecimal.ZERO;
    private final BigDecimal discount = BigDecimal.ZERO;

    /**
     * Constructor.
     * @param id the markupId, an html div suffice to host a dialog.
     * @param dialog parent
     * @param order 
     * @param customFeedbackPanel 
     */
    public OrderForceCostDialogPanel(String id, final OrderForceCostDialog dialog, final Order order, final CustomFeedbackPanel customFeedbackPanel) {
        super(id);
        Injector.get().inject(this);
        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        this.add(container);
        this.label = new Label("message", new ResourceModel("orderForceCostDialog.desc").getObject());
        container.add(this.label.setOutputMarkupId(true));
        final CustomFeedbackPanel feedbackPanel = new CustomFeedbackPanel("feedBackPanel");
        container.add(feedbackPanel);

        Form<OrderForceCostDialogPanel> form =  new Form<OrderForceCostDialogPanel>("form", new CompoundPropertyModel<OrderForceCostDialogPanel>(this));
        container.add(form);
        final TextField<BigDecimal> costField = new TextField<BigDecimal>("cost");
        costField.setRequired(true);
        form.add(costField);
        final TextField<BigDecimal> discountField = new TextField<BigDecimal>("discount");
        discountField.setRequired(true);
        form.add(discountField);
        
        AjaxButton okButton = new AjaxButton("ok", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                orderService.forcePriceAndDiscountAndRecalculate(order, costField.getModelObject(), discountField.getModelObject());
                orderService.save(order);
                customFeedbackPanel.info(new ResourceModel("info.order.forcePriceAndDiscountAndRecalculate").getObject());
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
