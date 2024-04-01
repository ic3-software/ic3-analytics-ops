package ic3.analyticsops.test.task.reporting;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.print.AORestApiPrintedReport;
import ic3.analyticsops.restapi.request.AORestApiPrintReportRequest;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.assertion.AOAssertion;
import org.jetbrains.annotations.Nullable;

public class AOPrintReportTask extends AOTask
{
    private String reportPath;

    @Nullable
    private Boolean withPDF;

    @Nullable
    private String pageSize;

    @Nullable
    private String pageOrientation;

    @Nullable
    private Integer timeoutS;

    @Override
    public String getKind()
    {
        return "PrintReport";
    }

    public void run(AOTaskContext context)
            throws AORestApiException
    {
        final AORestApiPrintedReport reply = context.sendRequest(

                new AORestApiPrintReportRequest()
                        .reportPath(reportPath)
                        .withPDF(withPDF != null ? withPDF : false)
                        .pageSize(pageSize != null ? pageSize : "A4")
                        .pageOrientation(pageOrientation != null ? pageOrientation : "portrait")
                        .timeoutS(timeoutS != null ? timeoutS : 60)

        );

        AOAssertion.assertTrue("print-report-status", reply.done);

        if (withPDF)
        {
            AOAssertion.assertNotNull("print-report-pdf-name", reply.pdfName);
            AOAssertion.assertNotNull("print-report-pdf-content", reply.pdfContent);

//            try(final OutputStream out = new BufferedOutputStream(new FileOutputStream("/tmp/" + reply.pdfName)))
//            {
//                out.write(reply.pdfContent);
//            }
//            catch (IOException ex)
//            {
//                throw new AORestApiException("ouch!", ex);
//            }
        }
    }

}
