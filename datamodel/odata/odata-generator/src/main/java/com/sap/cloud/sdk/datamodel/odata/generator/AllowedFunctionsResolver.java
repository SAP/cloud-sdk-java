package com.sap.cloud.sdk.datamodel.odata.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.vavr.control.Option;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

class AllowedFunctionsResolver
{
    private static final Logger logger = MessageCollector.getLogger(AllowedFunctionsResolver.class);

    private static final String ANNOTATIONS_ELEMENT_NAME = "Annotations";
    private static final String ANNOTATION_ELEMENT_NAME = "Annotation";
    private static final String RECORD_ELEMENT_NAME = "Record";
    private static final String PROPERTYVALUE_ELEMENT_NAME = "PropertyValue";

    /**
     * This pattern matches the expected Annotations target, being:
     * <p>
     * The Target needs to start with some arbitrary String (group 1), followed by a dot (.), again followed by a
     * "dot-and-slash-less" String (group 2). If the Target continues with a Slash, everything after it until the end is
     * caught in group 3.
     */
    private static final Pattern ANNOTATION_TARGET_PATTERN = Pattern.compile("^(.+)\\.([^./]+)(?:/(.+))?$");
    private static final String TARGET_ATTRIBUTE_NAME = "Target";
    private static final String PROPERTY_ATTRIBUTE_NAME = "Property";
    private static final String BOOL_ATTRIBUTE_NAME = "Bool";
    private static final String ADDRESSABLE_ATTRIBUTE_NAME = "addressable";
    private static final String CREATABLE_ATTRIBUTE_NAME = "creatable";
    private static final String DELETABLE_ATTRIBUTE_NAME = "deletable";
    private static final String UPDATABLE_ATTRIBUTE_NAME = "updatable";

    private static final String READABLE_PROPERTY_NAME = "readable";
    private static final String INSERTABLE_PROPERTY_NAME = "insertable";
    private static final String DELETABLE_PROPERTY_NAME = "deletable";
    private static final String UPDATABLE_PROPERTY_NAME = "updatable";

    private final Charset encoding;

    AllowedFunctionsResolver( final Charset encoding )
    {
        this.encoding = encoding;
    }

    @Nonnull
    Multimap<String, ApiFunction> findAllowedFunctions(
        @Nonnull final Edm metadata,
        @Nullable final File swaggerFile,
        @Nullable final File metadataFile )
    {
        Multimap<String, ApiFunction> allowedFunctionsByEntity = null;
        if( swaggerFile != null && swaggerFile.exists() ) {
            allowedFunctionsByEntity = readFromSwaggerFile(swaggerFile);
        } else if( swaggerFile != null ) {
            if( logger.isDebugEnabled() ) {
                logger
                    .debug(
                        "Could not find swagger file at "
                            + swaggerFile.getAbsolutePath()
                            + ". Trying to read the allowed functions from the metadata file.");
            }
        } else {
            logger.debug("No swagger file given. Trying to read the allowed functions from the metadata file.");
        }
        if( allowedFunctionsByEntity == null && metadataFile != null ) {
            allowedFunctionsByEntity = readOdataSpecFromMetadataFile(metadataFile, metadata);
        }

        if( allowedFunctionsByEntity == null ) {
            allowedFunctionsByEntity = readSAPSpecFromMetadataFile(metadata);
        }

        return allowedFunctionsByEntity;
    }

    @Value
    private static class AnnotationTarget
    {
        String namespace;
        String entityContainer;
        EntitySetName entitySet;
        String property;

        boolean isTargetingEntitySet()
        {
            return !StringUtils.isBlank(entitySet.get()) && StringUtils.isBlank(property);
        }

        /**
         * Assumption: the target of an annotation has one of the following patterns:
         * <ul>
         * <li>{@code someNamespace.entitySet}</li>
         * <li>{@code someNamespace.entitySet/propertyName}</li>
         * <li>{@code someNamespace.entityContainer/entitySet}</li>
         * </ul>
         * The namespace section can contain multiple '.' characters, while the EntitySet and EntityContainer must not
         * contain them. This means the last '.' before a '/' will separate the namespace from the following EntitySet
         * or EntityContainer.
         * <p>
         * If these assumptions are broken the method return an object will all properties set to {@code null}.
         */
        @Nonnull
        static AnnotationTarget of( @Nonnull final String annotationTarget, @Nonnull final Edm metadata )
        {
            final Matcher matcher = ANNOTATION_TARGET_PATTERN.matcher(annotationTarget);
            if( matcher.matches() ) {
                final String namespace = matcher.group(1);
                final EntitySetName maybeEntitySetName = EntitySetName.of(matcher.group(2));

                final EntitySetName entitySet;
                final String entityContainer;
                final String property;

                if( matcher.groupCount() == 3 ) {
                    if( getEntitySetNames(metadata).contains(maybeEntitySetName) ) {
                        entitySet = maybeEntitySetName;
                        property = matcher.group(3);
                        entityContainer = null;
                    } else {
                        entityContainer = matcher.group(2);
                        entitySet = EntitySetName.of(matcher.group(3));
                        property = null;
                    }
                } else {
                    entitySet = maybeEntitySetName;
                    entityContainer = null;
                    property = null;
                }

                return new AnnotationTarget(namespace, entityContainer, entitySet, property);
            } else {
                return new AnnotationTarget(null, null, null, null);
            }
        }
    }

    @Nullable
    private
        Multimap<String, ApiFunction>
        readOdataSpecFromMetadataFile( @Nonnull final File metadataFile, @Nonnull final Edm metadataObject )
    {
        final NodeList annotationsList = retrieveEdmxAnnotations(metadataFile);

        if( annotationsList.getLength() == 0 ) {
            return null;
        }

        final Map<EntitySetName, Set<DefinedProperty>> definedPropertyPerEntitySet =
            determineDefinedPropertiesPerEntitySet(metadataObject, annotationsList);

        if( containsNoProperties(definedPropertyPerEntitySet) ) {
            return null;
        }

        return convertPropertiesToAllowedFunctions(metadataObject, definedPropertyPerEntitySet);
    }

    private NodeList retrieveEdmxAnnotations( @Nonnull final File metadataFile )
    {
        final Document doc = MetadataFileUtils.getMetadataDocumentObject(metadataFile);
        return doc.getElementsByTagName(ANNOTATIONS_ELEMENT_NAME);
    }

    private Multimap<String, ApiFunction> convertPropertiesToAllowedFunctions(
        @Nonnull final Edm metadataObject,
        @Nonnull final Map<EntitySetName, Set<DefinedProperty>> definedPropertyPerEntitySet )
    {
        final Multimap<String, ApiFunction> allowedFunctionsByEntity =
            MultimapBuilder.hashKeys().hashSetValues().build();

        for( final EntitySetName entitySetName : getEntitySetNames(metadataObject) ) {
            final Set<ApiFunction> supportedFunctions;

            if( definedPropertyPerEntitySet.containsKey(entitySetName) ) {
                supportedFunctions = determineSupportedFunctions(definedPropertyPerEntitySet.get(entitySetName));
            } else {
                supportedFunctions = getAllFunctions();
            }
            allowedFunctionsByEntity.putAll(entitySetName.get(), supportedFunctions);
        }

        return allowedFunctionsByEntity;
    }

    private static Set<EntitySetName> getEntitySetNames( final Edm metadataObject )
    {
        try {
            return metadataObject
                .getEntitySets()
                .stream()
                .map(EntitySetName::of)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        catch( final EdmException e ) {
            throw new ODataGeneratorReadException("Could not access EntitySets.", e);
        }
    }

    private static boolean containsNoProperties(
        final Map<EntitySetName, Set<DefinedProperty>> definedPropertyPerEntitySet )
    {
        return definedPropertyPerEntitySet.values().stream().allMatch(Set::isEmpty);
    }

    @Nonnull
    private
        Map<EntitySetName, Set<DefinedProperty>>
        determineDefinedPropertiesPerEntitySet( @Nonnull final Edm metadataObject, final NodeList annotationsList )
    {
        final Map<EntitySetName, Set<DefinedProperty>> definedPropertyPerEntitySet = new HashMap<>();
        for( int i = 0; i < annotationsList.getLength(); i++ ) {

            final Node annotations = annotationsList.item(i);
            final NamedNodeMap annotationsAttributeMap = annotations.getAttributes();
            final Node entitySetTarget = annotationsAttributeMap.getNamedItem(TARGET_ATTRIBUTE_NAME);

            final AnnotationTarget annotationTarget =
                AnnotationTarget.of(entitySetTarget.getNodeValue(), metadataObject);

            if( annotationTarget.isTargetingEntitySet() ) {
                final EntitySetName entitySetName = annotationTarget.getEntitySet();
                final Set<DefinedProperty> alreadyKnownProperties =
                    definedPropertyPerEntitySet.computeIfAbsent(entitySetName, key -> new LinkedHashSet<>());
                alreadyKnownProperties.addAll(readDefinedProperties(annotations));
            }
        }
        return definedPropertyPerEntitySet;
    }

    private Set<ApiFunction> determineSupportedFunctions( @Nonnull final Collection<DefinedProperty> definedProperties )
    {
        final Set<ApiFunction> supportedFunctions = EnumSet.allOf(ApiFunction.class);
        definedProperties
            .stream()
            .map(DefinedProperty::determineUnsupportedFunction)
            .filter(Option::isDefined)
            .map(Option::get)
            .forEach(supportedFunctions::remove);
        return supportedFunctions;
    }

    private Set<ApiFunction> getAllFunctions()
    {
        return Sets.newHashSet(ApiFunction.values());
    }

    @Nonnull
    private Set<DefinedProperty> readDefinedProperties( @Nonnull final Node annotationsParent )
    {
        final List<NodeList> annotations =
            getChildrenFromNodeList(ANNOTATION_ELEMENT_NAME, annotationsParent.getChildNodes());

        final Set<DefinedProperty> definedProperties = new LinkedHashSet<>();
        for( final NodeList annotation : annotations ) {
            final List<NodeList> records = getChildrenFromNodeList(RECORD_ELEMENT_NAME, annotation);

            for( final NodeList record : records ) {
                final List<NamedNodeMap> attributes = getAttributesFromNodeList(PROPERTYVALUE_ELEMENT_NAME, record);

                for( final NamedNodeMap attribute : attributes ) {
                    final Option<String> attributeName =
                        Option
                            .of(attribute.getNamedItem(PROPERTY_ATTRIBUTE_NAME))
                            .flatMap(node -> Option.of(node.getNodeValue()))
                            .map(String::toLowerCase);

                    final Option<String> attributeValue =
                        Option
                            .of(attribute.getNamedItem(BOOL_ATTRIBUTE_NAME))
                            .flatMap(node -> Option.of(node.getNodeValue()))
                            .map(String::toLowerCase);

                    if( attributeName.isDefined() && attributeValue.isDefined() ) {
                        definedProperties.add(DefinedProperty.of(attributeName.get(), attributeValue.get()));
                    }
                }
            }
        }
        return definedProperties;
    }

    @Nonnull
    private
        List<NodeList>
        getChildrenFromNodeList( @Nonnull final String childNodeName, @Nonnull final NodeList children )
    {
        return IntStream
            .range(0, children.getLength())
            .mapToObj(children::item)
            .filter(annotation -> childNodeName.equalsIgnoreCase(annotation.getNodeName()))
            .filter(Node::hasChildNodes)
            .map(Node::getChildNodes)
            .collect(Collectors.toList());
    }

    @Nonnull
    private
        List<NamedNodeMap>
        getAttributesFromNodeList( @Nonnull final String childNodeName, @Nonnull final NodeList children )
    {
        return IntStream
            .range(0, children.getLength())
            .mapToObj(children::item)
            .filter(annotation -> childNodeName.equalsIgnoreCase(annotation.getNodeName()))
            .filter(Node::hasAttributes)
            .map(Node::getAttributes)
            .collect(Collectors.toList());
    }

    private Multimap<String, ApiFunction> readSAPSpecFromMetadataFile( final Edm metadata )
    {
        final Multimap<String, ApiFunction> allowedFunctionsByEntity =
            MultimapBuilder.hashKeys().hashSetValues().build();
        try {
            final List<EdmEntitySet> entitySets = metadata.getEntitySets();

            for( final EdmEntitySet entitySet : entitySets ) {

                final String entityName = entitySet.getName();
                final EdmAnnotations annotations = entitySet.getAnnotations();

                allowedFunctionsByEntity.put(entityName, ApiFunction.READ_BY_KEY);
                if( getAttributeValue(ADDRESSABLE_ATTRIBUTE_NAME, annotations) ) {
                    allowedFunctionsByEntity.put(entityName, ApiFunction.READ);
                }
                if( getAttributeValue(CREATABLE_ATTRIBUTE_NAME, annotations) ) {
                    allowedFunctionsByEntity.put(entityName, ApiFunction.CREATE);
                }
                if( getAttributeValue(DELETABLE_ATTRIBUTE_NAME, annotations) ) {
                    allowedFunctionsByEntity.put(entityName, ApiFunction.DELETE);
                }
                if( getAttributeValue(UPDATABLE_ATTRIBUTE_NAME, annotations) ) {
                    allowedFunctionsByEntity.put(entityName, ApiFunction.UPDATE);
                }
            }
        }
        catch( final EdmException e ) {
            throw new ODataGeneratorReadException(e);
        }

        return allowedFunctionsByEntity;
    }

    private boolean getAttributeValue( final String attributeName, final EdmAnnotations annotations )
    {
        final String sapNamespace = "http://www.sap.com/Protocols/SAPData";
        final EdmAnnotationAttribute attribute = annotations.getAnnotationAttribute(attributeName, sapNamespace);
        if( attribute != null && "false".equals(attribute.getText()) ) {
            return false;
        } else if( attribute == null || "true".equals(attribute.getText()) ) {
            // the default values of the attributes is true, see
            // https://wiki.scn.sap.com/wiki/display/EmTech/SAP+Annotations+for+OData+Version+2.0#SAPAnnotationsforODataVersion2.0-Elementedm:EntitySet
            return true;
        } else {
            throw new ODataGeneratorReadException(
                "Could not parse attribute '" + attribute + "' with value '" + attribute.getText() + "'");
        }
    }

    private Multimap<String, ApiFunction> readFromSwaggerFile( final File swaggerFile )
    {
        final Multimap<String, ApiFunction> allowedFunctionsByEntity =
            MultimapBuilder.hashKeys().hashSetValues().build();
        final Iterable<Map.Entry<String, JsonElement>> paths = readPaths(swaggerFile);

        for( final Map.Entry<String, JsonElement> pathsEntry : paths ) {
            handleSwaggerPath(allowedFunctionsByEntity, pathsEntry);
        }

        return allowedFunctionsByEntity.isEmpty() ? null : allowedFunctionsByEntity;
    }

    private void handleSwaggerPath(
        final Multimap<String, ApiFunction> allowedFunctionsByEntity,
        final Map.Entry<String, JsonElement> pathsEntry )
    {
        final String key = pathsEntry.getKey();
        final String[] split = key.substring(1).split("\\(");
        final String entity = split[0];

        for( final Map.Entry<String, JsonElement> entry : pathsEntry.getValue().getAsJsonObject().entrySet() ) {
            switch( entry.getKey() ) {
                case "get":
                    if( split.length > 1 ) {
                        allowedFunctionsByEntity.put(entity, ApiFunction.READ_BY_KEY);
                    } else {
                        allowedFunctionsByEntity.put(entity, ApiFunction.READ);
                    }
                    break;
                case "post":
                    allowedFunctionsByEntity.put(entity, ApiFunction.CREATE);
                    break;
                case "patch":
                    allowedFunctionsByEntity.put(entity, ApiFunction.UPDATE);
                    break;
                case "delete":
                    allowedFunctionsByEntity.put(entity, ApiFunction.DELETE);
                    break;
                case "put":
                case "options":
                case "head":
                case "trace":
                    logger.warn("Skipping unsupported operation '" + entry.getKey() + "'.");
                    break;
                case "summary":
                case "description":
                case "parameters":
                case "servers":
                    logger.debug("Skipping field '" + entry.getKey() + "' from the Swagger file.");
                    break;
                default:
                    logger.info("Skipping unexpected field " + entry.getKey() + "' from Swagger file.");
                    break;
            }
        }
    }

    private Iterable<Map.Entry<String, JsonElement>> readPaths( final File swaggerFile )
    {
        try( Reader reader = new InputStreamReader(Files.newInputStream(swaggerFile.toPath()), encoding) ) {
            final JsonElement swaggerJson = JsonParser.parseReader(reader);
            return swaggerJson.getAsJsonObject().get("paths").getAsJsonObject().entrySet();
        }
        catch( final IOException e ) {
            throw new ODataGeneratorReadException(e);
        }
    }

    @ToString
    @EqualsAndHashCode
    private static final class DefinedProperty
    {
        private final String propertyName;
        private final String propertyValue;

        private DefinedProperty( final String propertyName, final String propertyValue )
        {
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
        }

        Option<ApiFunction> determineUnsupportedFunction()
        {
            final ApiFunction supportedFunction;

            if( booleanValue() ) {
                switch( propertyName ) {
                    case READABLE_PROPERTY_NAME:
                        supportedFunction = ApiFunction.READ;
                        break;
                    case INSERTABLE_PROPERTY_NAME:
                        supportedFunction = ApiFunction.CREATE;
                        break;
                    case DELETABLE_PROPERTY_NAME:
                        supportedFunction = ApiFunction.DELETE;
                        break;
                    case UPDATABLE_PROPERTY_NAME:
                        supportedFunction = ApiFunction.UPDATE;
                        break;
                    default:
                        supportedFunction = null;
                }
            } else {
                supportedFunction = null;
            }
            return Option.of(supportedFunction);
        }

        boolean booleanValue()
        {
            return "false".equals(propertyValue);
        }

        static DefinedProperty of( final String propertyName, final String propertyValue )
        {
            return new DefinedProperty(propertyName, propertyValue);
        }
    }

    @ToString
    @EqualsAndHashCode
    private static final class EntitySetName
    {
        private final String entitySetName;

        private EntitySetName( @Nonnull final String entitySetName )
        {
            this.entitySetName = entitySetName;
        }

        String get()
        {
            return entitySetName;
        }

        static EntitySetName of( @Nonnull final EdmEntitySet entitySet )
        {
            try {
                return new EntitySetName(entitySet.getName());
            }
            catch( final EdmException e ) {
                throw new ODataGeneratorReadException(e);
            }
        }

        static EntitySetName of( @Nonnull final String entitySetName )
        {
            return new EntitySetName(entitySetName);
        }
    }
}
