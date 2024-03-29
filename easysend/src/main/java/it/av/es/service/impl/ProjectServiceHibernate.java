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
package it.av.es.service.impl;

import it.av.es.model.Product;
import it.av.es.model.Project;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class ProjectServiceHibernate extends ApplicationServiceHibernate<Project> implements ProjectService {

    @Autowired
    private ProductService productService;
    
    @Override
    public Project saveAndAddProduct(Project p, Product product) {
        product = productService.save(product);
        p = getByID(p.getId());
        p.addProduct(product);
        return save(p);
    }

    @Override
    public Project saveAndRemoveProduct(Project p, Product product) {
        product = productService.getByID(product.getId());
        p = getByID(p.getId());
        p.getProducts().remove(product);
        productService.remove(product);
        return save(p);
    }


}