package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static java.util.Objects.requireNonNull;

import static com.sap.cloud.sdk.s4hana.connectivity.rfc.AbapToSoapNameConverter.abapParameterNameToSoapParameterName;
import static com.sap.cloud.sdk.s4hana.connectivity.rfc.AbapToSoapNameConverter.soapParameterNameToAbapParameterName;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.json.JsonSanitizer;
import com.sap.cloud.sdk.result.ResultElement;

import lombok.RequiredArgsConstructor;

/**
 * Serializer for SOAP-based remote function queries.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic result type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor
@Deprecated
public class SoapRemoteFunctionRequestSerializer<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
    extends
    com.sap.cloud.sdk.s4hana.connectivity.AbstractRequestSerializer<RequestT, RequestResultT>
{
    private static final String SOAP_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";

    private static final String FEATURE_EXTERNAL_GENERAL_ENTITIES =
        "http://xml.org/sax/features/external-general-entities";

    private static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES =
        "http://xml.org/sax/features/external-parameter-entities";

    private final Class<RequestResultT> resultType;

    // FIXME type converters
    private final com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer erpTypeSerializer =
        new com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer()
            .withTypeConverters(
                new com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter("yyyy-MM-dd"),
                new com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter("HH:mm:ss"));

    private Document createNewSoapDocument()
        throws ParserConfigurationException
    {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setExpandEntityReferences(false);
        docBuilderFactory.setFeature(FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
        docBuilderFactory.setFeature(FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);

        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }

    private Element createSoapServiceMessageElement( final Document soapDocument, final String functionName )
    {
        final Element envelopeRootElement = createEnvelopeRootDocument(soapDocument);
        soapDocument.appendChild(envelopeRootElement);

        final Element envelopeHeaderElement = createElementInSoapNamespace(soapDocument, "soapenv:Header");
        envelopeRootElement.appendChild(envelopeHeaderElement);

        final Element envelopeBodyElement = createElementInSoapNamespace(soapDocument, "soapenv:Body");
        envelopeRootElement.appendChild(envelopeBodyElement);

        final String functionNameInSoapFormat = AbapToSoapNameConverter.abapFunctionNameToSoapMessageName(functionName);
        final Element soapServiceMessageElement = soapDocument.createElement("urn:" + functionNameInSoapFormat);
        envelopeBodyElement.appendChild(soapServiceMessageElement);

        return soapServiceMessageElement;
    }

    private String serializeSoapEnvelopeWithoutXmlHeaderLine( final Document soapDocument )
        throws TransformerException
    {
        // explicitly specify factory to avoid override by container
        final TransformerFactory tf =
            TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);

        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        final Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        final StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(soapDocument), new StreamResult(writer));

        return writer.toString();
    }

    private Element createEnvelopeRootDocument( final Document doc )
    {
        final Element envelopeRootElement = doc.createElementNS(SOAP_NAMESPACE_URI, "soapenv:Envelope");
        envelopeRootElement
            .setAttributeNS(
                "http://www.w3.org/2000/xmlns/",
                "xmlns:urn",
                "urn:sap-com:document:sap:soap:functions:mc-style");
        return envelopeRootElement;
    }

    private Element createElementInSoapNamespace( final Document soapDocument, final String elementName )
    {
        return soapDocument.createElementNS(SOAP_NAMESPACE_URI, elementName);
    }

    /**
     * Returns the results of the return parameters.
     *
     * @param result
     *            The result of the request.
     * @return The results of the return parameters.
     */
    protected List<AbstractRemoteFunctionRequestResult.Result> getReturnParameterResults( final RequestResultT result )
    {
        final Set<String> returnParameterNames = result.getRequest().getReturnParameterNames();
        final List<AbstractRemoteFunctionRequestResult.Result> returnParameterResults = new ArrayList<>();

        final ArrayList<AbstractRemoteFunctionRequestResult.Result> resultList = result.getResultList();

        if( resultList != null ) {
            for( final AbstractRemoteFunctionRequestResult.Result resultItem : resultList ) {
                if( returnParameterNames.contains(resultItem.getName()) ) {
                    returnParameterResults.add(resultItem);
                }
            }
        }

        return returnParameterResults;
    }

    @Nonnull
    @Override
    protected com.sap.cloud.sdk.s4hana.connectivity.SerializedRequest<RequestT> serializeRequest(
        @Nonnull final RequestT request )
        throws ParserConfigurationException,
            TransformerException
    {
        final Document soapDocument = createNewSoapDocument();

        final Element soapServiceMessageElement =
            createSoapServiceMessageElement(soapDocument, request.getFunctionName());

        for( final Parameter parameter : request.getParameters() ) {
            //Skip adding importing (output) parameters
            if( ParameterKind.IMPORTING == parameter.getParameterKind() ) {
                continue;
            }
            serializeValue(soapDocument, soapServiceMessageElement, parameter.getParameterValue());
        }

        return new SoapSerializedRequestBuilder<>(request, serializeSoapEnvelopeWithoutXmlHeaderLine(soapDocument))
            .build();
    }

    private void serializeValue(
        @Nonnull final Document soapDocument,
        @Nonnull final Element container,
        @Nonnull final Value<?> value )
    {
        final String currentParameterNameInSoapNamePattern =
            abapParameterNameToSoapParameterName(requireNonNull(value.getName()));

        final Element currentParameterElement = soapDocument.createElement(currentParameterNameInSoapNamePattern);
        container.appendChild(currentParameterElement);

        switch( value.getValueType() ) {
            case FIELD:
                currentParameterElement.setTextContent(erpTypeSerializer.toErp(value.getValue()).orNull());
                break;
            case STRUCTURE:
                for( final Value<?> item : value.getAsStructure() ) {
                    serializeValue(soapDocument, currentParameterElement, item);
                }
                break;
            case TABLE:
                final List<List<Value<?>>> cells = value.getAsTable();
                final boolean isTableVector =
                    !cells.isEmpty() && cells.get(0).size() == 1 && cells.get(0).get(0).getName() == null;
                for( final List<Value<?>> values : cells ) {
                    final Element row = soapDocument.createElement("item");
                    currentParameterElement.appendChild(row);
                    for( final Value<?> item : values ) {
                        if( isTableVector ) {
                            row.setTextContent(erpTypeSerializer.toErp(item.getValue()).orNull());
                        } else {
                            serializeValue(soapDocument, row, item);
                        }
                    }
                }
                break;
        }
    }

    private JsonElement unpackNestedArrays( final JsonElement jsonElement )
    {
        if( jsonElement.isJsonObject() ) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            final JsonElement nestedArray = jsonObject.get("item");

            if( nestedArray == null ) {
                final JsonObject rewrittenObject = new JsonObject();

                for( final Entry<String, JsonElement> entry : jsonObject.entrySet() ) {
                    final JsonElement attribute = entry.getValue();
                    final JsonElement unpacked = unpackNestedArrays(attribute);

                    rewrittenObject.add(soapParameterNameToAbapParameterName(requireNonNull(entry.getKey())), unpacked);
                }

                return rewrittenObject;
            } else {
                if( nestedArray.isJsonArray() ) {
                    return unpackNestedArrays(nestedArray.getAsJsonArray());
                } else if( nestedArray.isJsonObject() ) {
                    final JsonArray jsonArray = new JsonArray();
                    jsonArray.add(unpackNestedArrays(nestedArray));
                    return jsonArray;
                }
            }
        }

        if( jsonElement.isJsonArray() ) {
            final JsonArray jsonArray = jsonElement.getAsJsonArray();

            for( int i = 0; i < jsonArray.size(); ++i ) {
                final JsonElement elementInArray = jsonArray.get(i);
                final JsonElement unpacked = unpackNestedArrays(elementInArray);

                if( unpacked != elementInArray ) {
                    jsonArray.set(i, unpacked);
                }
            }
        }

        return jsonElement;
    }

    @Nonnull
    @Override
    protected RequestResultT deserializeRequestResult(
        @Nonnull final com.sap.cloud.sdk.s4hana.connectivity.SerializedRequestResult<RequestT> serializedRequestResult )
        throws ParserConfigurationException
    {
        final RequestT request = serializedRequestResult.getRequest();

        final JsonElement bodyAsJson =
            JsonParser
                .parseString(
                    JsonSanitizer.sanitize(XML.toJSONObject(serializedRequestResult.getBody(), true).toString()));

        final String resultSoapElementName =
            SoapNamespace.RESPONSE_PREFIX_N0
                + ":"
                + AbapToSoapNameConverter.abapFunctionNameToSoapMessageName(request.getFunctionName())
                + "Response";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        factory.setValidating(true);
        factory.setFeature(FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
        factory.setFeature(FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);

        final JsonElement json =
            unpackNestedArrays(
                bodyAsJson
                    .getAsJsonObject()
                    .get(SoapNamespace.RESPONSE_PREFIX_SOAP_ENV + ":Envelope")
                    .getAsJsonObject()
                    .get(SoapNamespace.RESPONSE_PREFIX_SOAP_ENV + ":Body")
                    .getAsJsonObject()
                    .get(resultSoapElementName));

        final JsonObject resultObj = new JsonObject();
        final JsonArray array = new JsonArray();
        resultObj.add("RESULT", array);
        for( final Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet() ) {
            if( ("XMLNS:" + SoapNamespace.RESPONSE_PREFIX_N0).equalsIgnoreCase(entry.getKey()) ) {
                continue;
            }

            final String parameterName = entry.getKey();
            final JsonElement parameterValue = entry.getValue();

            final JsonObject obj = new JsonObject();
            obj.addProperty("NAME", parameterName);
            obj.add("VALUE", parameterValue);
            array.add(obj);
        }

        // FIXME type converters
        final List<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters =
            Lists.newArrayList(request.getTypeConverters());
        typeConverters.add(new com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter("yyyy-MM-dd"));
        typeConverters.add(new com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter("HH:mm:ss"));

        final GsonBuilder gsonBuilder = RemoteFunctionGsonBuilder.newSoapRequestResultGsonBuilder(typeConverters);

        final RequestResultT result = gsonBuilder.create().fromJson(resultObj, resultType);
        result.setRequest(request);

        final AbstractRemoteFunctionRequestResult.ExceptionResult exceptionResult = result.getException();

        if( exceptionResult != null ) {
            MessageResultReader.addMessageToResult(result, exceptionResult);
        }

        for( final AbstractRemoteFunctionRequestResult.Result returnParameterResult : getReturnParameterResults(
            result) ) {

            final ResultElement resultElement = returnParameterResult.getValue();
            final Collection<ResultElement> elements = new ArrayList<>();

            if( resultElement.isResultCollection() ) {
                Iterables.addAll(elements, resultElement.getAsCollection());
            } else {
                elements.add(resultElement);
            }

            for( final ResultElement element : elements ) {
                if( element.isResultObject() ) {
                    MessageResultReader
                        .addMessageToResult(
                            result,
                            element.getAsObject().as(AbstractRemoteFunctionRequestResult.MessageResult.class));
                }
            }
        }

        return result;
    }
}
