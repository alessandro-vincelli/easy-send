package it.av.es.web;

import it.av.es.model.ClosingDays;
import it.av.es.model.ClosingRange;
import it.av.es.model.DeliveryDays;
import it.av.es.model.DeliveryType;
import it.av.es.model.DeliveryVehicle;
import it.av.es.model.PaymentType;
import it.av.es.web.converter.ClosingDaysConverter;
import it.av.es.web.converter.ClosingRangeConverter;
import it.av.es.web.converter.CustomBigDecimalConverter;
import it.av.es.web.converter.DeliveryDaysConverter;
import it.av.es.web.converter.DeliveryTypeConverter;
import it.av.es.web.converter.DeliveryVehicleConverter;
import it.av.es.web.converter.PaymentTypeConverter;
import it.av.es.web.manager.GroupManagerPage;
import it.av.es.web.manager.GroupMembersManagerPage;
import it.av.es.web.manager.ProductFamilyManagerPage;
import it.av.es.web.manager.ProjectManagerPage;
import it.av.es.web.manager.UserManagerPage;
import it.av.es.web.security.SecuritySession;

import java.math.BigDecimal;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class WicketApplication extends AuthenticatedWebApplication {

    // Constants

    private static final Logger LOGGER = LoggerFactory.getLogger(WicketApplication.class);

    @Override
    protected void init() {
        super.init();
        if(getSpringContext() != null){
            getComponentInstantiationListeners().add(new SpringComponentInjector(this, getSpringContext(), true));
        }
        
        //mountPage("/products", ProductManagerPage.class);
        mountPage("/users", UserManagerPage.class);
        mountPage("/newOrder", PlaceNewOrderPage.class);
        mountPage("/customers", CustomerManagerPage.class);
        mountPage("/customer/${" + CustomHttpParams.CUSTOMER_ID + "}/" , CustomerPage.class);
        mountPage("/customer" , CustomerPage.class);
        mountPage("/projects", ProjectManagerPage.class);
        mountPage("/signIn", SignIn.class);
        mountPage("/signOut", SignOut.class);
        mountPage("/signUp", SignUpPage.class);
        mountPage("/orders", OrderManagerPage.class);
        mountPage("/passwordRecover", PasswordRecoverPage.class);
        mountPage("/userAccount" , UserAccountPage.class);
        mountPage("/groups" , GroupManagerPage.class);
        mountPage("/groupMembers" , GroupMembersManagerPage.class);
        mountPage("/productFamily" , ProductFamilyManagerPage.class);
    }


    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }


    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return SecuritySession.class;
    }


    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignIn.class;
    }


    /**
     * @return WebApplicationContext
     */
    public final WebApplicationContext getSpringContext() {
        return WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    }


    @Override
    protected IConverterLocator newConverterLocator() {
        return new CustomConverterLocator();
    }
    
    private class CustomConverterLocator extends ConverterLocator{
        public CustomConverterLocator() {
            super();
            set(DeliveryType.class, new DeliveryTypeConverter());
            set(PaymentType.class, new PaymentTypeConverter());
            set(DeliveryVehicle.class, new DeliveryVehicleConverter());
            set(DeliveryDays.class, new DeliveryDaysConverter());
            set(ClosingDays.class, new ClosingDaysConverter());
            set(ClosingRange.class, new ClosingRangeConverter());
            set(BigDecimal.class, new CustomBigDecimalConverter());
        }
    }
}
