package com.sap.cloud.sdk.datamodel.odata.client.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

class ExpressionsTest
{
    private static final LocalDate localDate = LocalDate.of(2001, 1, 1);
    private static final LocalTime localTime = LocalTime.of(13, 37, 00);
    private static final LocalTime localTimeWith3Nanos = LocalTime.of(13, 37, 00, 111);
    private static final LocalTime localTimeWith9Nanos = LocalTime.of(13, 37, 00, 111111111);
    private static final LocalDateTime localDateTime = LocalDateTime.of(2001, 1, 1, 13, 37, 00);
    private static final LocalDateTime localDateTimeWith3Nanos = LocalDateTime.of(2001, 1, 1, 13, 37, 00, 111);
    private static final LocalDateTime localDateTimeWith9Nanos = LocalDateTime.of(2001, 1, 1, 13, 37, 00, 111111111);

    private static final ValueDate date1 = ValueDate.literal(localDate);
    private static final ValueDate date2 = ValueDate.literal(localDate.plusDays(1));
    private static final ValueDateTime dateTime1 = ValueDateTime.literal(localDateTime);
    private static final ValueDateTime dateTimeWith3Nanos = ValueDateTime.literal(localDateTimeWith3Nanos);
    private static final ValueDateTime dateTimeWith9Nanos = ValueDateTime.literal(localDateTimeWith9Nanos);
    private static final ValueDuration duration1 = ValueDuration.literal(Duration.ofDays(1));
    private static final ValueDuration duration2 = ValueDuration.literal(Duration.ofDays(2));
    private static final ValueNumeric numberInt = ValueNumeric.literal(1);
    private static final ValueNumeric number2 = ValueNumeric.literal(2);
    private static final ValueDateTimeOffset dateTimeOffset =
        ValueDateTimeOffset.literal(LocalDateTime.of(localDate, localTime).atOffset(ZoneOffset.UTC));
    private static final ValueDateTimeOffset dateTimeOffsetWith3Nanos =
        ValueDateTimeOffset.literal(LocalDateTime.of(localDate, localTimeWith3Nanos).atOffset(ZoneOffset.UTC));
    private static final ValueDateTimeOffset dateTimeOffsetWith9Nanos =
        ValueDateTimeOffset.literal(LocalDateTime.of(localDate, localTimeWith9Nanos).atOffset(ZoneOffset.UTC));
    private static final ValueTimeOfDay timeOfDay = ValueTimeOfDay.literal(localTime);
    private static final ValueNumeric numberFloat = ValueNumeric.literal(0.9f);
    // the below example covers that we transform the exponential notation to a plain value in case of OData V2
    // don't randomly change this since it doesn't work with all values, e.g. 234.5E-2 doesn't work
    private static final ValueNumeric numberDecimal = ValueNumeric.literal(new BigDecimal("1.1E+2"));
    private static final ValueNumeric numberDouble = ValueNumeric.literal(Double.parseDouble("1E+10d"));

    private final ValueCollection multiple1 = ValueCollection.literal(Lists.newArrayList("A", "B"));
    private final ValueCollection multiple2 = ValueCollection.literal(Lists.newArrayList("C", "D"));
    private final ValueBoolean boolean1 = ValueBoolean.literal(true);
    private final ValueBoolean boolean2 = ValueBoolean.literal(false);
    private final ValueString string1 = ValueString.literal("Aaa");
    private final ValueString string2 = ValueString.literal("Bbb");
    private final ValueCollection fieldCollection = FieldReference.of("Friends").asCollection();
    private final ValueCollection fieldCollection2 = FieldReference.of("Feet").asCollection();
    private final FieldUntyped fieldUntyped = FieldReference.of("BestFriend");
    private final FieldReference fieldComplex = () -> "OperatingSystem";
    private final FieldReference fieldPrimitive = () -> "ShowSize";
    private final ValueGuid guid = ValueGuid.literal(UUID.fromString("b3e130fe-d72c-4a5b-8dcf-463b497f985c"));

    private final ValueEnum fieldEnum = ValueEnum.literal("EnumType", "EnumValue");
    private final ValueEnum fieldEnumAnonymous = ValueEnum.literal("Debian");

    @Test
    void testLiteralsV2()
    {
        assertThat(numberInt.getExpression(ODataProtocol.V2)).isEqualTo("1");
        assertThat(numberFloat.getExpression(ODataProtocol.V2)).isEqualTo("0.9f");
        assertThat(numberDecimal.getExpression(ODataProtocol.V2)).isEqualTo("110M");
        assertThat(numberDouble.getExpression(ODataProtocol.V2)).isEqualTo("1.0E10d");

        assertThat(guid.getExpression(ODataProtocol.V2)).isEqualTo("guid'b3e130fe-d72c-4a5b-8dcf-463b497f985c'");
        assertThat(timeOfDay.getExpression(ODataProtocol.V2)).isEqualTo("time'PT13H37M'");
        assertThat(dateTime1.getExpression(ODataProtocol.V2)).isEqualTo("datetime'2001-01-01T13:37:00'");
        assertThat(dateTimeWith3Nanos.getExpression(ODataProtocol.V2))
            .isEqualTo("datetime'2001-01-01T13:37:00.0000001'");
        assertThat(dateTimeWith9Nanos.getExpression(ODataProtocol.V2))
            .isEqualTo("datetime'2001-01-01T13:37:00.1111111'");
        assertThat(dateTimeOffset.getExpression(ODataProtocol.V2)).isEqualTo("datetimeoffset'2001-01-01T13:37:00Z'");
    }

    @Test
    void testLiteralsV4()
    {
        assertThat(numberInt.getExpression(ODataProtocol.V4)).isEqualTo("1");
        assertThat(numberFloat.getExpression(ODataProtocol.V4)).isEqualTo("0.9");
        assertThat(numberDecimal.getExpression(ODataProtocol.V4)).isEqualTo("1.1E+2");
        assertThat(numberDouble.getExpression(ODataProtocol.V4)).isEqualTo("1.0E10");

        assertThat(guid.getExpression(ODataProtocol.V4)).isEqualTo("b3e130fe-d72c-4a5b-8dcf-463b497f985c");
        assertThat(fieldEnum.getExpression(ODataProtocol.V4)).isEqualTo("EnumType'EnumValue'");

        assertThat(duration1.getExpression(ODataProtocol.V4)).isEqualTo("duration'PT24H'");
        assertThat(timeOfDay.getExpression(ODataProtocol.V4)).isEqualTo("13:37:00");
        assertThat(date1.getExpression(ODataProtocol.V4)).isEqualTo("2001-01-01");
        assertThat(dateTimeOffset.getExpression(ODataProtocol.V4)).isEqualTo("2001-01-01T13:37:00Z");
        assertThat(dateTimeOffsetWith3Nanos.getExpression(ODataProtocol.V4))
            .isEqualTo("2001-01-01T13:37:00.000000111Z");
        assertThat(dateTimeOffsetWith9Nanos.getExpression(ODataProtocol.V4))
            .isEqualTo("2001-01-01T13:37:00.111111111Z");
    }

    @Test
    void testArithmethic()
    {
        assertThat(numberInt.add(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 add 2)");
        assertThat(date1.add(duration1).getExpression(ODataProtocol.V4)).isEqualTo("(2001-01-01 add duration'PT24H')");
        assertThat(date1.add(Duration.ofHours(24)).getExpression(ODataProtocol.V4))
            .isEqualTo("(2001-01-01 add duration'PT24H')");
        assertThat(dateTimeOffset.add(duration1).getExpression(ODataProtocol.V4))
            .isEqualTo("(2001-01-01T13:37:00Z add duration'PT24H')");
        assertThat(dateTimeOffset.add(Duration.ofHours(24)).getExpression(ODataProtocol.V4))
            .isEqualTo("(2001-01-01T13:37:00Z add duration'PT24H')");
        assertThat(duration1.add(duration2).getExpression(ODataProtocol.V4))
            .isEqualTo("(duration'PT24H' add duration'PT48H')");
        assertThat(duration1.add(Duration.ofHours(48)).getExpression(ODataProtocol.V4))
            .isEqualTo("(duration'PT24H' add duration'PT48H')");
        assertThat(numberFloat.ceil().getExpression(ODataProtocol.V4)).isEqualTo("ceiling(0.9)");
        assertThat(numberInt.divide(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 divby 2)");
        assertThat(duration1.divide(numberInt).getExpression(ODataProtocol.V4)).isEqualTo("(duration'PT24H' div 1)");
        assertThat(duration1.divide(1).getExpression(ODataProtocol.V4)).isEqualTo("(duration'PT24H' div 1)");
        assertThat(numberFloat.floor().getExpression(ODataProtocol.V4)).isEqualTo("floor(0.9)");
        assertThat(numberInt.modulo(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 mod 2)");
        assertThat(numberInt.multiply(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 mul 2)");
        assertThat(duration1.multiply(numberInt).getExpression(ODataProtocol.V4)).isEqualTo("(duration'PT24H' mul 1)");
        assertThat(duration1.multiply(1).getExpression(ODataProtocol.V4)).isEqualTo("(duration'PT24H' mul 1)");
        assertThat(numberInt.negate().getExpression(ODataProtocol.V4)).isEqualTo("-(1)");
        assertThat(duration1.negate().getExpression(ODataProtocol.V4)).isEqualTo("-(duration'PT24H')");
        assertThat(numberFloat.round().getExpression(ODataProtocol.V4)).isEqualTo("round(0.9)");
        assertThat(numberInt.subtract(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 sub 2)");
        assertThat(date1.difference(date2).getExpression(ODataProtocol.V4)).isEqualTo("(2001-01-01 sub 2001-01-02)");
        assertThat(dateTimeOffset.subtract(duration1).getExpression(ODataProtocol.V4))
            .isEqualTo("(2001-01-01T13:37:00Z sub duration'PT24H')");
        assertThat(dateTimeOffset.subtract(Duration.ofHours(24)).getExpression(ODataProtocol.V4))
            .isEqualTo("(2001-01-01T13:37:00Z sub duration'PT24H')");
        assertThat(duration1.subtract(duration2).getExpression(ODataProtocol.V4))
            .isEqualTo("(duration'PT24H' sub duration'PT48H')");
        assertThat(duration1.subtract(Duration.ofHours(48)).getExpression(ODataProtocol.V4))
            .isEqualTo("(duration'PT24H' sub duration'PT48H')");
        assertThat(date1.subtract(duration2).getExpression(ODataProtocol.V4))
            .isEqualTo("(2001-01-01 sub duration'PT48H')");
        assertThat(date1.subtract(Duration.ofHours(48)).getExpression(ODataProtocol.V4))
            .isEqualTo("(2001-01-01 sub duration'PT48H')");

        // not exposed directly
        assertThat(FilterExpressionArithmetic.divideEuclidean(numberInt, number2).getExpression(ODataProtocol.V4))
            .isEqualTo("(1 div 2)");
    }

    @Test
    void testCollection()
    {
        assertThat(multiple1.concat(multiple2).getExpression(ODataProtocol.V4))
            .isEqualTo("concat(['A','B'],['C','D'])");
        assertThat(multiple1.concat(Arrays.asList("C", "D")).getExpression(ODataProtocol.V4))
            .isEqualTo("concat(['A','B'],['C','D'])");

        assertThat(multiple1.contains(multiple2).getExpression(ODataProtocol.V4))
            .isEqualTo("contains(['A','B'],['C','D'])");
        assertThat(multiple1.contains(Arrays.asList("C", "D")).getExpression(ODataProtocol.V4))
            .isEqualTo("contains(['A','B'],['C','D'])");

        assertThat(multiple1.endsWith(multiple2).getExpression(ODataProtocol.V4))
            .isEqualTo("endswith(['A','B'],['C','D'])");
        assertThat(multiple1.endsWith(Arrays.asList("C", "D")).getExpression(ODataProtocol.V4))
            .isEqualTo("endswith(['A','B'],['C','D'])");

        assertThat(multiple1.hasSubSequence(multiple2).getExpression(ODataProtocol.V4))
            .isEqualTo("hassubsequence(['A','B'],['C','D'])");
        assertThat(multiple1.hasSubSequence(Arrays.asList("C", "D")).getExpression(ODataProtocol.V4))
            .isEqualTo("hassubsequence(['A','B'],['C','D'])");

        assertThat(multiple1.hasSubset(multiple2).getExpression(ODataProtocol.V4))
            .isEqualTo("hassubset(['A','B'],['C','D'])");
        assertThat(multiple1.hasSubset(Arrays.asList("C", "D")).getExpression(ODataProtocol.V4))
            .isEqualTo("hassubset(['A','B'],['C','D'])");

        assertThat(multiple1.indexOf(multiple2).getExpression(ODataProtocol.V4))
            .isEqualTo("indexof(['A','B'],['C','D'])");
        assertThat(multiple1.indexOf(Arrays.asList("C", "D")).getExpression(ODataProtocol.V4))
            .isEqualTo("indexof(['A','B'],['C','D'])");

        assertThat(multiple1.startsWith(multiple2).getExpression(ODataProtocol.V4))
            .isEqualTo("startswith(['A','B'],['C','D'])");
        assertThat(multiple1.startsWith(Arrays.asList("C", "D")).getExpression(ODataProtocol.V4))
            .isEqualTo("startswith(['A','B'],['C','D'])");

        assertThat(multiple1.length().getExpression(ODataProtocol.V4)).isEqualTo("length(['A','B'])");

        assertThat(multiple1.substring(1).getExpression(ODataProtocol.V4)).isEqualTo("substring(['A','B'],1)");
        assertThat(multiple1.substring(1, 2).getExpression(ODataProtocol.V4)).isEqualTo("substring(['A','B'],1,2)");

        // test lambdas
        final ValueBoolean.Expression c = FilterExpressionLogical.equalTo(fieldPrimitive, numberInt);
        assertThat(fieldCollection.all(c).getExpression(ODataProtocol.V4))
            .isEqualTo("Friends/all(a:(a/ShowSize eq 1))");
        assertThat(fieldCollection.any(c).getExpression(ODataProtocol.V4))
            .isEqualTo("Friends/any(a:(a/ShowSize eq 1))");
        assertThat(fieldCollection.any(fieldCollection2.any(c)).getExpression(ODataProtocol.V4))
            .isEqualTo("Friends/any(a:a/Feet/any(b:(b/ShowSize eq 1)))");
        assertThat(fieldCollection.any().getExpression(ODataProtocol.V4)).isEqualTo("Friends/any()");
    }

    @Test
    void testLogical()
    {
        assertThat(boolean1.in(boolean1, boolean2).getExpression(ODataProtocol.V4)).isEqualTo("(true in (true,false))");
        assertThat(numberInt.in(13.37, 42, "foo").getExpression(ODataProtocol.V4)).isEqualTo("(1 in (13.37,42,'foo'))");
        assertThat(numberInt.in(Arrays.asList(1, 2, 3)).getExpression(ODataProtocol.V4)).isEqualTo("(1 in (1,2,3))");
        assertThat(numberInt.in(fieldCollection).getExpression(ODataProtocol.V4)).isEqualTo("(1 in Friends)");
        assertThat(fieldUntyped.in(fieldCollection).getExpression(ODataProtocol.V4))
            .isEqualTo("(BestFriend in Friends)");
        assertThat(boolean1.and(boolean2).getExpression(ODataProtocol.V4)).isEqualTo("(true and false)");
        assertThat(multiple1.equalTo(multiple2).getExpression(ODataProtocol.V4)).isEqualTo("(['A','B'] eq ['C','D'])");
        assertThat(guid.equalTo(guid).getExpression(ODataProtocol.V4))
            .isEqualTo("(b3e130fe-d72c-4a5b-8dcf-463b497f985c eq b3e130fe-d72c-4a5b-8dcf-463b497f985c)");
        assertThat(numberInt.greaterThan(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 gt 2)");
        assertThat(numberInt.lessThan(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 lt 2)");
        assertThat(numberInt.greaterThanEqual(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 ge 2)");
        assertThat(numberInt.lessThanEqual(number2).getExpression(ODataProtocol.V4)).isEqualTo("(1 le 2)");
        assertThat(boolean1.not().getExpression(ODataProtocol.V4)).isEqualTo("(not true)");
        assertThat(multiple1.notEqualTo(numberFloat).getExpression(ODataProtocol.V4)).isEqualTo("(['A','B'] ne 0.9)");
        assertThat(boolean1.or(boolean2).getExpression(ODataProtocol.V4)).isEqualTo("(true or false)");
        assertThat(string1.equalToNull().getExpression(ODataProtocol.V4)).isEqualTo("('Aaa' eq null)");
        assertThat(string1.equalToNull().not().getExpression(ODataProtocol.V4)).isEqualTo("(not ('Aaa' eq null))");
        assertThat(string1.notEqualToNull().getExpression(ODataProtocol.V4)).isEqualTo("('Aaa' ne null)");

        // not exposed directly
        assertThat(FilterExpressionLogical.has(fieldComplex, fieldEnumAnonymous).getExpression(ODataProtocol.V4))
            .isEqualTo("(OperatingSystem has 'Debian')");
    }

    @Test
    void testString()
    {
        assertThat(string1.concat(string2).getExpression(ODataProtocol.V4)).isEqualTo("concat('Aaa','Bbb')");
        assertThat(string1.contains(string2).getExpression(ODataProtocol.V4)).isEqualTo("contains('Aaa','Bbb')");
        assertThat(string1.substringOf(string2).getExpression(ODataProtocol.V2)).isEqualTo("substringof('Aaa','Bbb')");
        assertThat(string1.substringOf(string2).getExpression(ODataProtocol.V2)).isEqualTo("substringof('Aaa','Bbb')");
        assertThat(string1.endsWith(string2).getExpression(ODataProtocol.V4)).isEqualTo("endswith('Aaa','Bbb')");
        assertThat(string1.indexOf(string2).getExpression(ODataProtocol.V4)).isEqualTo("indexof('Aaa','Bbb')");
        assertThat(string1.startsWith(string2).getExpression(ODataProtocol.V4)).isEqualTo("startswith('Aaa','Bbb')");
        assertThat(string1.length().getExpression(ODataProtocol.V4)).isEqualTo("length('Aaa')");
        assertThat(string1.matches(string2).getExpression(ODataProtocol.V4)).isEqualTo("matchesPattern('Aaa','Bbb')");
        assertThat(string1.substring(1, 2).getExpression(ODataProtocol.V4)).isEqualTo("substring('Aaa',1,2)");
        assertThat(string1.substring(1).getExpression(ODataProtocol.V4)).isEqualTo("substring('Aaa',1)");
        assertThat(string1.toLower().getExpression(ODataProtocol.V4)).isEqualTo("tolower('Aaa')");
        assertThat(string1.toUpper().getExpression(ODataProtocol.V4)).isEqualTo("toupper('Aaa')");
        assertThat(string1.trim().getExpression(ODataProtocol.V4)).isEqualTo("trim('Aaa')");
    }

    @Test
    void testTemporal()
    {
        assertThat(dateTimeOffset.date().getExpression(ODataProtocol.V4)).isEqualTo("date(2001-01-01T13:37:00Z)");
        assertThat(date1.dateDay().getExpression(ODataProtocol.V4)).isEqualTo("day(2001-01-01)");
        assertThat(dateTimeOffset.dateDay().getExpression(ODataProtocol.V4)).isEqualTo("day(2001-01-01T13:37:00Z)");
        assertThat(dateTimeOffset.timeFractionalSeconds().getExpression(ODataProtocol.V4))
            .isEqualTo("fractionalseconds(2001-01-01T13:37:00Z)");
        assertThat(timeOfDay.timeFractionalSeconds().getExpression(ODataProtocol.V4))
            .isEqualTo("fractionalseconds(13:37:00)");
        assertThat(dateTimeOffset.timeHour().getExpression(ODataProtocol.V4)).isEqualTo("hour(2001-01-01T13:37:00Z)");
        assertThat(dateTimeOffset.timeMinute().getExpression(ODataProtocol.V4))
            .isEqualTo("minute(2001-01-01T13:37:00Z)");
        assertThat(timeOfDay.timeMinute().getExpression(ODataProtocol.V4)).isEqualTo("minute(13:37:00)");
        assertThat(date1.dateMonth().getExpression(ODataProtocol.V4)).isEqualTo("month(2001-01-01)");
        assertThat(dateTimeOffset.dateMonth().getExpression(ODataProtocol.V4)).isEqualTo("month(2001-01-01T13:37:00Z)");
        assertThat(timeOfDay.timeHour().getExpression(ODataProtocol.V4)).isEqualTo("hour(13:37:00)");
        assertThat(dateTimeOffset.timeSecond().getExpression(ODataProtocol.V4))
            .isEqualTo("second(2001-01-01T13:37:00Z)");
        assertThat(timeOfDay.timeSecond().getExpression(ODataProtocol.V4)).isEqualTo("second(13:37:00)");
        assertThat(dateTimeOffset.time().getExpression(ODataProtocol.V4)).isEqualTo("time(2001-01-01T13:37:00Z)");
        assertThat(dateTimeOffset.offsetMinutes().getExpression(ODataProtocol.V4))
            .isEqualTo("totaloffsetminutes(2001-01-01T13:37:00Z)");
        assertThat(duration1.offsetSeconds().getExpression(ODataProtocol.V4))
            .isEqualTo("totaloffsetseconds(duration'PT24H')");
        assertThat(date1.dateYear().getExpression(ODataProtocol.V4)).isEqualTo("year(2001-01-01)");
        assertThat(dateTimeOffset.dateYear().getExpression(ODataProtocol.V4)).isEqualTo("year(2001-01-01T13:37:00Z)");

        // custom time zone
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        final OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.ofHoursMinutes(1, 0));
        final ValueDateTimeOffset dateTimeOffset2 = ValueDateTimeOffset.literal(offsetDateTime);
        assertThat(dateTimeOffset2.getExpression(ODataProtocol.V4)).isEqualTo("2001-01-01T13:37:00+01:00");
    }

    @Test
    void testGlobalFunctions()
    {
        assertThat(FilterExpressionTemporal.maxDateTime().getExpression(ODataProtocol.V4)).isEqualTo("maxdatetime()");
        assertThat(FilterExpressionTemporal.minDateTime().getExpression(ODataProtocol.V4)).isEqualTo("mindatetime()");
        assertThat(FilterExpressionTemporal.now().getExpression(ODataProtocol.V4)).isEqualTo("now()");
    }

    @Test
    void testPrimitives()
    {
        assertThat(Expressions.createOperand("A")).isInstanceOf(ValueString.class);
        assertThat(Expressions.createOperand(true)).isInstanceOf(ValueBoolean.class);
        assertThat(Expressions.createOperand(UUID.randomUUID())).isInstanceOf(ValueGuid.class);
        assertThat(Expressions.createOperand(42)).isInstanceOf(ValueNumeric.class);
        assertThat(Expressions.createOperand(42.0)).isInstanceOf(ValueNumeric.class);
        assertThat(Expressions.createOperand(Duration.ofMinutes(1))).isInstanceOf(ValueDuration.class);
        assertThat(Expressions.createOperand(OffsetDateTime.now())).isInstanceOf(ValueDateTimeOffset.class);
        assertThat(Expressions.createOperand(LocalDateTime.now())).isInstanceOf(ValueDateTime.class);
        assertThat(Expressions.createOperand(LocalDate.now())).isInstanceOf(ValueDate.class);
        assertThat(Expressions.createOperand(LocalTime.now())).isInstanceOf(ValueTimeOfDay.class);
        assertThat(Expressions.createOperand(null)).isInstanceOf(Expressions.OperandSingle.class);
        assertThat(Expressions.createOperand(numberFloat)).isEqualTo(numberFloat);
        assertThatCode(() -> Expressions.createOperand(this)).isInstanceOf(IllegalArgumentException.class);

        assertThat(FieldReference.ofPath("Friends", "NestedFriends").getFieldName())
            .isEqualTo(FieldReference.ofPath("Friends/NestedFriends").getFieldName());
    }

    @Test
    void testOperands()
    {
        final FilterExpression f2 = FilterExpressionTemporal.now();
        assertThat(f2.getOperands()).isEqualTo(Collections.emptyList());
        assertThat(f2.getOperator()).isEqualTo("now");

        final FilterExpression f3 = FilterExpressionTemporal.date(dateTimeOffset);
        assertThat(f3.getOperands()).isEqualTo(Collections.singletonList(dateTimeOffset));

        final FilterExpression f4 = FilterExpressionString.substring(string1, numberInt);
        assertThat(f4.getOperands()).isEqualTo(Lists.newArrayList(string1, numberInt));

        final FilterExpression f5 = FilterExpressionCollection.substring(fieldCollection, numberInt, number2);
        assertThat(f5.getOperands()).isEqualTo(Lists.newArrayList(fieldCollection, numberInt, number2));

        final ValueBoolean.Expression f6 = FilterExpressionLogical.and(boolean1, boolean2);
        assertThat(f6.getOperands()).isEqualTo(Lists.newArrayList(boolean1, boolean2));
    }
}
