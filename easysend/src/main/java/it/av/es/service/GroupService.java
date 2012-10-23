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

import it.av.es.model.Group;

import java.util.List;

/**
 * Operations on groups
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface GroupService {

       /**
     * Add a new group
     * 
     * @param group
     * @return just added group
     */
    Group save(Group group);

    /**
     * Return all the groups
     * 
     * @return all the groups
     */
    List<Group> getAll();

    /**
     * Remove the given group
     * 
     * @param Group
     */
    void remove(Group user);


    /**
     * Return the group by id
     * 
     * @param id
     * @return user with the passed id
     */
    Group getByID(String id);



}