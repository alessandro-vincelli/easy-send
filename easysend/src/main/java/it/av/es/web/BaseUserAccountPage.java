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

import it.av.es.model.Language;
import it.av.es.model.User;
import it.av.es.service.CountryService;
import it.av.es.service.LanguageService;
import it.av.es.service.UserService;
import it.av.es.web.security.SecuritySession;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User account manager page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@RequireHttps
@AuthorizeInstantiation({ "USER", "ADMIN" })
public class BaseUserAccountPage extends BasePageSimple {

    @SpringBean
    private UserService userService;
    @SpringBean
    private LanguageService languageService;
    @SpringBean
    private CountryService countryService;
    private User user;
    final private Form<User> accountForm;

    public BaseUserAccountPage() {
        String userId = getLoggedinUser().getId();
        user = userService.getByID(userId);

        accountForm = new Form<User>("account", new CompoundPropertyModel<User>(user));
        accountForm.setOutputMarkupId(true);
        add(accountForm);
        accountForm.add(new RequiredTextField<String>("firstname"));
        accountForm.add(new RequiredTextField<String>("lastname"));
        accountForm.add(new DropDownChoice<Language>("language", languageService.getAll(), new LanguageRenderer()));

    }

    protected class LanguageRenderer implements IChoiceRenderer<Language> {
        @Override
        public Object getDisplayValue(Language object) {
            return getString(object.getLanguage());
        }

        @Override
        public String getIdValue(Language object, int index) {
            return object.getId();
        }
    }

    public Form<User> getAccountForm() {
        return accountForm;
    }

    /**
     * @return the user
     */
    public User getEater() {
        return user;
    }

    /**
     * @param user the user to set
     */
    protected void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the userService
     */
    protected UserService getUserService() {
        return userService;
    }

    protected User getLoggedinUser() {
        if (getSession() != null && Session.exists()) {
            return ((SecuritySession) getSession()).getLoggedInUser();
        }
        return null;
    }
}