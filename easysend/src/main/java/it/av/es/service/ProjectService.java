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

import it.av.es.model.Product;
import it.av.es.model.Project;

import java.util.Collection;

/**
 * Services on the Project
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface ProjectService {
    
    /**
     * Save a Project
     * 
     * @param object
     * @return just saved profile
     */
    Project save(Project object);
    
    /**
     * 
     * 
     * @param object
     * @param product
     * @return just saved profile
     */
    Project saveAndAddProduct(Project object, Product product);
    
    /**
     * 
     * 
     * @param object
     * @param product
     * @return just saved profile
     */
    Project saveAndRemoveProduct(Project object, Product product);

    /**
     * Get all the uProject
     * 
     * @return all the Project
     */
    Collection<Project> getAll();

    /**
     * Remove a Project
     * 
     * @param Project
     */
    void remove(Project profile);
    
    /**
     * Return the project by id
     * 
     * @param id
     * @return user with the passed email
     */
    Project getByID(String id);

}