package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;

import lombok.Getter;

public class UncheckedFilterExpressionTest
{
    @Test
    public void testClientFilterExpressionEmpty()
    {
        final ValueBoolean filterExpression = ValueBoolean.literal(true);
        final ExpressionFluentHelper<Object> customFilterExpression = new ExpressionFluentHelper<>(filterExpression);

        assertThat(
            customFilterExpression.getDelegateExpressionWithoutOuterParentheses().getExpression(ODataProtocol.V2))
            .isEqualTo("true");
    }

    @Test
    public void testClientFilterExpressionInteger()
    {
        final ValueBoolean filterExpression = FieldReference.of("ShoeSize").equalTo(42);
        final ExpressionFluentHelper<MyEntity> customFilterExpression = new ExpressionFluentHelper<>(filterExpression);

        assertThat(
            customFilterExpression.getDelegateExpressionWithoutOuterParentheses().getExpression(ODataProtocol.V2))
            .isEqualTo("ShoeSize eq 42");

        final ODataRequestRead read = newFluentHelperRead().filter(customFilterExpression).toRequest();
        assertThat(read.getRelativeUri()).hasQuery("$filter=ShoeSize eq 42");
    }

    @Test
    public void testClientFilterExpressionString()
    {
        final ValueBoolean filterExpression = FieldReference.of("FirstName").equalTo("Alice");
        final ExpressionFluentHelper<MyEntity> customFilterExpression = new ExpressionFluentHelper<>(filterExpression);

        assertThat(
            customFilterExpression.getDelegateExpressionWithoutOuterParentheses().getExpression(ODataProtocol.V2))
            .isEqualTo("FirstName eq 'Alice'");

        final ODataRequestRead read = newFluentHelperRead().filter(customFilterExpression).toRequest();
        assertThat(read.getRelativeUri()).hasQuery("$filter=FirstName eq 'Alice'");
    }

    // test helper classes

    private static <
        T extends FluentHelperRead<T, MyEntity, MySelectable>>
        FluentHelperRead<T, MyEntity, MySelectable>
        newFluentHelperRead()
    {
        return FluentHelperFactory.withServicePath("some/path").read(MyEntity.class, "MyEntityCollection");
    }

    static class MyEntity extends VdmEntity<MyEntity>
    {
        @Getter
        private final String entityCollection = "MyEntityCollection";

        @Getter
        private final String defaultServicePath = "API_MY_ENTITY";

        @Nonnull
        @Override
        public Class<MyEntity> getType()
        {
            return MyEntity.class;
        }
    }

    static class MySelectable
    {
    }
}
