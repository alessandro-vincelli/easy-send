package it.av.es.web;

import it.av.es.model.Order;
import it.av.es.model.OrderStatus;
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
import it.av.es.web.component.OrderDeliveredDialog;
import it.av.es.web.component.OrderInvoiceDialog;
import it.av.es.web.data.OrderSortableDataProvider;
import it.av.es.web.data.table.CustomAjaxFallbackDefaultDataTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.apache.wicket.model.ResourceModel;
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
@AuthorizeInstantiation({ "USER", "VENDOR", "OPERATOR", "PROJECT_MANAGER" })
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
    private AJAXDownload downloadInvoice;
    private Order selectedOrder = new Order();

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
        columns.add(new PropertyColumn<Order, String>(new Model<String>(getString("order.status")), Order.STATUS_FIELD, Order.STATUS_FIELD) {
            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                item.add(new Label(componentId, getString(rowModel.getObject().getStatus().name())));
                item.add(AttributeModifier.prepend("style", "text-align: center;"));
                item.add(AttributeModifier.prepend("title", "Stato ordine"));
            }
        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Cliente"), Order.CUSTOMER_FIELD + ".corporateName", Order.CUSTOMER_FIELD + ".corporateName") {
            @Override
            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(AttributeModifier.prepend("style", "width: 200px;"));
            }
        });
        columns.add(new PropertyColumn<Order, String>(new Model<String>("Data"), Order.CREATIONTIME_FIELD, Order.CREATIONTIME_FIELD));
//        columns.add(new PropertyColumn<Order, String>(new Model<String>("Sped."), Order.SHIPPINGCOST_FIELD, Order.SHIPPINGCOST_FIELD) {
//
//            @Override
//            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
//                item.add(new Label(componentId, NumberUtil.italianCurrency.format(rowModel.getObject().getShippingCost())));
//                item.add(AttributeModifier.prepend("style", "text-align: center;"));
//            }
//        });
//        columns.add(new PropertyColumn<Order, String>(new Model<String>("P.T."), Order.PAYMENTTYPE_FIELD, Order.PAYMENTTYPE_FIELD) {
//
//            @Override
//            public void populateItem(Item<ICellPopulator<Order>> item, String componentId, IModel<Order> rowModel) {
//                Label label = new Label(componentId, getString(rowModel.getObject().getPaymentType().toString() + "-short"));
//                label.add(AttributeModifier.prepend("title", getString(rowModel.getObject().getPaymentType().toString())));
//                item.add(label);
//                item.add(AttributeModifier.prepend("style", "text-align: center;"));
//                item.add(AttributeModifier.prepend("title", "Tipo Pagamento"));
//            }
//        });
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
                cellItem.add(AttributeModifier.prepend("style", "width: 150px;"));
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
                            is = pdfExporter.exportOrdersList(ord, date, user, project, orderService);
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
        
                        
        downloadInvoice = new AJAXDownload() {

            @Override
            protected String getFileName() {
                StringBuffer fileName = new StringBuffer(80);
                fileName.append(getSecuritySession().getCurrentProject().getName());
                fileName.append("_fattura_n_" + selectedOrder.getInvoiceNumber().toString());
                fileName.append("_data_" + DateUtil.SDF2DATE.print(selectedOrder.getInvoiceDate().getTime()));
                fileName.append(".pdf");
                return fileName.toString();
            }

            @Override
            protected IResourceStream getResourceStream() {
                AbstractResourceStream stream = new AbstractResourceStream() {
                    InputStream is;
                    @Override
                    public InputStream getInputStream() throws ResourceStreamNotFoundException {
                        try {
                            is =  new ByteArrayInputStream(selectedOrder.getInvoice());
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
        add(downloadInvoice);
        
        
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
            Order order = model.getObject();
            PropertyListView<ProductOrdered> listView = new PropertyListView<ProductOrdered>("list", list) {

                @Override
                protected void populateItem(ListItem<ProductOrdered> item) {
                    ProductOrdered p = item.getModelObject();
                    item.add(new Label("productName", new Model<String>(p.getProduct().getName())));
                    item.add(new Label("productNumber", new Model<Integer>(p.getNumber())));
                    item.add(new Label("productWeigth", new Model<BigDecimal>(p.getTotalWeight())));
                    item.add(new Label("productPairs", new Model<Integer>(p.getTotalItemsInside())));
                    item.add(new Label("productVolume", new Model<BigDecimal>(p.getTotalVolume())));
                    item.add(new Label("productAmount", new Model<String>(NumberUtil.getItalian().format(p.getAmount()))));
                    item.add(new Label("productDiscount", new Model<Integer>(p.getDiscount())));
                }
            };
            add(new Label("shippingCost", new Model<String>(NumberUtil.getItalian().format(order.getShippingCost()))));
            add(new Label("numberOfItemsInProductOrdered", new Model<Integer>(order.getNumberOfItemsInProductOrdered())));
            add(new Label("totalPacksInProductOrdered", new Model<BigDecimal>(order.getTotalWeightInProductOrdered())));
            add(new Label("totalPairsInProductOrdered", new Model<Integer>(order.getTotalItemsInsideInProductOrdered())));
            add(new Label("totalVolumeInProductOrdered", new Model<BigDecimal>(order.getTotalVolumeInProductOrdered())));
            add(new Label("totalAmount", new Model<String>(NumberUtil.italianCurrency.format(order.getTotalAmount()))));
            add(new Label("shippingAddress", new Model<String>(order.getCustomerAddressForDisplay())));
            add(new Label("partitaIvaNumber", new Model<String>(order.getCustomer().getPartitaIvaNumber())));
            add(new Label("notesComplete").setDefaultModel(new Model<String>(orderService.getNotesForDisplay(order))));
            add(new Label("paymentType").setDefaultModel(new Model<String>(new ResourceModel(order.getPaymentType().name()).getObject())));
            Label invoice = new Label("invoice", new Model<String>(""));
            add(invoice.setVisible(false));
            if(order.getStatus().equals(OrderStatus.INVOICE_APPROVED) || order.getStatus().equals(OrderStatus.INVOICE_PAID)){
                invoice.setDefaultModelObject(order.getInvoiceNumber().toString());
                invoice.setVisible(true);    
            }
            Label invoiceDueDate = new Label("invoiceDueDate", new Model<String>(""));
            add(invoiceDueDate.setVisible(false));
            if(order.getInvoiceDueDate() != null){
                invoiceDueDate.setDefaultModelObject(DateUtil.SDF2SHOWDATE.print(order.getInvoiceDueDate().getTime()));
                invoiceDueDate.setVisible(true);
                Calendar calendar = Calendar.getInstance();
                if(!order.getStatus().equals(OrderStatus.INVOICE_PAID) && order.getInvoiceDueDate().before(calendar.getTime())){
                    invoiceDueDate.add(AttributeModifier.replace("style", "color: red; font-weight:bold;"));
                }
            }
            Label invoiceDate = new Label("invoiceDate", new Model<String>(""));
            add(invoiceDate.setVisible(false));
            if(order.getInvoiceDate() != null){
                invoiceDate.setDefaultModelObject(DateUtil.SDF2SHOWDATE.print(order.getInvoiceDate().getTime()));
                invoiceDate.setVisible(true);
            }
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
            
            String url = getCallbackUrl().toString();

            if (addAntiCache) {
                url = url + (url.contains("?") ? "&" : "?");
                url = url + "antiCache=" + System.currentTimeMillis();
            }

            // the timeout is needed to let Wicket release the channel
            target.appendJavaScript("setTimeout(\"window.location.href='" + url + "'\", 100);");

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
            final MessageDialog warningDialog = new MessageDialog("warningDialog", new ResourceModel("dialog.confirmCancelOrderTitle").getObject(),  new ResourceModel("dialog.confirmCancelOrder").getObject()) {

                @Override
                protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                    if (buttonName.equals(ButtonName.BUTTON_YES)) {
                        try {
                            orderService.cancel(model.getObject(), getSecuritySession().getLoggedInUser());
                            getFeedbackPanel().info(new ResourceModel("order.orderCancelled").getObject());
                        } catch (Exception e) {
                            getFeedbackPanel().error(new ResourceModel("order.orderCancellNotPossible").getObject());
                        }
                        target.add(dataTable);
                        getFeedbackPanel().publishWithEffects(target);
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
                    if(getModelObject().getStatus().equals(OrderStatus.INCHARGE)){
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.removeInChargeOrder").getObject()));
                        tag.addBehavior(AttributeModifier.replace("class", "button-gray-small"));
                    }
                    else{
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.inChargeOrder").getObject()));
                        tag.addBehavior(AttributeModifier.replace("class", "button-green-small"));
                    }   
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    if(getModelObject().getStatus().equals(OrderStatus.INCHARGE)){
                        orderService.removeInCharge(getModelObject(), getSecuritySession().getLoggedInUser());
                        getFeedbackPanel().info(new ResourceModel("info.order.removedInCharge").getObject());
                    }
                    else{
                        orderService.setAsInCharge(getModelObject(), getSecuritySession().getLoggedInUser());    
                        getFeedbackPanel().info(new ResourceModel("info.order.approvedInCharge").getObject());
                    }
                    target.add(dataTable);
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(buttonInCharge);
            String buttonInChargeLabelText  = model.getObject().getStatus().equals(OrderStatus.INCHARGE)? new ResourceModel("button.removeInChargeOrder").getObject(): new ResourceModel("button.inChargeOrder").getObject();
            Label buttonInChargeLabel = new Label("label", buttonInChargeLabelText);
            buttonInCharge.add(buttonInChargeLabel);
            
            AjaxFallbackLink<Order> buttonOrderSent = new AjaxFallbackLink<Order>("buttonOrderSent", model) {

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    if(getModelObject().getStatus().equals(OrderStatus.SENT)){
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.removeOrderSent").getObject()));  
                        tag.addBehavior(AttributeModifier.replace("class", "button-gray-small"));
                    }
                    else{
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.orderSent").getObject()));
                        tag.addBehavior(AttributeModifier.replace("class", "button-green-small"));
                    }   
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    if(getModelObject().getStatus().equals(OrderStatus.SENT)){
                        orderService.removeSentStatus(getModelObject(), getSecuritySession().getLoggedInUser());
                        getFeedbackPanel().info(new ResourceModel("info.order.removedSentStatus").getObject());
                    }
                    else{
                        orderService.setSentStatus(getModelObject(), getSecuritySession().getLoggedInUser()); 
                        getFeedbackPanel().info(new ResourceModel("info.order.approvedSentStatus").getObject());
                    }
                    target.add(dataTable);
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(buttonOrderSent);
            String buttonOrderSentLabelText  = model.getObject().getStatus().equals(OrderStatus.SENT)? new ResourceModel("button.removeOrderSent").getObject(): new ResourceModel("button.orderSent").getObject();
            Label buttonOrderSentOrderLabel = new Label("label", buttonOrderSentLabelText);
            buttonOrderSent.add(buttonOrderSentOrderLabel);
            
            
            final OrderDeliveredDialog orderDeliveredDialog = new OrderDeliveredDialog("orderDeliveredDialog", model.getObject(), getFeedbackPanel()) {
                @Override
                protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                    target.add(dataTable);
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(orderDeliveredDialog);
            
            AjaxFallbackLink<Order> buttonOrderDelivered = new AjaxFallbackLink<Order>("buttonOrderDelivered", model) {

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    if(getModelObject().getStatus().equals(OrderStatus.DELIVERED)){
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.removeOrderDelivered").getObject()));
                        tag.addBehavior(AttributeModifier.replace("class", "button-gray-small"));
                    }
                    else{
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.orderDelivered").getObject()));
                        tag.addBehavior(AttributeModifier.replace("class", "button-green-small"));
                    }   
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    if(getModelObject().getStatus().equals(OrderStatus.DELIVERED)){
                        orderService.removeDeliveredStatus(getModelObject(), getSecuritySession().getLoggedInUser());
                        getFeedbackPanel().info(new ResourceModel("info.order.removedDeliveredStatus").getObject());
                        target.add(dataTable);
                    }
                    else{
                        orderDeliveredDialog.show(target); 
                    }
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(buttonOrderDelivered);
            String buttonOrderDeliveredLabelText  = model.getObject().getStatus().equals(OrderStatus.DELIVERED)? new ResourceModel("button.removeOrderDelivered").getObject(): new ResourceModel("button.orderDelivered").getObject();
            Label buttonOrderDeliveredLabel = new Label("label", buttonOrderDeliveredLabelText);
            buttonOrderDelivered.add(buttonOrderDeliveredLabel);
            
            final OrderInvoiceDialog invoiceDialog = new OrderInvoiceDialog("invoiceDialog", model.getObject(), getFeedbackPanel()) {
                @Override
                protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                    target.add(dataTable);
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(invoiceDialog);
            
            final MessageDialog removeCreatedInvoiceDialog = new MessageDialog("removeCreatedInvoiceDialog", new ResourceModel("dialog.confirmRemoveCreatedInvoiceDialogTitle").getObject(),  new ResourceModel("dialog.confirmRemoveCreatedInvoiceDialog").getObject()) {

                @Override
                protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                    if (buttonName.equals(ButtonName.BUTTON_YES)) {
                        try {
                            orderService.removeInvoiceCreatedStatus(model.getObject(), getSecuritySession().getLoggedInUser());
                            getFeedbackPanel().info(new ResourceModel("order.removedCreatedInvoice").getObject());
                        } catch (Exception e) {
                            getFeedbackPanel().error(new ResourceModel("order.removeCreatedInvoiceNotPossible").getObject());
                        }
                        target.add(dataTable);
                        getFeedbackPanel().publishWithEffects(target);
                    }
                }
            };
            add(removeCreatedInvoiceDialog);
            
            AjaxFallbackLink<Order> buttonCreateInvoiceOrder = new AjaxFallbackLink<Order>("buttonCreateInvoiceOrder", model) {

                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    if(getModelObject().getStatus().equals(OrderStatus.INVOICE_CREATED)){
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.removeInvoiceCreatedOrderDelivered").getObject())); 
                        tag.addBehavior(AttributeModifier.replace("class", "button-gray-small"));
                    }
                    else{
                        tag.addBehavior(AttributeModifier.replace("title", new ResourceModel("button.createdInvoiceOrder").getObject()));
                        tag.addBehavior(AttributeModifier.replace("class", "button-green-small"));
                    }   
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    if(getModelObject().getStatus().equals(OrderStatus.INVOICE_CREATED)){
                        removeCreatedInvoiceDialog.show(target);
                    }
                    else{
                        invoiceDialog.show(target); 
                    }
                    getFeedbackPanel().publishWithEffects(target);
                    //target.add(dataTable);
                }
            };
            add(buttonCreateInvoiceOrder);
            
            String buttonCreateInvoiceOrderLabelText  = model.getObject().getStatus().equals(OrderStatus.INVOICE_CREATED)? new ResourceModel("button.removeInvoiceCreatedOrderDelivered").getObject(): new ResourceModel("button.createdInvoiceOrder").getObject();
            Label buttonCreateInvoiceOrderLabel = new Label("buttonCreateInvoiceOrderLabel", buttonCreateInvoiceOrderLabelText);
            buttonCreateInvoiceOrder.add(buttonCreateInvoiceOrderLabel);
            
            AjaxFallbackLink<Order> buttonlOrderInvoice = new AjaxFallbackLink<Order>("invoice", model) {
                
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        selectedOrder = getModelObject();
                        downloadInvoice.initiate(target);
                    } catch (Exception e) {
                        getFeedbackPanel().error("Error generating PDF");
                    }
                    getFeedbackPanel().publishWithEffects(target);
                }
            };
            add(buttonlOrderInvoice);
            
            
            final MessageDialog approveInvoiceDialog = new MessageDialog("approveInvoiceDialog", new ResourceModel("dialog.confirmApproveInvoiceDialogTitle").getObject(),  new ResourceModel("dialog.confirmApproveInvoiceDialog").getObject()) {

                @Override
                protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                    if (buttonName.equals(ButtonName.BUTTON_YES)) {
                        try {
                            orderService.setInvoiceApprovedStatus(model.getObject(), getSecuritySession().getLoggedInUser());
                            getFeedbackPanel().info(new ResourceModel("order.orderInvoiceApproved").getObject());
                        } catch (Exception e) {
                            getFeedbackPanel().error(new ResourceModel("order.orderInvoiceApprovedNotPossible").getObject());
                        }
                        target.add(dataTable);
                        getFeedbackPanel().publishWithEffects(target);
                    }
                }
            };
            add(approveInvoiceDialog);
            AjaxFallbackLink<Order> buttonApproveInvoiceOrder = new AjaxFallbackLink<Order>("buttonApproveInvoiceOrder", model) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    approveInvoiceDialog.show(target);
                }
            };
            add(buttonApproveInvoiceOrder);
            
            final MessageDialog paidInvoiceDialog = new MessageDialog("paidInvoiceDialog", new ResourceModel("dialog.confirmPaidInvoiceDialogTitle").getObject(),  new ResourceModel("dialog.confirmPaidInvoiceDialog").getObject()) {

                @Override
                protected void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName) {
                    if (buttonName.equals(ButtonName.BUTTON_YES)) {
                        try {
                            orderService.setInvoicePaidStatus(model.getObject(), getSecuritySession().getLoggedInUser());
                            getFeedbackPanel().info(new ResourceModel("order.orderInvoicePaid").getObject());
                        } catch (Exception e) {
                            getFeedbackPanel().error(new ResourceModel("order.orderInvoicePaidNotPossible").getObject());
                        }
                        target.add(dataTable);
                        getFeedbackPanel().publishWithEffects(target);
                    }
                }
            };
            add(paidInvoiceDialog);
            AjaxFallbackLink<Order> buttonPaidInvoiceDialogOrder = new AjaxFallbackLink<Order>("buttonPaidInvoiceDialogOrder", model) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    paidInvoiceDialog.show(target);
                }
            };
            add(buttonPaidInvoiceDialogOrder);
            
            
            if(model.getObject().getStatus().equals(OrderStatus.CREATED)){
                buttonCancelOrder.setVisible(true);
                buttonInCharge.setVisible(true);
                buttonOrderSent.setVisible(false);
                buttonOrderDelivered.setVisible(false);
                buttonCreateInvoiceOrder.setVisible(false);
                buttonlOrderInvoice.setVisible(false);
                buttonApproveInvoiceOrder.setVisible(false);
                buttonPaidInvoiceDialogOrder.setVisible(false);
            }
            else if(model.getObject().getStatus().equals(OrderStatus.INCHARGE)){
                buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(true);
                buttonOrderSent.setVisible(true);
                buttonOrderDelivered.setVisible(false);
                buttonCreateInvoiceOrder.setVisible(false);
                buttonlOrderInvoice.setVisible(false);
                buttonApproveInvoiceOrder.setVisible(false);
                buttonPaidInvoiceDialogOrder.setVisible(false);
            }
            else if(model.getObject().getStatus().equals(OrderStatus.SENT)){
                buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(false);
                buttonOrderSent.setVisible(true);
                buttonOrderDelivered.setVisible(true);
                buttonCreateInvoiceOrder.setVisible(false);
                buttonlOrderInvoice.setVisible(false);
                buttonApproveInvoiceOrder.setVisible(false);
                buttonPaidInvoiceDialogOrder.setVisible(false);
            }
            else if(model.getObject().getStatus().equals(OrderStatus.DELIVERED)){
                buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(false);
                buttonOrderSent.setVisible(false);
                buttonOrderDelivered.setVisible(true);
                buttonCreateInvoiceOrder.setVisible(true);
                buttonlOrderInvoice.setVisible(false);
                buttonApproveInvoiceOrder.setVisible(false);
                buttonPaidInvoiceDialogOrder.setVisible(false);
            }
            else if(model.getObject().getStatus().equals(OrderStatus.INVOICE_CREATED)){
                buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(false);
                buttonOrderSent.setVisible(false);
                buttonOrderDelivered.setVisible(false);
                buttonCreateInvoiceOrder.setVisible(true);
                buttonlOrderInvoice.setVisible(true);
                buttonApproveInvoiceOrder.setVisible(true);
                buttonPaidInvoiceDialogOrder.setVisible(false);
            }
            else if(model.getObject().getStatus().equals(OrderStatus.INVOICE_APPROVED)){
                buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(false);
                buttonOrderSent.setVisible(false);
                buttonOrderDelivered.setVisible(false);
                buttonCreateInvoiceOrder.setVisible(false);
                buttonlOrderInvoice.setVisible(true);
                buttonApproveInvoiceOrder.setVisible(false);
                buttonPaidInvoiceDialogOrder.setVisible(true);
            }
            else if(model.getObject().getStatus().equals(OrderStatus.INVOICE_PAID)){
                buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(false);
                buttonOrderSent.setVisible(false);
                buttonOrderDelivered.setVisible(false);
                buttonCreateInvoiceOrder.setVisible(false);
                buttonlOrderInvoice.setVisible(true);
                buttonApproveInvoiceOrder.setVisible(false);
                buttonPaidInvoiceDialogOrder.setVisible(false);
            }
            else if(model.getObject().getStatus().equals(OrderStatus.CANCELLED)){
                buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(false);
                buttonOrderSent.setVisible(false);
                buttonOrderDelivered.setVisible(false);
                buttonCreateInvoiceOrder.setVisible(false);
                buttonlOrderInvoice.setVisible(false);
                buttonApproveInvoiceOrder.setVisible(false);
                buttonPaidInvoiceDialogOrder.setVisible(false);
            }
            
            User loggedInUser2 = getSecuritySession().getLoggedInUser();
            boolean operator = loggedInUser2.getUserProfile().getName().equals(UserProfile.OPERATOR);
            boolean pm = loggedInUser2.getUserProfile().getName().equals(UserProfile.PROJECT_MANAGER);
            
            if(!operator && !pm){
                //buttonCancelOrder.setVisible(false);
                buttonInCharge.setVisible(false);
                buttonOrderSent.setVisible(false);
                buttonOrderDelivered.setVisible(false);
                buttonCreateInvoiceOrder.setVisible(false);
                buttonlOrderInvoice.setVisible(false);
            }
        }

    }

    @Override
    public void onConfigure() {
        User loggedInUser2 = getSecuritySession().getLoggedInUser();
        boolean operator = loggedInUser2.getUserProfile().getName().equals(UserProfile.OPERATOR);
        boolean admin = loggedInUser2.getUserProfile().getName().equals(UserProfile.ADMIN);
        boolean pm = loggedInUser2.getUserProfile().getName().equals(UserProfile.PROJECT_MANAGER);
        exportAsPDFButton.setVisible(operator || pm);
        
    }
}
