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

import it.av.es.model.Group;
import it.av.es.model.OrderLog;
import it.av.es.service.OrderLogService;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class OrderLogServiceHibernate extends ApplicationServiceHibernate<OrderLog> implements OrderLogService {

    @Override
    public Group getByID(OrderLog id) {
        return null;
    }

}