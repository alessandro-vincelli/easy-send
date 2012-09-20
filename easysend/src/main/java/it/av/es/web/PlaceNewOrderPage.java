package it.av.es.web;

import it.av.es.model.City;
import it.av.es.model.ClosingDays;
import it.av.es.model.Country;
import it.av.es.model.Customer;
import it.av.es.model.DeliveryType;
import it.av.es.model.DeliveryVehicle;
import it.av.es.model.DeploingType;
import it.av.es.model.Order;
import it.av.es.model.PaymentType;
import it.av.es.model.Product;
import it.av.es.service.CittaService;
import it.av.es.service.CityService;
import it.av.es.service.CountryService;
import it.av.es.service.CustomerService;
import it.av.es.service.OrderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.json.JSONException;
import org.json.JSONWriter;

import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.TextChoiceProvider;

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
    @SpringBean
    private CustomerService customerService;
    @SpringBean
    private CityService cityService;
    @SpringBean
    private CittaService cittaService;
    @SpringBean
    private CountryService countryService;
    private Select2Choice<String> zipCode;
    private List<String> zipcodes = new ArrayList<String>();
    private DropDownChoice<String> province;
    private WebMarkupContainer step2;
    private WebMarkupContainer step1;

    public PlaceNewOrderPage() {
        super();
        final CompoundPropertyModel<Order> model = new CompoundPropertyModel<Order>(new Order());
        final Form<Order> formNewOrder = new Form<Order>("newOrder", model);
        add(formNewOrder);
        
        step1 = new WebMarkupContainer("step1");
        formNewOrder.add(step1);
        
        // add the single-select component
        final Select2Choice<Customer> customer = new Select2Choice<Customer>("customer",
                new Model<Customer>(new Customer()), new RecipientProvider());
        step1.add(customer);
        customer.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Customer rcp = customer.getModelObject();
                model.getObject().setCustomer(customerService.getByID(rcp.getId()));
                zipcodes = (cittaService.findCapByComune(rcp.getCity().getName(), 0));
                province.setChoices(cittaService.findProvinciaByComune(rcp.getCity().getName(), 0));
                target.add(formNewOrder);
            }
        });

        step1.add(new TextField<String>("customer.corporateName").setRequired(true));
        step1.add(new TextField<String>("customer.address").setRequired(true));
        province = new DropDownChoice<String>("customer.province", new ArrayList<String>());
        province.setRequired(true).setOutputMarkupId(true);
        step1.add(province);

        final Select2Choice<City> city = new Select2Choice<City>("customer.city", new PropertyModel<City>(model,
                "customer.city"), new CityProvider() {
        });
        city.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                zipcodes = (cittaService.findCapByComune(city.getModelObject().getName(), 0));
                province.setChoices(cittaService.findProvinciaByComune(city.getModelObject().getName(), 0));
                target.add(zipCode);
                target.add(province);
            }
        });
        city.setRequired(true);
        step1.add(city);
        zipCode = new Select2Choice<String>("customer.zipcode", new PropertyModel<String>(model,
                "customer.zipcode"), new ZipcodeProvider() {
        });
        zipCode.setRequired(true);
        step1.add(zipCode);
        
        
        
        step1.add(new TextField<String>("customer.email"));
        step1.add(new TextField<String>("customer.phoneNumber").setRequired(true));
        step1.add(new TextField<String>("customer.faxNumber"));
        step1.add(new TextField<String>("customer.partitaIvaNumber"));
        step1.add(new TextField<String>("customer.codiceFiscaleNumber"));
        formNewOrder.add(new DropDownChoice<PaymentType>("customer.paymentType", Arrays.asList(PaymentType.values())));
        formNewOrder.add(new TextField<String>("customer.iban"));
        formNewOrder.add(new TextField<String>("customer.bankName"));
        step1.add(new DropDownChoice<ClosingDays>("customer.closingDay", Arrays.asList(ClosingDays.values())));
        //formNewOrder.add(new DropDownChoice<ClosingDays>("customer.closingRange", Arrays.asList(ClosingDays.values())));
        step1.add(new DropDownChoice<DeploingType>("customer.deployngType", Arrays.asList(DeploingType.values())));
        //formNewOrder.add(new DropDownChoice<DeploingType>("customer.loadDateTime", Arrays.asList(DeploingType.values())));
        step1.add(new DropDownChoice<DeliveryType>("customer.deliveryType", Arrays.asList(DeliveryType.values())));
        step1.add(new TextField<String>("customer.deliveryNote"));
        step1.add(new DropDownChoice<DeliveryVehicle>("customer.deliveryVehicle", Arrays.asList(DeliveryVehicle.values())));
        step1.add(new CheckBox("customer.phoneForewarning"));
        
        

        step2 = new WebMarkupContainer("step2");
        
        formNewOrder.add(step2);
        
        ArrayList<Product> products = new ArrayList<Product>(getSecuritySession().getCurrentProject().getProducts());
        step2.add(new DropDownChoice<Product>(Order.PRODUCT_FIELD, products, new ProductChoiceRenderer())
                .setRequired(true));
        step2.add(new DropDownChoice<Integer>(Order.PRODUCTNUMBER_FIELD, Arrays.asList(1, 2, 3)).setRequired(true));

        step2.setVisible(false);
        
        formNewOrder.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if(step1.isVisible()){
                    step1.setVisible(false);
                    step2.setVisible(true);
                }
                if(step2.isVisible()){
                    
                }
                target.add(form);
//                Order p = (Order) form.getModelObject();
//                if (p.getCustomer().getId() == null) {
//                    p.setCustomer(customerService.save(p.getCustomer(), getSecuritySession().getLoggedInUser()));
//                }
//                orderService.placeNewOrder(p, getSecuritySession().getCurrentProject(), getSecuritySession().getLoggedInUser());
//                formNewOrder.setModelObject(new Order());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                //TODO adde feeback panel
//                getFeedbackPanel().anyErrorMessage();
//                target.add(getFeedbackPanel());
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

    private class RecipientProvider extends ChoiceProvider<Customer> {

        @Override
        public void query(String term, int page, Response<Customer> response) {
            response.addAll(getSecuritySession().getLoggedInUser().getCustomers());
        }

        @Override
        public void toJson(Customer choice, JSONWriter writer) throws JSONException {
            writer.key("id").value(choice.getId()).key("text").value(choice.getCorporateName());
        }

        @Override
        public Collection<Customer> toChoices(Collection<String> ids) {
            Collection<Customer> results = new ArrayList<Customer>();
            Set<Customer> customers = getSecuritySession().getLoggedInUser().getCustomers();
            for (String id : ids) {
                for (Customer rcp : customers) {
                    if (rcp.getId().equals(id)) {
                        results.add(rcp);
                    }
                }
            }
            return results;
        }

    }

    private class CityProvider extends ChoiceProvider<City> {

        @Override
        public void query(String term, int page, Response<City> response) {
            Country country = countryService.getByIso2("IT");
            response.addAll(cityService.find(term, country, 20));
        }

        @Override
        public void toJson(City choice, JSONWriter writer) throws JSONException {
            writer.key("id").value(choice.getId()).key("text").value(choice.getName());
        }

        @Override
        public Collection<City> toChoices(Collection<String> ids) {
            Country country = countryService.getByIso2("IT");
            List<City> cities = cityService.getByCountry(country);
            Collection<City> results = new ArrayList<City>();
            for (String id : ids) {
                for (City item : cities) {
                    if (item.getId().equals(id)) {
                        results.add(item);
                    }
                }
            }
            return results;
        }
    }

    private class ZipcodeProvider extends TextChoiceProvider<String> {

        @Override
        protected String getDisplayText(String choice) {
            return choice;
        }

        @Override
        protected Object getId(String choice) {
            return choice;
        }

        @Override
        public void query(String term, int page, Response<String> response) {
            for (String zc : zipcodes) {
                if (StringUtils.contains(zc, term)) {
                    response.add(zc);
                }
            }
        }

        @Override
        public Collection<String> toChoices(Collection<String> ids) {
            return ids;
        }

    }

}
