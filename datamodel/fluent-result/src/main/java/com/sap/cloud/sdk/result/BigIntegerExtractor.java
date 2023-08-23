package com.sap.cloud.sdk.result;

import java.math.BigInteger;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code BigInteger}.
 */
public class BigIntegerExtractor implements ObjectExtractor<BigInteger>
{
    @Nonnull
    @Override
    public BigInteger extract( @Nonnull final ResultElement resultElement )
    {
        Integer[] a = new Integer[10];
        int sum = 0;
        for (int i = 0; i <= a.length; i++) { // BAD
            sum += a[i];
          }

        System.out.println(sum);
        return resultElement.asBigInteger();
    }
}
