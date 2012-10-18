package it.av.es.web;

import it.av.es.model.Order;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.model.UserProfile;
import it.av.es.service.OrderService;
import it.av.es.service.pdf.PDFExporter;
import it.av.es.service.pdf.PDFExporterImpl;
import it.av.es.util.DateUtil;
import it.av.es.util.NumberUtil;
import it.av.es.web.component.ButtonName;
import it.av.es.web.component.MessageDialog;
import it.av.es.web.data.OrderSortableDataProvider;
import it.av.es.web.data.table.CustomAjaxFallbackDefaultDataTable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

/**
 * 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation({ "USER", "VENDOR", "OPERATOR" })
public class OrderManagerPage extends BasePageSimple {

    @SpringBean
    private OrderService orderService;
    private OrderSortableDataProvider dataProvider;
    private CustomAjaxFallbackDefaultDataTable<Order, String> dataTable;
    private AJAXDownload download;
    private User user;
    private Project project;
    private AjaxLink<String> exportAsPDFButton;
    private DropDownChoice<Date> orderDates;
    private boolean excludeCancelledOrders = true;

    public OrderManagerPage() {
        super();
        user = getSecuritySession().getLoggedInUser();
        project = getSecuritySession().getCurrentProject();

        List<IColumn<Order, String>> columns = new ArrayList<IColumn<Order, String>>();

        columns.add(new PropertyColumn<Order, String>(new Model<String>("N"), Order.REFERENCENUMBER_FIELD, Order.REFERENCENUMBER_FIELD) {
            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
            }
        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("L"), Order.ISINCHARGE_FIELD, Order.ISINCHARGE_FIELD) {
            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                item.add(new Label(componentId, getString(rowModel.getObject().getIsInCharge().toString())));
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
                item.add(AttributeModifier.prepend("title", "In Lavorazione"));
            }
        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Cliente"), Order.CUSTOMER_FIELD + ".corporateName", Order.CUSTOMER_FIELD + ".corporateName"));
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Data"), Order.CREATIONTIME_FIELD, Order.CREATIONTIME_FIELD));
//        columns.add(new PropertyColumn<Order, String>(new Model<String>("Sped."), Order.SHIPPINGCOST_FIELD, Order.SHIPPINGCOST_FIELD) {
//
//            @Override
//            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
//                item.add(new Label(componentId, NumberUtil.italianCurrency.format(rowModel.getObject().getShippingCost())));
//                item.add(AttributeModifier.prepend("style", "text-align: center;"));
//            }
//        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("P.T."), Order.PAYMENTTYPE_FIELD, Order.PAYMENTTYPE_FIELD) {

            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                Label label = new Label(componentId, getString(rowModel.getObject().getPaymentType().toString() + "-short"));
                label.add(AttributeModifier.prepend("title", getString(rowModel.getObject().getPaymentType().toString())));
                item.add(label);
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
                item.add(AttributeModifier.prepend("title", "Tipo Pagamento"));
            }
        });
        AbstractColumn<Order, String> prodotti = new AbstractColumn<Order, String>(new Model<String>("Prodotti"), "Prodotti") {
            public void populateItem(Item<ICellPopulator<Order>> cellItem, String componentId, IModel<Order> model) {
                cellItem.add(new OrderedProductPanel(componentId, model));
            }

            @Override
            public boolean isSortable() {
                return false;
            }
        };
        columns.add(prodotti);

        columns.add(new AbstractColumn<Order, String>(new Model<String>("Azioni")) {
            public void populateItem(Item<ICellPopulator<Order>> cellItem, String componentId, IModel<Order> model) {
                cellItem.add(new ActionPanel(componentId, model));
                //cellItem.add(AttributeModifier.replace("class", "options-width"));
            }
        });

        orderDates = new DropDownChoice<Date>("orderDates", new Model<Date>(), orderService.getDates(user, project));
        add(orderDates);
        orderDates.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataProvider.setFilterDate(orderDates.getModelObject());
                target.add(dataTable);
            }

        });
        
        final MessageDialog exportAsPDFButtonDialog = new MessageDialog("exportAsPDFButtonDialog", getString("dialog.exportAsPDFButtonDialogTitle"), getString("dialog.exportAsPDFButtonDialog")) {

            @Override
            protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                //put selected in Charge
                if (buttonName.equals(ButtonName.BUTTON_YES)) {
                    try {
                        Date date = orderDates.getModelObject();
                        orderService.setAsInCharge(user, project, date);
                    } catch (Exception e) {
                        getFeedbackPanel().error("Error setting orders in charge");
                    }
                }
                // then create the PDF
                try {
                    download.initiate(target);
                } catch (Exception e) {
                    getFeedbackPanel().error("Error generating PDF");
                }
                target.add(dataTable);
                getFeedbackPanel().publishWithEffects(target);
            }
        };
        add(exportAsPDFButtonDialog);
        
        exportAsPDFButton = new AjaxLink<String>("exportAsPDFButton") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    if(orderDates.getModelObject() == null){
                        getFeedbackPanel().warn(getString("message.noOrderDatesSelectForExport"));                        
                    }
                    else{
                        exportAsPDFButtonDialog.show(target);   
                    }
                } catch (Exception e) {
                    getFeedbackPanel().error(e.getMessage());
                }
                getFeedbackPanel().publishWithEffects(target);
            }
        };
        add(exportAsPDFButton);
        download = new AJAXDownload() {

            @Override
            protected String getFileName() {
                StringBuffer fileName = new StringBuffer(80);
                fileName.append(getSecuritySession().getCurrentProject().getName());
                fileName.append("_of_");
                fileName.append(DateUtil.SDF2DATE.print(orderDates.getModelObject().getTime()));
                fileName.append("_print_");
                fileName.append(DateUtil.SDF2DATE.print(new Date().getTime()));
                fileName.append("_");
                fileName.append(DateUtil.SDF2TIME.print(new Date().getTime()));
                fileName.append("_");
                //fileName.append(StringUtils.deleteWhitespace(message.getSubject()));
                fileName.append(".pdf");
                return fileName.toString();
            }

            @Override
            protected IResourceStream getResourceStream() {
                AbstractResourceStream stream = new AbstractResourceStream() {
                    InputStream is;

                    @Override
                    public InputStream getInputStream() throws ResourceStreamNotFoundException {
                        PDFExporter pdfExporter = new PDFExporterImpl();
                        try {
                            Date date = orderDates.getModelObject();
                            List<Order> ord = new ArrayList<Order>(orderService.get(user, project, date, excludeCancelledOrders, 0, 0, Order.REFERENCENUMBER_FIELD, true));
                            is = pdfExporter.exportOrdersList(ord, date, user, project, getLocalizer(), getPage());
                            return is;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void close() throws IOException {
                        is.close();
                    }
                };
                return stream;
            }

        };
        add(download);
        dataProvider = new OrderSortableDataProvider(user, project, excludeCancelledOrders);
        dataTable = new CustomAjaxFallbackDefaultDataTable<Order, String>("dataTable", columns, dataProvider, 25) {

            @Override
            protected Item<Order> newRowItem(String id, int index, IModel<Order> model) {
                if (true) {
                    return new Item<Order>(id, index, model) {

                        @Override
                        protected void onComponentTag(ComponentTag tag) {
                            super.onComponentTag(tag);
                            Order order = getModelObject();
                            if (order != null && order.getIsCancelled()) {
                                tag.put("style", "background-color: #EBEBEB;");
                            }
                            else if (order != null && order.getIsInCharge()) {
                                tag.put("style", "background-color: #E2FFEC;");
                            }
                        }

                    };
                }
                return super.newRowItem(id, index, model);
            }

        };
        add(dataTable);
        
        AjaxCheckBox excludeCancelledOrders = new AjaxCheckBox("excludeCancelledOrders", new PropertyModel<Boolean>(this, "excludeCancelledOrders")) {
            
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                dataProvider.setExcludeCancelledOrder(getModelObject());
                target.add(dataTable);
            }
        };
        add(excludeCancelledOrders);
    }

    public class OrderedProductPanel extends Panel {

        public OrderedProductPanel(String id, IModel<Order> model) {
            super(id, model);
            List<ProductOrdered> list = model.getObject().getProductsOrdered();
            PropertyListView<ProductOrdered> listView = new PropertyListView<ProductOrdered>("list", list) {

                @Override
                protected void populateItem(ListItem<ProductOrdered> item) {
                    ProductOrdered p = item.getModelObject();
                    item.add(new Label("productName", new Model<String>(p.getProduct().getName())));
                    item.add(new Label("productNumber", new Model<Integer>(p.getNumber())));
                    item.add(new Label("productAmount", new Model<String>(NumberUtil.getItalian().format(p.getAmount()))));
                    item.add(new Label("productDiscount", new Model<Integer>(p.getDiscount())));
                }
            };
            add(new Label("shippingCost", new Model<String>(NumberUtil.getItalian().format(model.getObject().getShippingCost()))));
            add(new Label("numberOfItemsInProductOrdered", new Model<Integer>(model.getObject().getNumberOfItemsInProductOrdered())));
            add(new Label("totalAmount", new Model<String>(NumberUtil.italianCurrency.format(model.getObject().getTotalAmount()))));
            add(listView);
        }

    }

    public abstract class AJAXDownload extends AbstractAjaxBehavior {
        private boolean addAntiCache;

        public AJAXDownload() {
            this(true);
        }

        public AJAXDownload(boolean addAntiCache) {
            super();
            this.addAntiCache = addAntiCache;
        }

        /**
         * Call this method to initiate the download.
         */
        public void initiate(AjaxRequestTarget target) {
            if (orderDates.getModelObject() != null) {
                String url = getCallbackUrl().toString();

                if (addAntiCache) {
                    url = url + (url.contains("?") ? "&" : "?");
                    url = url + "antiCache=" + System.currentTimeMillis();
                }

                // the timeout is needed to let Wicket release the channel
                target.appendJavaScript("setTimeout(\"window.location.href='" + url + "'\", 100);");
            } else {
                getFeedbackPanel().warn("Selezionare una data");
                target.add(getFeedbackPanel());
            }

        }

        public void onRequest() {
            ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(getResourceStream(), getFileName());
            handler.setContentDisposition(ContentDisposition.ATTACHMENT);
            getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
        }

        /**
         * Override this method for a file name which will let the browser prompt with a save/open dialog.
         * @see ResourceStreamRequestTarget#getFileName()
         */
        protected String getFileName() {
            return null;
        }

        /**
         * Hook method providing the actual resource stream.
         */
        protected abstract IResourceStream getResourceStream();
    }

    public class ActionPanel extends Panel {

        public ActionPanel(String id, final IModel<Order> model) {
            super(id, model);
            Injector.get().inject(this);
            final MessageDialog warningDialog = new MessageDialog("warningDialog", getString("dialog.confirmCancelOrderTitle"), getString("dialog.confirmCancelOrder")) {

                @Override
                protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                    if (buttonName.equals(ButtonName.BUTTON_YES)) {
                        try {
                            orderService.cancel(model.getObject());
                            getFeedbackPanel().info(getString("order.orderCancelled"));
                        } catch (Exception e) {
                            getFeedbackPanel().error(getString("order.orderCancellNotPossible"));
                        }
                        target.add(dataTable);
                        target.add(getFeedbackPanel());
                    }
                }

            };
            add(warningDialog);
            AjaxFallbackLink<Order> buttonCancelOrder = new AjaxFallbackLink<Order>("remove", model) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    warningDialog.show(target);
                }
            };
            if((!model.getObject().canBeCancelled()))buttonCancelOrder.setEnabled(false);
            add(buttonCancelOrder);
            
            AjaxFallbackLink<Order> buttonInCharge = new AjaxFallbackLink<Order>("buttonInCharge", model) {

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    if(getModelObject().getIsInCharge()){
                        tag.addBehavior(AttributeModifier.replace("title", getString("button.removeInChargeOrder")));    
                    }
                    else{
                        tag.addBehavior(AttributeModifier.replace("title", getString("button.inChargeOrder")));
                    }   
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    if(getModelObject().getIsInCharge()){
                        orderService.removeInCharge(getModelObject());
                    }
                    else{
                        orderService.setAsInCharge(getModelObject());    
                    }
                    target.add(dataTable);
                }
            };
            User loggedInUser2 = getSecuritySession().getLoggedInUser();
            boolean operator = loggedInUser2.getUserProfile().getName().equals(UserProfile.OPERATOR);
            buttonInCharge.setVisible(operator);
            add(buttonInCharge);
        }

    }

    @Override
    public void onConfigure() {
        User loggedInUser2 = getSecuritySession().getLoggedInUser();
        boolean operator = loggedInUser2.getUserProfile().getName().equals(UserProfile.OPERATOR);
        exportAsPDFButton.setVisible(operator);
    }
}
