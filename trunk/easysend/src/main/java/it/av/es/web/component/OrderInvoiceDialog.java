package it.av.es.web.component;

import it.av.es.model.Order;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;

/**
 * 
 */
public abstract class OrderInvoiceDialog extends ModalWindow {

    /**
     * Constructor.
     * @param id the markupId, an html div suffice to host a dialog.
     * @param title the title of the dialog 
     * @param string the message to be displayed
     * @param icon the predefined icon to display
     */
    public OrderInvoiceDialog(String id, Order order) {
        super(id);
        setContent(new OrderInvoiceDialogPanel(getContentId(), this, order));
        setTitle(new ResourceModel("invoiceDialog.title").getObject());
        setCookieName(null);
        setCssClassName(CSS_CLASS_GRAY);
        setHeightUnit("px");
        setResizable(false);
        setInitialHeight(400);
        setInitialWidth(600);
    }

  @Override
  public void show(AjaxRequestTarget target) {
      super.show(target);
//      target.appendJavaScript("Wicket.Window.create(settings).show()");
  }

    
    protected abstract void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName);

}
