/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

class MetadataFileUtils
{
    static Document getMetadataDocumentObject( final File metadataFile )
    {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            // add secure processing to circumvent XML Entity Expansion (XML Bomb)
            docBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            //disable inline doctype declaration to prevent XML External Entity Injection
            docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            //enable XML validation which is required to satisfy Fortify
            docBuilderFactory.setValidating(true);
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            return docBuilder.parse(metadataFile);
        }
        catch( final ParserConfigurationException | IOException | SAXException e ) {
            throw new ODataGeneratorReadException(e);
        }
    }
}
