/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.annotation.Nonnull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;

import lombok.AllArgsConstructor;
import lombok.Getter;

class ODataFilterTest
{
    private static final EntityField<TestEntity, String> STRING_FIELD = new EntityField<>("field");
    private static final EntityField<TestEntity, ZonedDateTime> ZONED_DATE_TIME_FIELD = new EntityField<>("field");
    private static final EntityField<TestEntity, LocalDateTime> LOCAL_DATE_TIME_FIELD = new EntityField<>("field");
    private static final EntityField<TestEntity, LocalTime> LOCAL_TIME_FIELD = new EntityField<>("field");
    private static final EntityField<TestEntity, Integer> INT_FIELD = new EntityField<>("field");

    @ParameterizedTest
    @EnumSource( TestInput.class )
    void testFilterExpression( @Nonnull final TestInput testInput )
    {
        final ValueBoolean clientExpression = testInput.helper.getDelegateExpressionWithoutOuterParentheses();
        assertThat(clientExpression.getExpression(ODataProtocol.V2)).isEqualTo(testInput.expectedFilterString);

        final ODataRequestRead request =
            FluentHelperFactory
                .withServicePath("/foo/bar")
                .read(TestEntity.class, "foo")
                .filter(testInput.helper)
                .toRequest();
        assertThat(request.getRelativeUri()).hasQuery("$filter=" + testInput.expectedFilterString);
    }

    @AllArgsConstructor
    enum TestInput
    {
        STRING_EQUALITY(STRING_FIELD.eq("va'l''ue"), "field eq 'va''l''''ue'"),
        STRING_SUBSTRING(STRING_FIELD.substringOf("foo"), "substringof('foo',field)"),
        STRING_ENDS_WITH(STRING_FIELD.endsWith("foo"), "endswith(field,'foo')"),
        STRING_STARTS_WITH(STRING_FIELD.startsWith("foo"), "startswith(field,'foo')"),
        STRING_DISJUNCTION(
            STRING_FIELD.startsWith("foo").or(STRING_FIELD.ne("bar")),
            "startswith(field,'foo') or (field ne 'bar')"),
        STRING_NEGATION(STRING_FIELD.endsWith("foo").not(), "not endswith(field,'foo')"),
        STRING_COMPLEX_EXPRESSION(
            STRING_FIELD
                .substringOf("bar")
                .and(STRING_FIELD.endsWith("foo"))
                .or(STRING_FIELD.startsWith("foobar").not())
                .not(),
            "not ((substringof('bar',field) and endswith(field,'foo')) or (not startswith(field,'foobar')))"),
        ZONED_DATE_TIME(
            ZONED_DATE_TIME_FIELD
                .eq(ZonedDateTime.of(LocalDate.of(2001, 1, 1), LocalTime.of(20, 15), ZoneId.of("UTC"))),
            "field eq datetimeoffset'2001-01-01T20:15:00Z'"),
        LOCAL_DATE_TIME(
            LOCAL_DATE_TIME_FIELD.eq(LocalDateTime.of(LocalDate.of(2001, 1, 1), LocalTime.of(20, 15))),
            "field eq datetime'2001-01-01T20:15:00'"),
        LOCAL_TIME(LOCAL_TIME_FIELD.eq(LocalTime.of(20, 15)), "field eq time'PT20H15M'"),
        INT_LOGICAL_OPERATORS(
            INT_FIELD.eq(1).and(ExpressionFluentHelper.not(INT_FIELD.ne(2))).or(INT_FIELD.eq(3)).not(),
            "not (((field eq 1) and (not (field ne 2))) or (field eq 3))"),
        INT_RELATIVE_OPERATORS(
            INT_FIELD.le(1).and(INT_FIELD.ge(2)).and(INT_FIELD.lt(3).and(INT_FIELD.gt(4))),
            "((field le 1) and (field ge 2)) and ((field lt 3) and (field gt 4))"),
        INT_EQUALITY(INT_FIELD.eq(1), "field eq 1"),
        INT_INEQUALITY(INT_FIELD.ne(1), "field ne 1"),
        INT_GREATER_THAN(INT_FIELD.gt(1), "field gt 1"),
        INT_GREATER_THAN_OR_EQUAL(INT_FIELD.ge(1), "field ge 1"),
        INT_LESS_THAN(INT_FIELD.lt(1), "field lt 1"),
        INT_LESS_THAN_OR_EQUAL(INT_FIELD.le(1), "field le 1"),
        EQUAL_NULL(INT_FIELD.eq(null), "field eq null"),
        EQUALNULL(INT_FIELD.eqNull(), "field eq null"),
        NOT_EQUAL_NULL(INT_FIELD.ne(null), "field ne null"),
        NOT_EQUALNULL(INT_FIELD.neNull(), "field ne null"),
        GREATER_THAN_NULL(INT_FIELD.gt(null), "field gt null"),
        GREATER_THAN_OR_EQUAL_NULL(INT_FIELD.ge(null), "field ge null"),
        LESS_THAN_NULL(INT_FIELD.lt(null), "field lt null"),
        LESS_THAN_OR_EQUAL_NULL(INT_FIELD.le(null), "field le null"),;

        @Nonnull
        private final ExpressionFluentHelper<TestEntity> helper;
        @Nonnull
        private final String expectedFilterString;
    }

    private static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        final String entityCollection = null;

        @Getter
        private final String defaultServicePath = "/";

        @Getter
        private final Class<TestEntity> type = TestEntity.class;
    }
}
