package ic3.analyticsops.test.task.reporting;

import ic3.analyticsops.common.AOException;
import ic3.analyticsops.restapi.reply.print.AORestApiPrintedReport;
import ic3.analyticsops.restapi.request.AORestApiPrintReportRequest;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTask;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

public class AOPrintReportTask extends AOTask
{
    private final String reportPath;

    @Nullable
    private final Boolean withPDF;

    @Nullable
    private final String pageSize;

    @Nullable
    private final String pageOrientation;

    @Nullable
    private final Integer timeoutS;

    protected AOPrintReportTask()
    {
        // JSON deserialization

        this.reportPath = null;
        this.withPDF = null;
        this.pageSize = null;
        this.pageOrientation = null;
        this.timeoutS = null;
    }

    @Override
    public void validateProps()
            throws AOTestValidationException
    {
        super.validateProps();

        validateNonEmptyField(validateFieldPathPrefix() + "reportPath", reportPath);
    }

    @Override
    public String getKind()
    {
        return "PrintReport";
    }

    @Override
    public boolean withAssertions()
    {
        return false;
    }

    public void run(AOTaskContext context)
            throws AOException
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
