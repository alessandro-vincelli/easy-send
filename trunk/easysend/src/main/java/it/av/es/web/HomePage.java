package it.av.es.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * The page provides the home page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "ADMIN", "VENDOR", "OPERATOR","PROJECT_MANAGER" })
public class HomePage extends BasePageSimple{

    public HomePage() {
        super();
    }
    

}
