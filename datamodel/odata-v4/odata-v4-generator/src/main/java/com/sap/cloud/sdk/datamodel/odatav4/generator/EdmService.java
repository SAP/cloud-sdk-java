package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotatable;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTyped;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.EdmEnumTypeImpl;
import org.apache.olingo.commons.core.edm.primitivetype.AbstractGeospatialType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDate;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.commons.core.edm.primitivetype.EdmTimeOfDay;
import org.slf4j.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

class EdmService implements Service
{
    private static final Logger logger = MessageCollector.getLogger(EdmService.class);

    private static final String[] TERMS_LABEL = { "Common.Label", "SAP__common.Label" };
    private static final String[] TERMS_QUICK_INFO = { "Common.QuickInfo", "SAP__common.QuickInfo" };
    private static final String[] TERMS_DESCRIPTION = { "Core.Description", "SAP__core.Description" };
    private static final String[] TERMS_LONG_DESCRIPTION = { "Core.LongDescription", "SAP__core.LongDescription" };

    private final String name;
    private final PropertiesConfiguration serviceNameMappings;
    private final Edm metadata;
    private final ServiceDetails details;
    private final Function<String, Collection<ApiFunction>> allowedFunctionsByEntity;
    private final boolean hasLinkToApiBusinessHub;
    @Setter( AccessLevel.PACKAGE )
    private boolean generateExplicitDeprecationNotices = false;

    private final Map<String, EntitySet> entitySets = new LinkedHashMap<>();
    private final Multimap<String, ServiceFunction> serviceFunctions =
        MultimapBuilder.linkedHashKeys().linkedListValues().build();
    private final Multimap<String, ServiceBoundFunction> serviceBoundFunctions =
        MultimapBuilder.linkedHashKeys().linkedListValues().build();
    private final Multimap<String, ServiceAction> serviceActions =
        MultimapBuilder.linkedHashKeys().linkedListValues().build();
    private final Multimap<String, ServiceBoundAction> serviceBoundActions =
        MultimapBuilder.linkedHashKeys().linkedListValues().build();

    EdmService(
        final String name,
        final PropertiesConfiguration serviceNameMappings,
        final Edm metadata,
        final ServiceDetails details,
        final Multimap<String, ApiFunction> allowedFunctionsByEntity,
        final boolean hasLinkToApiBusinessHub )
    {
        this.name = name;
        this.serviceNameMappings = serviceNameMappings;
        this.metadata = metadata;
        this.details = details;
        this.allowedFunctionsByEntity = allowedFunctionsByEntity::get;
        this.hasLinkToApiBusinessHub = hasLinkToApiBusinessHub;

        try {
            workaroundOlingoLazyEntityTypeInstantiation(metadata);

            for( final EdmEntitySet entitySet : metadata.getEntityContainer().getEntitySetsWithAnnotations() ) {
                entitySets.put(entitySet.getName(), new EntitySetAdapter(entitySet));
            }
            for( final EdmFunctionImport edmFunctionImport : metadata.getEntityContainer().getFunctionImports() ) {
                for( final EdmFunction edmUnboundFunction : edmFunctionImport.getUnboundFunctions() ) {
                    serviceFunctions
                        .put(
                            edmFunctionImport.getName(),
                            new ServiceFunctionAdapter(edmFunctionImport, edmUnboundFunction));
                }
            }
            for( final EdmActionImport edmActionImport : metadata.getEntityContainer().getActionImports() ) {
                serviceActions
                    .put(
                        edmActionImport.getName(),
                        new ServiceActionAdapter(edmActionImport, edmActionImport.getUnboundAction()));
            }
            for( final EdmFunction edmBoundFunction : metadata.getSchemas().get(0).getFunctions() ) {
                if( edmBoundFunction.isBound() ) {
                    logger.info("Found bound function with name:" + edmBoundFunction.getName());
                    serviceBoundFunctions.put(edmBoundFunction.getName(), new BoundFunctionAdapter(edmBoundFunction));
                }
            }
            for( final EdmAction edmBoundAction : metadata.getSchemas().get(0).getActions() ) {
                if( edmBoundAction.isBound() ) {
                    logger.info("Found bound action with name: {}", edmBoundAction.getName());
                    serviceBoundActions.put(edmBoundAction.getName(), new BoundActionAdapter(edmBoundAction));
                }
            }
        }
        catch( final EdmException e ) {
            throw new ODataGeneratorReadException(e);
        }
    }

    /*
     * Workaround for a bug in Olingo that causes the loss of entity type annotations (such as
     * Core.Description, or SAP__commons.Label).
     * The bug appears if `metadata.getSchemas()` is called before all of the entity types have been
     * accessed like done down below.
     *
     * We discovered this bug when we introduced bound actions and bound functions, which can only be
     * accessed through the underlying `EdmSchema`. Using the schema, however, caused our Javadoc generation
     * to change, since the aforementioned annotations were lost due to the lazy instantiation of the
     * EdmEntityTypes.
     *
     * This workaround forces the instantiation of said EdmEntityTypes by simply accessing them.
     */
    private void workaroundOlingoLazyEntityTypeInstantiation( @Nonnull final Edm metadata )
    {
        for( final EdmEntitySet entitySet : metadata.getEntityContainer().getEntitySets() ) {
            metadata.getEntityType(entitySet.getEntityType().getFullQualifiedName());
        }
    }

    @Override
    public String getTitle()
    {
        final ServiceDetails.Info swaggerInfo = details.getInfo();
        if( swaggerInfo != null && !Strings.isNullOrEmpty(swaggerInfo.getTitle()) ) {
            return swaggerInfo.getTitle();
        } else {
            return NamingUtils.apiNameToServiceTitle(name);
        }
    }

    /**
     * Check if the service is deprecated either based on user input given to mark the entire VDM deprecated or if the
     * service specification contains deprecation notices
     *
     * @return
     */
    @Override
    public boolean isDeprecated()
    {
        return generateExplicitDeprecationNotices || details.isDeprecated();
    }

    @Override
    public Option<DeprecationInfo> getDeprecationInfo()
    {
        if( isDeprecated() ) {
            return details.getStateInfo().map(DefaultDeprecationInfo::new);
        }
        return Option.none();
    }

    @Override
    public String getServiceUrl()
    {
        return details.getServiceUrl();
    }

    @Override
    public EntitySet getEntitySet( final String entitySetName )
    {
        return entitySets.get(entitySetName);
    }

    @Override
    public Collection<EntitySet> getAllEntitySets()
    {
        return entitySets.values();
    }

    @Override
    public Collection<ServiceFunction> getServiceFunction( final String serviceFunctionName )
    {
        return serviceFunctions.get(serviceFunctionName);
    }

    @Override
    public Collection<ServiceFunction> getAllServiceFunctions()
    {
        return serviceFunctions.values();
    }

    @Override
    public Collection<ServiceBoundFunction> getAllServiceBoundFunctions()
    {
        return serviceBoundFunctions.values();
    }

    @Override
    public Collection<ServiceBoundAction> getAllServiceBoundActions()
    {
        return serviceBoundActions.values();
    }

    @Override
    public Collection<ServiceAction> getServiceAction( final String serviceActionName )
    {
        return serviceActions.get(serviceActionName);
    }

    @Override
    public Collection<ServiceAction> getAllServiceActions()
    {
        return serviceActions.values();
    }

    @Override
    public String getJavaPackageName()
    {
        final String javaPackageNameKey = name + SERVICE_MAPPINGS_PACKAGE_SUFFIX;
        String javaPackageName = serviceNameMappings.getString(javaPackageNameKey);

        if( javaPackageName == null ) {
            javaPackageName = NamingUtils.serviceNameToJavaPackageName(getTitle());
        }
        return javaPackageName;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getJavaClassName()
    {
        final String javaClassNameKey = name + SERVICE_MAPPINGS_CLASS_SUFFIX;
        String javaClassName = serviceNameMappings.getString(javaClassNameKey);

        if( javaClassName == null ) {
            javaClassName = NamingUtils.serviceNameToBaseJavaClassName(getTitle());
        }
        return javaClassName;
    }

    @Override
    public String getInfoDescription()
    {
        if( details.getInfo() == null ) {
            return null;
        }
        return details.getInfo().getDescription();
    }

    @Override
    public String getInfoVersion()
    {
        if( details.getInfo() == null ) {
            return null;
        }
        return details.getInfo().getVersion();
    }

    @Override
    public String getMinErpVersion()
    {
        return details.getMinErpVersion();
    }

    @Override
    public String getExternalUrl()
    {
        if( details.getExternalDocs() == null ) {
            return null;
        }
        return details.getExternalDocs().getUrl();
    }

    @Override
    public String getExternalDescription()
    {
        if( details.getExternalDocs() == null ) {
            return null;
        }
        return details.getExternalDocs().getDescription();
    }

    @Override
    public List<ExternalOverview> getExternalOverview()
    {
        final List<? extends ServiceDetails.ExternalOverview> input = details.getExtOverview();
        if( input == null ) {
            return null;
        }

        final List<ExternalOverview> result = new ArrayList<>(input.size());
        for( final ServiceDetails.ExternalOverview value : input ) {
            result.add(new ExternalOverviewAdapter(value));
        }
        return result;
    }

    @Override
    public Collection<ApiFunction> getAllowedFunctionsByEntity( final String entity )
    {
        return allowedFunctionsByEntity.apply(entity);
    }

    @Override
    public boolean hasLinkToApiBusinessHub()
    {
        return hasLinkToApiBusinessHub;
    }

    @RequiredArgsConstructor
    static class DefaultDeprecationInfo implements DeprecationInfo
    {
        static DefaultDeprecationInfo EMPTY = new DefaultDeprecationInfo(new ServiceDetails.StateInfo()
        {
            @Override
            public ServiceDetails.State getState()
            {
                return null;
            }

            @Override
            public String getDeprecationRelease()
            {
                return null;
            }

            @Override
            public String getSuccessorApi()
            {
                return null;
            }

            @Override
            public String getDeprecationDate()
            {
                return null;
            }
        });

        @Nonnull
        private final ServiceDetails.StateInfo details;

        @Override
        public Option<String> getSuccessorApi()
        {
            return Option.of(details.getSuccessorApi());
        }

        @Override
        public Option<String> getDeprecationDate()
        {
            return Option.of(details.getDeprecationDate());
        }

        @Override
        public Option<String> getDeprecationRelease()
        {
            return Option.of(details.getDeprecationRelease());
        }
    }

    private static final class ExternalOverviewAdapter implements ExternalOverview
    {
        private final ServiceDetails.ExternalOverview overview;

        private ExternalOverviewAdapter( @Nonnull final ServiceDetails.ExternalOverview overview )
        {
            this.overview = overview;
        }

        @Override
        public String getName()
        {
            return overview.getName();
        }

        @Override
        public List<String> getValues()
        {
            return overview.getValues();
        }
    }

    private class AnnotationsAdapter implements Annotations
    {
        private final EdmAnnotatable edmAnnotatable;

        protected AnnotationsAdapter( final EdmAnnotatable edmAnnotatable )
        {
            this.edmAnnotatable = edmAnnotatable;
        }

        @Override
        public String getLabel()
        {
            return getAnnotationStringValue(edmAnnotatable, TERMS_LABEL);
        }

        @Override
        public String getQuickInfo()
        {
            return getAnnotationStringValue(edmAnnotatable, TERMS_QUICK_INFO);
        }

        @Override
        public String getDescription()
        {
            return getAnnotationStringValue(edmAnnotatable, TERMS_DESCRIPTION);
        }

        @Override
        public String getLongDescription()
        {
            return getAnnotationStringValue(edmAnnotatable, TERMS_LONG_DESCRIPTION);
        }
    }

    private abstract static class TypeAdapter implements Type
    {
        private final EdmType edmType;

        TypeAdapter( @Nonnull final EdmType edmType )
        {
            this.edmType = edmType;
        }

        @Override
        public String getName()
        {
            try {
                return edmType.getName();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public TypeKind getKind()
        {
            return EdmUtils.convertTypeKind(edmType.getKind());
        }
    }

    private static final class PrimitiveTypeAdapter extends TypeAdapter implements PrimitiveType
    {
        private final EdmPrimitiveType simpleType;

        private PrimitiveTypeAdapter( final EdmPrimitiveType simpleType )
        {
            super(simpleType);
            this.simpleType = simpleType;
        }

        @Override
        public Class<?> getDefaultJavaType()
        {
            if( simpleType instanceof EdmDate ) {
                return LocalDate.class;
            } else if( simpleType instanceof EdmTimeOfDay ) {
                return LocalTime.class;
            } else if( simpleType instanceof EdmDateTimeOffset ) {
                return OffsetDateTime.class;
            } else {
                return simpleType.getDefaultType();
            }
        }

        @Override
        public boolean isSupportedEdmType()
        {
            return !(simpleType instanceof AbstractGeospatialType);
        }
    }

    private abstract class ElementAdapter implements Element
    {
        private final EdmTyped edmTyped;

        ElementAdapter( @Nonnull final EdmTyped edmTyped )
        {
            this.edmTyped = edmTyped;
        }

        @Override
        public Type getType()
        {
            try {
                return convertType(edmTyped.getType());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Multiplicity getMultiplicity()
        {
            try {
                return EdmUtils.convertMultiplicity(edmTyped);
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }
    }

    private static final class ReturnTypeFacetsAdapter implements Facets
    {
        private final EdmReturnType edmReturnType;

        private ReturnTypeFacetsAdapter( @Nonnull final EdmReturnType edmReturnType )
        {
            this.edmReturnType = edmReturnType;
        }

        @Override
        public Boolean isNullable()
        {
            return edmReturnType.isNullable();
        }

        @Override
        public String getDefaultValue()
        {
            return null;
        }

        @Override
        public Integer getMaxLength()
        {
            return edmReturnType.getMaxLength();
        }

        @Override
        public Integer getPrecision()
        {
            return edmReturnType.getPrecision();
        }

        @Override
        public Integer getScale()
        {
            return edmReturnType.getScale();
        }
    }

    private final class ReturnTypeAdapter extends ElementAdapter implements ReturnType
    {
        private final EdmReturnType edmReturnType;

        private ReturnTypeAdapter( final EdmReturnType edmReturnType )
        {
            super(edmReturnType);
            this.edmReturnType = edmReturnType;
        }

        @Override
        public Facets getFacets()
        {
            return new ReturnTypeFacetsAdapter(edmReturnType);
        }
    }

    private static final class ParameterFacetsAdapter implements Facets
    {
        private final EdmParameter edmParameter;

        private ParameterFacetsAdapter( @Nonnull final EdmParameter edmParameter )
        {
            this.edmParameter = edmParameter;
        }

        @Override
        public Boolean isNullable()
        {
            return edmParameter.isNullable();
        }

        @Override
        public String getDefaultValue()
        {
            return null;
        }

        @Override
        public Integer getMaxLength()
        {
            return edmParameter.getMaxLength();
        }

        @Override
        public Integer getPrecision()
        {
            return edmParameter.getPrecision();
        }

        @Override
        public Integer getScale()
        {
            return edmParameter.getScale();
        }
    }

    private final class ParameterAdapter extends ElementAdapter implements Parameter
    {
        private final EdmParameter edmParameter;

        private ParameterAdapter( @Nonnull final EdmParameter edmParameter )
        {
            super(edmParameter);
            this.edmParameter = edmParameter;
        }

        @Override
        public Facets getFacets()
        {
            return new ParameterFacetsAdapter(edmParameter);
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(edmParameter);
        }
    }

    private static final class PropertyFacetsAdapter implements Facets
    {
        private final EdmProperty edmProperty;

        private PropertyFacetsAdapter( @Nonnull final EdmProperty edmProperty )
        {
            this.edmProperty = edmProperty;
        }

        @Override
        public Boolean isNullable()
        {
            return edmProperty.isNullable();
        }

        @Override
        public String getDefaultValue()
        {
            return edmProperty.getDefaultValue();
        }

        @Override
        public Integer getMaxLength()
        {
            return edmProperty.getMaxLength();
        }

        @Override
        public Integer getPrecision()
        {
            return edmProperty.getPrecision();
        }

        @Override
        public Integer getScale()
        {
            return edmProperty.getScale();
        }
    }

    private final class PropertyAdapter extends ElementAdapter implements Property
    {
        private final EdmProperty edmProperty;

        private PropertyAdapter( @Nonnull final EdmProperty edmProperty )
        {
            super(edmProperty);
            this.edmProperty = edmProperty;
        }

        @Override
        public Facets getFacets()
        {
            return new PropertyFacetsAdapter(edmProperty);
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(edmProperty);
        }
    }

    private static final class NavigationPropertyFacetsAdapter implements Facets
    {
        private final EdmNavigationProperty edmNavigationProperty;

        private NavigationPropertyFacetsAdapter( @Nonnull final EdmNavigationProperty edmNavigationProperty )
        {
            this.edmNavigationProperty = edmNavigationProperty;
        }

        @Override
        public Boolean isNullable()
        {
            return edmNavigationProperty.isNullable();
        }

        @Override
        public String getDefaultValue()
        {
            return null;
        }

        @Override
        public Integer getMaxLength()
        {
            return null;
        }

        @Override
        public Integer getPrecision()
        {
            return null;
        }

        @Override
        public Integer getScale()
        {
            return null;
        }
    }

    private final class NavigationPropertyAdapter extends ElementAdapter implements NavigationProperty
    {
        private final EdmNavigationProperty edmNavigationProperty;

        private NavigationPropertyAdapter( @Nonnull final EdmNavigationProperty edmNavigationProperty )
        {
            super(edmNavigationProperty);
            this.edmNavigationProperty = edmNavigationProperty;
        }

        @Override
        public Facets getFacets()
        {
            return new NavigationPropertyFacetsAdapter(edmNavigationProperty);
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(edmNavigationProperty);
        }
    }

    private abstract class StructuralTypeAdapter implements StructuralType
    {
        private final EdmStructuredType edmStructuralType;

        StructuralTypeAdapter( @Nonnull final EdmStructuredType edmStructuralType )
        {
            this.edmStructuralType = edmStructuralType;
        }

        @Override
        public String getName()
        {
            try {
                return edmStructuralType.getName();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getFullyQualifiedName()
        {
            return edmStructuralType.getFullQualifiedName().getFullQualifiedNameAsString();
        }

        @Override
        public Collection<String> getPropertyNames()
        {
            try {
                return edmStructuralType.getPropertyNames();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Property getProperty( final String propertyName )
        {
            try {
                return new PropertyAdapter(edmStructuralType.getStructuralProperty(propertyName));
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(edmStructuralType);
        }
    }

    private final class ComplexTypeAdapter extends StructuralTypeAdapter implements ComplexType
    {
        private ComplexTypeAdapter( final EdmComplexType edmComplexType )
        {
            super(edmComplexType);
        }
    }

    private final class EnumTypeAdapter extends TypeAdapter implements EnumType
    {
        private final EdmEnumTypeImpl enumType;

        private EnumTypeAdapter( final EdmEnumTypeImpl simpleType )
        {
            super(simpleType);
            this.enumType = simpleType;
        }

        @Override
        public String getFullyQualifiedName()
        {
            return enumType.getFullQualifiedName().getFullQualifiedNameAsString();
        }

        @Override
        public Collection<String> getMemberNames()
        {
            return enumType.getMemberNames();
        }

        @Override
        public String getMemberValue( final String name )
        {
            return enumType.getMember(name).getValue();
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(enumType);
        }
    }

    private final class EntityTypeAdapter extends StructuralTypeAdapter implements EntityType
    {
        private final EdmEntityType entityType;

        private EntityTypeAdapter( final EdmEntityType entityType )
        {
            super(entityType);
            this.entityType = entityType;
        }

        @Override
        public Collection<String> getKeyPropertyNames()
        {
            try {
                return entityType.getKeyPredicateNames();
            }
            catch( final EdmException e ) {
                logger.info("Entity type \"" + entityType + "\" does not define a key.");
                return Collections.emptyList();
            }
        }

        @Override
        public Collection<String> getNavigationPropertyNames()
        {
            try {
                return entityType.getNavigationPropertyNames();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public NavigationProperty getNavigationProperty( final String navigationPropertyName )
        {
            try {
                return new NavigationPropertyAdapter(entityType.getNavigationProperty(navigationPropertyName));
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public boolean hasMediaStream()
        {
            try {
                return entityType.hasStream();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }
    }

    private final class EntitySetAdapter implements EntitySet
    {
        private final EdmEntitySet entitySet;

        private EntitySetAdapter( @Nonnull final EdmEntitySet entitySet )
        {
            this.entitySet = entitySet;
        }

        @Override
        public String getName()
        {
            try {
                return entitySet.getName();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public EntityType getEntityType()
        {
            try {
                return new EntityTypeAdapter(entitySet.getEntityType());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(entitySet);
        }
    }

    private final class ServiceFunctionAdapter implements ServiceFunction
    {
        private final EdmFunctionImport functionImport;
        private final EdmFunction unboundFunction;

        private ServiceFunctionAdapter(
            @Nonnull final EdmFunctionImport functionImport,
            @Nonnull final EdmFunction unboundFunction )
        {
            this.functionImport = functionImport;
            this.unboundFunction = unboundFunction;
        }

        @Override
        public String getName()
        {
            try {
                return functionImport.getName();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Nullable
        @Override
        public ReturnType getReturnType()
        {
            try {
                @Nullable
                final EdmReturnType returnType = unboundFunction.getReturnType();

                if( returnType == null ) {
                    return null;
                }

                return new ReturnTypeAdapter(returnType);
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getHttpMethod()
        {
            // Hardcoded assumption based on the OData V4 spec
            return "GET";
        }

        @Override
        public Collection<String> getParameterNames()
        {
            try {
                return unboundFunction.getParameterNames();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Parameter getParameter( final String parameterName )
        {
            try {
                return new ParameterAdapter(unboundFunction.getParameter(parameterName));
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(unboundFunction);
        }
    }

    @RequiredArgsConstructor
    private abstract class AbstractBoundOperationAdapter implements ServiceBoundOperation
    {
        /**
         * The EdmOperation that this adapter wraps.
         */
        @Getter
        protected final EdmOperation operation;

        @Override
        public String getName()
        {
            try {
                return operation.getName();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Nullable
        @Override
        public ReturnType getReturnType()
        {
            try {
                @Nullable
                final EdmReturnType returnType = operation.getReturnType();

                if( returnType == null ) {
                    return null;
                }

                return new ReturnTypeAdapter(returnType);
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getHttpMethod()
        {
            return "GET";
        }

        @Override
        public Collection<String> getParameterNames()
        {
            try {
                return operation.getParameterNames();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Parameter getParameter( final String parameterName )
        {
            try {
                return new ParameterAdapter(operation.getParameter(parameterName));
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(operation);
        }
    }

    private final class BoundFunctionAdapter extends AbstractBoundOperationAdapter implements ServiceBoundFunction
    {

        public BoundFunctionAdapter( final EdmFunction function )
        {
            super(function);
        }

        @Nonnull
        @Override
        public EdmFunction getBoundFunction()
        {
            return (EdmFunction) operation;
        }

        @Override
        public boolean isFunction()
        {
            return true;
        }
    }

    private final class BoundActionAdapter extends AbstractBoundOperationAdapter implements ServiceBoundAction
    {

        public BoundActionAdapter( final EdmAction action )
        {
            super(action);
        }

        @Nonnull
        @Override
        public EdmAction getBoundAction()
        {
            return (EdmAction) operation;
        }

        @Override
        public boolean isFunction()
        {
            return false;
        }
    }

    private final class ServiceActionAdapter implements ServiceAction
    {
        private final EdmActionImport actionImport;
        private final EdmAction unboundAction;

        private ServiceActionAdapter(
            @Nonnull final EdmActionImport actionImport,
            @Nonnull final EdmAction unboundAction )
        {
            this.actionImport = actionImport;
            this.unboundAction = unboundAction;
        }

        @Override
        public String getName()
        {
            try {
                return actionImport.getName();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Nullable
        @Override
        public ReturnType getReturnType()
        {
            try {
                @Nullable
                final EdmReturnType returnType = unboundAction.getReturnType();

                if( returnType == null ) {
                    return null;
                }

                return new ReturnTypeAdapter(returnType);
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getHttpMethod()
        {
            // Hardcoded assumption based on the OData V4 spec
            return "POST";
        }

        @Override
        public Collection<String> getParameterNames()
        {
            try {
                return unboundAction.getParameterNames();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Parameter getParameter( final String parameterName )
        {
            try {
                return new ParameterAdapter(unboundAction.getParameter(parameterName));
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Annotations getAnnotations()
        {
            return new AnnotationsAdapter(unboundAction);
        }
    }

    private Type convertType( @Nonnull final EdmType type )
    {
        if( type instanceof EdmEnumTypeImpl ) {
            return new EnumTypeAdapter((EdmEnumTypeImpl) type);
        }

        if( type instanceof EdmPrimitiveType ) {
            return new PrimitiveTypeAdapter((EdmPrimitiveType) type);
        }

        if( type instanceof EdmEntityType ) {
            return new EntityTypeAdapter((EdmEntityType) type);
        }

        if( type instanceof EdmComplexType ) {
            return new ComplexTypeAdapter((EdmComplexType) type);
        }

        throw new ODataGeneratorException("Found unknown EdmType implementation: " + type.getClass());
    }

    private String getAnnotationStringValue( final EdmAnnotatable edmAnnotatable, final String[] annotationTerms )
    {
        final Map<EdmTerm, Object> annotationTermToValue =
            edmAnnotatable
                .getAnnotations()
                .stream()
                .filter(a -> a.getTerm() != null)
                .filter(a -> a.getExpression().isConstant())
                .collect(Collectors.toMap(EdmAnnotation::getTerm, a -> a.getExpression().asConstant().asPrimitive()));

        for( final String termFqn : annotationTerms ) {
            final EdmTerm term = metadata.getTerm(new FullQualifiedName(termFqn));
            final Object result = annotationTermToValue.get(term);
            if( result instanceof String ) {
                return (String) result;
            }
        }
        return null;
    }
}
