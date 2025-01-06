package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.Collection;

import org.apache.olingo.odata2.api.edm.EdmAnnotationElement;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;

class EdmUtils
{
    static Multiplicity convertMultiplicity( final EdmMultiplicity multiplicity )
    {
        switch( multiplicity ) {
            case MANY:
                return Multiplicity.MANY;
            case ONE:
                return Multiplicity.ONE;
            case ZERO_TO_ONE:
                return Multiplicity.ZERO_TO_ONE;
            default:
                throw new ODataGeneratorException("Encountered unknown multiplicity: " + multiplicity);
        }
    }

    static TypeKind convertTypeKind( final EdmTypeKind typeKind )
    {
        switch( typeKind ) {
            case SIMPLE:
                return TypeKind.SIMPLE;
            case COMPLEX:
                return TypeKind.COMPLEX;
            case ENTITY:
                return TypeKind.ENTITY;
            default:
                throw new ODataGeneratorException("Encountered unknown type kind: " + typeKind);
        }
    }

    static EdmAnnotationElement getDocumentationElement( final Collection<EdmAnnotationElement> annotationElements )
    {
        if( annotationElements != null && !annotationElements.isEmpty() ) {
            // getAnnotationElement() does not work, so do this the old-fashioned way
            for( final EdmAnnotationElement element : annotationElements ) {
                if( element.getName().equals("Documentation") ) {
                    return element;
                }
            }
        }
        return null;
    }
}
