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

import it.av.es.model.UserProfile;
import it.av.es.service.UserProfileService;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class UserProfileServiceHibernate extends ApplicationServiceHibernate<UserProfile> implements UserProfileService {

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getRegolarUserProfile() {
        return getByName(UserProfile.VENDOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getAdminUserProfile() {
        return getByName(UserProfile.ADMIN);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getOperatorUserProfile() {
        return getByName(UserProfile.OPERATOR);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getProjectManagerUserProfile() {
        return getByName(UserProfile.PROJECT_MANAGER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfile getByName(String name) {
        Criterion crit = Restrictions.eq(UserProfile.NAME, name);
        List<UserProfile> result = super.findByCriteria(crit);
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }
}