/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.es.web;

import it.av.es.model.User;
import it.av.es.util.CookieUtil;
import it.av.es.web.security.SecuritySession;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxRequestTarget.IJavaScriptResponse;
import org.apache.wicket.ajax.AjaxRequestTarget.IListener;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.cookies.CookieUtils;

/**
 * Base Page without user session. Contains some commons elements.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class BasePageSimple extends WebPage implements IAjaxIndicatorAware{
    
    private CustomFeedbackPanel feedbackPanel;
    private Label titlePage;
    private SecuritySession session;
    private User loggedInUser;

    /**
     * Construct.
     */
    public BasePageSimple() {
        HtmlUtil.fixInitialHtml(this);
        titlePage = new Label("pageTitle", ":: EasyTrack - Eurocargo ::");
        add(titlePage);
        
        feedbackPanel = new CustomFeedbackPanel("feedBackPanel");
        feedbackPanel.add(new AbstractDefaultAjaxBehavior() {
            
//            @Override
//            public void onBeforeRespond(Map<String, Component> map, AjaxRequestTarget target) {
//            }
//            
//            @Override
//            public void onAfterRespond(Map<String, Component> map, IJavaScriptResponse response) {
//                response.addJavaScript("jQuery('#" + electionContainer.getMarkupId() + "').fadeTo(300, 0.5, function() {})");
//                response.addJavaScript("jQuery('#" + electionContainer.getMarkupId() + "').fadeTo(300, 1.0, function() {})");
//            }
//            
            @Override
            protected void respond(AjaxRequestTarget target) {
                target.appendJavaScript("jQuery('#" + feedbackPanel.getMarkupId() + "').fadeTo(300, 0.5, function() {})");
                
             // register the onSuccess listener that will execute Handlebars logic
                target.addListener(new IListener() {
                    
                    @Override
                    public void onBeforeRespond(Map<String, Component> map, AjaxRequestTarget target) {
                        System.out.println("");
                    }
                    
                    @Override
                    public void onAfterRespond(Map<String, Component> map, IJavaScriptResponse response) {
                        response.addJavaScript("jQuery('#" + feedbackPanel.getMarkupId() + "').fadeTo(300, 0.5, function() {})");
                        response.addJavaScript("jQuery('#" + feedbackPanel.getMarkupId() + "').fadeTo(300, 1.0, function() {})");
                    }
                });
            }
        });
        ;
        
        feedbackPanel.setOutputMarkupId(true);
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);
        
        session = ((SecuritySession)getSession());
        
        
        
        loggedInUser = session.getLoggedInUser();
        if(new CookieUtils().load(CookieUtil.LANGUAGE) != null) {
            getSession().setLocale(
                    new Locale(new CookieUtils().load(CookieUtil.LANGUAGE)));
        } else {
            if (loggedInUser != null) {
                ((WebResponse)RequestCycle.get().getResponse()).addCookie((new Cookie(CookieUtil.LANGUAGE, loggedInUser.getLanguage().getLanguage())));
                getSession().setLocale(new Locale(loggedInUser.getLanguage().getLanguage()));
            }
        }
        
        //BookmarkablePageLink goAccount = new BookmarkablePageLink<String>("goAccount", UserAccountPage.class, goAccountParameters);
        
//        Label name = new Label("loggedInUser", loggedInUser != null ? loggedInUser.getFirstname() + " " +loggedInUser.getLastname() : "");
//        add(name);
        
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
//        add(new BookmarkablePageLink<String>("goProductManagerPage", ProductManagerPage.class) {
//            @Override
//            protected void onBeforeRender() {
//                super.onBeforeRender();
//                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
//                        .isInstantiationAuthorized(ProductManagerPage.class)));
//            }
//        });
        add(new BookmarkablePageLink<String>("goOrderManagerPage", OrderManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(OrderManagerPage.class)));
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
        add(new BookmarkablePageLink<String>("goCustomerManagerPage", CustomerManagerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(CustomerManagerPage.class)));
            }
        });
        add(new BookmarkablePageLink<String>("goCustomerNewPage", CustomerPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(CustomerPage.class)));
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
        add(new BookmarkablePageLink<String>("goUserAccountPage", UserAccountPage.class) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible((getApplication().getSecuritySettings().getAuthorizationStrategy()
                        .isInstantiationAuthorized(UserAccountPage.class)));
                add(AttributeModifier.replace("value", loggedInUser.getFirstname() + " " +loggedInUser.getLastname()));                
            }
        });        

        
//        BookmarkablePageLink goInfo = new BookmarkablePageLink("goInfo", AboutPage.class);
//        add(goInfo);
//        
//        BookmarkablePageLink goPrivacy = new BookmarkablePageLink("goPrivacy", PrivacyPage.class);
//        add(goPrivacy);
    }

    public final CustomFeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }

    protected void setPageTitle(Label titlePage) {
        this.titlePage = titlePage;
    }
    
    protected Label getPageTitle() {
        return titlePage;
    }
    
    protected void appendToPageTile(String title){
        titlePage.setDefaultModelObject(titlePage.getDefaultModelObjectAsString().concat(title));
    }

    public SecuritySession getSecuritySession() {
        return session;
    }
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
//        response.render(CssHeaderItem.forReference(new CssResourceReference(HomePage.class, "/960.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(HomePage.class, "/template.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(HomePage.class, "/colour.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(HomePage.class, "/text.css")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAjaxIndicatorMarkupId() {
        return "bysy_indicator";
    }
    
}
