package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;

import lombok.Getter;

public class ODataFilterTest
{
    private static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        final String entityCollection = null;

        @Getter
        private final String defaultServicePath = "/";

        @Getter
        private final Class<TestEntity> type = TestEntity.class;
    }

    @Test
    public void testString()
    {
        final EntityField<TestEntity, String> field = new EntityField<>("field");

        final ExpressionFluentHelper<TestEntity> helper = field.eq("va'l''ue");
        final String expected = "field eq 'va''l''''ue'";

        final ValueBoolean clientExpression = helper.getDelegateExpressionWithoutOuterParentheses();
        assertThat(clientExpression.getExpression(ODataProtocol.V2)).isEqualTo(expected);
    }

    @Test
    public void testZonedDateTime()
    {
        final EntityField<TestEntity, ZonedDateTime> field = new EntityField<>("LastModified");
        final ZonedDateTime dateTime =
            ZonedDateTime.of(LocalDate.of(2001, 1, 1), LocalTime.of(20, 15), ZoneId.of("UTC"));

        final ExpressionFluentHelper<TestEntity> helper = field.eq(dateTime);
        final String expected = "LastModified eq datetimeoffset'2001-01-01T20:15:00Z'";

        assertFilter(helper, expected);
    }

    @Test
    public void testLocalDateTime()
    {
        final EntityField<TestEntity, LocalDateTime> field = new EntityField<>("LastModified");
        final LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2001, 1, 1), LocalTime.of(20, 15));

        final String expected = "LastModified eq datetime'2001-01-01T20:15:00'";
        final ExpressionFluentHelper<TestEntity> expressionHelper = field.eq(dateTime);

        assertFilter(expressionHelper, expected);
    }

    @Test
    public void testLocalTime()
    {
        final EntityField<TestEntity, LocalTime> field = new EntityField<>("LastModified");
        final LocalTime time = LocalTime.of(20, 15);

        final String expected = "LastModified eq time'PT20H15M'";
        final ExpressionFluentHelper<TestEntity> expressionHelper = field.eq(time);

        assertFilter(expressionHelper, expected);
    }

    @Test
    public void testLogicalOperators()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");

        final ExpressionFluentHelper<TestEntity> helper =
            // f1
            field
                .eq(1)
                // f1 and not(f2)
                .and(ExpressionFluentHelper.not(field.ne(2)))
                // (f1 and not(f2)) or f3
                .or(field.eq(3))
                // (not (f1 and (not f2)) or f3)
                .not();

        final String expected = "not (((field eq 1) and (not (field ne 2))) or (field eq 3))";

        final ValueBoolean clientExpression = helper.getDelegateExpressionWithoutOuterParentheses();
        assertThat(clientExpression.getExpression(ODataProtocol.V2)).isEqualTo(expected);
    }

    @Test
    public void testRelativeOperators()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");

        final ExpressionFluentHelper<TestEntity> helper =
            field.le(1).and(field.ge(2)).and(field.lt(3).and(field.gt(4)));
        final String expected = "((field le 1) and (field ge 2)) and ((field lt 3) and (field gt 4))";

        assertFilter(helper, expected);
    }

    @Test
    public void testFilterEqualsNull()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");
        assertFilter(field.eq(null), "field eq null");
        assertFilter(field.eqNull(), "field eq null");
    }

    @Test
    public void testFilterDoesNotEqualNull()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");
        assertFilter(field.ne(null), "field ne null");
        assertFilter(field.neNull(), "field ne null");
    }

    @Test
    public void testFilterGreaterThanNull()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");
        assertFilter(field.gt(null), "field gt null");
    }

    @Test
    public void testFilterGreaterOrEqualsNull()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");
        assertFilter(field.ge(null), "field ge null");
    }

    @Test
    public void testFilterLessThanNull()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");
        assertFilter(field.lt(null), "field lt null");
    }

    @Test
    public void testFilterLessOrEqualsNull()
    {
        final EntityField<TestEntity, Integer> field = new EntityField<>("field");
        assertFilter(field.le(null), "field le null");
    }

    private static
        void
        assertFilter( @Nonnull final ExpressionFluentHelper<TestEntity> helper, @Nonnull final String expected )
    {
        final ValueBoolean clientExpression = helper.getDelegateExpressionWithoutOuterParentheses();
        assertThat(clientExpression.getExpression(ODataProtocol.V2)).isEqualTo(expected);
    }
}
