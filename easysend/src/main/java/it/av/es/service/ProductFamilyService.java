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

import it.av.es.model.ProductFamily;

import java.util.List;

/**
 * Operations on ProductFamilys
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface ProductFamilyService {

       /**
     * Add a new ProductFamily
     * 
     * @param ProductFamily
     * @return just added ProductFamily
     */
    ProductFamily save(ProductFamily ProductFamily);

    /**
     * Return all the ProductFamilys
     * 
     * @return all the ProductFamilys
     */
    List<ProductFamily> getAll();

    /**
     * Remove the given ProductFamily
     * 
     * @param ProductFamily
     */
    void remove(ProductFamily user);


    /**
     * Return the ProductFamily by id
     * 
     * @param id
     * @return user with the passed id
     */
    ProductFamily getByID(String id);



}