package it.av.es.web.manager;

import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.ProjectService;
import it.av.es.service.UserService;
import it.av.es.web.BasePageSimple;
import it.av.es.web.data.UserSortableDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

@AuthorizeInstantiation({ "ADMIN" })
public class UserManagerPage extends BasePageSimple {

    @SpringBean
    private UserService userService;
    @SpringBean
    private ProjectService projectService;

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

        AjaxFallbackDefaultDataTable<User, String> dataTable = new AjaxFallbackDefaultDataTable<User, String>("dataTable",
                columns, new UserSortableDataProvider(), 50);
        add(dataTable);

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

}
