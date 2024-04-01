package ic3.analyticsops.restapi.reply.tidy.mdx;

@FunctionalInterface
public interface AORestApiMdxTidyTableMemberInfoSupplier<INFO>
{
    INFO get(AORestApiMdxTidyTableMembersInfo membersInfo, int memberIndex);
}
