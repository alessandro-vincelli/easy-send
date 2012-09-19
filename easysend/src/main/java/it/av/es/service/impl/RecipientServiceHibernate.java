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
package it.av.es.service.impl;

import it.av.es.model.Customer;
import it.av.es.model.User;
import it.av.es.service.RecipientService;
import it.av.es.service.UserService;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the operation on {@link Customer}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Transactional(readOnly = true)
@Repository
public class RecipientServiceHibernate extends ApplicationServiceHibernate<Customer> implements RecipientService {

    @Autowired
    private UserService userService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getAll() {
        Order orderBYName = Order.asc(Customer.NAME_FIELD);
        return super.findByCriteria(orderBYName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> find(String string, int maxResults) {
        Criterion critByName = Restrictions.ilike("name", string + "%");
        Order orderByName = Order.asc(Customer.NAME_FIELD);
        return findByCriteria(orderByName, 0, maxResults, critByName);
    }

    @Override
    public Customer save(Customer customer, User user) {
        user = userService.getByID(user.getId());
        customer.setUser(user);
        user.addRecipient(customer);
        save(customer);
        userService.update(user);
        return customer;
    }

}