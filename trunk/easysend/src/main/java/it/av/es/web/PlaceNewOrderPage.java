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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
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
    private RecipientService recipientService;
    @SpringBean
    private CityService cityService;
    @SpringBean
    private CittaService cittaService;
    @SpringBean
    private CountryService countryService;
    private Select2Choice<String> zipCode;
    private List<String> zipcodes = new ArrayList<String>();
    private DropDownChoice<String> province;

    public PlaceNewOrderPage() {
        super();
        final CompoundPropertyModel<Order> model = new CompoundPropertyModel<Order>(new Order());
        final Form<Order> formNewOrder = new Form<Order>("newOrder", model);
        add(formNewOrder);
        // add the single-select component
        final Select2Choice<Recipient> recipient = new Select2Choice<Recipient>("recipient",
                new Model<Recipient>(new Recipient()), new RecipientProvider());
        formNewOrder.add(recipient);
        recipient.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Recipient rcp = recipient.getModelObject();
                model.getObject().setRecipient(recipientService.getByID(rcp.getId()));
                zipcodes = (cittaService.findCapByComune(rcp.getCity().getName(), 0));
                province.setChoices(cittaService.findProvinciaByComune(rcp.getCity().getName(), 0));
                target.add(formNewOrder);
            }
        });

        formNewOrder.add(new TextField<String>("recipient.name").setRequired(true));
        formNewOrder.add(new TextField<String>("recipient.address").setRequired(true));
        province = new DropDownChoice<String>("recipient.province", new ArrayList<String>());
        province.setRequired(true).setOutputMarkupId(true);
        formNewOrder.add(province);

        final Select2Choice<City> city = new Select2Choice<City>("recipient.city", new PropertyModel<City>(model,
                "recipient.city"), new CityProvider() {
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
        formNewOrder.add(city);
        zipCode = new Select2Choice<String>("recipient.zipcode", new PropertyModel<String>(model,
                "recipient.zipcode"), new ZipcodeProvider() {
        });
        zipCode.setRequired(true);
        formNewOrder.add(zipCode);
        ArrayList<Product> products = new ArrayList<Product>(getSecuritySession().getCurrentProject().getProducts());
        formNewOrder.add(new DropDownChoice<Product>(Order.PRODUCT_FIELD, products, new ProductChoiceRenderer())
                .setRequired(true));
        formNewOrder.add(new DropDownChoice<Integer>(Order.PRODUCTNUMBER_FIELD, Arrays.asList(1, 2, 3)).setRequired(true));

        formNewOrder.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Order p = (Order) form.getModelObject();
                if (p.getRecipient().getId() == null) {
                    p.setRecipient(recipientService.save(p.getRecipient(), getSecuritySession().getLoggedInUser()));
                }
                orderService.placeNewOrder(p, getSecuritySession().getCurrentProject(), getSecuritySession().getLoggedInUser());
                formNewOrder.setModelObject(new Order());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                getFeedbackPanel().anyErrorMessage();
                target.add(getFeedbackPanel());
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
            response.addAll(getSecuritySession().getLoggedInUser().getRecipients());
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
