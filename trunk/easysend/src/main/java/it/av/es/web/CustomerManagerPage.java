package it.av.es.web;

import it.av.es.model.Customer;
import it.av.es.service.CustomerService;
import it.av.es.web.data.CustomerSortableDataProvider;
import it.av.es.web.data.table.CustomAjaxFallbackDefaultDataTable;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR" })
public class CustomerManagerPage extends BasePageSimple {

    @SpringBean
    private CustomerService userService;
    @SpringBean
    private CustomerService customerService;
    private CustomAjaxFallbackDefaultDataTable<Customer, String> dataTable;

    public CustomerManagerPage() {
        super();

        List<IColumn<Customer, String>> columns = new ArrayList<IColumn<Customer, String>>();
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.corporateName", this, null), Customer.CORPORATENAME_FIELD, Customer.CORPORATENAME_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.address", this, null), Customer.ADDRES_FIELD, Customer.ADDRES_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.zipcode", this, null), Customer.ZIPCODE_FIELD, Customer.ZIPCODE_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.city", this, null), Customer.CITY_FIELD, Customer.CITY_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.province", this, null), Customer.PROVINCE_FIELD, Customer.PROVINCE_FIELD));

        columns.add(new AbstractColumn<Customer, String>(new Model<String>("Azioni")) {
            public void populateItem(Item<ICellPopulator<Customer>> cellItem, String componentId, IModel<Customer> model) {
                cellItem.add(new ActionPanel(componentId, model));
                cellItem.add(AttributeModifier.replace("class", "options-width"));
            }
        });

        dataTable = new CustomAjaxFallbackDefaultDataTable<Customer, String>("dataTable", columns, new CustomerSortableDataProvider(getSecuritySession().getLoggedInUser()), 25);
        add(dataTable);

    }

    public class ActionPanel extends Panel {

        public ActionPanel(String id, IModel<Customer> model) {
            super(id, model);
            Injector.get().inject(this);
            add(new AjaxFallbackLink<Customer>("edit", model) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    setResponsePage(CustomerPage.class, new PageParameters().add(CustomHttpParams.CUSTOMER_ID, getModelObject().getId()));
                }
            });
            add(new AjaxFallbackLink<Customer>("remove", model) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    customerService.remove(getModelObject());
                    target.add(dataTable);
                }
            });
        }

    }

}
