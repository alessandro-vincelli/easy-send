package it.av.es.web;

import it.av.es.service.ProjectService;
import it.av.es.web.security.SecuritySession;

import org.apache.wicket.spring.injection.annot.SpringBean;

public class SignInPanel extends org.apache.wicket.authroles.authentication.panel.SignInPanel {

    @SpringBean
    private ProjectService projectService;

    public SignInPanel(String id, boolean includeRememberMe) {
        super(id, includeRememberMe);
        remove("feedback");
        add(new CustomFeedbackPanel("feedback"));
    }

    @Override
    protected void onSignInSucceeded() {
        super.onSignInSucceeded();
        /**
         * set default project to the only ONE!
         */
        ((SecuritySession) getSession()).setCurrentProject(projectService.getAll().iterator().next());
    }

}
