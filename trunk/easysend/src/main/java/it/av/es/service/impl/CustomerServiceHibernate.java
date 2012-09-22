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

import it.av.es.EasySendException;
import it.av.es.model.Customer;
import it.av.es.model.User;
import it.av.es.service.CustomerService;
import it.av.es.service.UserService;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
public class CustomerServiceHibernate extends ApplicationServiceHibernate<Customer> implements CustomerService {

    @Autowired
    private UserService userService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getAll() {
        Order orderBYName = Order.asc(Customer.CORPORATENAME_FIELD);
        return super.findByCriteria(orderBYName);
    }
    
    /**
     * {@inheritDoc}
     */
    //@Cacheable("getAllCustomers")
    @Override
    public List<Customer> getAll(User user) {
        Criterion critByUser = Restrictions.eq(Customer.USER_FIELD, user);
        return super.findByCriteria(critByUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> find(String string, int maxResults) {
        Criterion critByName = Restrictions.ilike(Customer.CORPORATENAME_FIELD, string + "%");
        Order orderByName = Order.asc(Customer.CORPORATENAME_FIELD);
        return findByCriteria(orderByName, 0, maxResults, critByName);
    }

    //@CacheEvict({"getAllCustomers", "getCustomers"})
    @Override
    public Customer save(Customer obj) {
        if(obj.getUser() == null){
            throw new EasySendException("Non user setted on Customer");
        }
        return super.save(obj);
    }

    //@CacheEvict({"getAllCustomers", "getCustomers"})
    @Override
    public Customer save(Customer customer, User user) {
        user = userService.getByID(user.getId());
        customer.setUser(user);
        user.addCustomer(customer);
        save(customer);
        userService.update(user);
        return customer;
    }

    
    //@Cacheable("getCustomers")
    @Override
    public List<Customer> get(User user, int firstResult, int maxResult, String sortProperty, boolean isAscending) {
        Criterion critByUser = Restrictions.eq(Customer.USER_FIELD, user);
        Order orderByName = null;
        if (StringUtils.isNotBlank(sortProperty)) {
            if (isAscending) {
                orderByName = Order.asc(sortProperty);
            } else {
                orderByName = Order.desc(sortProperty);
            }
        }
        return findByCriteria(orderByName, firstResult, maxResult, critByUser);

    }

}