package it.av.es.service.pdf;

import it.av.es.model.Order;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;

/**
 * Utility to generate a PDF
 * 
 * @author Alessandro Vincelli
 *
 */
public interface PDFExporter {

	/**
	 * return a stream of bytes that represents the PDF version of the given message  
	 * 
	 * @param orders orders to export
	 * @param date date of orders
	 * @param user user that creates the pdf
	 * @param project project
	 * @param localizer
	 * @param component
	 * @param orderService
	 * @return an InputStream that represent the PDF
	 */
	InputStream exportOrdersList(List<Order> orders, Date date, User user, Project project, Localizer localizer, Component component, OrderService orderService);
}
