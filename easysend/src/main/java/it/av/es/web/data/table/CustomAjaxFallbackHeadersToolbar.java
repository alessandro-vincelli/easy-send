package it.av.es.web.data.table;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;

/**
 * 
 * Used to customize html
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class CustomAjaxFallbackHeadersToolbar extends AjaxFallbackHeadersToolbar {

    public CustomAjaxFallbackHeadersToolbar(DataTable table, ISortStateLocator stateLocator) {
        super(table, stateLocator);
    }

}