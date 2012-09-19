/**
 * Copyright 2009 the original author or authors
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
import it.av.es.model.User;

import java.util.List;

/**
 * Services on {@Link Customer}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface CustomerService extends ApplicationService<Customer> {
    
    /**
     * Finds the recipient using the given pattern
     * 
     * @param string
     * @param maxResults
     * @return found customers
     */
    List<Customer> find(String string, int maxResults);
    
    /**
     * Save a Customer
     * 
     * @param customer
     * @param user user that relates to the recipient
     * @return just saved profile
     */
    Customer save(Customer customer, User user);

    /**
     * Get all the user profile
     * 
     * @return all the user profile
     */
    List<Customer> getAll();

    /**
     * Remove a profile
     * 
     * @param profile
     */
    void remove(Customer profile);
    
    /**
     * Return the order by id
     * 
     * @param id
     * @return user with the passed email
     */
    Customer getByID(String id);


}