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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.https.RequireHttps;

/**
 * SignIn page performs authentication on an internal youeat db and on Facebook
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@RequireHttps
public class SignIn extends WebPage {

    private Label titlePage;
    private CustomFeedbackPanel feedbackPanel;

    /**
     * Constructor
     */
    public SignIn() {
        
        titlePage = new Label("pageTitle", ":: EaysSend - Eurocargo ::");
        add(titlePage);
        
        feedbackPanel = new CustomFeedbackPanel("feedBackPanel");
        feedbackPanel.setOutputMarkupId(true);
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);
        
        //to eliminate duplicated feedback panel
        feedbackPanel.setVisible(false);
        appendToPageTile(" " + getString("basepage.goSignIn"));

        add(new SignInPanel("signInPanel", true));
        
        add(new AjaxFallbackLink<String>("signUp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SignUpPage.class);
            }
        });
        
    }

    protected void appendToPageTile(String title){
        titlePage.setDefaultModelObject(titlePage.getDefaultModelObjectAsString().concat(title));
    }
}