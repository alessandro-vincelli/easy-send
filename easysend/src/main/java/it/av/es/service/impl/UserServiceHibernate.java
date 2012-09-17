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
import it.av.es.UserAlreadyExistsException;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.model.UserProfile;
import it.av.es.service.ProjectService;
import it.av.es.service.UserProfileService;
import it.av.es.service.UserService;
import it.av.es.util.DateUtil;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class UserServiceHibernate extends ApplicationServiceHibernate<User> implements UserService {

    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private ProjectService projectService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User addRegolarUser(User user) {
        user.setUserProfile(userProfileService.getRegolarUserProfile());
        try {
            return add(user);
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void remove(User user) {
        User userToRemove = getByID(user.getId());
        super.remove(userToRemove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User addAdminUser(User user) {
        user.setUserProfile(userProfileService.getAdminUserProfile());
        try {
            return add(user);
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User add(User user) {
        if (user == null || StringUtils.isBlank(user.getEmail())) {
            throw new EasySendException("Vendor is null or email is empty");
        }
        user.setPasswordSalt(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getPasswordSalt()));
        user.setCreationTime(DateUtil.getTimestamp());
        if (user.getUserProfile() == null) {
            user.setUserProfile(userProfileService.getRegolarUserProfile());
        }
        return super.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User update(User object) {
        try {
            super.save(object);
        } catch (DataAccessException e) {
            throw new EasySendException(e);
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByEmail(String email) {
        Criterion crit = Restrictions.eq(User.EMAIL, email);
        List<User> result = super.findByCriteria(crit);
        if (result != null && result.size() == 1) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<User> find(String pattern) {
        Criterion critByName = Restrictions.ilike(User.LASTNAME, pattern);
        return findByCriteria(critByName);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return passwordEncoder.isPasswordValid(encPass, rawPass, salt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodePassword(String rawPass, Object salt) {
        return passwordEncoder.encodePassword(rawPass, salt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPasswordByEmail(User user, String newPassword) {
        //mailService.sendPassword(user, newPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User setRandomPassword(User user) {
        user.setPassword(encodePassword(UUID.randomUUID().toString().substring(0, 8), user.getPasswordSalt()));
        return update(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<User> getAllAdminUsers() {
        UserProfile profile = userProfileService.getAdminUserProfile();
        Criterion critByAdmin = Restrictions.eq(User.USERPROFILE, profile);
        return super.findByCriteria(critByAdmin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> find(String pattern, long firstResult, long maxResults, String sortField, boolean isAscending) {
        Criterion critByName = null;
        if(StringUtils.isNotBlank(pattern)){
            critByName = Restrictions.ilike(User.LASTNAME, pattern);
        }
        Order order = null;
        if(StringUtils.isNotBlank(sortField)){
            if(isAscending){
                order = Order.asc(sortField);
            }
            else{
                order = Order.desc(sortField);
            }            
        }
        return findByCriteria(order, (int)firstResult, (int)maxResults, critByName);
    }

    @Override
    public int count(String pattern) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        Criterion critByName = Restrictions.ilike(User.LASTNAME, pattern);
        criteria.setProjection(Projections.rowCount());
        if(StringUtils.isNotBlank(pattern)){
            criteria.add(critByName);
        }
        return ((Long) criteria.uniqueResult()).intValue();
    }

    @Override
    @Transactional
    public void assignUserToProject(User user, Project prj) {
        user = getByID(user.getId());
        prj = projectService.getByID(prj.getId());
        user.addProject(prj);
        prj.addUser(user);
        update(user);
        projectService.save(prj);
    }

    @Override
    @Transactional
    public void removeUserFromProject(User user, Project prj) {
        user = getByID(user.getId());
        prj = projectService.getByID(prj.getId());
        user.getProjects().remove(prj);
        prj.getUsers().remove(user);
        update(user);
        projectService.save(prj);
    }
}