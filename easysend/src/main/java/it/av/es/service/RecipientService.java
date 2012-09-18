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

import it.av.es.model.Recipient;

import java.util.List;

/**
 * Services on {@Link Recipient}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface RecipientService extends ApplicationService<Recipient> {
    
    /**
     * Finds the recipient using the given pattern
     * 
     * @param string
     * @param maxResults
     * @return found recipients
     */
    List<Recipient> find(String string, int maxResults);
    
    /**
     * Save a Recipient
     * 
     * @param object
     * @return just saved profile
     */
    Recipient save(Recipient object);

    /**
     * Get all the user profile
     * 
     * @return all the user profile
     */
    List<Recipient> getAll();

    /**
     * Remove a profile
     * 
     * @param profile
     */
    void remove(Recipient profile);
    
    /**
     * Return the order by id
     * 
     * @param id
     * @return user with the passed email
     */
    Recipient getByID(String id);


}