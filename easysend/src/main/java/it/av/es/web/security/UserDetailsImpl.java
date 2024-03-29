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

import it.av.es.model.User;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Provides user information
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class UserDetailsImpl implements UserDetails {

    private String passwordSalt;
    private User user = new User();

    /**
     * Constructor
     * 
     * @param user
     */
    public UserDetailsImpl(User user) {
        this.user = user;
        this.passwordSalt = user.getPasswordSalt();
    }

    /**
     * Default empty constructor
     */
    public UserDetailsImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> gaL = new ArrayList<GrantedAuthority>(0);
        if(user.getUserProfile() != null){
            GrantedAuthority ga = new GrantedAuthorityImpl(user.getUserProfile().getName());
            gaL.add(ga);
        }
        return gaL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return the logged user
     */
    public final User getUser() {
        return user;
    }

    /**
     * @return th password salt
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

}
