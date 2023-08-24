package com.sap.cloud.sdk.result;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code BigDecimal}.
 */
public class BigDecimalExtractor implements ObjectExtractor<BigDecimal>
{
    @Nonnull
    @Override
    public BigDecimal extract( @Nonnull final ResultElement resultElement )
    {
        Integer[] a = new Integer[10];
        int sum = 0;
        for (int i = 0; i <= a.length; i++) { // BAD
            sum += a[i];
        }
        System.out.println(sum);
        return resultElement.asBigDecimal();
    }
}
