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
package it.av.es.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
public class OrderLog extends BasicEntity {

    public final static String NAME = "time";
    public final static String ORDERSATUSBEFORE = "orderSatusBefore";
    public final static String ORDERSATUS = "orderSatus";
    public final static String USER = "user";
    public final static String ORDER = "order";
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date time;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderSatusBefore;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderSatus;
    
    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;
    
    @ManyToOne()
    @JoinColumn(name = "order_fk")
    private Order order;

    /**
     * Constructor
     */
    public OrderLog() {
        super();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public OrderStatus getOrderSatusBefore() {
        return orderSatusBefore;
    }

    public void setOrderSatusBefore(OrderStatus orderSatusBefore) {
        this.orderSatusBefore = orderSatusBefore;
    }

    public OrderStatus getOrderSatus() {
        return orderSatus;
    }

    public void setOrderSatus(OrderStatus orderSatus) {
        this.orderSatus = orderSatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


}