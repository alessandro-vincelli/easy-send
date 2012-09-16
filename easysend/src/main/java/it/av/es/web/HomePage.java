package it.av.es.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation( { "USER", "VENDOR" })
public class HomePage extends BasePageSimple{

    public HomePage() {
        super();
    }
    

}
