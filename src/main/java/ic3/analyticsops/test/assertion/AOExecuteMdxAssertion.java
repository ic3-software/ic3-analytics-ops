package ic3.analyticsops.test.assertion;

import ic3.analyticsops.restapi.error.AORestApiException;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxError;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxResult;
import ic3.analyticsops.restapi.reply.mdx.AORestApiMdxScriptResult;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTable;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTableCellColumn;
import ic3.analyticsops.restapi.reply.tidy.mdx.AORestApiMdxTidyTableCellError;
import ic3.analyticsops.restapi.request.AORestApiExecuteMdxRequest;
import ic3.analyticsops.test.AOAssertion;
import ic3.analyticsops.test.AOTaskContext;
import ic3.analyticsops.test.AOTestValidationException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AOExecuteMdxAssertion extends AOAssertion
{
    @Nullable
    private final Equals equals;

    @Nullable
    private final CellEquals cellEquals;

    @Nullable
    private final StatementOnError statementOnError;

    @Nullable
    private final CellOnError cellOnError;

    protected AOExecuteMdxAssertion()
    {
        // JSON deserialization

        this.equals = null;
        this.cellEquals = null;
        this.statementOnError = null;
        this.cellOnError = null;
    }

    @Override
    public void validate()
            throws AOTestValidationException
    {
        super.validate();

        validateNonEmptyFields(validateFieldPathPrefix() + "equals|cellEquals|statementOnError|cellOnError",
                equals,
                cellEquals,
                statementOnError,
                cellOnError
        );

        if (equals != null)
        {
            validateNonEmptyField(validateFieldPathPrefix() + "equals.statement", equals.statement);
        }
        else if (cellEquals != null)
        {
            validateNonEmptyField(validateFieldPathPrefix() + "cellEquals.statement", cellEquals.statement);
        }
        else if (statementOnError != null)
        {
            validateNonEmptyField(validateFieldPathPrefix() + "statementOnError.errorCode", statementOnError.errorCode);
        }
        else if (cellOnError != null)
        {
            validateNonEmptyField(validateFieldPathPrefix() + "cellOnError.errorCode", cellOnError.errorCode);
        }
    }

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
        private final String statement;

        protected Equals()
        {
            // JSON deserialization

            this.statement = null;
        }

        public void assertOk(AOTaskContext context, String schema, AORestApiMdxTidyTable actualResult)
                throws AORestApiException
        {
            final AORestApiMdxScriptResult reply = context.sendRequest(

                    new AORestApiExecuteMdxRequest()
                            .schema(schema)
                            .mdx(statement)

            );

            actualResult.assertEquals(assertOnlyDataset(reply), 0);
        }
    }

    /**
     * Only the content of the cells are the same : member columns, name of the columns are ignored.
     */
    static class CellEquals
    {
        private final String statement;

        protected CellEquals()
        {
            // JSON deserialization

            this.statement = null;
        }

        public void assertOk(AOTaskContext context, String schema, AORestApiMdxTidyTable actualResult)
                throws AORestApiException
        {
            final AORestApiMdxScriptResult reply = context.sendRequest(

                    new AORestApiExecuteMdxRequest()
                            .schema(schema)
                            .mdx(statement)

            );

            actualResult.assertCellEquals(assertOnlyDataset(reply), 0);
        }
    }

    /**
     * The statement must generate an expected error.
     */
    static class StatementOnError
    {
        private final String errorCode;

        protected StatementOnError()
        {
            // JSON deserialization

            this.errorCode = null;
        }

        public void assertOk(AOTaskContext context, AORestApiMdxError actualResult)
        {
            AOAssertion.assertEquals("statement-error", errorCode, actualResult.errorCode);
        }
    }

    /**
     * The statement must generate a single cell with an expected error.
     */
    static class CellOnError
    {
        private final String errorCode;

        protected CellOnError()
        {
            // JSON deserialization

            this.errorCode = null;
        }

        public void assertOk(AOTaskContext context, AORestApiMdxError actualResult)
        {
            AOAssertion.assertEquals("cell-error", errorCode, actualResult.errorCode);
        }
    }
}
