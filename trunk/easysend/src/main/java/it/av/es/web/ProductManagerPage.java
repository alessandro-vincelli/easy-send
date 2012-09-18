package it.av.es.web;

import it.av.es.model.Product;
import it.av.es.service.ProductService;
import it.av.es.web.data.ProductSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR" })
public class ProductManagerPage extends BasePageSimple {

    @SpringBean
    private ProductService userService;
    @SpringBean
    private ProductService productService;

    public ProductManagerPage() {
        super();

        List<IColumn<Product, String>> columns = new ArrayList<IColumn<Product, String>>();

        columns.add(new PropertyColumn<Product, String>(new Model<String>("Product Name"), Product.NAME_FIELD, Product.NAME_FIELD));

        final AjaxFallbackDefaultDataTable<Product, String> dataTable = new AjaxFallbackDefaultDataTable<Product, String>(
                "dataTable", columns, new ProductSortableDataProvider(), 50);
        add(dataTable);

        final Form<Product> formPrj = new Form<Product>("prj", new CompoundPropertyModel<Product>(new Product()));
        add(formPrj);
        formPrj.add(new TextField<String>("name"));
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Product p = (Product) form.getModelObject();
                productService.save(p);
                target.add(dataTable);
                formPrj.setModelObject(new Product());
            }
        });

    }

}
