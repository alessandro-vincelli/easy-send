package it.av.es.web.manager;

import it.av.es.model.ProductFamily;
import it.av.es.service.ProductFamilyService;
import it.av.es.web.BasePageSimple;
import it.av.es.web.data.ProductFamilySortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

@AuthorizeInstantiation({ "ADMIN" })
public class ProductFamilyManagerPage extends BasePageSimple {

    @SpringBean
    private ProductFamilyService ProductFamilyService;
    private AjaxFallbackDefaultDataTable<ProductFamily, String> dataTable;

    public ProductFamilyManagerPage() {
        super();

        List<IColumn<ProductFamily, String>> columns = new ArrayList<IColumn<ProductFamily, String>>();

        columns.add(new AbstractColumn<ProductFamily, String>(new Model<String>("Actions")) {
            public void populateItem(Item<ICellPopulator<ProductFamily>> cellItem, String componentId, IModel<ProductFamily> model) {
                cellItem.add(new ActionPanel(componentId, model));
            }
        });

        columns.add(new PropertyColumn<ProductFamily, String>(new Model<String>("name"), ProductFamily.NAME_FIELD, ProductFamily.NAME_FIELD));

        dataTable = new AjaxFallbackDefaultDataTable<ProductFamily, String>("dataTable", columns, new ProductFamilySortableDataProvider(), 50);
        add(dataTable);

        final Form<ProductFamily> form = new Form<ProductFamily>("form", new CompoundPropertyModel<ProductFamily>(new ProductFamily()));
        add(form);
        form.add(new TextField<String>("name"));
        form.add(new AjaxSubmitLink("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> f) {
                super.onSubmit(target, form);
                ProductFamilyService.save(form.getModelObject());
                form.setModelObject(new ProductFamily());
                target.add(dataTable);
                target.add(form);
                getFeedbackPanel().success("ProductFamily added with success");
                getFeedbackPanel().publishWithEffects(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                getFeedbackPanel().error("An error occured");
                getFeedbackPanel().publishWithEffects(target);
            }
        });
    }

    class ActionPanel extends Panel {
        public ActionPanel(String id, final IModel<ProductFamily> ProductFamilyModel) {
            super(id, ProductFamilyModel);
            Injector.get().inject(this);
            AjaxLink<ProductFamily> buttonRemove = new AjaxLink<ProductFamily>("remove", ProductFamilyModel) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        ProductFamilyService.remove(getModelObject());
                        target.add(dataTable);
                        getFeedbackPanel().success(getString("ProductFamily.message.ProductFamilyRemoved"));
                    } catch (Exception e) {
                        getFeedbackPanel().error(getString("ProductFamily.message.ProductFamilyRemovedFailed"));
                    }
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(buttonRemove);
        }
    }

}
