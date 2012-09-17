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

import java.util.Collection;

/**
 * Services on the Product
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface ProductService {
    /**
     * Save a VendorProfile
     * 
     * @param object
     * @return just saved profile
     */
    Product save(Product object);

    /**
     * Get all the user profile
     * 
     * @return all the user profile
     */
    Collection<Product> getAll();

    /**
     * Remove a profile
     * 
     * @param profile
     */
    void remove(Product profile);
    
    /**
     * Return the product by id
     * 
     * @param id
     * @return user with the passed email
     */
    Product getByID(String id);

}