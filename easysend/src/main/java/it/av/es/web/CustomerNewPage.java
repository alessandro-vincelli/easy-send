package it.av.es.web;

import it.av.es.model.City;
import it.av.es.model.ClosingDays;
import it.av.es.model.ClosingRange;
import it.av.es.model.Country;
import it.av.es.model.Customer;
import it.av.es.model.DeliveryDays;
import it.av.es.model.DeliveryType;
import it.av.es.model.DeliveryVehicle;
import it.av.es.model.DeploingType;
import it.av.es.model.PaymentType;
import it.av.es.service.CittaService;
import it.av.es.service.CityService;
import it.av.es.service.CountryService;
import it.av.es.service.CustomerService;
import it.av.es.service.OrderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.yui.calendar.TimeField;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
public class CustomerNewPage extends BasePageSimple {

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

    public CustomerNewPage() {
        super();

        final CompoundPropertyModel<Customer> model = new CompoundPropertyModel<Customer>(new Customer());
        final Form<Customer> formNewOrder = new Form<Customer>("newCustomer", model);
        add(formNewOrder);
        
        formNewOrder.add(new TextField<String>("corporateName").setRequired(true));
        formNewOrder.add(new TextField<String>("address").setRequired(true));
        province = new DropDownChoice<String>("province", new ArrayList<String>());
        province.setRequired(true).setOutputMarkupId(true);
        formNewOrder.add(province);

        final Select2Choice<City> city = new Select2Choice<City>("city", new PropertyModel<City>(model, "city"), new CityProvider() {
        });
        city.add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                zipcodes = (cittaService.findCapByComune(city.getModelObject().getName(), 0));
                if (zipcodes != null && zipcodes.size() == 1) {
                    formNewOrder.getModelObject().setZipcode(zipcodes.get(0));
                }
                List<String> provinces = cittaService.findProvinciaByComune(city.getModelObject().getName(), 0);
                province.setChoices(provinces);
                if (provinces != null && provinces.size() == 1) {
                    formNewOrder.getModelObject().setProvince(provinces.get(0));
                }
                target.add(zipCode);
                target.add(province);
            }
        });
        city.setRequired(true);
        formNewOrder.add(city);
        zipCode = new Select2Choice<String>("zipcode", new PropertyModel<String>(model, "zipcode"), new ZipcodeProvider() {
        });
        zipCode.setRequired(true);
        formNewOrder.add(zipCode);

        formNewOrder.add(new TextField<String>("email"));
        formNewOrder.add(new TextField<String>("phoneNumber").setRequired(true));
        formNewOrder.add(new TextField<String>("faxNumber"));
        formNewOrder.add(new TextField<String>("partitaIvaNumber"));
        formNewOrder.add(new TextField<String>("codiceFiscaleNumber"));
        formNewOrder.add(new DropDownChoice<PaymentType>("paymentType", Arrays.asList(PaymentType.values())).setChoiceRenderer(new EnumChoiceRenderer<PaymentType>()));
        formNewOrder.add(new TextField<String>("iban"));
        formNewOrder.add(new TextField<String>("bankName"));
        formNewOrder.add(new DropDownChoice<ClosingDays>("closingDay", Arrays.asList(ClosingDays.values())).setChoiceRenderer(new EnumChoiceRenderer<ClosingDays>()));
        formNewOrder.add(new DropDownChoice<ClosingRange>("closingRange", Arrays.asList(ClosingRange.values())).setChoiceRenderer(new EnumChoiceRenderer<ClosingRange>()));
        formNewOrder.add(new DropDownChoice<DeploingType>("deployngType", Arrays.asList(DeploingType.values())).setChoiceRenderer(new EnumChoiceRenderer<DeploingType>()));

        formNewOrder.add(new TimeField("loadTimeAMFrom"));
        formNewOrder.add(new TimeField("loadTimeAMTo"));
        formNewOrder.add(new TimeField("loadTimePMFrom"));
        formNewOrder.add(new TimeField("loadTimePMTo"));
        
        CheckGroup<String> checks = new CheckGroup<String>("deliveryDays");
        formNewOrder.add(checks);
        ListView<DeliveryDays> checksList = new ListView<DeliveryDays>("deliveryDaysList", Arrays.asList(DeliveryDays.values()))
        {
            @Override
            protected void populateItem(ListItem<DeliveryDays> item)
            {
                Check<DeliveryDays> check = new Check<DeliveryDays>("check", item.getModel());
                check.setLabel(new Model<String>(getString(item.getModel().getObject().name())));
                item.add(check);
                item.add(new SimpleFormComponentLabel("number", check));
            }
        }.setReuseItems(true);
        checks.add(checksList);
        
        formNewOrder.add(new DropDownChoice<DeliveryType>("deliveryType", Arrays.asList(DeliveryType.values())).setChoiceRenderer(new EnumChoiceRenderer<DeliveryType>()));
        formNewOrder.add(new TextField<String>("deliveryNote"));
        formNewOrder.add(new DropDownChoice<DeliveryVehicle>("deliveryVehicle", Arrays.asList(DeliveryVehicle.values())).setChoiceRenderer(new EnumChoiceRenderer<DeliveryVehicle>()));
        formNewOrder.add(new CheckBox("phoneForewarning"));

        formNewOrder.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Customer c = (Customer) form.getModelObject();
                customerService.save(c);
                formNewOrder.setEnabled(false);
                getFeedbackPanel().info("salvato con successo");
                target.add(getFeedbackPanel());
                target.add(formNewOrder);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                getFeedbackPanel().anyErrorMessage();
                target.add(getFeedbackPanel());
            }
        });

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