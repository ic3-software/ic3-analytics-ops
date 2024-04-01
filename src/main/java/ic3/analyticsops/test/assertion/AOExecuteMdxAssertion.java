package ic3.analyticsops.test.assertion;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxError;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxResult;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTable;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTableCellColumn;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTableCellError;
import ic3.analyticsops.restapi.request.AORestApiExecuteMdxRequest;
import ic3.analyticsops.test.AOTaskContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AOExecuteMdxAssertion extends AOAssertion
{
    @Nullable
    private Equals equals;

    @Nullable
    private CellEquals cellEquals;

    @Nullable
    private StatementOnError statementOnError;

    @Nullable
    private CellOnError cellOnError;

    public void assertOk(AOTaskContext context, String schema, AORestApiMdxScriptResult actualResult)
            throws AORestApiException
    {
        if (equals != null)
        {
            equals.assertOk(context, schema, assertOnlyDataset(actualResult));
        }
        else if (cellEquals != null)
        {
            cellEquals.assertOk(context, schema, assertOnlyDataset(actualResult));
        }
        else if (statementOnError != null)
        {
            statementOnError.assertOk(context, assertOnlyError(actualResult));
        }
        else if (cellOnError != null)
        {
            cellOnError.assertOk(context, assertOnlyError(actualResult));
        }
        else
        {
            throw new RuntimeException("unexpected missing assertion");
        }
    }

    private static AORestApiMdxTidyTable assertOnlyDataset(AORestApiMdxScriptResult actualResult)
    {
        if (actualResult.results == null)
        {
            throw new AssertionError("missing results");
        }

        assertEquals("result size", 1, actualResult.results.size());

        return (AORestApiMdxTidyTable) actualResult.results.getFirst().dataSet;
    }

    private static AORestApiMdxError assertOnlyError(AORestApiMdxScriptResult actualResult)
    {
        if (actualResult.error != null)
        {
            return actualResult.error;
        }

        if (actualResult.results == null)
        {
            throw new AssertionError("missing results");
        }

        assertEquals("result size", 1, actualResult.results.size());

        final AORestApiMdxResult result = actualResult.results.getFirst();

        if (result.error != null)
        {
            return result.error;
        }

        final AORestApiMdxTidyTable dataSet = (AORestApiMdxTidyTable) result.dataSet;

        if (dataSet == null)
        {
            throw new AssertionError("missing result dataset");
        }

        final List<AORestApiMdxTidyTableCellColumn> columns = dataSet.getCellColumns();

        if (columns.size() != 1)
        {
            throw new AssertionError("unexpected result cell-columns count : " + columns.size());
        }

        final AORestApiMdxTidyTableCellError error = columns.getFirst().getError(0);

        if (error == null)
        {
            throw new AssertionError("unexpected missing result cell error");
        }

        return new AORestApiMdxError(error.errorCode, error.errorDescription);
    }

    /**
     * The both results are strictly the same.
     */
    static class Equals
    {
        private String statement;

        public void assertOk(AOTaskContext context, String schema, AORestApiMdxTidyTable actualResult)
                throws AORestApiException
        {
            final AORestApiMdxScriptResult reply = context.sendRequest(

                    new AORestApiExecuteMdxRequest()
                            .schema(schema)
                            .mdx(statement)

            );

            actualResult.assertEquals(assertOnlyDataset(reply));
        }
    }

    /**
     * Only the content of the cells are the same : member columns, name of the columns are ignored.
     */
    static class CellEquals
    {
        private String statement;

        public void assertOk(AOTaskContext context, String schema, AORestApiMdxTidyTable actualResult)
                throws AORestApiException
        {
            final AORestApiMdxScriptResult reply = context.sendRequest(

                    new AORestApiExecuteMdxRequest()
                            .schema(schema)
                            .mdx(statement)

            );

            actualResult.assertCellEquals(assertOnlyDataset(reply));
        }
    }

    /**
     * The result must generate an expected error.
     */
    static class StatementOnError
    {
        private String errorCode;

        public void assertOk(AOTaskContext context, AORestApiMdxError actualResult)
        {
            AOAssertion.assertEquals("statement-error", errorCode, actualResult.errorCode);
        }
    }

    /**
     * The result must generate a single cell with an expected error.
     */
    static class CellOnError
    {
        private String errorCode;

        public void assertOk(AOTaskContext context, AORestApiMdxError actualResult)
        {
            AOAssertion.assertEquals("cell-error", errorCode, actualResult.errorCode);
        }
    }
}
