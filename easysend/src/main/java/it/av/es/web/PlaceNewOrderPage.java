package it.av.es.web;

import it.av.es.model.Address;
import it.av.es.model.AddressType;
import it.av.es.model.City;
import it.av.es.model.ClosingDays;
import it.av.es.model.ClosingRange;
import it.av.es.model.Country;
import it.av.es.model.Customer;
import it.av.es.model.DeliveryDays;
import it.av.es.model.DeliveryType;
import it.av.es.model.DeliveryVehicle;
import it.av.es.model.DeploingType;
import it.av.es.model.Order;
import it.av.es.model.PaymentType;
import it.av.es.model.Product;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.service.CittaService;
import it.av.es.service.CityService;
import it.av.es.service.CountryService;
import it.av.es.service.CustomerService;
import it.av.es.service.OrderService;
import it.av.es.util.DateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.extensions.yui.calendar.TimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
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
    private List<String> zipcodes = new ArrayList<String>();
    private WebMarkupContainer step3;
    private WebMarkupContainer step2;
    private WebMarkupContainer step1;
    private WebMarkupContainer step1Number;
    private WebMarkupContainer step1Left;
    private WebMarkupContainer step1Right;
    private WebMarkupContainer step2Number;
    private WebMarkupContainer step2Left;
    private WebMarkupContainer step2Right;
    private WebMarkupContainer step3Number;
    private WebMarkupContainer step3Left;
    private WebMarkupContainer step3Right;
    private WebMarkupContainer step4Number;
    private WebMarkupContainer step4Left;
    private WebMarkupContainer step4Right;    
    private WebMarkupContainer fakeTabs;
    private AjaxSubmitLink submitConfirm;
    private AjaxSubmitLink submitNext;
    private Form<Order> formNewOrder;
    private Project currentProject;

    public PlaceNewOrderPage() {
        super();
        
        currentProject = getSecuritySession().getCurrentProject();
                
        final CompoundPropertyModel<Order> model = new CompoundPropertyModel<Order>(new Order(currentProject));
        formNewOrder = new Form<Order>("newOrder", model);
        add(formNewOrder);
        addFakeTabs(this);
        
        // add the single-select component
        final Select2Choice<Customer> customer = new Select2Choice<Customer>("customer", new Model<Customer>(new Customer()), new RecipientProvider());
        formNewOrder.add(customer);
        customer.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Customer rcp = customer.getModelObject();
                Customer customer2 = customerService.getByID(rcp.getId());
                model.getObject().setCustomer(customer2);
                model.getObject().setShippingAddress(customer2.getDefaultShippingAddresses());
                model.getObject().setPaymentType(customer2.getPaymentType());
                model.getObject().setNotes(customer2.getDeliveryNote());
                target.add(formNewOrder);
            }
        });
        step1 = new WebMarkupContainer("step1");
        formNewOrder.add(step1);
        
        step1.add(new TextField<String>("customer.corporateName").setRequired(true).setEnabled(false));


        step1.add(new TextField<String>("customer.email").setEnabled(false));
        step1.add(new TextField<String>("customer.phoneNumber").setRequired(true).setEnabled(false));
        step1.add(new TextField<String>("customer.faxNumber").setEnabled(false));
        step1.add(new TextField<String>("customer.partitaIvaNumber").setEnabled(false));
        step1.add(new TextField<String>("customer.codiceFiscaleNumber").setEnabled(false));
        step1.add(new DropDownChoice<PaymentType>("customer.paymentType", Arrays.asList(PaymentType.values())).setChoiceRenderer(new EnumChoiceRenderer<PaymentType>()).setEnabled(false));
        step1.add(new TextField<String>("customer.iban").setEnabled(false));
        step1.add(new TextField<String>("customer.bankName").setEnabled(false));
        step1.add(new DropDownChoice<ClosingDays>("customer.closingDay", Arrays.asList(ClosingDays.values())).setChoiceRenderer(new EnumChoiceRenderer<ClosingDays>()).setEnabled(false));
        step1.add(new DropDownChoice<ClosingRange>("customer.closingRange", Arrays.asList(ClosingRange.values())).setChoiceRenderer(new EnumChoiceRenderer<ClosingRange>()).setEnabled(false));
        step1.add(new DropDownChoice<DeploingType>("customer.deployngType", Arrays.asList(DeploingType.values())).setChoiceRenderer(new EnumChoiceRenderer<DeploingType>()).setEnabled(false));
        step1.add(new TimeField("customer.loadTimeAMFrom").setEnabled(false));
        step1.add(new TimeField("customer.loadTimeAMTo").setEnabled(false));
        step1.add(new TimeField("customer.loadTimePMFrom").setEnabled(false));
        step1.add(new TimeField("customer.loadTimePMTo").setEnabled(false));
        step1.add(new CheckBox("customer.phoneForewarning").setEnabled(false));

        CheckGroup<String> checks = new CheckGroup<String>("customer.deliveryDays");
        step1.add(checks.setEnabled(false));
        ListView<DeliveryDays> checksList = new ListView<DeliveryDays>("deliveryDaysList", Arrays.asList(DeliveryDays.values())) {
            @Override
            protected void populateItem(ListItem<DeliveryDays> item) {
                Check<DeliveryDays> check = new Check<DeliveryDays>("check", item.getModel());
                check.setLabel(new Model<String>(getString(item.getModel().getObject().name())));
                item.add(check);
                item.add(new SimpleFormComponentLabel("number", check).setEnabled(false));
            }
        }.setReuseItems(true);
        checks.add(checksList);

        step1.add(new DropDownChoice<DeliveryType>("customer.deliveryType", Arrays.asList(DeliveryType.values())).setChoiceRenderer(new EnumChoiceRenderer<DeliveryType>()).setEnabled(false));
        step1.add(new TextField<String>("customer.deliveryNote").setEnabled(false));
        step1.add(new DropDownChoice<DeliveryVehicle>("customer.deliveryVehicle", Arrays.asList(DeliveryVehicle.values()))
                .setChoiceRenderer(new EnumChoiceRenderer<DeliveryVehicle>()).setEnabled(false));

        /****** STEP 2 *****/
        step2 = new WebMarkupContainer("step2");
        step2.setVisible(false).setOutputMarkupId(true);
        formNewOrder.add(step2);
        RadioGroup<Address> group = new RadioGroup<Address>("shippingAddress");
        step2.add(group.setRequired(true));
        final PropertyListView<Address> addresses = new PropertyListView<Address>("customer.shippingAddresses") {

            @Override
            protected void populateItem(final ListItem<Address> item) {
                item.add(new Label("addressNumber", Integer.toString(item.getIndex() + 1)));
                item.add(new TextField<String>("name").setEnabled(false));
                item.add(new TextField<String>("address").setEnabled(false));
                item.add(new CheckBox("defaultAddress").setEnabled(false));
                item.add(new DropDownChoice<AddressType>("addressType", Arrays.asList(AddressType.values())).setChoiceRenderer(new EnumChoiceRenderer<AddressType>()).setEnabled(false));
                TextField<String> province = new TextField<String>("province");
                item.add(province.setEnabled(false));
                TextField<String> zipCode = new TextField<String>("zipcode");
                item.add(zipCode.setEnabled(false));
                TextField<City> city = new TextField<City>("city");
                item.add(city.setEnabled(false));
                item.add(new TextField<String>("phoneNumber").setEnabled(false));;
                item.add(new Radio<Address>("selectedAddress", item.getModel()));
            }
        };

        group.add(addresses);
        
        /****** STEP 3 *****/
        step3 = new WebMarkupContainer("step3");

        formNewOrder.add(step3);

        final ArrayList<Product> products = new ArrayList<Product>(getSecuritySession().getCurrentProject().getProducts());
        step3.setVisible(false).setOutputMarkupId(true);
        final DropDownChoice<Product> productToAdd = new DropDownChoice<Product>("product", new Model<Product>() ,products, new ProductChoiceRenderer());
        final DropDownChoice<Integer> productNumberToAdd = new DropDownChoice<Integer>("number", new Model<Integer>(), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        productNumberToAdd.setNullValid(false);
        step3.add(productToAdd);
        step3.add(productNumberToAdd);
        final WebMarkupContainer productsOrderedContanier = new WebMarkupContainer("productsOrderedContanier");
        productsOrderedContanier.setOutputMarkupId(true);
        step3.add(productsOrderedContanier);
        PropertyListView<ProductOrdered> listViewProductsOrdered = new PropertyListView<ProductOrdered>(Order.PRODUCTSORDERED_FIELD) {

            @Override
            protected void populateItem(ListItem<ProductOrdered> item) {
                item.add(new TextField<Product>("product.name").setEnabled(false));;
                item.add(new TextField<Integer>("number").setEnabled(false));
                item.add(new TextField<BigDecimal>("amount", BigDecimal.class).setEnabled(false));
                item.add(new TextField<Integer>("discount", Integer.class).setEnabled(false));
            }
        };
        productsOrderedContanier.add(listViewProductsOrdered);
        
        step3.add(new AjaxSubmitLink("addProductOrdered"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Product product = productToAdd.getModelObject();
                Integer numberToAdd = productNumberToAdd.getModelObject();
                if(product != null && numberToAdd != null ){
                    ProductOrdered ordered = orderService.addProductOrdered(model.getObject(), product, currentProject, numberToAdd);
                    formNewOrder.getModelObject().addProductOrdered(ordered);
                    formNewOrder.getModelObject().applyDiscountIfApplicable();
                    formNewOrder.getModelObject().applyFreeShippingCostIfApplicable();
                    getFeedbackPanel().info("Prodotto aggiunto all'ordine");
                    target.add(formNewOrder);
                    target.add(getFeedbackPanel());
                }
                else if (product == null){
                    getFeedbackPanel().warn("Selezionare un prodotto");
                    target.add(getFeedbackPanel());
                }
                else if (numberToAdd == null){
                    getFeedbackPanel().warn("Selezionare il numero di prodotti");
                    target.add(getFeedbackPanel());
                }
            }
        });
        productsOrderedContanier.add(new TextField<Integer>("numberOfItemsInProductOrdered").setEnabled(false));
        productsOrderedContanier.add(new TextField<BigDecimal>("totalAmount", BigDecimal.class).setEnabled(false));
        step3.add(new TextArea<String>("notes"));
        AbstractChoice<PaymentType,PaymentType> paymentType = new DropDownChoice<PaymentType>("paymentType", Arrays.asList(PaymentType.values())).setChoiceRenderer(new EnumChoiceRenderer<PaymentType>());
        step3.add(paymentType.setRequired(true));

        paymentType.add(new OnChangeAjaxBehavior() {
            
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                formNewOrder.getModelObject().applyDiscountIfApplicable();
                formNewOrder.getModelObject().applyFreeShippingCostIfApplicable();
                if(formNewOrder.getModel().getObject().getPaymentType().equals(PaymentType.PREPAYMENT)){
                    getFeedbackPanel().info("Applicato Sconto del 5% per pagamento anticipato.");                    
                }
                else{
                    getFeedbackPanel().info("Rimosso Sconto del 5% per pagamento anticipato.");
                }

                target.add(formNewOrder);
                target.add(getFeedbackPanel());
            }

        });
        
        step3.add(new TextField<BigDecimal>("shippingCost", BigDecimal.class).setEnabled(false));
        step3.add(new DateField("deliveryTimeRequired"));
        
        submitNext = new AjaxSubmitLink("submitNext") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                customer.setEnabled(false);
                Order o = (Order) form.getModelObject();
                if(o.getCustomer().getCorporateName() == null){
                    getFeedbackPanel().warn("È necessario selezionare un cliente come destinatario");
                    target.add(getFeedbackPanel());
                }
                else{
                    moveStep();
                    target.add(form);
                    target.add(fakeTabs);
                    target.add(getFeedbackPanel());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                getFeedbackPanel().anyErrorMessage();
                target.add(getFeedbackPanel());
            }
        };
        formNewOrder.add(submitNext);
        
        submitConfirm = new AjaxSubmitLink("submitConfirm") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                moveStep();
                Order o = (Order) form.getModelObject();
                //some validations
                if(o.getProductsOrdered() == null || o.getProductsOrdered().size() == 0){
                    getFeedbackPanel().warn("È necessario inserire almeno un prodotto");
                    target.add(getFeedbackPanel());
                }
                //save the order
                else{
                    Order newOrder = orderService.placeNewOrder(o, getSecuritySession().getCurrentProject(), getSecuritySession().getLoggedInUser());
                    orderService.sendNotificationNewOrder(newOrder);
                    formNewOrder.setEnabled(false);
                    target.add(form);
                    target.add(fakeTabs);
                    getFeedbackPanel().success("Ordine inserito con successo in data: " + DateUtil.SDF2SHOW.print(newOrder.getCreationTime().getTime()));
                }
                target.add(getFeedbackPanel());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                getFeedbackPanel().anyErrorMessage();
                target.add(getFeedbackPanel());
            }
        };
        submitConfirm.setVisible(false);
        formNewOrder.add(submitConfirm);
    }
    

    private void moveStep(){
        if (step1.isVisible()) {
            step1.setVisible(false);
            step2.setVisible(true);
            step1Number.add(AttributeModifier.replace("class", "step-no-off"));
            step1Left.add(AttributeModifier.replace("class", "step-light-left"));
            step1Right.add(AttributeModifier.replace("class", "step-light-right"));
            step2Number.add(AttributeModifier.replace("class", "step-no"));
            step2Left.add(AttributeModifier.replace("class", "step-dark-left"));
            step2Right.add(AttributeModifier.replace("class", "step-dark-right"));
            step3Number.add(AttributeModifier.replace("class", "step-no-off"));
            step3Left.add(AttributeModifier.replace("class", "step-light-left"));
            step3Right.add(AttributeModifier.replace("class", "step-light-right"));
        }
        else if (step2.isVisible()) {
            step2.setVisible(false);
            step3.setVisible(true);
            step1Number.add(AttributeModifier.replace("class", "step-no-off"));
            step1Left.add(AttributeModifier.replace("class", "step-light-left"));
            step1Right.add(AttributeModifier.replace("class", "step-light-right"));
            step2Number.add(AttributeModifier.replace("class", "step-no-off"));
            step2Left.add(AttributeModifier.replace("class", "step-light-left"));
            step2Right.add(AttributeModifier.replace("class", "step-light-right"));
            step3Number.add(AttributeModifier.replace("class", "step-no"));
            step3Left.add(AttributeModifier.replace("class", "step-dark-left"));
            step3Right.add(AttributeModifier.replace("class", "step-dark-right"));
            submitConfirm.setVisible(true);
            submitNext.setVisible(false);
        }
        else if (step3.isVisible()) {
            step1Number.add(AttributeModifier.replace("class", "step-no-off"));
            step1Left.add(AttributeModifier.replace("class", "step-light-left"));
            step1Right.add(AttributeModifier.replace("class", "step-light-right"));
            step2Number.add(AttributeModifier.replace("class", "step-no-off"));
            step2Left.add(AttributeModifier.replace("class", "step-light-left"));
            step2Right.add(AttributeModifier.replace("class", "step-light-right"));
            step3Number.add(AttributeModifier.replace("class", "step-no-off"));
            step3Left.add(AttributeModifier.replace("class", "step-light-left"));
            step3Right.add(AttributeModifier.replace("class", "step-light-right"));
            step4Number.add(AttributeModifier.replace("class", "step-no"));
            step4Left.add(AttributeModifier.replace("class", "step-dark-left"));
            step4Right.add(AttributeModifier.replace("class", "step-dark-round"));
        }
    }
    
    private void addFakeTabs(PlaceNewOrderPage placeNewOrderPage){
        fakeTabs = new WebMarkupContainer("tabs");
        fakeTabs.setOutputMarkupId(true);
        add(fakeTabs);
        step1Number = new WebMarkupContainer("step1-number");
        step1Left = new WebMarkupContainer("step1-left");
        step1Right = new WebMarkupContainer("step1-right");
        step2Number = new WebMarkupContainer("step2-number");
        step2Left = new WebMarkupContainer("step2-left");
        step2Right = new WebMarkupContainer("step2-right");
        step3Number = new WebMarkupContainer("step3-number");
        step3Left = new WebMarkupContainer("step3-left");
        step3Right = new WebMarkupContainer("step3-right");
        step4Number = new WebMarkupContainer("step4-number");
        step4Left = new WebMarkupContainer("step4-left");
        step4Right = new WebMarkupContainer("step4-right");
        fakeTabs.add(step1Number);
        fakeTabs.add(step1Left);
        fakeTabs.add(step1Right);
        fakeTabs.add(step2Number);
        fakeTabs.add(step2Left);
        fakeTabs.add(step2Right);
        fakeTabs.add(step3Number);
        fakeTabs.add(step3Left);
        fakeTabs.add(step3Right);
        fakeTabs.add(step4Number);
        fakeTabs.add(step4Left);
        fakeTabs.add(step4Right);        
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
            response.addAll(customerService.get(getSecuritySession().getLoggedInUser(), 0, 0, "corporateName", true));
        }

        @Override
        public void toJson(Customer choice, JSONWriter writer) throws JSONException {
            writer.key("id").value(choice.getId()).key("text").value(choice.getCorporateName());
        }

        @Override
        public Collection<Customer> toChoices(Collection<String> ids) {
            Collection<Customer> results = new ArrayList<Customer>();
            List<Customer> customers = customerService.get(getSecuritySession().getLoggedInUser(), 0, 0, "corporateName", true);
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
