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

import it.av.es.model.Citta;
import it.av.es.service.CittaService;
import it.av.es.service.CountryService;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the operation on {@link Citta}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Transactional(readOnly = true)
@Repository
public class CittaServiceHibernate implements CittaService {

    private EntityManager entityManager;

    /**
     * @param entityManager
     */
    @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "easyTrackPersistence")
    public void setInternalEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    private CountryService countryService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable("cittaFindByComune")
    public List<Citta> findByComune(String comune, int maxResults) {
        Order orderByName = Order.asc("cap");
        if(StringUtils.isNotBlank(comune)){
            Criterion critByName = Restrictions.ilike("comune", comune, MatchMode.ANYWHERE);
            return findByCriteria(orderByName, 0, maxResults, critByName);
        }
        else{
            return findByCriteria(orderByName, 0, maxResults);    
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Citta> findByProvincia(String provincia, int maxResults) {
        Criterion critByName = Restrictions.ilike("provincia", provincia, MatchMode.ANYWHERE);
        Order orderByName = Order.asc("cap");
        return findByCriteria(orderByName, 0, maxResults, critByName);
    }

    @Transactional(readOnly = true)
    public List<Citta> findByCriteria(Order order, int firstResult, int maxResults, Criterion... criterion) {
        return findByCriteria(Citta.class, order, firstResult, maxResults, criterion);
    }

    @Transactional(readOnly = true)
    protected List<Citta> findByCriteria(Order order, Criterion... criterion) {
        return findByCriteria(Citta.class, order, 0, 0, criterion);
    }

    @Transactional(readOnly = true)
    protected List<Citta> findByCriteria(Class<Citta> actualClass, Order order, int firstResult, int maxResults,
            Criterion... criterion) {
        Criteria criteria = getHibernateSession().createCriteria(Citta.class);
        if (order != null) {
            criteria.addOrder(order);
        }
        for (Criterion c : criterion) {
            if (c != null) {
                criteria.add(c);
            }
        }
        if (firstResult > 0) {
            criteria.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        return criteria.list();
    }

    protected final Session getHibernateSession() {
        return (Session) entityManager.getDelegate();
    }

    @Override
    public List<String> findCapByComune(String comune, int maxResults) {
        Order orderByName = Order.asc("cap");
        Criteria criteria = getHibernateSession().createCriteria(Citta.class);
        criteria.setProjection(Projections.distinct(Projections.property("cap")));
        if(StringUtils.isNotBlank(comune)){
            Criterion critByName = Restrictions.ilike("comune", comune);
            criteria.add(critByName);            
        }
        criteria.addOrder(orderByName);
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        return criteria.list();
    }
    
    @Override
    public List<String> findCap(String pattern, int maxResults) {
        Order orderByName = Order.asc("cap");
        Criteria criteria = getHibernateSession().createCriteria(Citta.class);
        criteria.setProjection(Projections.distinct(Projections.property("cap")));
        if(StringUtils.isNotBlank(pattern)){
            Criterion critByName = Restrictions.ilike("cap", pattern, MatchMode.ANYWHERE);
            criteria.add(critByName);            
        }
        criteria.addOrder(orderByName);
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        return criteria.list();
    }

    @Override
    public List<String> findProvinciaByComune(String comune, int maxResults) {
        Criterion critByName = Restrictions.ilike("comune", comune);
        Order orderByName = Order.asc("provincia");
        Criteria criteria = getHibernateSession().createCriteria(Citta.class);
        criteria.setProjection(Projections.distinct(Projections.property("provincia")));
        criteria.add(critByName);
        criteria.addOrder(orderByName);
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        return criteria.list();
    }

}