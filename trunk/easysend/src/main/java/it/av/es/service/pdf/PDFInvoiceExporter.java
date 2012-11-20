package it.av.es.service.pdf;

import it.av.es.model.Order;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;

import java.io.InputStream;

/**
 * Utility to generate a PDF invoice
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
	 * @param orderService
	 * @return an InputStream that represent the PDF
	 */
	InputStream createInvoice(Order order, User user, Project project, OrderService orderService);
}
