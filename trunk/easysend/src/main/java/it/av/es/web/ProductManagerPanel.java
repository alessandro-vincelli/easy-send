package it.av.es.web;

import it.av.es.model.Price;
import it.av.es.model.Product;
import it.av.es.model.Project;
import it.av.es.service.PriceService;
import it.av.es.service.ProductService;
import it.av.es.service.ProjectService;
import it.av.es.web.data.ProductSortableDataProvider;
import it.av.es.web.data.table.CustomAjaxFallbackDefaultDataTable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "ADMIN" })
public class ProductManagerPanel extends Panel {

    @SpringBean
    private ProductService userService;
    @SpringBean
    private ProductService productService;
    @SpringBean
    private PriceService priceService;
    @SpringBean
    private ProjectService projectService;
    
    private CustomAjaxFallbackDefaultDataTable<Product, String> dataTable;

    public ProductManagerPanel(String id, final IModel<Project> modelProject) {
        super(id, modelProject);

        List<IColumn<Product, String>> columns = new ArrayList<IColumn<Product, String>>();

        columns.add(new AbstractColumn<Product, String>(new Model<String>("Assign Products")) {
            public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> model) {
                cellItem.add(new ProductPanel(componentId, model, modelProject));
            }
        });

        columns.add(new AbstractColumn<Product, String>(new Model<String>("Assign Products")) {
            public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> model) {
                cellItem.add(new PriceListPanel(componentId, model));
            }
        });

        dataTable = new CustomAjaxFallbackDefaultDataTable<Product, String>("dataTable", columns, new ProductSortableDataProvider(modelProject.getObject()), 50);
        add(dataTable);

        final Form<Product> formPrj = new Form<Product>("newProduct", new CompoundPropertyModel<Product>(new Product()));
        add(formPrj);
        formPrj.add(new TextField<String>("name"));
        formPrj.add(new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Product p = (Product) form.getModelObject();
                p = productService.save(p);
                modelProject.getObject().addProduct(p);
                Project project = projectService.save(modelProject.getObject());
                modelProject.setObject(project);
                target.add(dataTable.getPage());
                formPrj.setModelObject(new Product());
            }
        });

    }

    class PriceListPanel extends Panel {

        public PriceListPanel(String id, final IModel<Product> model) {
            super(id, model);
            final Form<Product> formPriceList = new Form<Product>("priceListForm", new CompoundPropertyModel<Product>(model.getObject()));
            formPriceList.setOutputMarkupId(true);
            add(formPriceList);
            final PropertyListView<Price> listView = new PropertyListView<Price>("prices") {

                @Override
                protected void populateItem(final ListItem<Price> item) {
                    item.add(new TextField<Integer>("fromNumber", Integer.class));
                    item.add(new TextField<Integer>("toNumber", Integer.class));
                    item.add(new TextField<BigDecimal>("amount", BigDecimal.class));
                    item.add(new TextField<Currency>("currency", Currency.class));
                    item.add(new TextField<Double>("percentDiscount", Double.class));
                    AjaxSubmitLink savePrice = new AjaxSubmitLink("savePrice", formPriceList) {
                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            Price p = item.getModelObject();
                            p = priceService.save(p);
                            item.setModelObject(p);
                            target.add(formPriceList);
                        }
                    };
                    item.add(savePrice);
                }
            };
            listView.setReuseItems(true);
            formPriceList.add(listView);
            listView.setOutputMarkupId(true);
            AjaxSubmitLink addPrice = new AjaxSubmitLink("addPrice", formPriceList) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Product p = (Product) form.getModelObject();
                    p.addPrice(new Price());
                    formPriceList.setModelObject(p);
                    target.add(formPriceList);
                }

            };
            formPriceList.add(addPrice);
            AjaxSubmitLink savePrice = new AjaxSubmitLink("savePriceList", formPriceList) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Product product = productService.save(formPriceList.getModelObject());
                    formPriceList.setModelObject(product);
                    target.add(formPriceList);
                }

            };
            formPriceList.add(savePrice);
        }

    }

    class ProductPanel extends Panel {

        public ProductPanel(String id, IModel<Product> model, final IModel<Project> project) {
            super(id, model);
            final Form<Product> formProd = new Form<Product>("form", new CompoundPropertyModel<Product>(model.getObject()));
            formProd.setOutputMarkupId(true);
            add(formProd);
            formProd.add(new TextField<String>(Product.NAME_FIELD));
            AjaxSubmitLink savePrice = new AjaxSubmitLink("save", formProd) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Product product = productService.save(formProd.getModelObject());
                    project.getObject().addProduct(product);
                    project.setObject(projectService.save(project.getObject()));
                    formProd.setModelObject(product);
                    target.add(formProd);
                }
            };
            formProd.add(savePrice);
            AjaxSubmitLink removePrice = new AjaxSubmitLink("remove", formProd) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    projectService.saveAndRemoveProduct(project.getObject(), formProd.getModelObject());
                    target.add(dataTable);
                }
            };
            formProd.add(removePrice);
        }

    }

}
