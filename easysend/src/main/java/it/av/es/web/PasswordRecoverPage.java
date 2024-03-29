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

import it.av.es.model.User;
import it.av.es.service.UserService;
import it.av.es.web.component.CustomFeedbackPanel;

import java.io.Serializable;
import java.util.UUID;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * The page provides password recovery.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@RequireHttps
public class PasswordRecoverPage extends WebPage {

    @SpringBean
    private UserService userService;
    private final Link<String> goSignInAfterRecover;
    private CustomFeedbackPanel feedbackPanel;

    /**
     * Constructor.
     */
    public PasswordRecoverPage() {
        Label titlePage = new Label("pageTitle", ":: EasyTrack - Eurocargo ::");
        add(titlePage);

        feedbackPanel = new CustomFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        add(feedbackPanel);
        Form<PasswordRecoverBean> pwdRecoverForm = new Form<PasswordRecoverBean>("pwdRecoverForm",
                new CompoundPropertyModel<PasswordRecoverBean>(new PasswordRecoverBean()));
        pwdRecoverForm.setOutputMarkupId(true);
        //final KittenCaptchaPanel captcha = new KittenCaptchaPanel("captcha", new Dimension(400, 200));
        //pwdRecoverForm.add(captcha);
        // used to show error message
        //final HiddenField<String> captchaHidden = new HiddenField<String>("captchaHidden", new Model());
        //pwdRecoverForm.add(captchaHidden);
        //pwdRecoverForm.add(new KittenCaptchaValidator(captchaHidden, captcha));
        add(pwdRecoverForm);
        pwdRecoverForm.add(new RequiredTextField<String>("email").add(new EmailCheckExistValidator()).add(
                new EmailCheckExistValidator()));
        pwdRecoverForm.add(new SubmitButton("buttonRecoverPassword", pwdRecoverForm));

        goSignInAfterRecover = new Link<String>("goSignInAfterRecover") {

            @Override
            public void onClick() {
                setResponsePage(SignIn.class);
            }
        };
        goSignInAfterRecover.setOutputMarkupPlaceholderTag(true);
        goSignInAfterRecover.setVisible(false);
        add(goSignInAfterRecover);
    }

    private class SubmitButton extends AjaxButton {

        public SubmitButton(String id, Form<PasswordRecoverBean> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            PasswordRecoverBean bean = (PasswordRecoverBean) form.getModelObject();
            User user = userService.getByEmail(bean.getEmail());
            String newPassword = UUID.randomUUID().toString().substring(0, 8);
            user.setPassword(userService.encodePassword(newPassword, user.getPasswordSalt()));
            userService.update(user);
            userService.sendPasswordByEmail(user, newPassword);
            form.setVisible(false);
            this.setVisible(false);
            goSignInAfterRecover.setVisible(true);
            if (target != null) {
                target.add(feedbackPanel);
                target.add(form);
                target.add(this);
                target.add(goSignInAfterRecover);
            }
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form form) {
            target.add(feedbackPanel);
            target.add(form);
        }
    }

    /**
     * Check if the mail exists
     */
    private class EmailCheckExistValidator extends AbstractValidator<String> {

        @Override
        protected void onValidate(IValidatable<String> validatable) {
            if (userService.getByEmail(validatable.getValue()) == null) {
                error(validatable);
            }
        }
    }

    private class PasswordRecoverBean implements Serializable {
        private String email;

        public PasswordRecoverBean() {
            super();
            email = "";
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
