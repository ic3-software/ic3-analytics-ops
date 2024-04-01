package ic3.analyticsops.restapi.reply.schema;

public enum AORestApiSchemaStatus
{
    LOADING,
    // --
    LOADED,
    LOADED_REFRESH_FAILED,
    LOADED_RESTORE_FAILED,
    // --
    UNLOADED,
    UNLOADED_REFRESH_FAILED,
    UNLOADED_RESTORE_FAILED,
    ;

}
