/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmAnnotatable;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotationElement;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmComplexType;
import org.apache.olingo.odata2.api.edm.EdmElement;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFacets;
import org.apache.olingo.odata2.api.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmParameter;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.core.edm.EdmDateTime;
import org.apache.olingo.odata2.core.edm.EdmDateTimeOffset;
import org.apache.olingo.odata2.core.edm.EdmTime;
import org.slf4j.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
class EdmService implements Service
{
    private static final Logger logger = MessageCollector.getLogger(EdmService.class);

    private static final String sapNamespace = "http://www.sap.com/Protocols/SAPData";

    private final String name;
    private final PropertiesConfiguration serviceNameMappings;
    private final ServiceDetails details;
    private final Function<String, Collection<ApiFunction>> allowedFunctionsByEntity;
    private final boolean linkToApiBusinessHub;

    private final Map<String, EntitySet> entitySets = new LinkedHashMap<>();
    private final Map<String, FunctionImport> functionImports = new LinkedHashMap<>();
    @Setter( AccessLevel.PACKAGE )
    private boolean generateExplicitDeprecationNotices = false;

    EdmService(
        @Nonnull final String name,
        @Nonnull final PropertiesConfiguration serviceNameMappings,
        @Nonnull final Edm metadata,
        @Nonnull final ServiceDetails details,
        @Nonnull final Multimap<String, ApiFunction> allowedFunctionsByEntity,
        final boolean linkToApiBusinessHub )
    {
        this.name = name;
        this.serviceNameMappings = serviceNameMappings;
        this.details = details;
        this.allowedFunctionsByEntity = allowedFunctionsByEntity::get;
        this.linkToApiBusinessHub = linkToApiBusinessHub;

        try {
            for( final EdmEntitySet entitySet : metadata.getEntitySets() ) {
                entitySets.put(entitySet.getName(), new EntitySetAdapter(entitySet));
            }
            for( final EdmFunctionImport value : metadata.getFunctionImports() ) {
                functionImports.put(value.getName(), new FunctionImportAdapter(value));
            }
        }
        catch( final EdmException e ) {
            throw new ODataGeneratorReadException(e);
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
    public FunctionImport getFunctionImport( final String functionImportName )
    {
        return functionImports.get(functionImportName);
    }

    @Override
    public Collection<FunctionImport> getAllFunctionImports()
    {
        return functionImports.values();
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
    public Collection<ApiFunction> getAllowedFunctionsByEntity( final String entity )
    {
        return allowedFunctionsByEntity.apply(entity);
    }

    @Override
    public boolean hasLinkToApiBusinessHub()
    {
        return linkToApiBusinessHub;
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

    private static final class EntitySetAdapter implements EntitySet
    {
        private final EdmEntitySet entitySet;

        private EntitySetAdapter( @Nonnull final EdmEntitySet entitySet )
        {
            this.entitySet = entitySet;
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
        public String getName()
        {
            try {
                return entitySet.getName();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }
    }

    private abstract static class TypedAdapter implements Typed
    {
        private final EdmTyped edmTyped;

        TypedAdapter( @Nonnull final EdmTyped edmTyped )
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
                return EdmUtils.convertMultiplicity(edmTyped.getMultiplicity());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }
    }

    private static final class FunctionImportAdapter implements FunctionImport
    {
        private final EdmFunctionImport functionImport;

        private FunctionImportAdapter( @Nonnull final EdmFunctionImport functionImport )
        {
            this.functionImport = functionImport;
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

        @Override
        public String getLabel()
        {
            try {
                return EdmService.getLabel(functionImport.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getQuickInfo()
        {
            try {
                return EdmService.getQuickInfo(functionImport.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public AnnotationElement getDocumentationElement()
        {
            return EdmService.getDocumentationElement(functionImport);
        }

        @Nullable
        @Override
        public Type getReturnType()
        {
            try {
                @Nullable
                final EdmTyped returnType = functionImport.getReturnType();

                if( returnType == null ) {
                    return null;
                }

                return convertType(returnType.getType());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Nullable
        @Override
        public Multiplicity getReturnTypeMultiplicity()
        {
            try {
                @Nullable
                final EdmTyped returnType = functionImport.getReturnType();

                if( returnType == null ) {
                    return null;
                }

                return EdmUtils.convertMultiplicity(returnType.getMultiplicity());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getHttpMethod()
        {
            try {
                return functionImport.getHttpMethod();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Collection<String> getParameterNames()
        {
            try {
                return functionImport.getParameterNames();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Parameter getParameter( final String parameterName )
        {
            try {
                return new ParameterAdapter(functionImport.getParameter(parameterName));
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
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
        public TypeKind getKind()
        {
            return EdmUtils.convertTypeKind(edmType.getKind());
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
    }

    private static final class ParameterAdapter extends ElementAdapter implements Parameter
    {
        private final EdmParameter edmParameter;

        private ParameterAdapter( @Nonnull final EdmParameter edmParameter )
        {
            super(edmParameter);
            this.edmParameter = edmParameter;
        }

        @Override
        public String getLabel()
        {
            try {
                return EdmService.getLabel(edmParameter.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getQuickInfo()
        {
            try {
                return EdmService.getQuickInfo(edmParameter.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public AnnotationElement getDocumentationElement()
        {
            return EdmService.getDocumentationElement(edmParameter);
        }

        @Override
        public Type getType()
        {
            try {
                return convertType(edmParameter.getType());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public Multiplicity getMultiplicity()
        {
            try {
                return EdmUtils.convertMultiplicity(edmParameter.getMultiplicity());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }
    }

    private static final class FacetsAdapter implements Facets
    {
        private final EdmFacets edmFacets;

        private FacetsAdapter( @Nonnull final EdmFacets edmFacets )
        {
            this.edmFacets = edmFacets;
        }

        @Override
        public Boolean isNullable()
        {
            return edmFacets.isNullable();
        }

        @Override
        public String getDefaultValue()
        {
            return edmFacets.getDefaultValue();
        }

        @Override
        public Integer getMaxLength()
        {
            return edmFacets.getMaxLength();
        }

        @Override
        public Integer getPrecision()
        {
            return edmFacets.getPrecision();
        }

        @Override
        public Integer getScale()
        {
            return edmFacets.getScale();
        }
    }

    private static final class AnnotationElementAdapter implements AnnotationElement
    {
        private final EdmAnnotationElement edmAnnotationElement;

        private AnnotationElementAdapter( @Nonnull final EdmAnnotationElement edmAnnotationElement )
        {
            this.edmAnnotationElement = edmAnnotationElement;
        }

        @Override
        public Iterable<? extends AnnotationElement> getChildElements()
        {
            final List<EdmAnnotationElement> input = edmAnnotationElement.getChildElements();
            final Collection<AnnotationElementAdapter> result = new ArrayList<>(input.size());

            for( final EdmAnnotationElement value : input ) {
                result.add(new AnnotationElementAdapter(value));
            }
            return result;
        }

        @Override
        public String getText()
        {
            return edmAnnotationElement.getText();
        }

        @Override
        public String getName()
        {
            return edmAnnotationElement.getName();
        }
    }

    private abstract static class StructuralTypeAdapter extends EdmService.TypeAdapter implements StructuralType
    {
        private final EdmStructuralType edmStructuralType;

        StructuralTypeAdapter( @Nonnull final EdmStructuralType edmStructuralType )
        {
            super(edmStructuralType);
            this.edmStructuralType = edmStructuralType;
        }

        @Override
        public String getLabel()
        {
            try {
                return EdmService.getLabel(edmStructuralType.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getQuickInfo()
        {
            try {
                return EdmService.getQuickInfo(edmStructuralType.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public AnnotationElement getDocumentationElement()
        {
            return EdmService.getDocumentationElement(edmStructuralType);
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
        public Typed getProperty( final String propertyName )
        {
            try {
                return convertTyped(edmStructuralType.getProperty(propertyName));
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
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
    }

    private static final class SimpleTypeAdapter extends TypeAdapter implements SimpleType
    {
        private final EdmSimpleType simpleType;

        private SimpleTypeAdapter( final EdmSimpleType simpleType )
        {
            super(simpleType);
            this.simpleType = simpleType;
        }

        @Override
        public Class<?> getDefaultType()
        {
            if( simpleType instanceof EdmDateTime ) {
                return LocalDateTime.class;
            } else if( simpleType instanceof EdmTime ) {
                return LocalTime.class;
            } else if( simpleType instanceof EdmDateTimeOffset ) {
                return ZonedDateTime.class;
            } else {
                return simpleType.getDefaultType();
            }
        }
    }

    private static final class ComplexTypeAdapter extends StructuralTypeAdapter implements ComplexType
    {
        private ComplexTypeAdapter( final EdmStructuralType edmComplexType )
        {
            super(edmComplexType);
        }
    }

    private static final class EntityTypeAdapter extends StructuralTypeAdapter implements EntityType
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
                return entityType.getKeyPropertyNames();
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

    private abstract static class ElementAdapter extends TypedAdapter implements Element
    {
        private final EdmElement edmElement;

        ElementAdapter( @Nonnull final EdmElement edmElement )
        {
            super(edmElement);
            this.edmElement = edmElement;
        }

        @Override
        public Facets getFacets()
        {
            final EdmFacets facets;
            try {
                facets = edmElement.getFacets();
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
            if( facets == null ) {
                return null;
            }
            return new FacetsAdapter(facets);
        }
    }

    private static final class PropertyAdapter extends ElementAdapter implements Property
    {
        private final EdmProperty edmProperty;

        private PropertyAdapter( @Nonnull final EdmProperty edmProperty )
        {
            super(edmProperty);
            this.edmProperty = edmProperty;
        }

        @Override
        public String getLabel()
        {
            try {
                return EdmService.getLabel(edmProperty.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public String getQuickInfo()
        {
            try {
                return EdmService.getQuickInfo(edmProperty.getAnnotations());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        @Override
        public AnnotationElement getDocumentationElement()
        {
            return EdmService.getDocumentationElement(edmProperty);
        }
    }

    private static final class NavigationPropertyAdapter extends TypedAdapter
    {
        NavigationPropertyAdapter( final EdmTyped edmTyped )
        {
            super(edmTyped);
        }
    }

    private static Service.Type convertType( @Nonnull final EdmType type )
    {
        if( type instanceof EdmSimpleType ) {
            return new SimpleTypeAdapter((EdmSimpleType) type);
        }

        if( type instanceof EdmEntityType ) {
            return new EntityTypeAdapter((EdmEntityType) type);
        }

        if( type instanceof EdmComplexType ) {
            return new ComplexTypeAdapter((EdmStructuralType) type);
        }

        throw new ODataGeneratorException("Found unknown EdmType implementation: " + type.getClass());
    }

    private static Service.Typed convertTyped( @Nonnull final EdmTyped typed )
    {
        if( typed instanceof EdmProperty ) {
            return new PropertyAdapter((EdmProperty) typed);
        }
        if( typed instanceof EdmParameter ) {
            return new ParameterAdapter((EdmParameter) typed);
        }
        if( typed instanceof EdmNavigationProperty ) {
            return new NavigationPropertyAdapter(typed);
        }

        throw new ODataGeneratorException("Found unknown EdmTyped implementation: " + typed.getClass());
    }

    private static AnnotationElement getDocumentationElement( final EdmAnnotatable annotatable )
    {
        final List<EdmAnnotationElement> annotationElements;
        try {
            annotationElements = annotatable.getAnnotations().getAnnotationElements();
        }
        catch( final EdmException e ) {
            throw new ODataGeneratorReadException(e);
        }

        final EdmAnnotationElement documentationElement = EdmUtils.getDocumentationElement(annotationElements);

        if( documentationElement == null ) {
            return null;
        }
        return new AnnotationElementAdapter(documentationElement);
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

    private static String getLabel( final EdmAnnotations annotations )
    {
        final String attributeName = "label";
        return getAttributeText(annotations, attributeName);
    }

    private static String getQuickInfo( final EdmAnnotations annotations )
    {
        final String attributeName = "quickinfo";
        return getAttributeText(annotations, attributeName);
    }

    private static String getAttributeText( final EdmAnnotations annotations, final String attributeName )
    {
        final EdmAnnotationAttribute attribute = annotations.getAnnotationAttribute(attributeName, sapNamespace);
        if( attribute == null ) {
            return null;
        }
        return attribute.getText();
    }
}
