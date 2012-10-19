package it.av.es.web.manager;

import it.av.es.model.Group;
import it.av.es.model.User;
import it.av.es.service.GroupService;
import it.av.es.web.BasePageSimple;
import it.av.es.web.data.GroupSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

@AuthorizeInstantiation({ "ADMIN" })
public class GroupManagerPage extends BasePageSimple {

    @SpringBean
    private GroupService groupService;

    public GroupManagerPage() {
        super();

        List<IColumn<Group, String>> columns = new ArrayList<IColumn<Group, String>>();

        columns.add(new AbstractColumn<Group, String>(new Model<String>("Actions")) {
            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> model) {
                cellItem.add(new ActionPanel(componentId, model));
            }
        });

        columns.add(new PropertyColumn<Group, String>(new Model<String>("name"), User.LASTNAME, User.LASTNAME));
        columns.add(new PropertyColumn<Group, String>(new Model<String>("description"), User.FIRSTNAME, User.FIRSTNAME));

        AjaxFallbackDefaultDataTable<Group, String> dataTable = new AjaxFallbackDefaultDataTable<Group, String>("dataTable",
                columns, new GroupSortableDataProvider(), 50);
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
        public ActionPanel(String id, final IModel<Group> userModel) {
            super(id, userModel);
            Injector.get().inject(this);
//            add(new ListView<Project>("projects", new ArrayList<Project>(projectService.getAll())) {
//                @Override
//                protected void populateItem(final ListItem<Project> item) {
//                    Set<Project> projects = userModel.getObject().getProjects();
//                    boolean checked = false;
//                    if (projects != null && projects.size() > 0) {
//                        if (projects.contains(item.getModelObject())) {
//                            checked = true;
//                        }
//                    }
//                    AjaxCheckBox ajaxCheckBox = new AjaxCheckBox("prj", new Model<Boolean>(checked)) {
//                        @Override
//                        protected void onUpdate(AjaxRequestTarget target) {
//                            if (getModel().getObject()) {
//                                groupService.assignUserToProject(userModel.getObject(), item.getModelObject());
//                            } else {
//                                groupService.removeUserFromProject(userModel.getObject(), item.getModelObject());
//                            }
//
//                        }
//                    };
//                    item.add(ajaxCheckBox);
//                    item.add(new Label("prjName", new Model<String>(item.getModelObject().getName())));
//                }
//            });

        }
    }

}
