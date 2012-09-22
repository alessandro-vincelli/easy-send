package it.av.es.web.data.table;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

public class CustomPagingNavigator extends PagingNavigator {

    public CustomPagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }

}
