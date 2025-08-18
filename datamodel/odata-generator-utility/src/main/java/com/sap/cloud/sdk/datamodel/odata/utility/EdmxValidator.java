package com.sap.cloud.sdk.datamodel.odata.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.sap.cloud.sdk.cloudplatform.util.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to enable validity checks for OData specifications from EDMX files. <b>This class is meant for internal
 * usage only.</b>
 */
@Slf4j
@NoArgsConstructor( access = AccessLevel.PRIVATE ) // utility class
public class EdmxValidator
{

    // See reference implementation in org.apache.olingo.odata2.core.commons.XmlHelper#createStreamReader
    private static final XMLInputFactory factory = XMLInputFactory.newInstance();
    static {
        factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    }

    /**
     * OData version to manage different validation steps.
     */
    public enum Version
    {
        /**
         * OData V2.
         */
        V2,

        /**
         * OData V4.
         */
        V4
    }

    /**
     * Validation method to check whether the provided EDMX file qualifies for the given OData version definition.
     *
     * @param edmxFile
     *            The EDMX file to check.
     * @param version
     *            The OData Version to use.
     * @return {@code true} if the EDMX file is qualified for code generation. {@code false} otherwise.
     */
    public static boolean isQualified( @Nonnull final File edmxFile, @Nonnull final Version version )
    {
        log.debug("Checking whether the EDMX file {} matches protocol {}.", edmxFile, version);
        try( InputStream content = Files.newInputStream(edmxFile.toPath()) ) {
            final XMLStreamReader reader = factory.createXMLStreamReader(content, StandardCharsets.UTF_8.name());
            switch( version ) {
                case V2:
                    return isQualifiedForODataV2(reader);
                case V4:
                    return isQualifiedForODataV4(reader);
            }
        }
        catch( final IOException | XMLStreamException e ) {
            log.error("Unable to check whether the file {} and matches protocol {}.", edmxFile, version, e);
        }
        return false;
    }

    /*
     * See EDMX schema definition https://docs.oasis-open.org/odata/odata-csdl-xml/v4.01/os/schemas/edmx.xsd
     */
    private static boolean isQualifiedForODataV4( @Nonnull final XMLStreamReader reader )
        throws XMLStreamException
    {
        while( reader.hasNext() ) {
            reader.next();
            if( reader.isStartElement() && "Edmx".equalsIgnoreCase(reader.getLocalName()) ) {
                final String version = StringUtils.substringBefore(getAttribute(reader, "Version"), '.');
                if( "4".equals(version) ) {
                    log.debug("EDMX document qualifies for OData V4 protocol.");
                    return true;
                }
            }
        }

        log.debug("EDMX document does not qualify for OData V4 protocol.");
        return false;
    }

    /*
     * See reference implementation in org.apache.olingo.odata2.core.ep.consumer.XmlMetadataConsumer#readMetadata
     */
    private static boolean isQualifiedForODataV2( @Nonnull final XMLStreamReader reader )
        throws XMLStreamException
    {
        while( reader.hasNext() ) {
            reader.next();
            if( reader.isStartElement() && "DataServices".equalsIgnoreCase(reader.getLocalName()) ) {
                final String version = StringUtils.substringBefore(getAttribute(reader, "DataServiceVersion"), '.');
                if( "1".equals(version) || "2".equals(version) ) {
                    log.debug("EDMX document qualifies for OData V2 protocol.");
                    return true;
                }
            }
        }

        log.debug("EDMX document does not qualify for OData V2 protocol.");
        return false;
    }

    @Nullable
    private static String getAttribute( @Nonnull final XMLStreamReader reader, @Nonnull final String key )
    {
        for( int i = 0; i < reader.getAttributeCount(); i++ ) {
            if( key.equalsIgnoreCase(reader.getAttributeName(i).getLocalPart()) ) {
                return reader.getAttributeValue(i);
            }
        }
        return null;
    }
}
