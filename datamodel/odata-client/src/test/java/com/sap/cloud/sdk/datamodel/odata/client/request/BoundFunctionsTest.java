/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

class BoundFunctionsTest
{
    private static final String SERVICE_PATH = "/service/";
    private static final String FUNCTION = "Model.Function";
    private static final String ENTITY = "Entity";
    private static final ODataEntityKey ENTITY_KEY = new ODataEntityKey(ODataProtocol.V4);
    private static final ODataFunctionParameters FUNCTION_KEY = new ODataFunctionParameters(ODataProtocol.V4);

    @BeforeAll
    static void setup()
    {
        ENTITY_KEY.addKeyProperty("key1", "foo/bar");
        ENTITY_KEY.addKeyProperty("key2", 123);

        FUNCTION_KEY.addParameter("param1", "foo/bar");
        FUNCTION_KEY.addParameter("param2", 123);
    }

    // Reference service support for bound functions is terrible

    // working only on https://services.odata.org/V4/(S(hash))/TripPinServiceRW/:

    // /People('russellwhyte')/Microsoft.OData.SampleService.Models.TripPin.GetFavoriteAirline()
    // /People('russellwhyte')/Microsoft.OData.SampleService.Models.TripPin.GetFavoriteAirline
    // /People('russellwhyte')/Microsoft.OData.SampleService.Models.TripPin.GetFavoriteAirline()/Name

    // working only on http://services.odata.org/TripPinRESTierService/(S(3mslpb2bc0k5ufk24olpghzx))/:

    // /People('russellwhyte')/Trips(0)/Microsoft.OData.Service.Sample.TrippinInMemory.Models.GetInvolvedPeople()
    // /People('russellwhyte')/Trips(0)/Microsoft.OData.Service.Sample.TrippinInMemory.Models.GetInvolvedPeople
    // /People('russellwhyte')/Trips(0)/Microsoft.OData.Service.Sample.TrippinInMemory.Models.GetInvolvedPeople()?$top=1
    // /People('russellwhyte')/Trips(0)/Microsoft.OData.Service.Sample.TrippinInMemory.Models.GetInvolvedPeople()?$filter=FirstName%20eq%20'Russell'

    // not working:
    // /People('russellwhyte')/GetFavoriteAirline()
    // /People('russellwhyte')/GetFavoriteAirline

    // not supported in general:
    // $each
    // /People/$each/Microsoft.OData.SampleService.Models.TripPin.GetFavoriteAirline()

    @Test
    void testFunctionOnEntityCollection()
    {
        final String expected = "/service/Entity/Model.Function()";

        final ODataResourcePath functionPath =
            ODataResourcePath.of(ENTITY).addSegment(FUNCTION, ODataFunctionParameters.empty(ODataProtocol.V4));

        final ODataRequestFunction request =
            new ODataRequestFunction(SERVICE_PATH, functionPath, null, ODataProtocol.V4);

        final String actual = request.getRelativeUri().toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFunctionOnEntity()
    {
        final String expected = "/service/Entity(key1='foo%2Fbar',key2=123)/Model.Function()";

        final ODataResourcePath functionPath =
            new ODataResourcePath()
                .addSegment(ENTITY, ENTITY_KEY)
                .addSegment(FUNCTION, ODataFunctionParameters.empty(ODataProtocol.V4));

        final ODataRequestFunction request =
            new ODataRequestFunction(SERVICE_PATH, functionPath, null, ODataProtocol.V4);

        final String actual = request.getRelativeUri().toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFunctionOnPrimitive()
    {
        final String expected = "/service/Entity(key1='foo%2Fbar',key2=123)/SimpleProperty/Model.Function()";

        final ODataResourcePath functionPath =
            ODataResourcePath
                .of(ENTITY, ENTITY_KEY)
                .addSegment("SimpleProperty")
                .addSegment(FUNCTION, ODataFunctionParameters.empty(ODataProtocol.V4));

        final ODataRequestFunction request =
            new ODataRequestFunction(SERVICE_PATH, functionPath, null, ODataProtocol.V4);

        final String actual = request.getRelativeUri().toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFunctionOnNavigationalProperty()
    {
        final String expected =
            "/service/Entity(key1='foo%2Fbar',key2=123)/NavigationPropertyCollection(0)/Model.Function()";

        final ODataEntityKey key = new ODataEntityKey(ODataProtocol.V4);
        key.addKeyProperty("key1", 0);

        final ODataResourcePath functionPath =
            ODataResourcePath
                .of(ENTITY, ENTITY_KEY)
                .addSegment("NavigationPropertyCollection", key)
                .addSegment(FUNCTION, ODataFunctionParameters.empty(ODataProtocol.V4));

        final ODataRequestFunction request =
            new ODataRequestFunction(SERVICE_PATH, functionPath, null, ODataProtocol.V4);

        final String actual = request.getRelativeUri().toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFunctionWithParameters()
    {
        final String expected = "/service/Entity/Model.Function(param1='foo%2Fbar',param2=123)";

        final ODataResourcePath functionPath = ODataResourcePath.of(ENTITY).addSegment(FUNCTION, FUNCTION_KEY);

        final ODataRequestFunction request =
            new ODataRequestFunction(SERVICE_PATH, functionPath, null, ODataProtocol.V4);

        final String actual = request.getRelativeUri().toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testComposableFunction()
    {
        final String expected = "/service/Entity/Model.Function()/ResultProperty?$top=5";

        final ODataResourcePath functionPath =
            ODataResourcePath
                .of(ENTITY)
                .addSegment(FUNCTION, ODataFunctionParameters.empty(ODataProtocol.V4))
                .addSegment("ResultProperty");

        final ODataRequestFunction request =
            new ODataRequestFunction(SERVICE_PATH, functionPath, "$top=5", ODataProtocol.V4);

        final String actual = request.getRelativeUri().toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFunctionOnEachEntity()
    {
        final String expected = "/service/Entity/$each/Model.Function()";

        final ODataResourcePath functionPath =
            ODataResourcePath
                .of(ENTITY)
                .addSegment("$each")
                .addSegment(FUNCTION, ODataFunctionParameters.empty(ODataProtocol.V4));

        final ODataRequestFunction request =
            new ODataRequestFunction(SERVICE_PATH, functionPath, null, ODataProtocol.V4);

        final String actual = request.getRelativeUri().toString();

        assertThat(actual).isEqualTo(expected);
    }
}
