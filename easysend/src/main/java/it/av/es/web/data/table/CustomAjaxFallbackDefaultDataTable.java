package it.av.es.web.data.table;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

/**
 * 
 * Used to customize html
 * 
 * @see CustomAjaxFallbackHeadersToolbar
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class CustomAjaxFallbackDefaultDataTable<T, S> extends DataTable<T, S>{

    public CustomAjaxFallbackDefaultDataTable(String id, List<? extends IColumn<T, S>> columns, ISortableDataProvider<T, S> dataProvider, int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
        setOutputMarkupId(true);
        setVersioned(false);
        //addTopToolbar(new AjaxNavigationToolbar(this));
        addTopToolbar(new CustomAjaxFallbackHeadersToolbar(this, dataProvider));
        addBottomToolbar(new CustomAjaxNavigationToolbar(this));
        addBottomToolbar(new NoRecordsToolbar(this));
    }

    @Override
    protected Item<T> newRowItem(final String id, final int index, final IModel<T> model)
    {
        return new OddEvenItem<T>(id, index, model);
    }

}
