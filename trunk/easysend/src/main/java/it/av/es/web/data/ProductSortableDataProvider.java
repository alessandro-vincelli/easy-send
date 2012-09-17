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
package it.av.es.web.data;

import it.av.es.model.Product;
import it.av.es.service.ProductService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class ProductSortableDataProvider extends SortableDataProvider<Product, String> {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private ProductService productService;
    private transient Collection<Product> results;
    private long size;

    /**
     * Constructor
     */
    public ProductSortableDataProvider() {
        super();
        Injector.get().inject(this);
        results = productService.getAll();
        // setSort(LightVac.SortedFieldNames.dateTime.value(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long size() {
        
            size = productService.getAll().size();
            return size;
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IModel<Product> model(Product product) {
        return new ProductDetachableModel(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {
        results = null;
    }


    @Override
    public Iterator<? extends Product> iterator(long first, long count) {
        results = productService.getAll();
        return Collections.synchronizedList(new ArrayList<Product>(results)).iterator();
    }

}