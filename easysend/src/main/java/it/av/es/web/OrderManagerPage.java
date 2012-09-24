package it.av.es.web;

import it.av.es.model.Order;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;
import it.av.es.web.data.OrderSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
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
        User user = getSecuritySession().getLoggedInUser();
        Project project = getSecuritySession().getCurrentProject();
        
        List<IColumn<Order, String>> columns = new ArrayList<IColumn<Order, String>>();

        columns.add(new PropertyColumn<Order, String>(new Model<String>("Cliente"), Order.CUSTOMER_FIELD +".corporateName", Order.CUSTOMER_FIELD +".corporateName"));
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Creation time"), Order.CREATIONTIME_FIELD, Order.CREATIONTIME_FIELD));

        final AjaxFallbackDefaultDataTable<Order, String> dataTable = new AjaxFallbackDefaultDataTable<Order, String>(
                "dataTable", columns, new OrderSortableDataProvider(user, project), 50);
        add(dataTable);

    }

}
