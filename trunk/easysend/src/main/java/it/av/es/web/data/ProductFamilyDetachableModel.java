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

import it.av.es.EasySendException;
import it.av.es.model.ProductFamily;
import it.av.es.service.ProductFamilyService;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class ProductFamilyDetachableModel extends LoadableDetachableModel<ProductFamily> {

    private static final long serialVersionUID = 1L;
    private final String id;
    @SpringBean
    private ProductFamilyService ProductFamilyService;

    /**
     * 
     * @param object
     */
    public ProductFamilyDetachableModel(ProductFamily object) {
        this(object.getId());
        Injector.get().inject(this);
    }

    /**
     * @param id
     */
    public ProductFamilyDetachableModel(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    /**
     * used for dataview with ReuseIfModelsEqualStrategy item reuse strategy
     * 
     * @see org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof ProductFamilyDetachableModel) {
            ProductFamilyDetachableModel other = (ProductFamilyDetachableModel) obj;
            return other.id == id;
        }
        return false;
    }

    /**
     * @see org.apache.wicket.model.LoadableDetachableModel#load()
     */
    @Override
    protected final ProductFamily load() {
        try {
            return ProductFamilyService.getByID(id);
        } catch (EasySendException e) {
            return null;
        }
    }
}