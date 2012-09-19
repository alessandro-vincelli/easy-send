package it.av.es.web;

import it.av.es.web.security.SecuritySession;

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
        
        mountPage("/products", ProductManagerPage.class);
        mountPage("/users", UserManagerPage.class);
        mountPage("/newOrder", PlaceNewOrderPage.class);
        mountPage("/customers", CustomerManagerPage.class);
        mountPage("/projects", ProjectManagerPage.class);
        mountPage("/signIn", SignIn.class);
        mountPage("/signOut", SignOut.class);
        mountPage("/signUp", SignUpPage.class);
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
}
