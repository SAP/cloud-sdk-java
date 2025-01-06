/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;

class FilterExpressionsWithNullTest
{
    private static final FieldUntyped field1 = FieldReference.of("FirstName");
    private static final FieldUntyped field2 = FieldReference.of("Age");

    @Test
    void testNullInComplexExpression()
    {
        final String expression =
            field1
                .equalTo(ValueString.NULL)
                .and(field2.greaterThan(ValueNumeric.NULL))
                .or(ValueBoolean.NULL)
                .getExpression(ODataProtocol.V4);
        assertThat(expression).isEqualTo("(((FirstName eq null) and (Age gt null)) or null)");

        final ODataRequestRead read =
            new ODataRequestRead("/service/path", "EntityName", "$filter=" + expression, ODataProtocol.V4);
        assertThat(read.getRequestQuery()).isEqualTo("$filter=(((FirstName eq null) and (Age gt null)) or null)");
    }

    @Test
    void testNullInBooleanExpression()
    {
        final String andExpression =
            field1.equalTo(ValueString.NULL).and(ValueBoolean.NULL).getExpression(ODataProtocol.V4);
        assertThat(andExpression).isEqualTo("((FirstName eq null) and null)");

        final String orExpression =
            field1.asString().contains(ValueString.NULL).or(ValueBoolean.NULL).getExpression(ODataProtocol.V4);
        assertThat(orExpression).isEqualTo("(contains(FirstName,null) or null)");
    }

    @Test
    void testNullInLogicalExpression()
    {
        assertThat(field1.equalTo(ValueString.NULL).getExpression(ODataProtocol.V4)).isEqualTo("(FirstName eq null)");
        assertThat(ValueNumeric.NULL.notEqualTo(123).getExpression(ODataProtocol.V4)).isEqualTo("(null ne 123)");

        assertThat(ValueNumeric.NULL.greaterThan(23).getExpression(ODataProtocol.V4)).isEqualTo("(null gt 23)");
        assertThat(ValueString.NULL.greaterThanEqual("abc").getExpression(ODataProtocol.V4))
            .isEqualTo("(null ge 'abc')");

        assertThat(ValueNumeric.NULL.lessThan(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(null lt null)");
        assertThat(ValueString.NULL.lessThanEqual("abc").getExpression(ODataProtocol.V4)).isEqualTo("(null le 'abc')");
    }

    @Test
    void testNullInStringExpressions()
    {
        final String expressionWithContains =
            field1.asString().contains(ValueString.NULL).getExpression(ODataProtocol.V4);
        assertThat(expressionWithContains).isEqualTo("contains(FirstName,null)");

        final String expressionWithSubStringOf =
            field1.asString().substringOf(ValueString.NULL).getExpression(ODataProtocol.V2);
        assertThat(expressionWithSubStringOf).isEqualTo("substringof(FirstName,null)");

        final String expressionWithSubStringOf1 =
            field1.asString().substringOf(ValueString.NULL).getExpression(ODataProtocol.V4);
        assertThat(expressionWithSubStringOf1).isEqualTo("substringof(FirstName,null)");

        final String expressionWithConcat = field1.asString().concat(ValueString.NULL).getExpression(ODataProtocol.V4);
        assertThat(expressionWithConcat).isEqualTo("concat(FirstName,null)");

        final String expressionWithStartsWith =
            field1.asString().startsWith(ValueString.NULL).getExpression(ODataProtocol.V4);
        assertThat(expressionWithStartsWith).isEqualTo("startswith(FirstName,null)");

        final String expressionWithEndsWith =
            field1.asString().endsWith(ValueString.NULL).getExpression(ODataProtocol.V4);
        assertThat(expressionWithEndsWith).isEqualTo("endswith(FirstName,null)");

        final String expressionWithToLower = ValueString.NULL.toLower().getExpression(ODataProtocol.V4);
        assertThat(expressionWithToLower).isEqualTo("tolower(null)");

        final String expressionWithToUpper = ValueString.NULL.toUpper().getExpression(ODataProtocol.V4);
        assertThat(expressionWithToUpper).isEqualTo("toupper(null)");

        final String expressionWithIndexOf =
            field1.asString().indexOf(ValueString.NULL).getExpression(ODataProtocol.V4);
        assertThat(expressionWithIndexOf).isEqualTo("indexof(FirstName,null)");

        final String expressionWithSubstring = ValueString.NULL.substring(1).getExpression(ODataProtocol.V4);
        assertThat(expressionWithSubstring).isEqualTo("substring(null,1)");

        final String expressionWithMatches =
            field1.asString().matches(ValueString.NULL).getExpression(ODataProtocol.V4);
        assertThat(expressionWithMatches).isEqualTo("matchesPattern(FirstName,null)");
    }

    @Test
    void testNullInCollectionExpression()
    {
        final ValueCollection collection = ValueCollection.literal(Lists.newArrayList("A", "B"));

        assertThat(ValueCollection.NULL.concat(ValueCollection.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("concat(null,null)");
        assertThat(collection.contains(ValueCollection.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("contains(['A','B'],null)");
        assertThat(collection.endsWith(ValueCollection.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("endswith(['A','B'],null)");
        assertThat(collection.hasSubSequence(ValueCollection.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("hassubsequence(['A','B'],null)");
        assertThat(collection.hasSubset(ValueCollection.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("hassubset(['A','B'],null)");
        assertThat(collection.indexOf(ValueCollection.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("indexof(['A','B'],null)");
        assertThat(collection.startsWith(ValueCollection.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("startswith(['A','B'],null)");
        assertThat(ValueCollection.NULL.substring(1).getExpression(ODataProtocol.V4)).isEqualTo("substring(null,1)");
    }

    @Test
    void testNullInArithmeticExpression()
    {
        assertThat(ValueNumeric.NULL.divide(3).getExpression(ODataProtocol.V4)).isEqualTo("(null divby 3)");
        assertThat(ValueNumeric.NULL.divide(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(null divby null)");

        assertThat(ValueNumeric.NULL.multiply(3).getExpression(ODataProtocol.V4)).isEqualTo("(null mul 3)");
        assertThat(ValueNumeric.NULL.multiply(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(null mul null)");

        assertThat(field2.asNumber().add(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(Age add null)");
        assertThat(ValueNumeric.NULL.add(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(null add null)");

        assertThat(field2.asNumber().subtract(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(Age sub null)");
        assertThat(ValueNumeric.NULL.subtract(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(null sub null)");

        assertThat(field2.asNumber().modulo(ValueNumeric.NULL).getExpression(ODataProtocol.V4))
            .isEqualTo("(Age mod null)");
    }
}
