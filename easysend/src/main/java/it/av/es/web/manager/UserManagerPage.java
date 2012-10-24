package it.av.es.web.manager;

import it.av.es.model.Language;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.LanguageService;
import it.av.es.service.ProjectService;
import it.av.es.service.UserService;
import it.av.es.web.BasePageSimple;
import it.av.es.web.data.UserSortableDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

@AuthorizeInstantiation({ "ADMIN", "OPERATOR" })
public class UserManagerPage extends BasePageSimple {

    @SpringBean
    private UserService userService;
    @SpringBean
    private ProjectService projectService;
    @SpringBean
    private LanguageService languageService;
    private final Form<User> accountForm;
    private User user = new User();
    private String confirmPassword = "";
    private String newPasswordValue = "";
    private AjaxFallbackDefaultDataTable<User, String> dataTable;

    public UserManagerPage() {
        super();

        List<IColumn<User, String>> columns = new ArrayList<IColumn<User, String>>();

        columns.add(new AbstractColumn<User, String>(new Model<String>("Assign Projects")) {
            public void populateItem(Item<ICellPopulator<User>> cellItem, String componentId, IModel<User> model) {
                cellItem.add(new ActionPanel(componentId, model));
            }
        });

        columns.add(new PropertyColumn<User, String>(new Model<String>("Last Name"), User.LASTNAME, User.LASTNAME));
        columns.add(new PropertyColumn<User, String>(new Model<String>("First Name"), User.FIRSTNAME, User.FIRSTNAME));
        columns.add(new PropertyColumn<User, String>(new Model<String>("Email"), User.EMAIL, User.EMAIL));

        dataTable = new AjaxFallbackDefaultDataTable<User, String>("dataTable", columns, new UserSortableDataProvider(), 50);
        add(dataTable);

        accountForm = new Form<User>("user", new CompoundPropertyModel<User>(user));
        accountForm.setOutputMarkupId(true);
        add(accountForm);
        accountForm.add(new RequiredTextField<String>("firstname"));
        accountForm.add(new RequiredTextField<String>("lastname"));
        accountForm.add(new RequiredTextField<String>(User.PHONENUMBER));
        accountForm.add(new DropDownChoice<Language>("language", languageService.getAll(), new LanguageRenderer()).setNullValid(false).setRequired(true));
        accountForm.add(new RequiredTextField<String>("email"));
        StringValidator pwdValidator = StringValidator.lengthBetween(6, 20);
        PasswordTextField pwd1 = new PasswordTextField("newPassword", new Model<String>(newPasswordValue));
        pwd1.setRequired(false);
        pwd1.add(pwdValidator);
        pwd1.setResetPassword(false);
        accountForm.add(pwd1);
        PasswordTextField pwd2 = new PasswordTextField("password-confirm", new Model<String>(confirmPassword));
        pwd2.setRequired(false);
        accountForm.add(pwd2);
        EqualPasswordInputValidator passwordInputValidator = new EqualPasswordInputValidator(pwd1, pwd2);
        accountForm.add(passwordInputValidator);
        accountForm.add(new SubmitButton("saveAccount", accountForm));
    }

    private class SubmitButton extends AjaxButton {
        public SubmitButton(String id, Form<User> form) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {

            String newPwd = form.get("password-confirm").getDefaultModelObjectAsString();
            if ((!newPwd.isEmpty())) {
                ((User) form.getModelObject()).setPassword(userService.encodePassword(newPwd, ((User) form.getModelObject()).getPasswordSalt()));
            }
            User eater = (User) form.getModelObject();
            eater = userService.addOrUpdate(eater);
            ((CompoundPropertyModel<User>) form.getModel()).setObject(userService.getByID(eater.getId()));
            newPasswordValue = "";
            confirmPassword = "";
            success(getString("info.accountSaved"));
            getFeedbackPanel().publishWithEffects(target);
            target.add(dataTable);
            target.add(accountForm);
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            target.add(getFeedbackPanel());
        }
    }

    /**
     * 
     */
    class ActionPanel extends Panel {
        /**
         * @param id
         *            component id
         * @param userModel
         *            model for contact
         */
        public ActionPanel(String id, final IModel<User> userModel) {
            super(id, userModel);
            Injector.get().inject(this);
            AjaxSubmitLink edit = new AjaxSubmitLink("edit", accountForm) {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    super.onSubmit(target, form);
                    user = userModel.getObject();
                    accountForm.setDefaultModelObject(userModel);
                    target.add(accountForm);
                }
                
            };
            edit.setDefaultFormProcessing(false);
            add(edit);
            add(new ListView<Project>("projects", new ArrayList<Project>(projectService.getAll())) {
                @Override
                protected void populateItem(final ListItem<Project> item) {
                    Set<Project> projects = userModel.getObject().getProjects();
                    boolean checked = false;
                    if (projects != null && projects.size() > 0) {
                        if (projects.contains(item.getModelObject())) {
                            checked = true;
                        }
                    }
                    AjaxCheckBox ajaxCheckBox = new AjaxCheckBox("prj", new Model<Boolean>(checked)) {
                        @Override
                        protected void onUpdate(AjaxRequestTarget target) {
                            if (getModel().getObject()) {
                                userService.assignUserToProject(userModel.getObject(), item.getModelObject());
                            } else {
                                userService.removeUserFromProject(userModel.getObject(), item.getModelObject());
                            }

                        }
                    };
                    item.add(ajaxCheckBox);
                    item.add(new Label("prjName", new Model<String>(item.getModelObject().getName())));
                }
            });

        }
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
}
