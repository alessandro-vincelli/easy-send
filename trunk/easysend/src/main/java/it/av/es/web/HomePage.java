package it.av.es.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

@AuthorizeInstantiation( { "USER", "VENDOR" })
public class HomePage extends BasePageSimple{

    public HomePage() {
        super();
        add(new BookmarkablePageLink<String>("goUserManagerPage", UserManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserManagerPage.class)));
            }
        });
        add(new BookmarkablePageLink<String>("goProjectManagerPage", ProjectManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(ProjectManagerPage.class)));
            }
        });
    }
    

}
