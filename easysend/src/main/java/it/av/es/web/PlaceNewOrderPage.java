package it.av.es.web;

import it.av.es.model.City;
import it.av.es.model.Country;
import it.av.es.model.Order;
import it.av.es.model.Product;
import it.av.es.model.Recipient;
import it.av.es.service.CittaService;
import it.av.es.service.CityService;
import it.av.es.service.CountryService;
import it.av.es.service.OrderService;
import it.av.es.service.RecipientService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
    private RecipientService recipientService;
    @SpringBean
    private CityService cityService;
    @SpringBean
    private CittaService cittaService;
    @SpringBean
    private CountryService countryService;

    public PlaceNewOrderPage() {
        super();
        CompoundPropertyModel<Order> model = new CompoundPropertyModel<Order>(new Order());
        final Form<Order> formNewOrder = new Form<Order>("newOrder", model);
        add(formNewOrder);
        // add the single-select component
        Select2Choice<Recipient> recipient = new Select2Choice<Recipient>("recipient", new Model<Recipient>(new Recipient()),
                new RecipientProvider());
        formNewOrder.add(recipient);

        formNewOrder.add(new TextField<String>("recipient.name"));
        formNewOrder.add(new TextField<String>("recipient.address"));
        Select2Choice<City> city = new Select2Choice<City>("recipient.city", new PropertyModel<City>(model, "recipient.city"),
                new CityProvider());
        formNewOrder.add(city);
        formNewOrder.add(new DropDownChoice<String>("recipient.zipcode", new ArrayList<String>()));
        ArrayList<Product> products = new ArrayList<Product>(getSecuritySession().getCurrentProject().getProducts());
        formNewOrder.add(new DropDownChoice<Product>(Order.PRODUCT_FIELD, products, new ProductChoiceRenderer()));
        formNewOrder.add(new DropDownChoice<Integer>(Order.PRODUCTNUMBER_FIELD, Arrays.asList(1, 2, 3)));

        formNewOrder.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Order p = (Order) form.getModelObject();
                orderService.save(p);
                formNewOrder.setModelObject(new Order());
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

    private class RecipientProvider extends ChoiceProvider<Recipient> {

        @Override
        public void query(String term, int page, Response<Recipient> response) {
            response.addAll(recipientService.find(term, 0));
        }

        @Override
        public void toJson(Recipient choice, JSONWriter writer) throws JSONException {
            writer.key("id").value(choice.getId()).key("text").value(choice.getName());
        }

        @Override
        public Collection<Recipient> toChoices(Collection<String> ids) {
            Collection<Recipient> results = new ArrayList<Recipient>();
            Set<Recipient> recipients = getSecuritySession().getLoggedInUser().getRecipients();
            for (String id : ids) {
                for (Recipient rcp : recipients) {
                    if (rcp.equals(id)) {
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
            response.addAll(cityService.find(term, country, 0));
        }

        @Override
        public void toJson(City choice, JSONWriter writer) throws JSONException {
            writer.key("id").value(choice.getId()).key("text").value(choice.getName());
        }

        @Override
        public Collection<City> toChoices(Collection<String> ids) {
            Country country = countryService.getByIso2("IT");
            List<City> cities = cityService.find("", country, 0);
            Collection<City> results = new ArrayList<City>();
            for (String id : ids) {
                for (City rcp : cities) {
                    if (rcp.equals(id)) {
                        results.add(rcp);
                    }
                }
            }
            return results;
        }
    }

}
