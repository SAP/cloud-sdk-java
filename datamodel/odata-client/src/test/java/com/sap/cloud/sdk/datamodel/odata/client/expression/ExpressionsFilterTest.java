/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import static com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionLogical.not;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;

class ExpressionsFilterTest
{
    private static final FieldUntyped field = FieldReference.of("Field");

    @Test
    void testUntypedExpressions()
    {
        final FieldUntyped field1 = FieldReference.of("FirstName");
        final FieldUntyped field2 = FieldReference.of("Age");

        final String expression = field1.equalTo("Foobar").and(field2.greaterThan(42)).getExpression(ODataProtocol.V4);
        assertThat(expression).isEqualTo("((FirstName eq 'Foobar') and (Age gt 42))");

        final ODataRequestRead read =
            new ODataRequestRead("/service/path", "EntityName", "$filter=" + expression, ODataProtocol.V4);
        assertThat(read.getRequestQuery()).isEqualTo("$filter=((FirstName eq 'Foobar') and (Age gt 42))");
    }

    @Test
    void testTypedExpressions()
    {
        final FieldUntyped field1 = FieldReference.of("FirstName");
        final FieldUntyped field2 = FieldReference.of("Age");
        final FieldUntyped field3 = FieldReference.of("IsRetired");

        assertThat(field1.asBoolean()).isInstanceOf(ValueBoolean.class);
        assertThat(field1.asBinary()).isInstanceOf(ValueBinary.class);
        assertThat(field1.asString()).isInstanceOf(ValueString.class);
        assertThat(field1.asNumber()).isInstanceOf(ValueNumeric.class);
        assertThat(field1.asCollection()).isInstanceOf(ValueCollection.class);
        assertThat(field1.asDate()).isInstanceOf(ValueDate.class);
        assertThat(field1.asDateTimeOffset()).isInstanceOf(ValueDateTimeOffset.class);
        assertThat(field1.asDuration()).isInstanceOf(ValueDuration.class);
        assertThat(field1.asTimeOfDay()).isInstanceOf(ValueTimeOfDay.class);

        final ValueBoolean condition1 = field1.asString().substring(1, 5).equalTo("ooba");
        final ValueBoolean condition2 = field2.asNumber().modulo(10).equalTo(0);
        final ValueBoolean condition3 = field3.asBoolean().or(false);

        final String expression1 = condition1.and(condition2).getExpression(ODataProtocol.V4);
        assertThat(expression1).isEqualTo("((substring(FirstName,1,5) eq 'ooba') and ((Age mod 10) eq 0))");

        final String expression2 = condition1.or(condition3).getExpression(ODataProtocol.V4);
        assertThat(expression2).isEqualTo("((substring(FirstName,1,5) eq 'ooba') or (IsRetired or false))");
    }

    @Test
    void testNumberExpressions()
    {
        assertThat(field.asNumber().divide(3).getExpression(ODataProtocol.V4)).isEqualTo("(Field divby 3)");
        assertThat(field.asNumber().divide(field.asNumber()).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field divby Field)");

        assertThat(field.asNumber().multiply(3).getExpression(ODataProtocol.V4)).isEqualTo("(Field mul 3)");
        assertThat(field.asNumber().multiply(field.asNumber()).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field mul Field)");

        assertThat(field.asNumber().add(3).getExpression(ODataProtocol.V4)).isEqualTo("(Field add 3)");
        assertThat(field.asNumber().add(field.asNumber()).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field add Field)");

        assertThat(field.asNumber().subtract(3).getExpression(ODataProtocol.V4)).isEqualTo("(Field sub 3)");
        assertThat(field.asNumber().subtract(field.asNumber()).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field sub Field)");

        assertThat(field.asNumber().modulo(3).getExpression(ODataProtocol.V4)).isEqualTo("(Field mod 3)");
        assertThat(field.asNumber().modulo(field.asNumber()).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field mod Field)");
    }

    @Test
    void testBinaryExpressions()
    {
        assertThat(field.asBinary().equalTo(new byte[] { 1, 2, 3 }).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field eq binary'AQID')");
    }

    @Test
    void testBooleanExpressions()
    {
        assertThat(field.asBoolean().and(true).getExpression(ODataProtocol.V4)).isEqualTo("(Field and true)");
        assertThat(field.asBoolean().and(field.asBoolean()).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field and Field)");

        assertThat(field.asBoolean().or(true).getExpression(ODataProtocol.V4)).isEqualTo("(Field or true)");
        assertThat(field.asBoolean().or(field.asBoolean()).getExpression(ODataProtocol.V4))
            .isEqualTo("(Field or Field)");

        assertThat(field.asBoolean().not().getExpression(ODataProtocol.V4)).isEqualTo("(not Field)");

    }

    @Test
    void testStringExpressions()
    {
        assertThat(field.asString().concat("abc").getExpression(ODataProtocol.V4)).isEqualTo("concat(Field,'abc')");
        assertThat(field.asString().concat(field.asString()).getExpression(ODataProtocol.V4))
            .isEqualTo("concat(Field,Field)");

        assertThat(field.asString().startsWith("abc").getExpression(ODataProtocol.V4))
            .isEqualTo("startswith(Field,'abc')");
        assertThat(field.asString().startsWith(field.asString()).getExpression(ODataProtocol.V4))
            .isEqualTo("startswith(Field,Field)");

        assertThat(field.asString().endsWith("abc").getExpression(ODataProtocol.V4)).isEqualTo("endswith(Field,'abc')");
        assertThat(field.asString().endsWith(field.asString()).getExpression(ODataProtocol.V4))
            .isEqualTo("endswith(Field,Field)");

        assertThat(field.asString().contains("abc").getExpression(ODataProtocol.V4)).isEqualTo("contains(Field,'abc')");
        assertThat(field.asString().contains(field.asString()).getExpression(ODataProtocol.V4))
            .isEqualTo("contains(Field,Field)");

        assertThat(field.asString().substringOf("abc").getExpression(ODataProtocol.V2))
            .isEqualTo("substringof(Field,'abc')");
        assertThat(field.asString().substringOf(field.asString()).getExpression(ODataProtocol.V2))
            .isEqualTo("substringof(Field,Field)");

        assertThat(field.asString().toLower().getExpression(ODataProtocol.V4)).isEqualTo("tolower(Field)");
        assertThat(field.asString().toUpper().getExpression(ODataProtocol.V4)).isEqualTo("toupper(Field)");

        assertThat(field.asString().indexOf("abc").getExpression(ODataProtocol.V4)).isEqualTo("indexof(Field,'abc')");
        assertThat(field.asString().indexOf(field.asString()).getExpression(ODataProtocol.V4))
            .isEqualTo("indexof(Field,Field)");

        assertThat(field.asString().substring(1).getExpression(ODataProtocol.V4)).isEqualTo("substring(Field,1)");
        assertThat(field.asString().substring(1, 3).getExpression(ODataProtocol.V4)).isEqualTo("substring(Field,1,3)");

        assertThat(field.asString().matches("abc").getExpression(ODataProtocol.V4))
            .isEqualTo("matchesPattern(Field,'abc')");
        assertThat(field.asString().length().getExpression(ODataProtocol.V4)).isEqualTo("length(Field)");
        assertThat(field.asString().trim().getExpression(ODataProtocol.V4)).isEqualTo("trim(Field)");

    }

    @Test
    void testLogicalExpressions()
    {
        assertThat(field.equalTo("abc").getExpression(ODataProtocol.V4)).isEqualTo("(Field eq 'abc')");
        assertThat(field.notEqualTo(123).getExpression(ODataProtocol.V4)).isEqualTo("(Field ne 123)");

        assertThat(field.equalTo(field).getExpression(ODataProtocol.V4)).isEqualTo("(Field eq Field)");
        assertThat(field.notEqualTo(field).getExpression(ODataProtocol.V4)).isEqualTo("(Field ne Field)");

        assertThat(field.greaterThan(field).getExpression(ODataProtocol.V4)).isEqualTo("(Field gt Field)");
        assertThat(field.greaterThanEqual(field).getExpression(ODataProtocol.V4)).isEqualTo("(Field ge Field)");

        assertThat(field.greaterThan("abc").getExpression(ODataProtocol.V4)).isEqualTo("(Field gt 'abc')");
        assertThat(field.greaterThanEqual(123).getExpression(ODataProtocol.V4)).isEqualTo("(Field ge 123)");

        assertThat(field.lessThan(field).getExpression(ODataProtocol.V4)).isEqualTo("(Field lt Field)");
        assertThat(field.lessThanEqual(field).getExpression(ODataProtocol.V4)).isEqualTo("(Field le Field)");

        assertThat(field.lessThan("abc").getExpression(ODataProtocol.V4)).isEqualTo("(Field lt 'abc')");
        assertThat(field.lessThanEqual(123).getExpression(ODataProtocol.V4)).isEqualTo("(Field le 123)");

        assertThat(field.in("abc", "foo").getExpression(ODataProtocol.V4)).isEqualTo("(Field in ('abc','foo'))");
        assertThat(field.in(Arrays.asList(1, 2, 3)).getExpression(ODataProtocol.V4)).isEqualTo("(Field in (1,2,3))");
        assertThat(field.in(field.asCollection()).getExpression(ODataProtocol.V4)).isEqualTo("(Field in Field)");
    }

    @Test
    void testLogicalComplexExpressions()
    {
        final ValueBoolean cond1 = field.equalTo(1);
        final ValueBoolean cond2 = field.equalTo(2);
        final String cond1Exp = cond1.getExpression(ODataProtocol.V2);
        final String cond2Exp = cond2.getExpression(ODataProtocol.V2);

        String expected;
        String actual;

        expected = String.format("(not %s)", cond1Exp);
        assertThat(cond1.not().getExpression(ODataProtocol.V2)).isEqualTo(expected);
        assertThat(not(cond1).getExpression(ODataProtocol.V2)).isEqualTo(expected);

        expected = String.format("((not %s) and %s)", cond1Exp, cond2Exp);
        actual = cond1.not().and(cond2).getExpression(ODataProtocol.V2);
        assertThat(actual).isEqualTo(expected);

        expected = String.format("((not %s) or %s)", cond1Exp, cond2Exp);
        actual = cond1.not().or(cond2).getExpression(ODataProtocol.V2);
        assertThat(actual).isEqualTo(expected);

        expected = String.format("(not (%s or %s))", cond1Exp, cond2Exp);
        actual = cond1.or(cond2).not().getExpression(ODataProtocol.V2);
        assertThat(actual).isEqualTo(expected);

        expected = String.format("(not (%s or %s))", cond1Exp, cond2Exp);
        actual = not(cond1.or(cond2)).getExpression(ODataProtocol.V2);
        assertThat(actual).isEqualTo(expected);
    }
}
