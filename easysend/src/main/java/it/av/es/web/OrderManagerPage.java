package it.av.es.web;

import it.av.es.model.Order;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;
import it.av.es.util.NumberUtil;
import it.av.es.web.data.OrderSortableDataProvider;
import it.av.es.web.data.table.CustomAjaxFallbackDefaultDataTable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR", "OPERATOR" })
public class OrderManagerPage extends BasePageSimple {

    @SpringBean
    private OrderService orderService;
    private OrderSortableDataProvider dataProvider;
    private CustomAjaxFallbackDefaultDataTable<Order, String> dataTable;

    public OrderManagerPage() {
        super();
        User user = getSecuritySession().getLoggedInUser();
        Project project = getSecuritySession().getCurrentProject();

        List<IColumn<Order, String>> columns = new ArrayList<IColumn<Order, String>>();

        columns.add(new PropertyColumn<Order, String>(new Model<String>("N"), Order.REFERNCENUMBER_FIELD, Order.REFERNCENUMBER_FIELD) {
            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
            }
        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("L"), Order.ISINCHARGE_FIELD, Order.ISINCHARGE_FIELD) {
            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                item.add(new Label(componentId, getString(rowModel.getObject().getIsInCharge().toString())));
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
            }
        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Cliente"), Order.CUSTOMER_FIELD + ".corporateName", Order.CUSTOMER_FIELD + ".corporateName"));
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Data"), Order.CREATIONTIME_FIELD, Order.CREATIONTIME_FIELD));
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Sped."), Order.SHIPPINGCOST_FIELD, Order.SHIPPINGCOST_FIELD) {

            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                item.add(new Label(componentId, NumberUtil.italianCurrency.format(rowModel.getObject().getShippingCost())));
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
            }
        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("P.A."), Order.ISPREPAYMENT_FIELD, Order.ISPREPAYMENT_FIELD) {

            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                item.add(new Label(componentId, getString(rowModel.getObject().getIsPrePayment().toString())));
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
            }
        });
        AbstractColumn<Order, String> prodotti = new AbstractColumn<Order, String>(new Model<String>("Prodotti"), "Prodotti") {
            public void populateItem(Item<ICellPopulator<Order>> cellItem, String componentId, IModel<Order> model) {
                cellItem.add(new OrderedProductPanel(componentId, model));
            }

            @Override
            public boolean isSortable() {
                return false;
            }
        };
        columns.add(prodotti);

        final DropDownChoice<Date> downChoice = new DropDownChoice<Date>("orderDates", new Model<Date>(new Date()), orderService.getDates(user, project));
        add(downChoice);
        downChoice.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataProvider.setFilterDate(downChoice.getModelObject());
                target.add(dataTable);
            }

        });
        dataProvider = new OrderSortableDataProvider(user, project);
        dataTable = new CustomAjaxFallbackDefaultDataTable<Order, String>("dataTable", columns, dataProvider, 25);
        add(dataTable);
    }

    public class OrderedProductPanel extends Panel {

        public OrderedProductPanel(String id, IModel<Order> model) {
            super(id, model);
            List<ProductOrdered> list = model.getObject().getProductsOrdered();
            PropertyListView<ProductOrdered> listView = new PropertyListView<ProductOrdered>("list", list) {

                @Override
                protected void populateItem(ListItem<ProductOrdered> item) {
                    ProductOrdered p = item.getModelObject();
                    item.add(new Label("productName", new Model<String>(p.getProduct().getName())));
                    item.add(new Label("productNumber", new Model<Integer>(p.getNumber())));
                    item.add(new Label("productAmount", new Model<String>(NumberUtil.italianCurrency.format(p.getAmount()))));
                    item.add(new Label("productDiscount", new Model<Integer>(p.getDiscount())));
                }
            };
            add(new Label("numberOfItemsInProductOrdered", new Model<Integer>(model.getObject().getNumberOfItemsInProductOrdered())));
            add(new Label("totalAmount", new Model<String>(NumberUtil.italianCurrency.format(model.getObject().getTotalAmount()))));
            add(listView);
        }

    }

}
