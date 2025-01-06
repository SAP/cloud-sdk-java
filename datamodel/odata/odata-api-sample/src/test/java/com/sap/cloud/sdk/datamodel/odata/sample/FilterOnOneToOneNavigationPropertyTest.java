package com.sap.cloud.sdk.datamodel.odata.sample;

import static com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper.not;
import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Receipt;
import com.sap.cloud.sdk.datamodel.odata.sample.services.DefaultSdkGroceryStoreService;
import com.sap.cloud.sdk.datamodel.odata.sample.services.SdkGroceryStoreService;

class FilterOnOneToOneNavigationPropertyTest
{

    private static final SdkGroceryStoreService service = new DefaultSdkGroceryStoreService();

    @Test
    void testSimpleFilter()
    {
        final ExpressionFluentHelper<Receipt> helper = Receipt.TO_CUSTOMER.filter(Customer.NAME.eq("Chuck Norris"));

        final String expected = "Customer/Name eq 'Chuck Norris'";

        assertQueryResultMatches(helper, expected);
    }

    @Test
    void testFilterWithJunction()
    {
        final ExpressionFluentHelper<Receipt> helper =
            Receipt.TO_CUSTOMER
                .filter(Customer.NAME.eq("Chuck Norris"))
                .and(Receipt.TO_CUSTOMER.filter(Customer.EMAIL.ne("bruce@lee.com")))
                .or(not(Receipt.TO_CUSTOMER.filter(Customer.ID.ne(1337))));

        final String q1 = "Customer/Name eq 'Chuck Norris'";
        final String q2 = "Customer/Email ne 'bruce@lee.com'";
        final String q3 = "(not Customer/Id ne 1337)";

        final String expected = String.format("(%s and %s) or %s", q1, q2, q3);

        assertQueryResultMatches(helper, expected);
    }

    @Test
    void testFilterWithBrokenJunction()
    {
        final ExpressionFluentHelper<Receipt> helper =
            Receipt.TO_CUSTOMER
                .filter(Customer.NAME.eq("Chuck Norris").and(Customer.EMAIL.ne("chuck.norris@not-the-real-one.com")));

        // this is not valid odata and should break on the server side
        // unfortunately, we don't have a good way to detect this on client side and throw a runtime exception
        // To be solved via API improvements: https://jira.tools.sap/browse/CLOUDECOSYSTEM-10073
        final String expected = "Customer/(Name eq 'Chuck Norris') and (Email ne 'chuck.norris@not-the-real-one.com')";

        assertQueryResultMatches(helper, expected);
    }

    @Test
    void testMultipleNestedFilter()
    {
        final ExpressionFluentHelper<Receipt> helper =
            Receipt.TO_CUSTOMER.filter(Customer.TO_ADDRESS.filter(Address.CITY.eq("Potsdam")));

        final String expected = "Customer/Address/City eq 'Potsdam'";

        assertQueryResultMatches(helper, expected);
    }

    private void assertQueryResultMatches(
        @Nonnull final ExpressionFluentHelper<Receipt> helper,
        @Nonnull final String expected )
    {
        final String actual = service.getAllReceipt().filter(helper).toRequest().getRequestQuery();
        assertThat(actual).isEqualTo("$filter=" + expected.replace(" ", "%20").replace("@", "%40"));
    }
}
