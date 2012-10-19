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

import it.av.es.model.Customer;
import it.av.es.model.Project;
import it.av.es.model.User;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * Operations on vendors
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface UserService {

    /**
     * Update a user
     * 
     * @param user
     * @return just updated user
     */
    User update(User user);

    /**
     * Add a new user, if the role is empty, it's used the USER role
     * 
     * @param user
     * @return just added user
     */
    User add(User vendor);

    /**
     * Insert a new user, during the insert is also encrypted the users's password
     * 
     * @param user
     * @return just inserted user
     */
    User addRegolarUser(User user);

    /**
     * <b>Don't use it</b> Insert a new admin user, during the insert is also encrypted the users's password
     * 
     * @param object
     * @return just inserted user
     */
    User addAdminUser(User object);


    /**
     * Return all the users
     * 
     * @return all the users
     */
    Collection<User> getAll();

    /**
     * Return all the admin user
     * 
     * @return all the admin
     */
    Collection<User> getAllAdminUsers();

    /**
     * Search users
     * 
     * @param pattern
     * @return found users
     */
    Collection<User> find(String pattern);
    
    /**
     * Search user
     * 
     * @param pattern
     * @param first first result
     * @param maxResults max number of result, 0 to disable 
     * @param sortField property name on which sort, NULL to disable
     * @param isAscending is ascending sort
     * @return found users
     */
    List<User> find(String pattern, long first, long maxResults, String sortField, boolean isAscending);

    /**
     * Remove the given user
     * 
     * @param user
     */
    @Transactional
    void remove(User user);

    /**
     * Return the user with this email, there is an unique constraint on the user email
     * 
     * @param email
     * @return user with the passed email
     */
    User getByEmail(String email);

    /**
     * Return the user by id
     * 
     * @param id
     * @return user with the passed email
     */
    User getByID(String id);


    /**
     * Takes a previously encoded password and compares it with a rawpassword after mixing in the salt and encoding that value
     * 
     * @param encPass previously encoded password
     * @param rawPass plain text password
     * @param salt salt to mix into password
     * @return true or false
     */
    boolean isPasswordValid(String encPass, String rawPass, Object salt);

    /**
     * Encodes the rawPass using a MessageDigest. If a salt is specified it will be merged with the password before encoding.
     * 
     * @param rawPass The plain text password
     * @param salt The salt to sprinkle
     * @return Hex string of password digest (or base64 encoded string if encodeHashAsBase64 is enabled.
     */
    String encodePassword(String rawPass, Object salt);

    /**
     * Send the new password by email to the given user
     * 
     * @param vendor
     * @param newPassword
     */
    void sendPasswordByEmail(User vendor, String newPassword);

    /**
     * Set a random password for the given vendor, and save the vendor in the database
     * 
     * @param vendor
     * @return vendor with updated password
     */
    User setRandomPassword(User vendor);

    /**
     * count users in the DB
     * 
     * @return number of users
     */
    int count();
    
    /**
     * count users in the DB that match the given pattern.
     * Useful togheter with the find() 
     * 
     * @param pattern pattern to filter on
     * @return number of users
     */
    int count(String pattern);
    
    /**
     * Assign a user to a Project
     * 
     * @param user
     * @param prj
     */
    void assignUserToProject(User user, Project prj);
    
    /**
     * DeAssign a user to a Project
     * 
     * @param user
     * @param prj
     */
    void removeUserFromProject(User user, Project prj);
    
    /**
     * Remove a customer from a user
     * 
     * @param user
     * @param customer
     */
    void removeCustomer(User user, Customer customer);
}