package it.av.es.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * 
 */
public abstract class MessageDialog extends ModalWindow {

    /**
     * Constructor.
     * @param id the markupId, an html div suffice to host a dialog.
     * @param title the title of the dialog 
     * @param string the message to be displayed
     * @param icon the predefined icon to display
     */
    public MessageDialog(String id, String title, String string) {
        super(id);
        setContent(new MessageDialogPanel(getContentId(), this, title, string));
        setTitle(title);
        setCookieName(null);
        setCssClassName(CSS_CLASS_GRAY);
        setHeightUnit("px");
        setResizable(false);
        setInitialHeight(110);
        setInitialWidth(400);
    }

  @Override
  public void show(AjaxRequestTarget target) {
      super.show(target);
//      target.appendJavaScript("Wicket.Window.create(settings).show()");
  }

    
    protected abstract void onCloseDialog(AjaxRequestTarget target, ButtonName buttonName);

}
