package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
import testcomparison.namespaces.nameclash.TestEntityMultiLink;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkByKeyFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkCreateFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkDeleteFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityMultiLinkUpdateFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2;
import testcomparison.namespaces.nameclash.TestEntityV2ByKeyFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2CreateFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2DeleteFluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2FluentHelper;
import testcomparison.namespaces.nameclash.TestEntityV2UpdateFluentHelper;
import testcomparison.namespaces.nameclash.batch.DefaultNameClashServiceBatch;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>name-clash</td></tr></table>
 *
 */
public class DefaultNameClashService
    implements NameClashService
{

    @Nonnull
    private final String servicePath;

    /**
     * Creates a service using {@link NameClashService#DEFAULT_SERVICE_PATH} to send the requests.
     *
     */
    public DefaultNameClashService() {
        servicePath = NameClashService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     *
     */
    private DefaultNameClashService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultNameClashService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultNameClashService(servicePath);
    }

    @Override
    @Nonnull
    public DefaultNameClashServiceBatch batch() {
        return new DefaultNameClashServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public TestEntityV2FluentHelper getAllTestEntity() {
        return new TestEntityV2FluentHelper(servicePath, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2ByKeyFluentHelper getTestEntityByKey(final UUID keyPropertyGuid) {
        return new TestEntityV2ByKeyFluentHelper(servicePath, "A_TestEntity", keyPropertyGuid);
    }

    @Override
    @Nonnull
    public TestEntityV2CreateFluentHelper createTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2CreateFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2UpdateFluentHelper updateTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2UpdateFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityV2DeleteFluentHelper deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return new TestEntityV2DeleteFluentHelper(servicePath, testEntityV2, "A_TestEntity");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkFluentHelper getAllTestEntityMultiLink() {
        return new TestEntityMultiLinkFluentHelper(servicePath, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkByKeyFluentHelper getTestEntityMultiLinkByKey(final String keyProperty) {
        return new TestEntityMultiLinkByKeyFluentHelper(servicePath, "A_TestEntityMultiLink", keyProperty);
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkCreateFluentHelper createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new TestEntityMultiLinkCreateFluentHelper(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkUpdateFluentHelper updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new TestEntityMultiLinkUpdateFluentHelper(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

    @Override
    @Nonnull
    public TestEntityMultiLinkDeleteFluentHelper deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return new TestEntityMultiLinkDeleteFluentHelper(servicePath, testEntityMultiLink, "A_TestEntityMultiLink");
    }

}
