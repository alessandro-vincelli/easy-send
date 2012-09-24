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
import it.av.es.model.Order;
import it.av.es.model.Price;
import it.av.es.model.Product;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;
import it.av.es.service.UserService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class OrderServiceHibernate extends ApplicationServiceHibernate<Order> implements OrderService {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    
    @Override
    @Transactional
    public Order placeNewOrder(Order order, Project project, User user) {
        order.setCreationTime(new Date());
        project = projectService.getByID(project.getId());
        project.addOrder(order);
        user = userService.getByID(user.getId());
        user.addOrder(order);
        order = this.save(order);
        userService.update(user);
        projectService.save(project);
        return order;
    }

    @Override
    public ProductOrdered addProductOrdered(Order order, Product product, int numberOfProds) {
        ProductOrdered ordered = new ProductOrdered();
        ordered.setProduct(product);
        ordered.setNumber(numberOfProds);
        BigDecimal amount = new BigDecimal(0);
        Currency currency;
        int percentDiscount = 0;
        List<Price> prices = product.getPrices();
        for (Price price : prices) {
            if(numberOfProds >= price.getFromNumber() && numberOfProds <= price.getToNumber()){
                amount= price.getAmount();
                currency = price.getCurrency();
                percentDiscount = price.getPercentDiscount();
            }
        }
        if(amount == BigDecimal.ZERO){
            throw new EasySendException("Price non available");
        }
        ordered.setAmount(amount.multiply(BigDecimal.valueOf(numberOfProds)));
        ordered.setDiscount(percentDiscount);
        return ordered;
    }


}