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

import it.av.es.model.Provincia;
import it.av.es.service.ProvinciaService;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Transactional(readOnly = true)
@Repository
public class ProvinciaServiceHibernate implements ProvinciaService {

    private EntityManager entityManager;

    /**
     * @param entityManager
     */
    @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "easySendPersistence")
    public void setInternalEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    protected final Session getHibernateSession() {
        return (Session) entityManager.getDelegate();
    }

    @Override
    public List<Provincia> getAll() {
        Criteria criteria = getHibernateSession().createCriteria(Provincia.class);
        return criteria.list();
    }


    @Override
    public List<String> getAllSigle() {
        Criteria criteria = getHibernateSession().createCriteria(Provincia.class);
        criteria.setProjection(Projections.property("sigla"));
        return criteria.list();

    }

}