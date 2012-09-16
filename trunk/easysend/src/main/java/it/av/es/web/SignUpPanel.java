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

import it.av.es.EasySendException;
import it.av.es.UserAlreadyExistsException;
import it.av.es.model.Language;
import it.av.es.model.User;
import it.av.es.service.LanguageService;
import it.av.es.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * The panel provides the Sign Up form
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SignUpPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private Form<User> signUpForm;
    private FeedbackPanel feedbackPanel;
    @SpringBean
    private UserService userService;
    private Link<String> goSignInAfterSignUp;
    private String passwordConfirm = "";
    @SpringBean
    private LanguageService languageService;

    /**
     * Constructor
     * 
     * @param id
     * @param feedbackPanel
     * @throws EasySendException
     */
    public SignUpPanel(String id, FeedbackPanel feedbackPanel) throws EasySendException {
        super(id);
        Injector.get().inject(this);
        this.feedbackPanel = feedbackPanel;

        RfcCompliantEmailAddressValidator emailAddressValidator = RfcCompliantEmailAddressValidator.getInstance();
        StringValidator pwdValidator = StringValidator.lengthBetween(6, 20);
        EmailPresentValidator emailPresentValidator = new EmailPresentValidator();

        User user = new User();
        user.setLanguage(languageService.getSupportedLanguage(getLocale()));

        signUpForm = new Form<User>("signUpForm", new CompoundPropertyModel<User>(user));

        signUpForm.setOutputMarkupId(true);
        signUpForm.add(new RequiredTextField<String>(User.FIRSTNAME));
        signUpForm.add(new RequiredTextField<String>(User.LASTNAME));
        signUpForm.add(new RequiredTextField<String>(User.EMAIL).add(emailAddressValidator).add(emailPresentValidator));

        signUpForm.add(new DropDownChoice<Language>("language", languageService.getAll(), new LanguageRenderer())
                .setRequired(true));
        PasswordTextField pwd1 = new PasswordTextField(User.PASSWORD);
        pwd1.add(pwdValidator);
        signUpForm.add(pwd1);
        PasswordTextField pwd2 = new PasswordTextField("password-confirm", new Model<String>(passwordConfirm));
        signUpForm.add(pwd2);
        EqualPasswordInputValidator passwordInputValidator = new EqualPasswordInputValidator(pwd1, pwd2);
        signUpForm.add(passwordInputValidator);
        SubmitButton submitButton = new SubmitButton("buttonCreateNewAccount", signUpForm);
        submitButton.setOutputMarkupId(true);
        add(submitButton);
        signUpForm.setDefaultButton(submitButton);
        goSignInAfterSignUp = new Link<String>("goSignInAfterSignUp") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(SignIn.class);
            }
        };
        goSignInAfterSignUp.setOutputMarkupId(true);
        goSignInAfterSignUp.setOutputMarkupPlaceholderTag(true);
        goSignInAfterSignUp.setVisible(false);
        add(goSignInAfterSignUp);
        add(signUpForm);
    }

    /**
     * Check if another user is already register with the given email
     */
    private class EmailPresentValidator extends StringValidator {

        @Override
        protected ValidationError decorate(ValidationError error, IValidatable<String> validatable) {
            if (userService.getByEmail(validatable.getValue()) != null) {
                error.addKey("emailAlreadyPresent");
            }
            return error;
        }

    }

    private class SubmitButton extends AjaxButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<User> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            try {
                User user = (User) form.getModelObject();
                if (StringUtils.isNotBlank(user.getId())) {
                    getFeedbackPanel().info(new StringResourceModel("error.operationNotPermitted", this, null).getString());
                } else {
                    userService.addRegolarUser(user);
                    signUpForm.setVisible(false);
                    this.setVisible(false);
                    goSignInAfterSignUp.setVisible(true);
                }
            } catch (UserAlreadyExistsException e) {
                getFeedbackPanel().error(new StringResourceModel("error.userAlreadyExistsException", this, null).getString());
            } catch (EasySendException e) {
                getFeedbackPanel().error(new StringResourceModel("error.operationNotPermitted", this, null).getString());
            }
            if (target != null) {
                target.add(getFeedbackPanel());
                target.add(signUpForm);
                target.add(goSignInAfterSignUp);
                target.add(this);
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form form) {
            getFeedbackPanel().anyErrorMessage();
            target.add(getFeedbackPanel());
            target.add(form);
        }
    }

    public final FeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
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