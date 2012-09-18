package it.av.es.web;

import it.av.es.model.Project;
import it.av.es.service.ProjectService;
import it.av.es.web.data.ProjectSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The page provides the home page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR" })
public class ProjectManagerPage extends BasePageSimple {

    @SpringBean
    private ProjectService userService;
    @SpringBean
    private ProjectService projectService;

    public ProjectManagerPage() {
        super();

        List<IColumn<Project, String>> columns = new ArrayList<IColumn<Project, String>>();

        columns.add(new PropertyColumn<Project, String>(new Model<String>("Project Name"), Project.NAME_FIELD, Project.NAME_FIELD));

        final AjaxFallbackDefaultDataTable<Project, String> dataTable = new AjaxFallbackDefaultDataTable<Project, String>(
                "dataTable", columns, new ProjectSortableDataProvider(), 50);
        add(dataTable);

        final Form<Project> formPrj = new Form<Project>("prj", new CompoundPropertyModel<Project>(new Project()));
        add(formPrj);
        formPrj.add(new TextField<String>("name"));
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Project p = (Project) form.getModelObject();
                projectService.save(p);
                target.add(dataTable);
                formPrj.setModelObject(new Project());
            }
        });
    }
    

}
