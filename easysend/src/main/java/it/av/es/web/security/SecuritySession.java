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
package it.av.es.web.security;

import it.av.es.model.Project;
import it.av.es.model.User;

import java.util.Locale;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implements the authentication strategies
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SecuritySession extends AuthenticatedWebSession {
    
    @SpringBean(name="authenticationManager")
    private ProviderManager authenticationProvider;
    /**
     * Construct.
     * 
     * @param request The current request object
     */
    public SecuritySession(Request request) {
        super(request);
        Injector.get().inject(this);
        setLocale(Locale.ITALIAN);
    }

    private Authentication auth;
    private String username = "";
    private Roles roles;
    private Project currentProject = null;

    /**
     * @see org.apache.wicket.authentication.AuthenticatedWebSession#authenticate(java.lang.String, java.lang.String)
     */
    @Override
    public boolean authenticate(final String username, final String password) {
        // Check username and password
        try {
            auth = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(auth);
            signIn(true);
            return auth.isAuthenticated();
        } catch (BadCredentialsException e) {
            // in general this error on a not existing user
            return false;
        }
    }


    /**
     * @see org.apache.wicket.authentication.AuthenticatedWebSession#getRoles()
     */
    @Override
    public Roles getRoles() {
        if (roles == null && SecurityContextHelper.getAuthenticatedUser() != null &&  SecurityContextHelper.isAuthenticatedUser() ) {
            roles = new Roles(SecurityContextHelper.getAuthenticatedUserDetails().getAuthorities().iterator().next()
                    .getAuthority());
        }
        return roles;
    }

    /**
     * @return the auth
     */
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    public User getLoggedInUser() {
        return SecurityContextHelper.getAuthenticatedUser();
    }


    public Project getCurrentProject() {
        if(currentProject != null){
            return currentProject;
        }
        if(getLoggedInUser().getProjects() != null){
            return getLoggedInUser().getProjects().iterator().next();    
        }
        return null;
    }


    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }
    
}