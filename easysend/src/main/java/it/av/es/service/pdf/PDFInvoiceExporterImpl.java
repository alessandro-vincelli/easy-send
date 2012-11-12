package it.av.es.service.pdf;

import it.av.es.model.Order;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFInvoiceExporterImpl implements PDFInvoiceExporter {

    private Font fontNormal;
    private Font fontSmall;
    private Font fontBold;
    private Order order;
    private User user;
    private Project project;
    private Localizer localizer;
    private Component component;
    private OrderService orderService;

    @Override
    public InputStream createInvoice(Order order, User user, Project project, Localizer localizer, Component component, OrderService orderService) {
        this.order = order;
        this.user = user;
        this.project = project;
        this.localizer = localizer;
        this.component = component;
        this.orderService = orderService;
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BaseFont basefontNormal = BaseFont.createFont(this.getClass().getResource("Times_New_Roman.ttf").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontNormal = new Font(basefontNormal, 11);
            BaseFont basefontSmall = BaseFont.createFont(this.getClass().getResource("Times_New_Roman.ttf").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontSmall = new Font(basefontSmall, 8);
            BaseFont basefontBold = BaseFont.createFont(this.getClass().getResource("Times_New_Roman_Bold.ttf").getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontBold = new Font(basefontBold, 11);

            PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
            pdfWriter.setPDFXConformance(PdfWriter.PDFX32002);
            document.open();

            //Image on top
            String logoLocation = localizer.getString("images.printlogo", component);
            Image gif = Image.getInstance(this.getClass().getResource(logoLocation));
            gif.setAlignment(Element.ALIGN_CENTER);
            gif.scalePercent(50);
            document.add(gif);
            
            // Table with Message Sender, Subject, Date
            
            PdfPTable tableHeader1 = new PdfPTable(1);
            tableHeader1.setWidthPercentage(100f);
            //table.setWidthPercentage(288 / 5.23f);
            tableHeader1.getDefaultCell().setPaddingLeft(0);
            tableHeader1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableHeader1.addCell(new Phrase("Eurocargo", fontSmall));
            document.add(tableHeader1);
            
            PdfPTable tableHeader2 = new PdfPTable(1);
            tableHeader2.setWidthPercentage(100f);
            tableHeader2.getDefaultCell().setPaddingLeft(0);
            tableHeader2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableHeader2.addCell(new Phrase("Invoice", fontSmall));
            document.add(tableHeader1);
            
            float[] widths = { 6f, 1f};
            PdfPTable tableHeader3 = new PdfPTable(widths);
            tableHeader3.setWidthPercentage(100f);
            tableHeader3.getDefaultCell().setPaddingLeft(0);
            tableHeader3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableHeader3.addCell(new Phrase("Invoice", fontSmall));
            tableHeader3.addCell(new Phrase("Invoice", fontSmall));
            document.add(tableHeader1);

            document.addCreationDate();
            //document.addSubject(message.getSubject());
            document.addCreator("EasyTrack : EuroCargo");
            //Create a PDF/A by adding XMP metadata using the document metadata 
            pdfWriter.createXmpMetadata();
            document.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                baos.close();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private PdfPCell builderNormalHCenter(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontNormal));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.RIGHT + Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.TOP);
        cell.setPaddingTop(3);
        cell.setPaddingBottom(3);
        return cell;
    }
    
    private PdfPCell builderNormalHRight(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontNormal));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.RIGHT + Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.TOP);
        cell.setPaddingTop(3);
        cell.setPaddingBottom(3);
        return cell;
    }
    
    private PdfPCell builderNormalHLeft(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontNormal));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.RIGHT + Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.TOP);
        cell.setPaddingTop(3);
        cell.setPaddingBottom(3);
        return cell;
    }
    
    private PdfPCell builderNormalCenter(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontNormal));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
        cell.setPaddingTop(3);
        cell.setPaddingBottom(3);
        return cell;
    }
    
    private PdfPCell builderNormalRight(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontNormal));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
        cell.setPaddingBottom(0.9f);
        cell.setPaddingTop(3);
        cell.setPaddingBottom(3);
        return cell;
    }
    
    private PdfPCell builderNormalLeft(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontNormal));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
        cell.setPaddingTop(3);
        cell.setPaddingBottom(3);
        return cell;
    }
    
    private PdfPCell builderSmallFontLeft(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontSmall));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
        cell.setPaddingTop(3);
        cell.setPaddingBottom(3);
        return cell;
    }

    private int totalPacks(List<Order> orders){
        int n = 0;
        for (Order order : orders) {
            n = n + order.getNumberOfItemsInProductOrdered();
        }
        return n;
    }
    
    private BigDecimal totalVolumes(List<Order> orders){
        BigDecimal n = BigDecimal.ZERO;
        for (Order order : orders) {
            n = n.add(order.getTotalVolumeInProductOrdered());
        }
        return n;
    }
    
    private BigDecimal totalWeight(List<Order> orders){
        BigDecimal n = BigDecimal.ZERO;
        for (Order order : orders) {
            n = n.add(order.getTotalWeightInProductOrdered());
        }
        return n;
    }
    
    private int totalItemInside(List<Order> orders){
        int n = 0;
        for (Order order : orders) {
            n = n + order.getTotalItemsInsideInProductOrdered();
        }
        return n;
    }
}
