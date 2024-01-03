/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import org.apache.olingo.commons.api.edm.EdmTyped;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

class EdmUtils
{
    static Multiplicity convertMultiplicity( final EdmTyped edmTyped )
    {
        if( edmTyped.isCollection() ) {
            return Multiplicity.MANY;
        } else {
            return Multiplicity.ONE;
        }
    }

    static TypeKind convertTypeKind( final EdmTypeKind typeKind )
    {
        switch( typeKind ) {
            case PRIMITIVE:
                return TypeKind.PRIMITIVE;
            case COMPLEX:
                return TypeKind.COMPLEX;
            case ENTITY:
                return TypeKind.ENTITY;
            case ENUM:
                return TypeKind.ENUM;
            default:
                throw new ODataGeneratorException("Encountered unknown type kind: " + typeKind);
        }
    }
}
