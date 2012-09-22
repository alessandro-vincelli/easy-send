package it.av.es.web.data.table;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

public class CustomAjaxNavigationToolbar extends AjaxNavigationToolbar {

    public CustomAjaxNavigationToolbar(DataTable<?, ?> table) {
        super(table);
    }

    @Override
    protected PagingNavigator newPagingNavigator(String navigatorId, DataTable<?, ?> table) {
        return new CustomPagingNavigator(navigatorId, table);
    }

}
