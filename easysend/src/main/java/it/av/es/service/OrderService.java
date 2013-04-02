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

import it.av.es.model.Order;
import it.av.es.model.OrderStatus;
import it.av.es.model.Product;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;

import java.math.BigDecimal;
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
     * @param filterDeliveredDate
     * @param filterStatus
     * @param firstResult
     * @param excludeCancelled
     * @param maxResult
     * @param sortProperty
     * @param isAscending
     * @return all the user profile
     */
    Collection<Order> get(User user, Project project, Date filterDate, Date filterDeliveredDate, OrderStatus filterStatus, boolean excludeCancelled, int firstResult,
            int maxResult, String sortProperty, boolean isAscending);

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
     * @param user user is performing the action
     * 
     */
    Order cancel(Order order, User user);

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
     * Creates a {@link ProductOrdered} with correct data, adds the new ProductOrdered to the Order, recalculates all the prices and discounts. <strong>NOT persists</strong>
     * 
     * @param order
     * @param product
     * @param project
     * @param numberOfProds

     * @return
     */
    ProductOrdered addProductOrdered(Order order, Product product, Project project, int numberOfProds);

    /**
     * Remove a {@link ProductOrdered} from the given order, recalculates all the prices and discounts on the order.<strong>NOT persists</strong>
     * 
     * @param order
     * @param productOrderedIndex position on the list of product to be removed
     * 
     */
    void removeProductOrdered(Order order, int productOrderedIndex);

    /**
     * 
     * @param user
     * @param project
     * @return
     */
    List<Date> getDates(User user, Project project);

    /**
     * 
     * @param user
     * @param project
     * @return
     */
    List<Date> getDeliveredDates(User user, Project project);

    /**
     * Send mail notification, <b>Note</b>: <b>notificationEnabled</b> properties must be set to true
     * 
     * @param order
     * @return
     */
    Order sendNotificationNewOrder(Order order);

    /**
     * Sets this order as in charge 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order setAsInCharge(Order order, User user);

    /**
     * Remove this order as in charge 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order removeInCharge(Order order, User user);

    /**
     * Sets this order as "sent" 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order setSentStatus(Order order, User user);

    /**
     * Remove this order as "sent" 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order removeSentStatus(Order order, User user);

    /**
     * Sets this order as "delivered" 
     * 
     * @param order
     * @param user user is performing the action
     * @param deliveredTime 
     * @return
     */
    Order setDeliveredStatus(Order order, User user, Date deliveredTime);

    /**
     * Remove this order as "delivered" 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order removeDeliveredStatus(Order order, User user);

    /**
     * Sets this order as "InvoiceApproved" 
     * 
     * @param order
     * @param user user is performing the action
     * @param invoiceDate
     * @param invoiceDueDate 
     * @return
     */
    Order setInvoiceApprovedStatus(Order order, User user);

    /**
     * Removes this order as "InvoiceApproved" 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order removeInvoiceApprovedStatus(Order order, User user);

    /**
     * Sets this order as "InvoiceCreated" 
     * 
     * @param order
     * @param user user is performing the action
     * @param invoiceDate
     * @param invoiceDueDate data di pagamento prevista 
     * @return
     */
    Order setInvoiceCreatedStatus(Order order, User user, Date invoiceDate, Date invoiceDueDate);

    /**
     * Sets this order as "PaidInvoice" 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order setInvoicePaidStatus(Order order, User user);

    /**
     * Remove this order as "InvoiceCreated" 
     * 
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order removeInvoiceCreatedStatus(Order order, User user);

    /**
     * Set the OrderStatus 
     * 
     * @param orderStatus
     * @param order
     * @param user user is performing the action
     * @return
     */
    Order setStatus(OrderStatus orderStatus, Order order, User user);

    /**
     * Set order on the given dates/project in charge
     * 
     * @param user
     * @param project
     * @param date
     * @return
     */
    void setAsInCharge(User user, Project project, Date date);

    /**
     * Get products base on the given order properties
     * 
     * @param order
     * @return
     */
    List<Product> getProducts(Order order);

    //    /**
    //     * 
    //     * @param o
    //     * @return
    //     */
    //    Order applyDiscountIfApplicable(Order o);
    //
    //    
    //    /**
    //     * 
    //     * @param o
    //     * @return
    //     */
    //    Order applyFreeShippingCostIfApplicable(Order o);

    /**
     * 
     * @param order
     * @return
     */
    boolean isOrderValid(Order order);

    String getNotesForDisplay(Order o);

    String getNotesForPDF(Order order);

    /**
     * Force discount on existing order <b>Use with caution</b>
     * 
     * @param order
     * @param discountToAppply to the order
     * @return
     */
    Order modifyDiscountToOrder(Order order, int discountToAppply);

    /**
    * 
    * @param o
    * @return the order, product ordered with the correct costs and discount applied
    */
    public abstract Order calculatesCostsAndDiscount(Order o);

    /**
     * 
     * @param o
     * @param cost cost to apply
     * @param discountForced discount to be forced
     * @return
     */
    Order forcePriceAndDiscountAndRecalculate(Order o, BigDecimal cost, BigDecimal discountForced);

}