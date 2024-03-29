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

import it.av.es.Locales;
import it.av.es.model.Language;
import it.av.es.service.LanguageService;

import java.util.Locale;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the operation on {@link Language}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Transactional(readOnly = true)
@Repository
public class LanguageServiceHibernate extends ApplicationServiceHibernate<Language> implements LanguageService {

    @Override
    public Language getSupportedLanguage(Locale locale) {
        return getLnaguage(Locales.getSupportedLocale(locale).getLanguage());
    }

    private Language getLnaguage(String nameiso2) {
        Criterion critByName = Restrictions.eq(Language.LANGUAGE_FIELD, nameiso2);
        return findByCriteria(critByName).get(0);

    }
}