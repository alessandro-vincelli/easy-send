package it.av.es.web;

import it.av.es.model.Address;
import it.av.es.model.AddressType;
import it.av.es.model.City;
import it.av.es.model.ClosingDays;
import it.av.es.model.ClosingRange;
import it.av.es.model.Country;
import it.av.es.model.Customer;
import it.av.es.model.CustomerType;
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
import it.av.es.service.ProvinciaService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.yui.calendar.TimeField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
@AuthorizeInstantiation({ "USER", "VENDOR", "OPERATOR" })
public class CustomerPage extends BasePageSimple {

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
    private ProvinciaService provinciaService;
    @SpringBean
    private CountryService countryService;
    //private Select2Choice<String> zipCode;
    private DropDownChoice<String> province;
    private Customer customer = new Customer();
    private BookmarkablePageLink<String> addOrder;
    private BookmarkablePageLink<String> addOrderButton;

    public CustomerPage(PageParameters parameters) {
        String customerId = parameters.get(CustomHttpParams.CUSTOMER_ID).toString("");

        if (StringUtils.isNotBlank(customerId)) {
            customer = customerService.getByID(customerId);
        }
        init();
    }

    public CustomerPage() {
        super();
        init();
    }

    private void init() {

        final CompoundPropertyModel<Customer> model = new CompoundPropertyModel<Customer>(customer);
        final Form<Customer> formNewOrder = new Form<Customer>("newCustomer", model);
        
        add(formNewOrder);

        formNewOrder.add(new TextField<String>("corporateName").setRequired(true));
//        formNewOrder.add(new TextField<String>("address").setRequired(true));
//        province = new DropDownChoice<String>("province", provinciaService.getAllSigle());
//        province.setRequired(true).setOutputMarkupId(true);
//        formNewOrder.add(province);
//
//        final Select2Choice<City> city = new Select2Choice<City>("city", new PropertyModel<City>(model, "city"), new CityProvider() {
//        });
//        city.add(new OnChangeAjaxBehavior() {
//
//            @Override
//            protected void onUpdate(AjaxRequestTarget target) {
//                List<String> zipcodes = (cittaService.findCapByComune(city.getModelObject().getName(), 0));
//                if (zipcodes != null && zipcodes.size() == 1) {
//                    formNewOrder.getModelObject().setZipcode(zipcodes.get(0));
//                }
//                List<String> provinces = cittaService.findProvinciaByComune(city.getModelObject().getName(), 0);
//                if (provinces != null && provinces.size() == 1) {
//                    formNewOrder.getModelObject().setProvince(provinces.get(0));
//                }
//                target.add(zipCode);
//                target.add(province);
//            }
//        });
//        city.setRequired(true);
//        formNewOrder.add(city);
//        zipCode = new Select2Choice<String>("zipcode", new PropertyModel<String>(model, "zipcode"), new ZipcodeProvider() {
//        });
//        zipCode.setRequired(true);
//        formNewOrder.add(zipCode);

        formNewOrder.add(new TextField<String>("email"));
        formNewOrder.add(new TextField<String>("www"));
        formNewOrder.add(new TextField<String>("facebookAccount"));
        formNewOrder.add(new TextField<String>("twitterAccount"));
        formNewOrder.add(new TextField<String>("referenceName"));
        formNewOrder.add(new TextField<String>("signboard"));
        formNewOrder.add(new DropDownChoice<CustomerType>("customerType", Arrays.asList(CustomerType.values())).setChoiceRenderer(new EnumChoiceRenderer<CustomerType>()));
        formNewOrder.add(new TextField<String>("phoneNumber").setRequired(true));
        formNewOrder.add(new TextField<String>("faxNumber"));
        formNewOrder.add(new TextField<String>("partitaIvaNumber").setRequired(true));
        formNewOrder.add(new TextField<String>("codiceFiscaleNumber"));
        formNewOrder.add(new DropDownChoice<PaymentType>("paymentType", Arrays.asList(PaymentType.values())).setChoiceRenderer(new EnumChoiceRenderer<PaymentType>()).setRequired(true));
        formNewOrder.add(new TextField<String>("iban"));
        formNewOrder.add(new TextField<String>("bankName"));
        formNewOrder.add(new DropDownChoice<ClosingDays>("closingDay", Arrays.asList(ClosingDays.values())).setChoiceRenderer(new EnumChoiceRenderer<ClosingDays>()).setRequired(true));

        formNewOrder.add(new DropDownChoice<ClosingRange>("closingRange", Arrays.asList(ClosingRange.values())).setChoiceRenderer(new EnumChoiceRenderer<ClosingRange>()));
        formNewOrder.add(new DropDownChoice<DeploingType>("deployngType", Arrays.asList(DeploingType.values())).setChoiceRenderer(new EnumChoiceRenderer<DeploingType>()));

        formNewOrder.add(new TimeField("loadTimeAMFrom"));
        formNewOrder.add(new TimeField("loadTimeAMTo"));
        formNewOrder.add(new TimeField("loadTimePMFrom"));
        formNewOrder.add(new TimeField("loadTimePMTo"));

        CheckGroup<String> checks = new CheckGroup<String>("deliveryDays");
        formNewOrder.add(checks);
        ListView<DeliveryDays> checksList = new ListView<DeliveryDays>("deliveryDaysList", Arrays.asList(DeliveryDays.values())) {
            @Override
            protected void populateItem(ListItem<DeliveryDays> item) {
                Check<DeliveryDays> check = new Check<DeliveryDays>("check", item.getModel());
                check.setLabel(new Model<String>(getString(item.getModel().getObject().name())));
                item.add(check);
                Component label = new SimpleFormComponentLabel("number", check);
                item.add(label);
                if(check.getModelObject().equals(DeliveryDays.SUNDAY) || check.getModelObject().equals(DeliveryDays.SATURDAY)){
                    label.add(AttributeModifier.replace("style", "color:red;"));
                }
            }
        }.setReuseItems(true);
        checks.add(checksList);

        formNewOrder.add(new DropDownChoice<DeliveryType>("deliveryType", Arrays.asList(DeliveryType.values())).setChoiceRenderer(new EnumChoiceRenderer<DeliveryType>()));
        formNewOrder.add(new TextField<String>("deliveryNote"));
        formNewOrder.add(new DropDownChoice<DeliveryVehicle>("deliveryVehicle", Arrays.asList(DeliveryVehicle.values()))
                .setChoiceRenderer(new EnumChoiceRenderer<DeliveryVehicle>()));
        formNewOrder.add(new CheckBox("phoneForewarning"));

        PropertyListView<Address> addresses = new PropertyListView<Address>("addresses") {

            @Override
            protected void populateItem(final ListItem<Address> item) {
                item.add(new Label("addressNumber", Integer.toString(item.getIndex() + 1)));
                item.add(new TextField<String>("name").setRequired(true));
                item.add(new TextField<String>("address").setRequired(true));
                item.add(new CheckBox("defaultAddress"));
                item.add(new DropDownChoice<AddressType>("addressType", Arrays.asList(AddressType.values())).setChoiceRenderer(new EnumChoiceRenderer<AddressType>()).setRequired(true));
                final DropDownChoice<String> province = new DropDownChoice<String>("province", provinciaService.getAllSigle());
                province.setRequired(true).setOutputMarkupId(true);
                item.add(province);
                item.add(new TextField<String>("zipcode").setRequired(true));
//                final Select2Choice<String> zipCode = new Select2Choice<String>("zipcode", new PropertyModel<String>(item.getModel(), "zipcode"), new ZipcodeProvider() {
//                });
//                zipCode.setRequired(true);
                //item.add(zipCode);
                final Select2Choice<City> city = new Select2Choice<City>("city", new PropertyModel<City>(item.getModel(), "city"), new CityProvider() {
                });
                city.add(new OnChangeAjaxBehavior() {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        List<String> zipcodes = (cittaService.findCapByComune(city.getModelObject().getName(), 0));
                        zipcodes = (cittaService.findCapByComune(city.getModelObject().getName(), 0));
                        if (zipcodes != null && zipcodes.size() == 1) {
                            item.getModelObject().setZipcode(zipcodes.get(0));
                        }
                        List<String> provinces = cittaService.findProvinciaByComune(city.getModelObject().getName(), 0);
                        if (provinces != null && provinces.size() == 1) {
                            item.getModelObject().setProvince(provinces.get(0));
                        }
                        //target.add(zipCode);
                        target.add(province);
                    }
                });
                city.setRequired(true);
                item.add(city);

                item.add(new TextField<String>("phoneNumber"));
            }
        };
        formNewOrder.add(addresses);
        
        AjaxSubmitLink addNewAddress = new AjaxSubmitLink("addNewAddress", formNewOrder) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Address address = new Address();
                if(formNewOrder.getModelObject().getAddresses().size() == 0){
                    address.setAddressType(AddressType.BILLINGANDSHIPPINGADDRESS);
                    address.setDefaultAddress(true);
                    if(StringUtils.isNotBlank(formNewOrder.getModelObject().getSignboard())){
                        address.setName(formNewOrder.getModelObject().getSignboard());
                    }
                    else if(StringUtils.isNotBlank(formNewOrder.getModelObject().getCorporateName())){
                        address.setName(formNewOrder.getModelObject().getCorporateName());
                    }
                }
                formNewOrder.getModelObject().addAddresses(address);
                target.add(form);
                getFeedbackPanel().publishWithEffects(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                getFeedbackPanel().publishWithEffects(target);
            }
            
        };
        formNewOrder.add(addNewAddress);
        
//        AjaxSubmitLink addNewAddress2 = new AjaxSubmitLink("addNewAddress2", formNewOrder) {
//
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                super.onSubmit(target, form);
//                formNewOrder.getModelObject().addAddresses(new Address());
//                target.add(formNewOrder);
//            }
//            
//        };
//        formNewOrder.add(addNewAddress2);
                
        formNewOrder.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Customer c = (Customer) form.getModelObject();
                if(c.getAddresses() == null || c.getAddresses().isEmpty() || c.getDefaultShippingAddresses() == null){
                    getFeedbackPanel().warn(getString("customer.message.deafulAddressRequired"));
                    getFeedbackPanel().publishWithEffects(target);
                }
                boolean invoiceAddress = false;
                for (Address a : c.getAddresses()) {
                    if(a.getAddressType().equals(AddressType.BILLINGADDRESS) || a.getAddressType().equals(AddressType.BILLINGANDSHIPPINGADDRESS)){
                        invoiceAddress = true;
                    }
                }
                if(!invoiceAddress){
                    getFeedbackPanel().warn(getString("customer.message.invoiceMessageRequired"));
                    getFeedbackPanel().publishWithEffects(target);
                }
                if(c.getAddresses() == null || c.getAddresses().isEmpty() || c.getDefaultShippingAddresses() == null){
                    getFeedbackPanel().warn(getString("customer.message.deafulAddressRequired"));
                    getFeedbackPanel().publishWithEffects(target);
                }
                else{
                    formNewOrder.setModelObject(customerService.save(c, getSecuritySession().getLoggedInUser()));
                    //formNewOrder.setEnabled(false);
                    getFeedbackPanel().success(getString("customer.message.saved"));
                    getFeedbackPanel().publishWithEffects(target);
                    if(!addOrderButton.isVisible()){
                        addOrderButton.setVisible(true);
                        target.add(addOrderButton);                       
                    }
                }
                target.add(formNewOrder);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                getFeedbackPanel().anyErrorMessage();
                getFeedbackPanel().publishWithEffects(target);
            }
        });

        PageParameters pp = new PageParameters();
        pp.add(CustomHttpParams.CUSTOMER_ID, model.getObject().getId()!=null?model.getObject().getId():"");
        
        addOrderButton = new BookmarkablePageLink<String>("addOrderButton", PlaceNewOrderPage.class, pp) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(PlaceNewOrderPage.class)));
            }

            @Override
            public PageParameters getPageParameters() {
                //FIX for first save on customer
                if(super.getPageParameters().get(CustomHttpParams.CUSTOMER_ID).isEmpty()){
                    PageParameters pp = new PageParameters();
                    pp.add(CustomHttpParams.CUSTOMER_ID, model.getObject().getId()!=null?model.getObject().getId():"");
                    return pp;
                }
                return super.getPageParameters();
            }
        };
        formNewOrder.add(addOrderButton);
        addOrderButton.setOutputMarkupPlaceholderTag(true);
        addOrderButton.setVisible(StringUtils.isNotBlank(model.getObject().getId()));
        
        addOrder = new BookmarkablePageLink<String>("addOrder", PlaceNewOrderPage.class, pp) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(PlaceNewOrderPage.class)));
            }
        };
        add(addOrder);
        addOrder.setOutputMarkupPlaceholderTag(true);
        addOrder.setVisible(StringUtils.isNotBlank(model.getObject().getId()));
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
            List<String> zipcodes = cittaService.findCap(term, 30);
            response.addAll(zipcodes);
        }

        @Override
        public Collection<String> toChoices(Collection<String> ids) {
            return ids;
        }
    }

}