package it.av.es.web.manager;

import it.av.es.model.Group;
import it.av.es.service.GroupService;
import it.av.es.web.BasePageSimple;
import it.av.es.web.data.GroupSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

@AuthorizeInstantiation({ "ADMIN" })
public class GroupManagerPage extends BasePageSimple {

    @SpringBean
    private GroupService groupService;
    private AjaxFallbackDefaultDataTable<Group, String> dataTable;

    public GroupManagerPage() {
        super();

        List<IColumn<Group, String>> columns = new ArrayList<IColumn<Group, String>>();

        columns.add(new AbstractColumn<Group, String>(new Model<String>("Actions")) {
            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> model) {
                cellItem.add(new ActionPanel(componentId, model));
            }
        });

        columns.add(new PropertyColumn<Group, String>(new Model<String>("name"), Group.NAME, Group.NAME));
        columns.add(new PropertyColumn<Group, String>(new Model<String>("description"), Group.DESCRIPTION, Group.DESCRIPTION));

        dataTable = new AjaxFallbackDefaultDataTable<Group, String>("dataTable", columns, new GroupSortableDataProvider(), 50);
        add(dataTable);

        final Form<Group> form = new Form<Group>("form", new CompoundPropertyModel<Group>(new Group()));
        add(form);
        form.add(new TextField<String>("name"));
        form.add(new TextField<String>("description"));
        form.add(new AjaxSubmitLink("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> f) {
                super.onSubmit(target, form);
                groupService.save(form.getModelObject());
                form.setModelObject(new Group());
                target.add(dataTable);
                target.add(form);
                getFeedbackPanel().success("Group added with success");
                getFeedbackPanel().publishWithEffects(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                getFeedbackPanel().error("An error occured");
                getFeedbackPanel().publishWithEffects(target);
            }
        });
    }

    class ActionPanel extends Panel {
        public ActionPanel(String id, final IModel<Group> groupModel) {
            super(id, groupModel);
            Injector.get().inject(this);
            AjaxLink<Group> buttonRemove = new AjaxLink<Group>("remove", groupModel) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        groupService.remove(getModelObject());
                        target.add(dataTable);
                        getFeedbackPanel().success(getString("group.message.groupRemoved"));
                    } catch (Exception e) {
                        getFeedbackPanel().error(getString("group.message.groupRemovedFailed"));
                    }
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(buttonRemove);
        }
    }

}
