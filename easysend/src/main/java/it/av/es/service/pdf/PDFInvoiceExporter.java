package it.av.es.service.pdf;

import it.av.es.model.Order;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;

import java.io.InputStream;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;

/**
 * Utility to generate a PDF
 * 
 * @author Alessandro Vincelli
 *
 */
public interface PDFInvoiceExporter {

	/**
	 * return a stream of bytes that represents an Invoice for the given order  
	 * 
	 * @param order order
	 * @param user user that creates the pdf
	 * @param project project
	 * @param localizer
	 * @param component
	 * @param orderService
	 * @return an InputStream that represent the PDF
	 */
	InputStream createInvoice(Order order, User user, Project project, Localizer localizer, Component component, OrderService orderService);
}
