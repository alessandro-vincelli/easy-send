/**
 * Copyright 2012 the original author or authors
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
import it.av.es.service.UserService;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * User account manager page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@RequireHttps
@AuthorizeInstantiation( { "USER", "ADMIN" , "VENDOR", "OPERATOR" })
public class UserAccountPage extends BaseUserAccountPage {

    @SpringBean
    private UserService eaterService;
    private String confirmPassword = "";
    private String oldPasswordValue = "";
    private String newPasswordValue = "";

    public UserAccountPage() {
        super();
        getAccountForm().add(new Label("email"));
        StringValidator pwdValidator = StringValidator.lengthBetween(6, 20);
        PasswordTextField oldPassword = new PasswordTextField("oldPassword", new Model<String>(oldPasswordValue));
        oldPassword.add(new OldPasswordValidator(getAccountForm()));
        getAccountForm().add(oldPassword);
        PasswordTextField pwd1 = new PasswordTextField("newPassword", new Model<String>(newPasswordValue));
        pwd1.setRequired(false);
        pwd1.add(pwdValidator);
        pwd1.setResetPassword(false);
        getAccountForm().add(pwd1);
        PasswordTextField pwd2 = new PasswordTextField("password-confirm", new Model<String>(confirmPassword));
        pwd2.setRequired(false);
        getAccountForm().add(pwd2);
        EqualPasswordInputValidator passwordInputValidator = new EqualPasswordInputValidator(pwd1, pwd2);
        getAccountForm().add(passwordInputValidator);
        getAccountForm().add(new SubmitButton("saveAccount", getAccountForm()));
    }

    private class SubmitButton extends AjaxFallbackButton {
        public SubmitButton(String id, Form<User> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            
            String newPwd = form.get("password-confirm").getDefaultModelObjectAsString();
            if ((!newPwd.isEmpty())) {
                ((User)form.getModelObject()).setPassword(eaterService.encodePassword(newPwd, ((User)form.getModelObject()).getPasswordSalt()));
            }
            User eater = (User) form.getModelObject();
            getUserService().update(eater);
            ((CompoundPropertyModel<User>)form.getModel()).setObject(getUserService().getByID(eater.getId()));
            newPasswordValue = "";
            oldPasswordValue = "";
            confirmPassword = "";
            success(getString("info.accountSaved"));
            if (target != null) {
                target.add(getFeedbackPanel());
                getFeedbackPanel().publishWithEffects(target);
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            target.add(getFeedbackPanel());
        }
    }

    private class OldPasswordValidator extends AbstractValidator<String> {
        Form<User> form;
        public OldPasswordValidator(Form<User> form) {
            super();
            this.form = form;
        }

        @Override
        protected void onValidate(IValidatable<String> validatable) {
            if (!eaterService.isPasswordValid(form.getModelObject().getPassword(), validatable.getValue().toString(), form.getModelObject()
                    .getPasswordSalt())) {
                error(validatable);
            }
        }
    }

    private class LanguageRenderer implements IChoiceRenderer<Language> {
        @Override
        public Object getDisplayValue(Language object) {
            return getString(object.getLanguage());
        }

        @Override
        public String getIdValue(Language object, int index) {
            return object.getId();
        }
    }

}