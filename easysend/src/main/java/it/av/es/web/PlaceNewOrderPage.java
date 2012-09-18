package it.av.es.web;

import it.av.es.model.Order;
import it.av.es.model.Product;
import it.av.es.service.OrderService;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;


/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR" })
public class PlaceNewOrderPage extends BasePageSimple {

    @SpringBean
    private OrderService userService;
    @SpringBean
    private OrderService orderService;

    public PlaceNewOrderPage() {
        super();


        final Form<Order> formPrj = new Form<Order>("order", new CompoundPropertyModel<Order>(new Order()));
        add(formPrj);
        formPrj.add(new TextField<String>("name"));
        ArrayList<Product> products = new ArrayList<Product>(getSession().getCurrentProject().getProducts());
        formPrj.add(new DropDownChoice<Product>(Order.PRODUCT_FIELD, products, new ProductChoiceRenderer()));
        formPrj.add(new DropDownChoice<Integer>(Order.PRODUCTNUMBER_FIELD, Arrays.asList(1, 2, 3)));
            
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Order p = (Order) form.getModelObject();
                orderService.save(p);
                formPrj.setModelObject(new Order());
            }
        });

    }
    
    private class ProductChoiceRenderer implements IChoiceRenderer<Product> {

        @Override
        public Object getDisplayValue(Product object) {
            return object.getName();
        }

        @Override
        public String getIdValue(Product object, int index) {
            return object.getId();
        }

    }

}
