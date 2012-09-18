package it.av.es.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * The page provides the home page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
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
        add(new BookmarkablePageLink<String>("goProductManagerPage", ProductManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(ProductManagerPage.class)));
            }
        });
        add(new BookmarkablePageLink<String>("goSelectProjectPage", SelectProjectPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(SelectProjectPage.class)));
            }
        });
        add(new BookmarkablePageLink<String>("goPlaceNewOrderPage", PlaceNewOrderPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(PlaceNewOrderPage.class)));
            }
        });
        add(new BookmarkablePageLink<String>("goSignOut", SignOut.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(SignOut.class)));
            }
        });
    }
    

}
