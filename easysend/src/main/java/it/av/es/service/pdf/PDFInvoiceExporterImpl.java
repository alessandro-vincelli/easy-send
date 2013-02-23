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
            String logoLocation = new ResourceModel("images.printlogoLuxuryDrink").getObject();
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
            table.getDefaultCell().setPaddingBottom(1);
            table.getDefaultCell().setPaddingTop(1);
            
            //first row
            PdfPCell cell = builderEmptySpanCell(3);
            cell.setPhrase(new Phrase("Luxury Drink Italy s.r.l.", fontBold));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(4));

            //second row
            table.addCell(builderEmptySpanCell(5));
            cell = builderEmptySpanCell(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPhrase(new Phrase(new ResourceModel("invoice").getObject(), fontSmall));
            table.addCell(cell);
            table.addCell(new Phrase(order.getInvoiceNumberAndYear(), fontSmall));
            
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
            table.addCell(new Phrase("Data", fontSmall));
            
            // 4 + 1row
            cell = builderEmptySpanCell(3);
            cell.setPhrase(new Phrase("P. IVA 07993140966", fontSmall));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(3));
            table.addCell(new Phrase(DateUtil.SDF2SHOWDATEINVOICE.print(order.getInvoiceDate().getTime()), fontSmall));

            
            //5 row
            cell = builderEmptySpanCell(3);
            cell.setPhrase(new Phrase("COORDINATE BANCARIE:", fontSmall));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(4));
            //5 row
            cell = builderEmptySpanCell(3);
            cell.setPhrase(new Phrase("Intestatario: LUXURY DRINKS ITALIA SRL", fontSmall));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(4));
            //5 row
            cell = builderEmptySpanCell(2);
            cell.setPhrase(new Phrase("IBAN: IT90N0611049800000081139980", fontSmall));
            table.addCell(cell);
            cell = builderEmptySpanCell(2);
            cell.setPhrase(new Phrase("BIC: RICAIT3C035", fontSmall));
            table.addCell(cell);
            table.addCell(builderEmptySpanCell(2));
            table.addCell(new Phrase("Pagamento Previsto", fontSmall));
            
            //5 row
            table.addCell(builderEmptySpanCell(6));
            table.addCell(new Phrase(DateUtil.SDF2SHOWDATEINVOICE.print(order.getInvoiceDueDate().getTime()), fontSmall));
            
            // 7row
            cell = builderEmptySpanCell(7);
            cell.setPhrase(new Phrase(" ", fontSmall));
            table.addCell(cell);
            
            

            
            // 9+1row Numero Ordine
            table.addCell(new Phrase("Ordine n.:", fontSmall));
            table.addCell(new Phrase(order.getReferenceNumber().toString(), fontSmall));
            table.addCell(builderEmptySpanCell(5));
            
            // 10row
            table.addCell(new Phrase("Spedito a", fontSmall));
            table.addCell(new Phrase(order.getCustomer().getCorporateName().toUpperCase(), fontSmall));
            table.addCell(new Phrase("Fatturato a", fontSmall));
            table.addCell(new Phrase(order.getCustomer().getCorporateName().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(3));
            
            // 11row
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getShippingAddress().getName().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getInvoiceAddress().getName().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(3));
            
            // 12row
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getShippingAddress().getAddress().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getInvoiceAddress().getAddress().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(3));
            
            // 13row
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getShippingAddress().getCity().getName().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getInvoiceAddress().getCity().getName().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(3));
            
            // 14row
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getShippingAddress().getZipcode().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase(order.getInvoiceAddress().getZipcode().toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(3));
            
            // 15row
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase("Italia".toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase("Italia".toUpperCase(), fontSmall));
            table.addCell(builderEmptySpanCell(3));
            
            // 15 + 1row
            table.addCell(builderEmptySpanCell(2));
            table.addCell(builderEmptySpanCell(1));
            table.addCell(new Phrase("P. IVA " + order.getCustomer().getPartitaIvaNumber(), fontSmall));
            table.addCell(builderEmptySpanCell(3));
            
            // 16row
            table.addCell(builderEmptySpanCell(6));
            table.addCell(new Phrase("Data consegna", fontSmall));
            
            // 17row
            table.addCell(builderEmptySpanCell(6));
            if(order.getDeliveredTime() != null){
                table.addCell(new Phrase(DateUtil.SDF2SHOWDATEINVOICE.print(order.getDeliveredTime().getTime()), fontSmall));    
            }
            
            cell = builderEmptySpanCell(7);
            cell.setPhrase(new Phrase(" "));
            cell.setPaddingTop(10);
            table.addCell(cell);
            
            document.add(table);
            
            // seconda tabella dati ordini
            PdfPTable table2 = new PdfPTable(new float[] {3f,2f,5,1.3f});
            table2.setWidthPercentage(100f);
            table2.getDefaultCell().setBorder(0);
            table2.getDefaultCell().setPaddingBottom(1);
            table2.getDefaultCell().setPaddingTop(1);
            
            //first row
            cell = builderSmallHLeftBorderTop("NUM. ORDINE");
            cell.setBorder(Rectangle.LEFT +  Rectangle.TOP + Rectangle.RIGHT);
            table2.addCell(cell);
            cell = builderSmallHLeftBorderTop("DATA ORDINE");
            table2.addCell(cell);
            cell = builderSmallHLeftBorderTop("TIPO PAGAMENTO");
            table2.addCell(cell);
            cell = builderSmallHLeftBorderTop("PAGINA");
            cell.setBorder(Rectangle.RIGHT +  Rectangle.TOP);
            table2.addCell(cell);
            
            cell = builderSmallHCenterBorderBottom(order.getReferenceNumber().toString());
            cell.setBorder(Rectangle.LEFT +  Rectangle.BOTTOM + Rectangle.RIGHT);
            table2.addCell(cell);
            cell = builderSmallHCenterBorderBottom(DateUtil.SDF2SHOWDATEINVOICE.print(order.getCreationTime().getTime()));
            table2.addCell(cell);
            cell = builderSmallHCenterBorderBottom(order.getPaymentTypeP().getName());
            table2.addCell(cell);
            cell = builderSmallHCenterBorderBottom("1/1");
            cell.setBorder(Rectangle.RIGHT +  Rectangle.BOTTOM);
            table2.addCell(cell);
            
            document.add(table2);
            
            
         // seconda tabella dati ordini
            PdfPTable table3 = new PdfPTable(new float[] {1f, 6, 2f, 2f, 2f,2f, 2f});
            table3.setWidthPercentage(100f);
            table3.getDefaultCell().setBorder(1);
            table3.getDefaultCell().setPaddingBottom(5);
            table3.getDefaultCell().setPaddingTop(1);
            
            //Item C Material Description Quantity Price Price Unit Value Tax       Rate %
            
            //first row
            cell = builderSmallHLeftBorderBottomBold("Art.");
            cell.setBorder(Rectangle.LEFT +  Rectangle.BOTTOM);
            table3.addCell(cell);
            cell = builderSmallHLeftBorderBottomBold("Descrizione");
            table3.addCell(cell);
            cell = builderSmallHLeftBorderBottomBold("Quantita'");
            table3.addCell(cell);
            cell = builderSmallHLeftBorderBottomBold("Prezzo");
            table3.addCell(cell);
            cell = builderSmallHLeftBorderBottomBold("Sconto %");
            table3.addCell(cell);
            cell = builderSmallHLeftBorderBottomBold("Valore");
            table3.addCell(cell);
            cell = builderSmallHLeftBorderBottomBold("Tasse %");
            cell.setBorder(Rectangle.RIGHT +  Rectangle.BOTTOM);
            table3.addCell(cell);
            
            // 2row articoli
            Integer index = 1; 
            for (ProductOrdered p : order.getProductsOrdered()) {
                cell = builderSmallItemLeft(index.toString());
                cell.setBorder(Rectangle.LEFT);
                table3.addCell(cell);
                cell = builderSmallItemLeft(p.getProduct().getName());
                table3.addCell(cell);
                cell = builderSmallItemLeft(Integer.toString(p.getNumber()));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table3.addCell(cell);
                cell = builderSmallItemLeft(" ");
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table3.addCell(cell);
                cell = builderSmallItemLeft(NumberUtil.getItalianTwoFractionDigits().format(p.getDiscount()));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table3.addCell(cell);
                cell = builderSmallItemLeft(NumberUtil.getItalianTwoFractionDigits().format(p.getAmount()));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table3.addCell(cell);
                cell = builderSmallItemLeft("21,00");
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.RIGHT);
                table3.addCell(cell);
                index = index + 1;
            }

            // costi di spedizione
            cell = builderSmallItemLeft(" ");
            cell.setBorder(Rectangle.LEFT);
            table3.addCell(cell);
            cell = builderSmallItemLeft("Costi di spedizione");
            table3.addCell(cell);
            cell = builderSmallItemLeft("");
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell);
            cell = builderSmallItemLeft(" ");
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell);
            cell = builderSmallItemLeft(" ");
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell);
            cell = builderSmallItemLeft(NumberUtil.getItalianTwoFractionDigits().format(order.getShippingCost()));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell);
            cell = builderSmallItemLeft("21,00");
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.RIGHT);
            table3.addCell(cell);
            index = index + 1;
            
            // 2row sconti
//            cell = builderSmallItemLeft(" ");
//            cell.setBorder(Rectangle.LEFT);
//            table3.addCell(cell);
//            cell = builderSmallItemLeft(" ");
//            table3.addCell(cell);
//            cell = builderSmallItemLeft("Sconto");
//            cell.setColspan(2);
//            table3.addCell(cell);
//            cell = builderSmallItemLeft(" ");
//            table3.addCell(cell);
//            cell = builderSmallItemLeft(" ");
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table3.addCell(cell);
//            cell = builderSmallItemLeft("5 %");
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            cell.setBorder(Rectangle.RIGHT);
//            table3.addCell(cell);
            
            cell = builderEmptySpanCell(7);
            cell.setPhrase(new Phrase(" "));
            cell.setBorder(Rectangle.BOTTOM + Rectangle.LEFT + Rectangle.RIGHT);
            cell.setPaddingTop(10);
            table3.addCell(cell);
            
            // 3row primo totale
            cell = builderSmallItemLeft(" ");
            cell.setBorder(Rectangle.LEFT);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom("Totale articoli");
            cell.setIndent(35);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom(" ");
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom("Valuta");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom("EUR");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom(NumberUtil.getItalianTwoFractionDigits().format(order.getTotalAmount()));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom(" ");
            cell.setBorder(Rectangle.RIGHT + Rectangle.BOTTOM);
            table3.addCell(cell);
            
            // 2row
            cell = builderSmallItemLeft(" ");
            cell.setBorder(Rectangle.LEFT);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom("Tasse");
            cell.setIndent(35);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom("IVA\n\n21%");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom("Base Imponibile\n\n" + NumberUtil.getItalianTwoFractionDigits().format(order.getTotalAmount()));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom(" ");
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom(NumberUtil.getItalianTwoFractionDigits().format(order.getTotalTax()));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell);
            cell = builderSmallItemLeftBorderBottom(" ");
            cell.setBorder(Rectangle.RIGHT + Rectangle.BOTTOM);
            table3.addCell(cell);
            
            
            // 3row totale generale
            cell = builderSmallItemLeft(" ");
            cell.setBorder(Rectangle.LEFT);
            table3.addCell(cell);
            cell = builderSmallItemLeft("Totale finale (tasse incluse)");
            cell.setIndent(35);
            table3.addCell(cell);
            cell = builderSmallItemLeft(" ");
            table3.addCell(cell);
            cell = builderSmallItemLeft("Valuta");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
            cell = builderSmallItemLeft("EUR");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(cell);
            cell = builderSmallItemLeft(NumberUtil.getItalianTwoFractionDigits().format(order.getTotalAmountTaxIncluded()));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell);
            cell = builderSmallItemLeft(" ");
            cell.setBorder(Rectangle.RIGHT);
            table3.addCell(cell);
            
            cell = builderEmptySpanCell(7);
            cell.setPhrase(new Phrase(" "));
            cell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
            cell.setPaddingTop(30);
            table3.addCell(cell);
            
            cell = builderEmptySpanCell(7);
            cell.setBorder(Rectangle.BOTTOM);
            cell.setPaddingTop(30);
            table3.addCell(cell);
            
            document.add(table3);
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
            document.addSubject("Luxury Drink, Fattura n. " + order.getInvoiceNumber() + " del " + DateUtil.SDF2SHOWDATEINVOICE.print(order.getInvoiceDate().getTime()));
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
    
    
    private PdfPCell builderSmallHLeftBorderTop(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontSmall));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.TOP + Rectangle.RIGHT);
        cell.setPaddingTop(1);
        cell.setPaddingBottom(1);
        return cell;
    }
        
    private PdfPCell builderSmallHCenterBorderBottom(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontSmall));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.BOTTOM + Rectangle.RIGHT);
        cell.setPaddingTop(1);
        cell.setPaddingBottom(5);
        return cell;
    }
    
    
    private PdfPCell builderSmallHLeftBorderBottomBold(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontSmall));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(1);
        cell.setPaddingBottom(12);
        cell.setBorderWidthBottom(2);
        return cell;
    }
    
    private PdfPCell builderSmallItemLeft(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontSmall));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        return cell;
    }
    
    private PdfPCell builderSmallItemLeftBorderBottom(String text){
        PdfPCell cell = new PdfPCell(new Phrase(text, fontSmall));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.BOTTOM );
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
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
