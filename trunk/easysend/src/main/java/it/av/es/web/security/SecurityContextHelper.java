/**
 * 
 */
package it.av.es.web.security;

import it.av.es.model.User;
import it.av.es.web.UserDetailsImpl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Simply the acces to to SpringSecurityContext
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class SecurityContextHelper {

    private SecurityContextHelper() {
    }

    /**
     * 
     * @return the authenticated user
     */
    public static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        }
        return null;
    }

    /**
     * 
     * @return the authenticated userDetails
     */
    public static UserDetails getAuthenticatedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 
     * @return true if the current user is authenticated
     */
    public static boolean isAuthenticatedUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return true;
        }
        return false;
    }

}
