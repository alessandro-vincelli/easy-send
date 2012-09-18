package it.av.es.web;

import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.ProjectService;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The page provides the home page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR" })
public class SelectProjectPage extends BasePageSimple {

    @SpringBean
    private ProjectService projectService;

    public SelectProjectPage() {
        super();
        User user = getSession().getLoggedInUser();

        final DropDownChoice<Project> choice = new DropDownChoice<Project>("selectProject", new Model<Project>(new Project()),
                new ArrayList<Project>(user.getProjects()), new ProjectChoiceRenderer());
        choice.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                getSession().setCurrentProject(choice.getModelObject());
            }
        });
        add(choice);
    }

    private class ProjectChoiceRenderer implements IChoiceRenderer<Project> {

        @Override
        public Object getDisplayValue(Project object) {
            return object.getName();
        }

        @Override
        public String getIdValue(Project object, int index) {
            return object.getId();
        }

    }

}
