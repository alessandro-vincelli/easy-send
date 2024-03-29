package it.av.es.service.pdf;

import it.av.es.model.Order;
import it.av.es.model.ProductOrdered;
import it.av.es.model.Project;
import it.av.es.model.User;
import it.av.es.service.OrderService;
import it.av.es.util.DateUtil;
import it.av.es.util.NumberUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.Date;
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

/**
 * Utility class to generate a PDF from a Message.
 * It uses itext to generate the PDF, <a href="http://itextdocs.lowagie.com/">http://itextdocs.lowagie.com</a>
 * 
 * @author Alessandro Vincelli
 *
 */
public final class PDFExporterImpl implements PDFExporter {

    private Font fontNormal;
    private Font fontSmall;
    private Font fontBold;
    private OrderService orderService;

    /**
     * {@inheritDoc}
     */
    public final InputStream exportOrdersList(List<Order> orders, Date date, User user, Project project, OrderService orderService) {
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
            String logoLocation = new ResourceModel("images.printlogoEasytrack").getObject();
            Image gif = Image.getInstance(this.getClass().getResource(logoLocation));
            gif.setAlignment(Element.ALIGN_CENTER);
            gif.scalePercent(50);
            document.add(gif);

            // Table with Message Sender, Subject, Date
            float[] widths = { 0.6f, 2f, 0.4f, 2f };
            PdfPTable tableHeader = new PdfPTable(4);
            tableHeader.setWidthPercentage(100f);
            //table.setWidthPercentage(288 / 5.23f);
            tableHeader.setWidths(widths);
            tableHeader.getDefaultCell().setBorder(1);
            tableHeader.getDefaultCell().setPaddingLeft(0);
            tableHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableHeader.addCell(new Phrase(new ResourceModel("pdfOrder.operator").getObject(), fontSmall));
            tableHeader.addCell(new Phrase(user.getFirstname() + " " + user.getLastname(), fontSmall));
            tableHeader.addCell(new Phrase(new ResourceModel("pdfOrder.date").getObject(), fontSmall));
            tableHeader.addCell(new Phrase(DateUtil.SDF2SHOW.print(new Date().getTime()), fontSmall));
            document.add(tableHeader);
            
            
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100f);
            float[] widthsTabel = { 0.15f, 0.15f, 0.15f, 0.15f, 0.3f, 0.6f, 0.6f };
            //table.setWidthPercentage(288 / 5.23f);
            table.setWidths(widthsTabel);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setPaddingLeft(0);
            table.getDefaultCell().setPaddingTop(3);
            table.getDefaultCell().setPaddingBottom(3);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            
          
            PdfPCell h1Cell2 = builderNormalHLeft(project.getName() + " - " + new ResourceModel("pdfOrder.ordersDate").getObject() + ": "+ DateUtil.SDF2SHOWDATE.print(date.getTime()));
            h1Cell2.setColspan(7);
            h1Cell2.setPadding(3);
            table.addCell(h1Cell2);
            table.getDefaultCell().setBorder(1);
            table.addCell(builderNormalHCenter(new ResourceModel("pdfOrder.number").getObject()));
            table.addCell(builderNormalHCenter(new ResourceModel("pdfOrder.packs").getObject()));
            table.addCell(builderNormalHCenter(new ResourceModel("pdfOrder.kilos").getObject()));
            //table.addCell(builderNormalHCenter(localizer.getString("pdfOrder.pairs", component)));
            table.addCell(builderNormalHCenter(new ResourceModel("pdfOrder.volume").getObject()));
            table.addCell(builderNormalHCenter(new ResourceModel("pdfOrder.commodity").getObject()));
            //table.addCell(builderNormalHCenter(localizer.getString("pdfOrder.shipr", component)));
            table.addCell(builderNormalHCenter(new ResourceModel("pdfOrder.cnee").getObject()));
            //table.addCell(builderNormalHCenter(localizer.getString("pdfOrder.terms", component)));
            table.addCell(builderNormalHCenter(new ResourceModel("pdfOrder.note").getObject()));
            
            
            table.addCell(builderNormalHRight(new ResourceModel("pdfOrder.total").getObject()));
            table.addCell(builderNormalHRight(Integer.toString(totalPacks(orders))));
            table.addCell(builderNormalHRight(NumberUtil.getItalian().format(totalWeight(orders))));
            //table.addCell(builderNormalHRight(NumberUtil.getItalian().format(totalItemInside(orders))));
            table.addCell(builderNormalHRight(NumberUtil.getItalianTwoFractionDigits().format(totalVolumes(orders))));
            PdfPCell fCell = builderNormalHRight("");
            fCell.setColspan(3);
            table.addCell(fCell);
            
            table.getDefaultCell().setColspan(1);
            //table.getDefaultCell().setBackgroundColor(null);
            // There are three special rows
            table.setHeaderRows(3);
            // One of them is a footer
            table.setFooterRows(1);
            

            for (Order o : orders) {
                table.addCell(builderNormalRight(o.getReferenceNumber().toString()));
                
                StringBuffer packs = new StringBuffer();
                StringBuffer kilos = new StringBuffer();
                StringBuffer pairs = new StringBuffer();
                StringBuffer volume = new StringBuffer();
                StringBuffer commodity = new StringBuffer();
                for (ProductOrdered po : o.getProductsOrdered()) {
                    packs.append(po.getNumber() + "\n");
                    kilos.append(NumberUtil.getItalian().format(po.getTotalWeight()) + "\n");
                    volume.append(NumberUtil.getItalianTwoFractionDigits().format(po.getTotalVolume()) + "\n");
                    pairs.append(po.getTotalItemsInside() + "\n");
                    commodity.append(po.getProduct().getShortName()+ "\n");
                }
                
                table.addCell(builderNormalRight(packs.toString()));
                table.addCell(builderNormalRight(kilos.toString()));
                //table.addCell(builderNormalRight(pairs.toString()));
                table.addCell(builderNormalRight(volume.toString()));
                table.addCell(builderNormalLeft(commodity.toString()));
                //table.addCell(builderNormalLeft(o.getUserAddressForDisplay()));
                table.addCell(builderNormalLeft(o.getCustomerAddressForDisplay()));
                //table.addCell(builderNormalCenter(localizer.getString(o.getPaymentType().name()+"-short", component)));
                table.addCell(builderSmallFontLeft(orderService.getNotesForPDF(o)));                

            }
            
            document.add(table);
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