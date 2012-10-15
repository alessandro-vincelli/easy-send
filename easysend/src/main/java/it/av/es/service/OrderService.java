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

import it.av.es.model.Order;
import it.av.es.model.Product;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Services on the Order
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface OrderService {
    /**
     * Save a order
     * 
     * @param object
     * @return just saved profile
     */
    Order save(Order object);

    /**
     * Get all the order
     * 
     * @return all the order
     */
    Collection<Order> getAll();
    
    /**
     * Get all the user profile filtered on user and project
     * 
     * @param user
     * @param project
     * @param filterDate
     * @param firstResult
     * @param excludeCancelled
     * @param maxResult
     * @param sortProperty
     * @param isAscending
     * @return all the user profile
     */
    Collection<Order> get(User user, Project project, Date filterDate, boolean excludeCancelled, int firstResult, int maxResult, String sortProperty, boolean isAscending);

    /**
     * Remove a profile
     * 
     * @param order
     */
    void remove(Order order);
    
    /**
     * Cancel an order
     * 
     * @param order
     */
    Order cancel(Order order);
    
    /**
     * Return the order by id
     * 
     * @param id
     * @return user with the passed email
     */
    Order getByID(String id);
    
    /**
     * place a new order
     * 
     * @param order the new order to insert
     * @param project project relates to order
     * @param user user that place the order
     * @return
     */
    Order placeNewOrder(Order order, Project project, User user);
    
    /**
     * Creates a {@link ProductOrdered} with correct ad data, <strong>NOT persists</strong>
     * 
     * @param order
     * @param product
     * @param project
     * @param numberOfProds

     * @return
     */
    ProductOrdered addProductOrdered(Order order, Product product, Project project, int numberOfProds);

    /**
     * 
     * @param user
     * @param project
     * @return
     */
    List<Date> getDates(User user, Project project);

    /**
     * Send mail notification
     * 
     * @param order
     * @return
     */
    Order sendNotificationNewOrder(Order order);

    
    /**
     * Sets this order as in charge 
     * 
     * @param order
     * @return
     */
    Order setAsInCharge(Order order);
        
    
    /**
     * Remove this order as in charge 
     * 
     * @param order
     * @return
     */
    Order removeInCharge(Order order);

    /**
     * Set order on the given dates/project in charge
     * 
     * @param user
     * @param project
     * @param date
     * @return
     */
    void setAsInCharge(User user, Project project, Date date);
    
}