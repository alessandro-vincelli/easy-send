package it.av.es.web;

import it.av.es.model.Product;
import it.av.es.model.Project;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;
import it.av.es.web.data.ProductsOfProjectsSortableDataProvider;
import it.av.es.web.data.ProjectSortableDataProvider;
import it.av.es.web.data.table.CustomAjaxFallbackDefaultDataTable;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The page provides the home page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "ADMIN" })
public class ProjectManagerPage extends BasePageSimple {

    @SpringBean
    private ProjectService userService;
    @SpringBean
    private ProjectService projectService;
    @SpringBean
    private ProductService productService;

    public ProjectManagerPage() {
        super();

        List<IColumn<Project, String>> columns = new ArrayList<IColumn<Project, String>>();

        columns.add(new PropertyColumn<Project, String>(new Model<String>("Progetto"), Project.NAME_FIELD, Project.NAME_FIELD));
        
        columns.add(new AbstractColumn<Project, String>(new Model<String>("Prodotti"), "Prodotti") {
            public void populateItem(Item<ICellPopulator<Project>> cellItem, String componentId, IModel<Project> model) {
                cellItem.add(new ProductManagerPanel(componentId, model));
            }
        });


        final CustomAjaxFallbackDefaultDataTable<Project, String> dataTable = new CustomAjaxFallbackDefaultDataTable<Project, String>("dataTable", columns,
                new ProjectSortableDataProvider(), 50);
        add(dataTable);

        final Form<Project> formPrj = new Form<Project>("prj", new CompoundPropertyModel<Project>(new Project()));
        add(formPrj);
        formPrj.add(new TextField<String>("name"));
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Project p = (Project) form.getModelObject();
                projectService.save(p);
                target.add(dataTable);
                formPrj.setModelObject(new Project());
            }
        });
    }

    public class ProductPanel extends Panel {
        public ProductPanel(String id, final IModel<Project> project) {
            super(id, project);
            Injector.get().inject(this);

            List<IColumn<Product, String>> columns = new ArrayList<IColumn<Product, String>>();

            columns.add(new PropertyColumn<Product, String>(new Model<String>("Product Name"), Product.NAME_FIELD, Product.NAME_FIELD));

            final ProductsOfProjectsSortableDataProvider dataProvider = new ProductsOfProjectsSortableDataProvider(new ArrayList<Product>(project.getObject().getProducts()));
            final AjaxFallbackDefaultDataTable<Product, String> dataTable = new AjaxFallbackDefaultDataTable<Product, String>("dataTable", columns, dataProvider, 50);
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

}
