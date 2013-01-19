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
import it.av.es.model.Customer;
import it.av.es.model.DeliveryDays;
import it.av.es.model.DeliveryType;
import it.av.es.model.DeliveryVehicle;
import it.av.es.model.DeploingType;
import it.av.es.model.Group;
import it.av.es.model.Order;
import it.av.es.model.OrderLog;
import it.av.es.model.OrderStatus;
import it.av.es.model.Price;
import it.av.es.model.Product;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.CustomerService;
import it.av.es.service.GroupService;
import it.av.es.service.OrderLogService;
import it.av.es.service.OrderService;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;
import it.av.es.service.UserProfileService;
import it.av.es.service.UserService;
import it.av.es.service.pdf.PDFInvoiceExporter;
import it.av.es.service.system.MailService;
import it.av.es.util.DateUtil;
import it.av.es.util.NumberUtil;

import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.mortbay.log.Log;
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
    private GroupService groupService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private MailService mailService;
    @Autowired
    private OrderLogService orderLogService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PDFInvoiceExporter pdfInvoiceExporter;
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
        Customer customer = customerService.getByID(order.getCustomer().getId());
        order.setInvoiceAddress(customer.searchInvoiceAddresses());
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
        //calculates  the price and apply discount 
        
        // per il cacolo somma tutti i prodotti dello stesso tipo
        int productAllOrder = order.getTotalProductforGivenProduct(product);
        // se il prodoptto appartiene ad una famigliam considera anche i prodotti di quella famiglia
        if(product.getProductFamily() != null){
            productAllOrder = order.getTotalProductforGivenProductFamily(product.getProductFamily());    
        }

        for (Price price : prices) {
            if (productAllOrder >= price.getFromNumber() && productAllOrder <= price.getToNumber()) {
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
    public Collection<Order> get(User user, Project project, Date filterDate, Date filterDeliveredDate, OrderStatus filterStatus, boolean excludeCancelled, int firstResult, int maxResult, String sortProperty, boolean isAscending) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());

        if (user.getUserProfile().equals(userProfileService.getAdminUserProfile()) || user.getUserProfile().equals(userProfileService.getOperatorUserProfile()) || user.getUserProfile().equals(userProfileService.getProjectManagerUserProfile())) {
            //sees all the orders
        }        
        // if the user is Admin of a group, filter on the group member
        else if (!groupService.isUserAdministratorOAGroups(user).isEmpty()) {
            List<Group> g = groupService.isUserAdministratorOAGroups(user);
            List<User> users = new ArrayList<User>();
            for (Group group : g) {
                users.addAll(group.getMembers());
            }
            users.add(user);
            criteria.add(Restrictions.in(Order.USER_FIELD, users));
        }
        else {
            // sees only his orders 
            criteria.add(Restrictions.eq(Order.USER_FIELD, user));
        }

        if (excludeCancelled) {
            criteria.add(Restrictions.eq(Order.ISCANCELLED_FIELD, Boolean.FALSE));
        }

        if (filterDate != null) {
            criteria.add(Restrictions.sqlRestriction("date_trunc('day', this_.creation_time) = '" + DateUtil.SDF2SIMPLEUSA.print(filterDate.getTime()) + "'"));
        }
        
        if (filterDeliveredDate != null) {
            criteria.add(Restrictions.sqlRestriction("date_trunc('day', this_.delivered_time) = '" + DateUtil.SDF2SIMPLEUSA.print(filterDeliveredDate.getTime()) + "'"));
        }
        
        if (filterStatus != null) {
            criteria.add(Restrictions.eq(Order.STATUS_FIELD, filterStatus));
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
        Collection<Order> list = get(user, project, null, null, null, false, 0, 0, Order.CREATIONTIME_FIELD, false);
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
    public List<Date> getDeliveredDates(User user, Project project) {
        Set<Date> d = new HashSet<Date>();
        Collection<Order> list = get(user, project, null, null, null, false, 0, 0, Order.DELIVEREDTIME_FIELD, false);
        for (Order o : list) {
            if(o.getDeliveredTime() != null){
                d.add(DateUtils.truncate(o.getDeliveredTime(), Calendar.DAY_OF_MONTH));    
            }
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
    public Order cancel(Order order, User user) {
        order = getByID(order.getId());
        order.setIsCancelled(true);
        order = setStatus(OrderStatus.CANCELLED, order, user);
        return save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setAsInCharge(Order order, User user) {
        if (order.getIsCancelled()) {
            throw new EasySendException("You cannot puth in charge a cancelled order.");
        }
        order = getByID(order.getId());
        order.setInCharge(true);
        order = setStatus(OrderStatus.INCHARGE, order, user);
        return save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order removeInCharge(Order order, User user) {
        order = getByID(order.getId());
        order.setInCharge(false);
        order = setStatus(OrderStatus.CREATED, order, user);
        return save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsInCharge(User user, Project project, Date date) {
        Collection<Order> collection = get(user, project, date, null, null, true, 0, 0, null, true);
        for (Order order : collection) {
            setAsInCharge(order, user);
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
    public String getNotesForDisplay(Order order) {
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
            ResourceModel resourceModel = new ResourceModel(closingDay.getClass().getSimpleName() + "." + closingDay.name());
            buffer.append(resourceModel.getObject());
            ClosingRange closingRange = order.getCustomer().getClosingRange();
            if(closingRange != null){
                buffer.append(" ");
                buffer.append(new ResourceModel(closingRange.getClass().getSimpleName() + "." + closingRange.name()).getObject());                
            }
        }
        if(order.getDeliveryTimeRequired() != null){
            buffer.append("\n");
            buffer.append("cons. tass.: ");
            buffer.append(DateUtil.SDF2SHOWDATE.print(order.getDeliveryTimeRequired().getTime()));
        }
        if(order.getCustomer().getSignboard() != null){
            buffer.append("\n");
            buffer.append(new ResourceModel("customer.signboard").getObject());
            buffer.append(": ");
            buffer.append(order.getCustomer().getSignboard());
        }
        if(!order.getCustomer().getDeliveryDays().isEmpty()){
            buffer.append("\n");
            buffer.append("consegna: ");
            for (DeliveryDays d : order.getCustomer().getDeliveryDays()) {
                buffer.append(new ResourceModel(d.name()).getObject());
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
            buffer.append(new ResourceModel(type.getClass().getSimpleName() + "." + type.name()).getObject());
        }
        if(order.getCustomer().getDeliveryVehicle() != null){
            buffer.append("\n");
            DeliveryVehicle dv = order.getCustomer().getDeliveryVehicle();
            buffer.append(new ResourceModel(dv.getClass().getSimpleName() + "." + dv.name()).getObject());
        }
        if(order.getCustomer().getDeliveryType() != null){
            buffer.append("\n");
            DeliveryType type = order.getCustomer().getDeliveryType();
            buffer.append(new ResourceModel(type.getClass().getSimpleName() + "." + type.name()).getObject());
        }
        buffer.append("\n");
        return buffer.toString();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getNotesForPDF(Order order) {
        StringBuilder buffer = new StringBuilder();
        if(StringUtils.isNotBlank(order.getNotes())){
            buffer.append("\n");
            buffer.append("Note: ");
            buffer.append(order.getNotes());
        }
        ClosingDays closingDay = order.getCustomer().getClosingDay();
        if(closingDay != null){
            buffer.append("\n");
            buffer.append("chiuso: ");
            buffer.append(new ResourceModel(closingDay.getClass().getSimpleName() + "." + closingDay.name()).getObject());
            ClosingRange closingRange = order.getCustomer().getClosingRange();
            if(closingRange != null){
                buffer.append(" ");
                buffer.append(new ResourceModel(closingRange.getClass().getSimpleName() + "." + closingRange.name()).getObject());
            }
        }
        if(order.getDeliveryTimeRequired() != null){
            buffer.append("\n");
            buffer.append("data rich: ");
            if(order.getDeliveryTimeRequiredType() != null){
                buffer.append(new ResourceModel(order.getDeliveryTimeRequiredType().name()).getObject());
            }
            buffer.append(DateUtil.SDF2SHOWDATE.print(order.getDeliveryTimeRequired().getTime()));
        }
        if(order.getCustomer().getSignboard() != null){
            buffer.append("\n");
            buffer.append(new ResourceModel("customer.signboard").getObject());
            buffer.append(": ");
            buffer.append(order.getCustomer().getSignboard());
        }
        if(!order.getCustomer().getDeliveryDays().isEmpty()){
            buffer.append("\n");
            buffer.append("consegna: ");
            for (DeliveryDays d : order.getCustomer().getDeliveryDays()) {
                buffer.append(new ResourceModel(d.name()).getObject());
            }
        }
        if(order.getCustomer().isPhoneForewarning()){
            buffer.append("\n");
            buffer.append("preavv. tel: ");
            buffer.append(StringUtils.isNotBlank(order.getShippingAddress().getPhoneNumber())?order.getShippingAddress().getPhoneNumber():"");
        }
        if(order.getCustomer().getDeployngType() != null){
            buffer.append("\n");
            buffer.append("scarico: ");
            DeploingType type = order.getCustomer().getDeployngType();
            buffer.append(new ResourceModel(type.getClass().getSimpleName() + "." + type.name()).getObject());
        }
        if(order.getCustomer().getDeliveryVehicle() != null){
            buffer.append("\n");
            buffer.append("mezzo: ");
            DeliveryVehicle dv = order.getCustomer().getDeliveryVehicle();
            buffer.append(new ResourceModel(dv.getClass().getSimpleName() + "." + dv.name()).getObject());
        }
        if(order.getCustomer().getDeliveryType() != null){
            buffer.append("\n");
            buffer.append("cons: ");
            DeliveryType type = order.getCustomer().getDeliveryType();
            buffer.append(new ResourceModel(type.getClass().getSimpleName() + "." + type.name()).getObject());
        }
        buffer.append("\n");
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setStatus(OrderStatus orderStatus, Order order, User user) {
        order = getByID(order.getId());
        user = userService.getByID(user.getId());
        OrderLog log = new OrderLog();
        log.setOrder(order);
        log.setOrderSatus(orderStatus);
        log.setOrderSatusBefore(order.getStatus());
        log.setTime(new Date());
        order.setStatus(orderStatus);
        order = save(order);
        orderLogService.save(log);
        return order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setSentStatus(Order order, User user) {
        return setStatus(OrderStatus.SENT, order, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order removeSentStatus(Order order, User user) {
        return setStatus(OrderStatus.INCHARGE, order, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setDeliveredStatus(Order order, User user, Date deliveredTime) {
        order.setDeliveredTime(deliveredTime);
        order = save(order);
        return setStatus(OrderStatus.DELIVERED, order, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order removeDeliveredStatus(Order order, User user) {
        order.setDeliveredTime(null);
        return setStatus(OrderStatus.SENT, order, user);
    }

    private int getLastInvoiceNumber(Project project, Date invoiceDate){
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.sqlRestriction("date_trunc('year', this_.creation_time) = '" + DateUtil.SDF2SIMPLEUSA.print(DateUtils.truncate(invoiceDate, Calendar.YEAR).getTime()) + "'"));
        criteria.add(Restrictions.isNotNull("invoiceNumber"));
        criteria.addOrder(org.hibernate.criterion.Order.desc("invoiceNumber"));
        List<Order> list = criteria.list();
        int lastInvoiceNumber = 0;
        if(list.size() > 0){
            lastInvoiceNumber = list.get(0).getInvoiceNumber();
        }
        return lastInvoiceNumber;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Order removeInvoiceApprovedStatus(Order order, User user) {
        order = save(order);
        return setStatus(OrderStatus.CREATED, order, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setInvoiceApprovedStatus(Order order, User user) {
        order = getByID(order.getId());
        if(order.getStatus().equals(OrderStatus.INVOICE_APPROVED)){
            throw new EasySendException("Invoice already approved");
        }
        order = save(order);
        setStatus(OrderStatus.INVOICE_APPROVED, order, user);
        return setStatus(OrderStatus.INVOICE_APPROVED, order, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setInvoiceCreatedStatus(Order order, User user, Date invoiceDate, Date invoiceDueDate) {
        order = getByID(order.getId());
        if(order.getInvoiceNumber() != null || order.getInvoiceDate() != null || order.getStatus().equals(OrderStatus.INVOICE_CREATED) || order.getInvoice() != null){
            throw new EasySendException("Invoice already created");
        }
        try {
            order.setInvoiceNumber(getLastInvoiceNumber(order.getProject(), invoiceDate) + 1);
            order.setInvoiceDate(invoiceDate);
            order.setInvoiceDueDate(invoiceDueDate);
            InputStream createInvoice = pdfInvoiceExporter.createInvoice(order, user, order.getProject(), this);
            order.setInvoice(IOUtils.toByteArray(createInvoice));
            order = save(order);
            setStatus(OrderStatus.INVOICE_CREATED, order, user);
            return setStatus(OrderStatus.INVOICE_CREATED, order, user);
        } catch (IOException e) {
            Log.warn(e);
            throw new EasySendException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order removeInvoiceCreatedStatus(Order order, User user) {
        order.setInvoiceNumber(null);
        order.setInvoiceDate(null);
        order.setInvoiceDueDate(null);
        order.setInvoice(null);
        order = save(order);
        return setStatus(OrderStatus.DELIVERED, order, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order setInvoicePaidStatus(Order order, User user) {
        return setStatus(OrderStatus.INVOICE_PAID, order, user);
    }
}