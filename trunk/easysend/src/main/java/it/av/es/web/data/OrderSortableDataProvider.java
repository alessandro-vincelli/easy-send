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
package it.av.es.web.data;

import it.av.es.model.Order;
import it.av.es.model.OrderStatus;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class OrderSortableDataProvider extends SortableDataProvider<Order, String> {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private OrderService orderService;
    private transient Collection<Order> results;
    private long size;
    private User user;
    private Project project;
    private Date filterDate;
    private OrderStatus filterStatus;
    private Boolean excludeCancelledOrder;

    public OrderSortableDataProvider(User user, Project project, Boolean excludeCancelledOrder) {
        super();
        this.user = user;
        this.project = project;
        this.excludeCancelledOrder = excludeCancelledOrder;
        Injector.get().inject(this);
        setSort(new SortParam<String>(Order.CREATIONTIME_FIELD, false));
        results = orderService.get(user, project, filterDate, filterStatus, excludeCancelledOrder, 0, 0, getSort().getProperty(), getSort().isAscending());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long size() {
        size = orderService.get(user, project, filterDate, filterStatus, excludeCancelledOrder, 0, 0, getSort().getProperty(), getSort().isAscending()).size();
        return size;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IModel<Order> model(Order order) {
        return new OrderDetachableModel(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {
        results = null;
    }

    @Override
    public Iterator<? extends Order> iterator(long first, long count) {
        results = orderService.get(user, project, filterDate, filterStatus, excludeCancelledOrder, (int) first, (int) count, getSort().getProperty(), getSort().isAscending());
        return Collections.synchronizedList(new ArrayList<Order>(results)).iterator();
    }

    public OrderStatus getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(OrderStatus filterStatus) {
        this.filterStatus = filterStatus;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public Date getFilterDate() {
        return filterDate;
    }

    public void setFilterDate(Date filterDate) {
        this.filterDate = filterDate;
    }

    public boolean isExcludeCancelledOrder() {
        return excludeCancelledOrder;
    }

    public void setExcludeCancelledOrder(boolean excludeCancelledOrder) {
        this.excludeCancelledOrder = excludeCancelledOrder;
    }

    
}