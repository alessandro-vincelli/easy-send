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

import it.av.es.EasySendException;
import it.av.es.model.ClosingDays;
import it.av.es.model.ClosingRange;
import it.av.es.model.DeliveryDays;
import it.av.es.model.DeliveryType;
import it.av.es.model.DeliveryVehicle;
import it.av.es.model.DeploingType;
import it.av.es.model.Order;
import it.av.es.model.Price;
import it.av.es.model.Product;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;
import it.av.es.service.UserProfileService;
import it.av.es.service.UserService;
import it.av.es.service.system.MailService;
import it.av.es.util.DateUtil;
import it.av.es.util.NumberUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private MailService mailService;
    private boolean notificationEnabled;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Order placeNewOrder(Order order, Project project, User user) {
        Assert.notNull(project, "project cannot be null, it must be associated to the order");
        Assert.notNull(user, "user cannot be null, it must be associated to the order");
        if (!isOrderValid(order)) {
            throw new EasySendException("Ordine non valido");
        }
        order.setCreationTime(new Date());
        order.setProject(project);
        project = projectService.getByID(project.getId());
        project.addOrder(order);
        user = userService.getByID(user.getId());
        user.addOrder(order);
        order.setUser(user);
        order = this.save(order);
        userService.update(user);
        projectService.save(project);
        return order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOrderValid(Order order) {
        List<ProductOrdered> productsOrdered = order.getProductsOrdered();
        int numberOfFreeProds = 0;
        for (ProductOrdered productOrdered : productsOrdered) {
            if (productOrdered.getProduct().getFree()) {
                numberOfFreeProds = numberOfFreeProds + 1;
            }
        }
        //check if  free product > 1
        if (numberOfFreeProds > 1) {
            return false;
        }
        //check  free product not allowed
        if (!order.isAllowedFreeItem() && order.containsFreeOrder()) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Order sendNotificationNewOrder(Order order) {
        if (notificationEnabled) {
            mailService.sendNewOrderNotification(order);
        }
        return order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductOrdered addProductOrdered(Order order, Product product, Project project, int numberOfProds) {
        BigDecimal amount = new BigDecimal(0);
        order.setProject(project);
        ProductOrdered ordered = new ProductOrdered();
        ordered.setProduct(product);
        ordered.setNumber(numberOfProds);
        ordered.setAmount(amount);
        if (product.getFree()) {
            if (order.isAllowedFreeItem()) {
                ordered.setNumber(1);
                return ordered;
            }
        }
        Currency currency;
        int percentDiscount = 0;
        List<Price> prices = product.getPrices();
        for (Price price : prices) {
            if (order.getTotalProductforGivenProduct(product) >= price.getFromNumber() && order.getTotalProductforGivenProduct(product) <= price.getToNumber()) {
                amount = price.getAmount();
                currency = price.getCurrency();
                percentDiscount = price.getPercentDiscount();
            }
        }
        if (amount == BigDecimal.ZERO) {
            throw new EasySendException("Price not available");
        }
        ordered.setAmount(amount.multiply(BigDecimal.valueOf(numberOfProds)));
        //apply discount if isPrepayment
        if (order.isPrePaymentDiscountApplicable()) {
            BigDecimal discount = ((ordered.getAmount().divide(BigDecimal.valueOf(100))).multiply(BigDecimal.valueOf(order.getProject().getPrePaymentDiscount())));
            ordered.setAmount(ordered.getAmount().subtract(discount));
            percentDiscount = percentDiscount + order.getProject().getPrePaymentDiscount();
        }
        ordered.setDiscount(percentDiscount);
        return ordered;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Order> get(User user, Project project, Date filterDate, boolean excludeCancelled, int firstResult, int maxResult, String sortProperty, boolean isAscending) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());

        if (user.getUserProfile().equals(userProfileService.getAdminUserProfile()) || user.getUserProfile().equals(userProfileService.getOperatorUserProfile())) {
            //sees all the orders
        } else {
            // sees only his orders 
            criteria.add(Restrictions.eq(Order.USER_FIELD, user));
        }

        if (excludeCancelled) {
            criteria.add(Restrictions.eq(Order.ISCANCELLED_FIELD, Boolean.FALSE));
        }

        if (filterDate != null) {
            criteria.add(Restrictions.sqlRestriction("date_trunc('day', this_.creation_time) = '" + DateUtil.SDF2SIMPLEUSA.print(filterDate.getTime()) + "'"));
        }

        Criterion critByProject = Restrictions.eq(Order.PROJECT_FIELD, project);

        org.hibernate.criterion.Order order = org.hibernate.criterion.Order.desc(Order.CREATIONTIME_FIELD);
        if (StringUtils.isNotBlank(sortProperty)) {
            if (isAscending) {
                order = org.hibernate.criterion.Order.asc(sortProperty);
            } else {
                order = org.hibernate.criterion.Order.desc(sortProperty);
            }
        }
        criteria.add(critByProject);

        //Crea l'alias per permetter il sort su property annidate come customer.corporateName 
        criteria.createAlias("customer", "customer");
        if (order != null) {
            criteria.addOrder(order);
        }
        if (firstResult > 0) {
            criteria.setFirstResult(firstResult);
        }
        if (maxResult > 0) {
            criteria.setMaxResults(maxResult);
        }
        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Date> getDates(User user, Project project) {
        Set<Date> d = new HashSet<Date>();
        Collection<Order> list = get(user, project, null, false, 0, 0, Order.CREATIONTIME_FIELD, false);
        for (Order o : list) {
            d.add(DateUtils.truncate(o.getCreationTime(), Calendar.DAY_OF_MONTH));
        }
        ArrayList<Date> dates = new ArrayList<Date>(d);
        Collections.sort(dates, new Comparator<Date>() {
            @Override
            public int compare(Date s1, Date s2) {
                return s2.compareTo(s1);
            }
        });
        return dates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order cancel(Order order) {
        order = getByID(order.getId());
        order.setIsCancelled(true);
        return save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setAsInCharge(Order order) {
        if (order.getIsCancelled()) {
            throw new EasySendException("You cannot puth in charge a cancelled order.");
        }
        order = getByID(order.getId());
        order.setInCharge(true);
        return save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order removeInCharge(Order order) {
        order = getByID(order.getId());
        order.setInCharge(false);
        return save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsInCharge(User user, Project project, Date date) {
        Collection<Order> collection = get(user, project, date, true, 0, 0, null, true);
        for (Order order : collection) {
            order.setInCharge(true);
            save(order);
        }
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> getProducts(Order order) {
        Project project = projectService.getByID(order.getProject().getId());
        if (order.isAllowedFreeItem() && !order.containsFreeOrder()) {
            return new ArrayList<Product>(project.getProducts());
        }
        Set<Product> products = project.getProducts();
        Set<Product> ps = new HashSet<Product>();
        for (Product product : products) {
            if (!product.getFree()) {
                ps.add(product);
            }
        }
        return new ArrayList<Product>(ps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order applyDiscountIfApplicable(Order o) {
        ArrayList<ProductOrdered> newList = new ArrayList<ProductOrdered>(o.getProductsOrdered().size());
        for (ProductOrdered p : o.getProductsOrdered()) {
            newList.add(addProductOrdered(o, p.getProduct(), o.getProject(), p.getNumber()));
        }
        o.setProductsOrdered(newList);
        return o;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order applyFreeShippingCostIfApplicable(Order o) {
        if (o.isFreeShippingCostApplicable()) {
            o.setShippingCost(BigDecimal.ZERO);
        } else {
            o.setShippingCost(o.getProject().getShippingCost());
        }
        return o;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNotesForDisplay(Order order, Localizer localizer, Component component) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("total: ");
        buffer.append(NumberUtil.italianCurrency.format(order.getTotalAmount()));
        if(StringUtils.isNotBlank(order.getNotes())){
            buffer.append("\n");
            buffer.append(order.getNotes());
        }
        ClosingDays closingDay = order.getCustomer().getClosingDay();
        if(closingDay != null){
            buffer.append("\n");
            buffer.append("closed: ");
            buffer.append(localizer.getString(closingDay.getClass().getSimpleName() + "." + closingDay.name(), component));
            ClosingRange closingRange = order.getCustomer().getClosingRange();
            if(closingRange != null){
                buffer.append(" ");
                buffer.append(localizer.getString(closingRange.getClass().getSimpleName() + "." + closingRange.name(), component));                
            }
        }
        if(order.getDeliveryTimeRequired() != null){
            buffer.append("\n");
            buffer.append("cons. tass.: ");
            buffer.append(DateUtil.SDF2SHOWDATE.print(order.getDeliveryTimeRequired().getTime()));
        }
        if(order.getCustomer().getSignboard() != null){
            buffer.append("\n");
            buffer.append(localizer.getString("customer.signboard", component));
            buffer.append(": ");
            buffer.append(order.getCustomer().getSignboard());
        }
        if(!order.getCustomer().getDeliveryDays().isEmpty()){
            buffer.append("\n");
            buffer.append("consegna: ");
            for (DeliveryDays d : order.getCustomer().getDeliveryDays()) {
                buffer.append(localizer.getString(d.name(), component));
                buffer.append(" ");
            }
        }
        if(order.getCustomer().isPhoneForewarning()){
            buffer.append("\n");
            buffer.append("preavv. tel: ");
            buffer.append(StringUtils.isNotBlank(order.getShippingAddress().getPhoneNumber())?order.getShippingAddress().getPhoneNumber():"");
        }
        if(order.getCustomer().getDeployngType() != null){
            buffer.append("\n");
            DeploingType type = order.getCustomer().getDeployngType();
            buffer.append(localizer.getString(type.getClass().getSimpleName() + "." + type.name(), component));
        }
        if(order.getCustomer().getDeliveryVehicle() != null){
            buffer.append("\n");
            DeliveryVehicle dv = order.getCustomer().getDeliveryVehicle();
            buffer.append(localizer.getString(dv.getClass().getSimpleName() + "." + dv.name(), component));
        }
        if(order.getCustomer().getDeliveryType() != null){
            buffer.append("\n");
            DeliveryType type = order.getCustomer().getDeliveryType();
            buffer.append(localizer.getString(type.getClass().getSimpleName() + "." + type.name(), component));
        }
        buffer.append("\n");
        return buffer.toString();
    }
}