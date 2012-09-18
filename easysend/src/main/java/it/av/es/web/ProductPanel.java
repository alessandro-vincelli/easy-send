package it.av.es.web;

import it.av.es.model.Product;
import it.av.es.model.Project;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;
import it.av.es.web.data.ProductsOfProjectsSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class ProductPanel extends Panel {

    @SpringBean
    private ProjectService projectService;
    @SpringBean
    private ProductService productService;
    private ProductsOfProjectsSortableDataProvider dataProvider;

    public ProductPanel(String id, final IModel<Project> project) {
        super(id, project);
        Injector.get().inject(this);

        List<IColumn<Product, String>> columns = new ArrayList<IColumn<Product, String>>();

        columns.add(new PropertyColumn<Product, String>(new Model<String>("Product Name"), Product.NAME_FIELD, Product.NAME_FIELD));

        dataProvider = new ProductsOfProjectsSortableDataProvider(new ArrayList<Product>(project.getObject().getProducts()));
        final AjaxFallbackDefaultDataTable<Product, String> dataTable = new AjaxFallbackDefaultDataTable<Product, String>(
                "dataTable", columns, dataProvider, 50);
        add(dataTable);

        final Form<Product> formPrj = new Form<Product>("prj", new CompoundPropertyModel<Product>(new Product()));
        add(formPrj);
        formPrj.add(new TextField<String>("name"));
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Product p = (Product) form.getModelObject();
                project.getObject().addProduct(p);
                productService.save(p);
                Project project2 = projectService.save(project.getObject());
                project.setObject(project2);
                dataProvider.setData(new ArrayList<Product>(project2.getProducts()));
                target.add(dataTable);
                formPrj.setModelObject(new Product());
            }
        });
    }

}
