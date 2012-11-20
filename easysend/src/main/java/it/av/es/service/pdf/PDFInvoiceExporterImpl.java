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

import org.apache.wicket.model.ResourceModel;

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
    private OrderService orderService;

    @Override
    public InputStream createInvoice(Order order, User user, Project project, OrderService orderService) {
        this.order = order;
        this.user = user;
        this.project = project;
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
            //pdfWriter.setPDFXConformance(PdfWriter.PDFX32002);
            document.open();

            //Image on top
            String logoLocation = new ResourceModel("images.printlogo").getObject();
            Image gif = Image.getInstance(this.getClass().getResource(logoLocation));
            gif.setAlignment(Element.ALIGN_CENTER);
            gif.scalePercent(50);
            document.add(gif);
            
            // Table with Message Sender, Subject, Date
            
//            PdfPTable tableHeader1 = new PdfPTable(1);
//            tableHeader1.setWidthPercentage(100f);
//            tableHeader1.getDefaultCell().setBorder(0);
//            //table.setWidthPercentage(288 / 5.23f);
//            tableHeader1.setHorizontalAlignment(Element.ALIGN_LEFT);
//            tableHeader1.addCell(new Phrase("Luxury Drink Italy s.r.l.", fontBold));
//            document.add(tableHeader1);
            
            PdfPTable table = new PdfPTable(new float[] {1.3f,5,1.3f,5,1,2,2});
            table.setWidthPercentage(100f);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setPaddingBottom(0);
            table.getDefaultCell().setPaddingTop(0);
            
            //first row
            PdfPCell cell = builderEmptySpanCell(3);
            cell.setPhrase(new Phrase("Luxury Drink Italy s.r.l.", fontBold));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(4));

            //second row
            table.addCell(builderEmptySpanCell(5));
            cell = builderEmptySpanCell(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPhrase(new Phrase("Invoice", fontSmall));
            table.addCell(cell);
            table.addCell(new Phrase("123", fontSmall));
            
            //third row
            cell = builderEmptySpanCell(3);
            cell.setPhrase(new Phrase("VIA PASSIONE 1, 20122 MILANO", fontSmall));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(4));
            
            // 4row
            cell = builderEmptySpanCell(3);
            cell.setPhrase(new Phrase("ITALIA", fontSmall));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(3));
            table.addCell(new Phrase("Date", fontSmall));
            
            // 5row
            table.addCell(builderEmptySpanCell(6));
            table.addCell(new Phrase("10.10.2012", fontSmall));
            
            // 7row
            cell = builderEmptySpanCell(7);
            cell.setPhrase(new Phrase(" ", fontSmall));
            table.addCell(cell);
            
            // 8row
            table.addCell(builderEmptySpanCell(6));
            table.addCell(new Phrase("Due Date", fontSmall));
            
            // 9row
            table.addCell(builderEmptySpanCell(6));
            table.addCell(new Phrase("10.10.2012", fontSmall));
            
            // 10row
            table.addCell(new Phrase("Spedito a", fontSmall));
            table.addCell(new Phrase("Alessandro Vincelli", fontSmall));
            
            table.addCell(new Phrase("Fatturato a", fontSmall));
            table.addCell(new Phrase("Alessandro Vincelli", fontSmall));
            table.addCell(builderEmptySpanCell(3));

            document.add(table);
//            
//            PdfPTable tableHeader3 = new PdfPTable(1);
//            tableHeader3.getDefaultCell().setBorder(0);
//            tableHeader3.getDefaultCell().setPaddingLeft(0);
//            tableHeader3.setWidthPercentage(100f);
//            tableHeader3.getDefaultCell().setPaddingLeft(0);
//            tableHeader3.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            tableHeader3.addCell(new Phrase("Luxury Drink Italy s.r.l", fontSmall));
//            document.add(tableHeader3);
//            
//            
//            PdfPTable tableHeader4 = new PdfPTable(1);
//            tableHeader4.setWidthPercentage(100f);
//            tableHeader4.getDefaultCell().setPaddingLeft(0);
//            tableHeader4.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            tableHeader4.addCell(new Phrase("via passione 1", fontSmall));
//            document.add(tableHeader4);

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
    
    private PdfPCell builderEmptySpanCell(int i){
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setColspan(i);
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
