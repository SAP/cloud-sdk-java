package com.sap.cloud.sdk.datamodel.odata.utility;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import com.sap.cloud.sdk.cloudplatform.util.StringUtils;

import lombok.NoArgsConstructor;

/**
 * Represents a {@link NamingStrategy}, which removes pre- and suffixes from the generated Java identifiers that are
 * typically used in S4Hana service definitions.
 */
@NoArgsConstructor
public final class S4HanaNamingStrategy extends AbstractNamingStrategy
{
    private static final Collection<String> ENTITY_NAME_PREFIXES_TO_REMOVE =
        ImmutableList.of("A_", "C_", "D_", "E_", "I_", "P_", "R_", "S_", "to_", "To_", "X_", "YY1_", "Z_");
    private static final Collection<String> PROPERTY_NAME_PREFIXES_TO_REMOVE =
        ImmutableList.of("SAP_", "to_", "To_", "YY1_");
    private static final Collection<String> CLASS_NAME_SUFFIXES_TO_REMOVE = ImmutableList.of("Type", "_");

    /**
     * Constructs a new {@link S4HanaNamingStrategy} instance.
     *
     * @param nameSource
     *            The {@link NameSource} that should be used by the newly created instance.
     */
    public S4HanaNamingStrategy( @Nonnull final NameSource nameSource )
    {
        super(nameSource);
    }

    @Nonnull
    @Override
    public String generateJavaClassName( @Nonnull final String name, @Nullable final String label )
    {
        String className = generateNameFromEntity(name, label);
        className = removeAllSuffixes(className, CLASS_NAME_SUFFIXES_TO_REMOVE);
        className = appendSuffixIfNameIsReservedKeyword(className, "Entity");

        throwIfConversionResultIsNullOrEmpty(name, label, className, "Java class name");

        return className;
    }

    @Nonnull
    @Override
    public String generateJavaFieldName( @Nonnull final String name, @Nullable final String label )
    {
        String fieldName = generateNameFromProperty(name, label);
        fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, fieldName);
        fieldName = appendSuffixIfNameIsReservedKeyword(fieldName, "Property");

        throwIfConversionResultIsNullOrEmpty(name, label, fieldName, "Java field name");

        return fieldName;
    }

    @Nonnull
    @Override
    public String generateJavaConstantName( @Nonnull final String name, @Nullable final String label )
    {
        String constantName = generateNameFromProperty(name, label);
        constantName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, constantName);
        constantName = removeRepeatedUnderscores(constantName);
        constantName = fixAcronymsInConstantNames(constantName);

        throwIfConversionResultIsNullOrEmpty(name, label, constantName, "Java constant name");

        return constantName;
    }

    @Nonnull
    @Override
    public String generateJavaNavigationPropertyFieldName( @Nonnull final String name )
    {
        String fieldName = generateNameFromProperty(name, null);
        fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, fieldName);

        if( !fieldName.startsWith("to") ) {
            fieldName = "to" + StringUtils.capitalize(fieldName);
        } else if( fieldName.charAt(2) == '_' ) {
            fieldName = fieldName.replaceFirst("_", "");
        }

        throwIfConversionResultIsNullOrEmpty(name, null, fieldName, "Java navigation property field name");

        return fieldName;
    }

    @Nonnull
    @Override
    public String generateJavaNavigationPropertyConstantName( @Nonnull final String name )
    {
        String constantName = generateNameFromProperty(name, null);
        constantName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, constantName);
        constantName = removeRepeatedUnderscores(constantName);
        constantName = fixAcronymsInConstantNames(constantName);

        if( !constantName.startsWith("TO_") ) {
            constantName = "TO_" + constantName;
        }

        throwIfConversionResultIsNullOrEmpty(name, null, constantName, "Java navigation property constant name");

        return constantName;
    }

    @Nonnull
    @Override
    public String generateJavaMethodName( @Nonnull final String name )
    {
        String methodName = generateNameFromProperty(name, null);
        methodName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName);
        methodName = appendSuffixIfNameIsReservedKeyword(methodName, "Objects");

        methodName = StringUtils.removeStartIgnoreCase(methodName, "to");
        methodName = StringUtils.removeStartIgnoreCase(methodName, "_");
        methodName = NamingUtils.uncapitalize(methodName);

        throwIfConversionResultIsNullOrEmpty(name, null, methodName, "Java method name");

        return methodName;
    }

    @Nonnull
    @Override
    public String generateJavaBuilderMethodName( @Nonnull final String name )
    {
        String methodName = generateNameFromProperty(name, null);
        methodName = StringUtils.removeStartIgnoreCase(methodName, "to");
        methodName = StringUtils.removeStartIgnoreCase(methodName, "_");
        methodName = uncapitalizeLeadingAcronym(methodName);
        methodName = appendSuffixIfNameIsReservedKeyword(methodName, "Property");

        throwIfConversionResultIsNullOrEmpty(name, null, methodName, "Java builder method name");

        return methodName;
    }

    @Nonnull
    @Override
    public String generateJavaOperationMethodName( @Nonnull final String name, @Nullable final String label )
    {
        String methodName = generateNameFromEntity(name, label);
        methodName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, methodName);
        methodName = appendSuffixIfNameIsReservedKeyword(methodName, "Function");

        methodName = StringUtils.removeStartIgnoreCase(methodName, "to");
        methodName = StringUtils.removeStartIgnoreCase(methodName, "_");

        throwIfConversionResultIsNullOrEmpty(name, label, methodName, "Java function import method name");

        return methodName;
    }

    @Nonnull
    @Override
    public String generateJavaMethodParameterName( @Nonnull final String name, @Nullable final String label )
    {
        String parameterName = generateNameFromProperty(name, label);
        parameterName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, parameterName);
        if( isReservedKeyword(parameterName) ) {
            parameterName += "Parameter";
        }

        throwIfConversionResultIsNullOrEmpty(name, label, parameterName, "Java method parameter name");

        return parameterName;
    }

    @Nonnull
    @Override
    public String generateJavaFluentHelperClassName( @Nonnull final String name, @Nullable final String label )
    {
        String className = generateNameFromEntity(name, label);
        className = removeAllSuffixes(className, CLASS_NAME_SUFFIXES_TO_REMOVE);
        className += "FluentHelper";

        throwIfConversionResultIsNullOrEmpty(name, label, className, "Java fluent helper class name");

        return className;
    }

    @Nonnull
    private String generateNameFromEntity( @Nonnull final String name, @Nullable final String label )
    {
        @Nullable
        String result;

        if( getNameSource() == NameSource.LABEL && !StringUtils.isBlankOrEmpty(label) ) {
            result = removeWhiteSpaces(label);
        } else {
            result = removeFirstPrefix(name, ENTITY_NAME_PREFIXES_TO_REMOVE);
        }

        if( StringUtils.isBlankOrEmpty(result) ) {
            throw new IllegalStateException(
                "Could not create a valid Java identifier based on the entity name '"
                    + name
                    + "' and the entity label '"
                    + label
                    + "'. Name source was '"
                    + getNameSource()
                    + "'");
        }

        result = removeInvalidJavaCharacters(result);
        result = NamingUtils.capitalize(result);

        return result;
    }

    @Nonnull
    private String generateNameFromProperty( @Nonnull final String name, @Nullable final String label )
    {
        @Nullable
        String result;

        if( getNameSource() == NameSource.LABEL && !StringUtils.isBlankOrEmpty(label) ) {
            result = removeWhiteSpaces(label);
        } else {
            result = removeFirstPrefix(name, PROPERTY_NAME_PREFIXES_TO_REMOVE);
        }

        if( StringUtils.isBlankOrEmpty(result) ) {
            throw new IllegalStateException(
                "Could not create a valid Java identifier based on the property name '"
                    + name
                    + "' and the property label '"
                    + label
                    + "'. Name source was '"
                    + getNameSource()
                    + "'");
        }

        result = removeInvalidJavaCharacters(result);
        result = NamingUtils.capitalize(result);

        return result;
    }

    private String removeFirstPrefix( final String name, final Iterable<String> prefixes )
    {
        String formattedName = name.trim();
        for( final String prefixToRemove : prefixes ) {
            if( formattedName.startsWith(prefixToRemove) ) {
                formattedName = formattedName.substring(prefixToRemove.length());
                break;
            }
        }
        return formattedName;
    }

    private String removeAllSuffixes( @Nonnull final String name, @Nonnull final Iterable<String> suffixes )
    {
        String formattedName = name;
        for( final String suffix : suffixes ) {
            formattedName = StringUtils.removeEndIgnoreCase(formattedName, suffix);
        }

        return formattedName;
    }
}
