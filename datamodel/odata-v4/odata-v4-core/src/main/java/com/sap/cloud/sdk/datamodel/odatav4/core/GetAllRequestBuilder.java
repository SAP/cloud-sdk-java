package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FieldOrdering;
import com.sap.cloud.sdk.datamodel.odatav4.expression.FilterableBoolean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of an OData request as a fluent interface for further configuring the request and
 * {@link #execute(Destination) executing} it.
 *
 * @param <EntityT>
 *            The type of the result entity.
 */
@Slf4j
public class GetAllRequestBuilder<EntityT extends VdmEntity<?>>
    extends
    AbstractEntityBasedRequestBuilder<GetAllRequestBuilder<EntityT>, EntityT, List<EntityT>>
    implements
    ProtocolQueryRead<EntityT>,
    ProtocolQueryReadCollection<EntityT>,
    ReadRequestBuilder<List<EntityT>>
{
    private final NavigationPropertyCollectionQuery<EntityT, EntityT> delegateQuery;

    @Getter( AccessLevel.PROTECTED )
    @Nonnull
    private final Class<EntityT> entityClass;

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityClass
     *            The expected entity type.
     * @param entityCollection
     *            The entity collection
     */
    public GetAllRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final Class<EntityT> entityClass,
        @Nonnull final String entityCollection )
    {
        this(servicePath, ODataResourcePath.of(entityCollection), entityClass);
    }

    /**
     * Instantiates this request builder using the given service path to send the requests.
     *
     * @param servicePath
     *            The service path to direct the requests to.
     * @param entityPath
     *            {@link ODataResourcePath} identifying the entity collection to read.
     * @param entityClass
     *            The expected entity type.
     */
    @SuppressWarnings( "this-escape" )
    GetAllRequestBuilder(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath entityPath,
        @Nonnull final Class<EntityT> entityClass )
    {
        super(servicePath, entityPath);
        this.entityClass = entityClass;
        this.delegateQuery = NavigationPropertyCollectionQuery.ofRootQuery(getResourcePath().toString());
    }

    /**
     * Creates an instance of the {@link ODataRequestRead} based on the Entity class.
     * <p>
     * The following settings are used:
     * <ul>
     * <li>the endpoint URL</li>
     * <li>the entity collection name</li>
     * <li>the number of entries to select (top)</li>
     * <li>the number of entries to ignore (skip)</li>
     * <li>the order direction</li>
     * <li>the filters to be applied</li>
     * <li>the fields to be selected</li>
     * </ul>
     *
     * @return An initialized {@link ODataRequestRead}.
     */
    @Override
    @Nonnull
    public ODataRequestRead toRequest()
    {
        final ODataRequestRead request =
            new ODataRequestRead(
                getServicePath(),
                getResourcePath(),
                delegateQuery.getEncodedQueryString(),
                ODataProtocol.V4);

        return super.toRequest(request);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Note:</strong> If the OData service responds with service-driven pagination, then the pages will be
     * iterated automatically. The returned list is an eagerly loaded aggregation of all pages. Access to a lazy loading
     * result-set can be enabled through the modifiers {@link #iteratingEntities()}, {@link #streamingEntities()} and
     * {@link #iteratingPages()}.
     */
    @Override
    @Nonnull
    public List<EntityT> execute( @Nonnull final Destination destination )
    {
        final Iterable<EntityT> iterableItems = iteratingEntities().execute(destination);
        return Lists.newArrayList(iterableItems); // eagerly request and parse all pages of the result-set
    }

    /**
     * Manually explore the individual pages from the result-set. The returning object allows for memory-efficient
     * consumption of all data through server-driven pagination.
     *
     * @return An instance of {@link RequestBuilderExecutable} with a response object to lazily iterate through the
     *         pages of entities.
     */
    @Nonnull
    public RequestBuilderExecutable<Iterable<List<EntityT>>> iteratingPages()
    {
        return this::executeInternal;
    }

    /**
     * Iterate through all entities from the result-set. The individual pages of the result-set are queried lazily. The
     * returning object allows for memory-efficient consumption of all data through server-driven pagination.
     *
     * @return An instance of {@link RequestBuilderExecutable} with a response object to lazily iterate through the
     *         entities.
     */
    @Nonnull
    public RequestBuilderExecutable<Iterable<EntityT>> iteratingEntities()
    {
        // concat applies lazy evaluation so individual pages will still be loaded lazily
        return destination -> Iterables.concat(executeInternal(destination));
    }

    /**
     * Stream through all entities from the result-set. The individual pages of the result-set are queried lazily. The
     * returning object allows for memory-efficient consumption of all data through server-driven pagination.
     *
     * @return An instance of {@link RequestBuilderExecutable} with a response object to lazily iterate through the
     *         entities.
     */
    @Nonnull
    public RequestBuilderExecutable<Stream<EntityT>> streamingEntities()
    {
        // concat applies lazy evaluation so individual pages will still be loaded lazily
        return destination -> Streams.stream(Iterables.concat(executeInternal(destination)));
    }

    /**
     * Set the preferred page size of the OData response. A result-set may be split into multiple pages, each including
     * a subset of the entities matching the query.
     * <p>
     * <strong>Note:</strong> The OData service might ignore the preferred page size setting and may not use pagination
     * at all.
     *
     * @param size
     *            The preferred page size
     * @return This request object with the added parameter.
     */
    @Nonnull
    public GetAllRequestBuilder<EntityT> withPreferredPageSize( final int size )
    {
        return withHeader("Prefer", "odata.maxpagesize=" + size);
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( { "varargs" } )
    public final GetAllRequestBuilder<EntityT> select( @Nonnull final Property<EntityT>... fields )
    {
        delegateQuery.select(fields);
        return this;
    }

    @Override
    @SafeVarargs
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final GetAllRequestBuilder<EntityT> filter( @Nonnull final FilterableBoolean<EntityT>... filters )
    {
        delegateQuery.filter(filters);
        return this;
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<EntityT> top( final int top )
    {
        delegateQuery.top(top);
        return this;
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<EntityT> skip( final int skip )
    {
        delegateQuery.skip(skip);
        return this;
    }

    @SafeVarargs
    @Override
    @Nonnull
    @SuppressWarnings( "varargs" )
    public final GetAllRequestBuilder<EntityT> orderBy( @Nonnull final FieldOrdering<EntityT>... ordering )
    {
        delegateQuery.orderBy(ordering);
        return this;
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<EntityT> search( @Nonnull final String search )
    {
        delegateQuery.search(search);
        return this;
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<EntityT> search( @Nonnull final SearchExpression expression )
    {
        delegateQuery.search(expression);
        return this;
    }

    @Nonnull
    private Iterable<List<EntityT>> executeInternal( @Nonnull final Destination destination )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        return toRequest().execute(httpClient).iteratePages(getEntityClass());
    }

    @Nonnull
    @Override
    public GetAllRequestBuilder<EntityT> withCsrfToken()
    {
        this.csrfTokenRetriever = new DefaultCsrfTokenRetriever();
        return this;
    }
}
