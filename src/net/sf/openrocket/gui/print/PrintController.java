/*
 * PrintController.java
 *
 */
package net.sf.openrocket.gui.print;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfWriter;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.gui.print.visitor.FinSetVisitorStrategy;
import net.sf.openrocket.gui.print.visitor.PartsDetailVisitorStrategy;
import net.sf.openrocket.rocketcomponent.ComponentVisitor;

import javax.print.attribute.standard.MediaSizeName;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * This is the main active object for printing.  It performs all actions necessary to create and populate the print
 * file.
 */
public class PrintController {

    /**
     * Print the selected components to a PDF document.
     *
     * @param doc         the OR document
     * @param toBePrinted the user chosen items to print
     * @param outputFile  the file being written to
     * @param msn         the paper size
     */
    public void print (OpenRocketDocument doc, Iterator<PrintableContext> toBePrinted, OutputStream outputFile, MediaSizeName msn) {

        Document idoc = new Document(convertWithDefault(msn));
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(idoc, outputFile);
            writer.setStrictImageSequence(true);

            writer.addViewerPreference(PdfName.PRINTSCALING, PdfName.NONE);
            writer.addViewerPreference(PdfName.PICKTRAYBYPDFSIZE, PdfBoolean.PDFTRUE);
            try {
                idoc.open();
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
            while (toBePrinted.hasNext()) {
                PrintableContext printableContext = toBePrinted.next();

                Set<Integer> stages = printableContext.getStageNumber();

                switch (printableContext.getPrintable()) {
                    case DESIGN_REPORT:
                        DesignReport dp = new DesignReport(doc, idoc);
                        dp.print(writer);
                        idoc.newPage();
                        break;
                    case FIN_TEMPLATE:
                        final ComponentVisitor finVisitor = new ComponentVisitor(new FinSetVisitorStrategy(idoc,
                                                                                                           writer,
                                                                                                           stages));
                        finVisitor.visit(doc.getRocket());
                        finVisitor.close();
                        break;
                    case PARTS_DETAIL:
                        final ComponentVisitor detailVisitor = new ComponentVisitor(new PartsDetailVisitorStrategy(idoc,
                                                                                                                   writer,
                                                                                                                   stages));
                        detailVisitor.visit(doc.getRocket());
                        detailVisitor.close();
                        idoc.newPage();
                        break;
                    /*     case PARTS_LIST:
                            final ComponentVisitor partsVisitor = new ComponentVisitor(new PartsListVisitorStrategy(idoc,
                                                                                                                    writer,
                                                                                                                    stages));
                            partsVisitor.visit(doc.getRocket());
                            partsVisitor.close();
                            idoc.newPage();
                            break;
                    */
                }
            }
            //Stupid iText throws a really nasty exception if there is no data when close is called.
            if (writer.getCurrentDocumentSize() <= 140) {
                writer.setPageEmpty(false);
            }
            writer.close();
            idoc.close();
        }
        catch (DocumentException e) {
        }
        catch (ExceptionConverter ec) {
        }
/*        finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException e) {
                }
            }
        }
  */
    }

    private Rectangle convertWithDefault (final MediaSizeName msn) {
        Rectangle result = PaperSize.convert(msn);
        if (result == null) {
            result = PaperSize.convert(PrintUtilities.getDefaultMedia().getMediaSizeName());
        }
        return result;
    }

}
