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
package it.av.es.service;

import it.av.es.model.UserProfile;

import java.util.Collection;

/**
 * Services on the User Profile
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface UserProfileService {
    /**
     * Save a VendorProfile
     * 
     * @param object
     * @return just saved profile
     */
    UserProfile save(UserProfile object);

    /**
     * Get all the user profile
     * 
     * @return all the user profile
     */
    Collection<UserProfile> getAll();

    /**
     * Remove a profile
     * 
     * @param profile
     */
    void remove(UserProfile profile);

    /**
     * Return the regular user profile, it must be the "USER" profile
     * 
     * @return UserProfile
     */
    UserProfile getRegolarUserProfile();
    
    /**
     * Return the operator user profile, it must be the "OPERATOR" profile
     * 
     * @return UserProfile
     */
    UserProfile getOperatorUserProfile();

    /**
     * Return the admin user profile, it must be the "ADMIN" profile
     * 
     * @return VendorProfile
     */
    UserProfile getAdminUserProfile();

    /**
     * Return the user with the passed name, there's a unique constraint on the user profile name
     * 
     * @param id
     * @return the user with the given name
     */
    UserProfile getByName(String id);

    /**
     * Return the ProjectManager user profile, it must be the "PROJECT_MANAGER" profile
     * 
     * @return UserProfile
     */
    UserProfile getProjectManagerUserProfile();
}