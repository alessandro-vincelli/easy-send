package it.av.es.web;

import it.av.es.model.Customer;
import it.av.es.service.CustomerService;
import it.av.es.web.data.CustomerSortableDataProvider;

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
import org.apache.wicket.model.StringResourceModel;
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

    public CustomerManagerPage() {
        super();

        List<IColumn<Customer, String>> columns = new ArrayList<IColumn<Customer, String>>();
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.corporateName", this, null), Customer.CORPORATENAME_FIELD, Customer.CORPORATENAME_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.address", this, null), Customer.ADDRES_FIELD, Customer.ADDRES_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.zipcode", this, null), Customer.ZIPCODE_FIELD, Customer.ZIPCODE_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.city", this, null), Customer.CITY_FIELD, Customer.CITY_FIELD));
        columns.add(new PropertyColumn<Customer, String>(new StringResourceModel("customer.province", this, null), Customer.PROVINCE_FIELD, Customer.PROVINCE_FIELD));

        final AjaxFallbackDefaultDataTable<Customer, String> dataTable = new AjaxFallbackDefaultDataTable<Customer, String>(
                "dataTable", columns, new CustomerSortableDataProvider(), 50);
        add(dataTable);

        final Form<Customer> formPrj = new Form<Customer>("prj", new CompoundPropertyModel<Customer>(new Customer()));
        add(formPrj);
        formPrj.add(new TextField<String>(Customer.CORPORATENAME_FIELD));
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Customer p = (Customer) form.getModelObject();
                customerService.save(p);
                target.add(dataTable);
                formPrj.setModelObject(new Customer());
            }
        });

    }

}
