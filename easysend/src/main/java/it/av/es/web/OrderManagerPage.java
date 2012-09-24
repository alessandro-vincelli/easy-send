package it.av.es.web;

import it.av.es.model.Order;
import it.av.es.service.OrderService;
import it.av.es.web.data.OrderSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;


/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR" })
public class OrderManagerPage extends BasePageSimple {

    @SpringBean
    private OrderService userService;
    @SpringBean
    private OrderService orderService;

    public OrderManagerPage() {
        super();

        List<IColumn<Order, String>> columns = new ArrayList<IColumn<Order, String>>();

        //columns.add(new PropertyColumn<Order, String>(new Model<String>("Product Name"), Order.PRODUCT_FIELD +".name", Order.NAME_FIELD + ".name"));
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Creation time"), Order.CREATIONTIME_FIELD, Order.CREATIONTIME_FIELD));

        final AjaxFallbackDefaultDataTable<Order, String> dataTable = new AjaxFallbackDefaultDataTable<Order, String>(
                "dataTable", columns, new OrderSortableDataProvider(), 50);
        add(dataTable);

        final Form<Order> formPrj = new Form<Order>("prj", new CompoundPropertyModel<Order>(new Order()));
        add(formPrj);
        formPrj.add(new TextField<String>("name"));
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Order p = (Order) form.getModelObject();
                orderService.save(p);
                target.add(dataTable);
                formPrj.setModelObject(new Order());
            }
        });

    }

}
