package ic3.analyticsops.restapi.request;

import ic3.analyticsops.restapi.reply.print.AORestApiPrintedReport;

public class AORestApiPrintReportRequest extends AORestApiTenantRequest<AORestApiPrintedReport>
{
    public AORestApiPrintReportRequest()
    {
        super(URL_CONSOLE + "/ReportingPrintReport", AORestApiPrintedReport.class);
    }

    public AORestApiPrintReportRequest reportPath(String reportPath)
    {
        return (AORestApiPrintReportRequest) addParam("reportPath", reportPath);
    }

    public AORestApiPrintReportRequest withPDF(boolean withPDF)
    {
        return (AORestApiPrintReportRequest) addParam("withPDF", String.valueOf(withPDF));
    }

    public AORestApiPrintReportRequest pageSize(String pageSize)
    {
        return (AORestApiPrintReportRequest) addParam("pageSize", pageSize);
    }

    public AORestApiPrintReportRequest pageOrientation(String pageOrientation)
    {
        return (AORestApiPrintReportRequest) addParam("pageOrientation", pageOrientation);
    }

    public AORestApiPrintReportRequest timeoutS(int timeoutS)
    {
        return (AORestApiPrintReportRequest) addParam("timeoutS", String.valueOf(timeoutS));
    }

}
