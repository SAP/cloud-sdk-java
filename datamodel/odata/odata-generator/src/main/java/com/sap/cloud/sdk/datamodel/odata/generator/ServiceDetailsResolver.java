/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ServiceDetailsResolver
{
    private static final Logger logger = MessageCollector.getLogger(ServiceDetailsResolver.class);

    @Nullable
    private final String defaultBasePath;
    private final Charset encoding;

    ServiceDetailsResolver( final Charset encoding )
    {
        this.encoding = encoding;
        defaultBasePath = null;
    }

    ServiceDetailsResolver( @Nullable final String defaultBasePath, final Charset encoding )
    {
        this.defaultBasePath = defaultBasePath;
        this.encoding = encoding;
    }

    @Nonnull
    ServiceDetails createServiceDetails( @Nonnull final File edmxFile, @Nullable final File swaggerFile )
    {
        ServiceDetails details = null;

        if( swaggerFile != null && swaggerFile.exists() ) {
            details = readFromSwaggerFile(swaggerFile);
        } else if( swaggerFile != null ) {
            if( logger.isDebugEnabled() ) {
                logger
                    .debug(
                        "Could not find swagger file at "
                            + swaggerFile.getAbsolutePath()
                            + ". Trying to use the default base path.");
            }
        } else {
            logger.debug("No swagger file given. Trying to use the default base path.");
        }

        if( details == null && defaultBasePath != null ) {
            details = createFromDefaultBasePath(edmxFile);
        } else if( details == null ) {
            logger.debug("No default base path given. Trying to read the base path from the metadata file.");
        }

        if( details == null ) {
            details = readServiceDetailsFromMetadataFile(edmxFile);
        }

        if( details == null || details.getServiceUrl() == null ) {
            throw new ODataGeneratorReadException(
                "Could not determine a valid default service path. A default service path is the prefix of the HTTP "
                    + "query component used for all outgoing queries of the service to be generated (if no custom "
                    + "service path is given).\n"
                    + "Tried to determine the service path with the following priority:\n"
                    + " 1. The 'basePath' field in the swagger file\n"
                    + " 2. The default base path (if given via the Maven Plugin or Builder method) concatenated with "
                    + "the namespace defined in metadata file. In case of no namespace, filename is considered as the service name.\n"
                    + " 3. The atom:link field in the metadata file.");
        }

        return details;
    }

    @Nonnull
    private ServiceDetails createFromDefaultBasePath( @Nonnull final File metadataFile )
    {
        final String serviceName = readServiceNameFromMetaDataFile(metadataFile);
        final String serviceUrl = defaultBasePath + serviceName;
        final ServiceDetails serviceDetails = new ServiceDetailsPojo();
        serviceDetails.setServiceUrl(serviceUrl);
        return serviceDetails;
    }

    @Nonnull
    private String readServiceNameFromMetaDataFile( @Nonnull final File metadataFile )
    {
        String serviceName = getMetadataNamespace(metadataFile);
        if( StringUtils.isBlank(serviceName) ) {
            // no namespace specified, consider filename as the service name
            serviceName = FilenameUtils.getBaseName(metadataFile.getAbsolutePath());
        }
        return serviceName;
    }

    @Nullable
    private String getMetadataNamespace( @Nonnull final File metadataFile )
    {
        final Document doc = MetadataFileUtils.getMetadataDocumentObject(metadataFile);
        if( doc != null ) {
            final NodeList schemas = doc.getElementsByTagName("Schema");
            if( schemas.getLength() > 1 ) {
                logger
                    .info(
                        String
                            .format(
                                "Expected to have a single schema tag in the metadata file %s, but found %d instead. "
                                    + "The first schema with a defined namespace found will be used.",
                                metadataFile.getName(),
                                schemas.getLength()));
            }
            return getFirstDefinedNamespace(schemas);
        }
        return null;
    }

    @Nullable
    private static String getFirstDefinedNamespace( @Nonnull final NodeList schemaNodes )
    {
        for( int i = 0; i < schemaNodes.getLength(); i++ ) {
            final Node schema = schemaNodes.item(i);
            final NamedNodeMap schemaAttributeMap = schema.getAttributes();
            if( schemaAttributeMap.getLength() != 0 ) {
                final Node namespaceNode = schemaAttributeMap.getNamedItem("Namespace");
                if( namespaceNode != null ) {
                    final String namespace = namespaceNode.getNodeValue();
                    if( !StringUtils.isBlank(namespace) ) {
                        return namespace;
                    }
                }
            }
        }
        return null;
    }

    @Nonnull
    private ServiceDetails readFromSwaggerFile( @Nonnull final File swaggerFile )
    {
        final ServiceDetails details = ServiceDetailsParser.parse(swaggerFile, encoding);
        adjustServiceUrl(details);
        return details;
    }

    private void adjustServiceUrl( @Nonnull final ServiceDetails details )
    {
        final String prefixToRemove = "/s4hanacloud";
        final String serviceUrl = details.getServiceUrl();
        final String adjustedUrl = StringUtils.removeStart(serviceUrl, prefixToRemove);
        details.setServiceUrl(adjustedUrl);
    }

    @Nullable
    private ServiceDetails readServiceDetailsFromMetadataFile( @Nonnull final File metadataFile )
    {
        final String fullServiceUrl = getMetadataServiceUrl(metadataFile);
        if( fullServiceUrl != null ) {
            final String serviceUrl = adjustMetadataServiceUrl(fullServiceUrl);
            final ServiceDetails serviceDetails = new ServiceDetailsPojo();
            serviceDetails.setServiceUrl(serviceUrl);
            return serviceDetails;
        }
        return null;
    }

    @Nonnull
    private String adjustMetadataServiceUrl( @Nonnull final String fullServiceUrl )
    {
        String parsableServiceUrl = fullServiceUrl;
        if( !fullServiceUrl.startsWith("http") ) {
            parsableServiceUrl = "https://" + parsableServiceUrl;
        }
        final URI uri;
        try {
            uri = new URI(parsableServiceUrl);
        }
        catch( final URISyntaxException e ) {
            throw new ODataGeneratorReadException("Could not parse the atom:link url " + fullServiceUrl, e);
        }
        return StringUtils.removeEnd(uri.getPath(), "/$metadata");
    }

    @Nullable
    private String getMetadataServiceUrl( @Nonnull final File metadataFile )
    {
        final Document doc = MetadataFileUtils.getMetadataDocumentObject(metadataFile);
        if( doc != null ) {
            final NodeList schemas = doc.getElementsByTagName("Schema");
            if( schemas.getLength() > 1 ) {
                logger
                    .info(
                        String
                            .format(
                                "Expected to have a single schema tag in the metadata file %s, but found %d instead. "
                                    + "The first atom:link found will be used.",
                                metadataFile.getName(),
                                schemas.getLength()));
            }

            final Node atomLinkNode = findFirstChildNodeWithAtomLink(schemas);
            if( atomLinkNode != null ) {
                return getAtomLinkServiceUrlFromNode(atomLinkNode);
            }
        }
        return null;
    }

    @Nullable
    private static Node findFirstChildNodeWithAtomLink( @Nonnull final NodeList schemaNodes )
    {
        for( int i = 0; i < schemaNodes.getLength(); i++ ) {
            final Node schema = schemaNodes.item(i);
            final NodeList childNodes = schema.getChildNodes();

            for( int j = 0; j < childNodes.getLength(); j++ ) {
                final Node child = childNodes.item(j);
                if( isNodeAtomLinkWithSelfRelation(child) ) {
                    return child;
                }
            }
        }
        return null;
    }

    private static boolean isNodeAtomLinkWithSelfRelation( @Nonnull final Node node )
    {
        if( node.getNodeType() == Node.ELEMENT_NODE && "atom:link".equals(node.getNodeName()) ) {
            return "self".equals(node.getAttributes().getNamedItem("rel").getNodeValue());
        }
        return false;
    }

    @Nullable
    private static String getAtomLinkServiceUrlFromNode( @Nonnull final Node node )
    {
        return node.getAttributes().getNamedItem("href").getNodeValue();
    }
}
