/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

import com.sun.codemodel.JExpressionImpl;
import com.sun.codemodel.JFormatter;

class CodeModelUtils
{
    @Nonnull
    static JExpressionImpl directExpression( @Nonnull final String source )
    {
        return new JExpressionImpl()
        {
            public void generate( @Nonnull final JFormatter f )
            {
                f.p(source);
            }
        };
    }
}
