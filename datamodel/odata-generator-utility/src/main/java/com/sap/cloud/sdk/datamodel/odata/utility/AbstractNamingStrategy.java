/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.SourceVersion;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an abstract base implementation of the {@link NamingStrategy}. It provides convenience methods (such as
 * {@link AbstractNamingStrategy#convertToJavaClassName(String, String)}) for inheritors to leverage.
 */
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractNamingStrategy implements NamingStrategy
{
    @Getter
    @Setter
    private NameSource nameSource = NameSource.LABEL;

    @Override
    public boolean isReservedKeyword( @Nonnull final String name )
    {
        return SourceVersion.isKeyword(name);
    }

    @Nonnull
    @Override
    public String generateJavaClassName( @Nonnull final String name, @Nullable final String label )
    {
        String className = convertToJavaClassName(name, label);
        className = finishJavaClassNameGeneration(className);

        throwIfConversionResultIsNullOrEmpty(name, label, className, "Java class name");
        throwIfConversionResultIsReservedKeyword(name, label, className, "Java class name");

        return className;
    }

    /**
     * Allows post-processing of the Java identifier generated by {@link #generateJavaClassName(String, String)}.
     *
     * @param className
     *            The Java class name to post-process.
     * @return The processed Java class name.
     */
    @Nonnull
    protected String finishJavaClassNameGeneration( @Nonnull final String className )
    {
        return className;
    }

    @Nonnull
    @Override
    public String generateJavaFieldName( @Nonnull final String name, @Nullable final String label )
    {
        String fieldName = convertToJavaFieldName(name, label);
        fieldName = finishJavaFieldNameGeneration(fieldName);

        throwIfConversionResultIsNullOrEmpty(name, label, fieldName, "Java field name");
        throwIfConversionResultIsReservedKeyword(name, label, fieldName, "Java field name");

        return fieldName;
    }

    /**
     * Allows post-processing of the Java identifier generated by {@link #generateJavaFieldName(String, String)}.
     *
     * @param fieldName
     *            The Java field name to post-process.
     * @return The processed Java field name.
     */
    @Nonnull
    protected String finishJavaFieldNameGeneration( @Nonnull final String fieldName )
    {
        return fieldName;
    }

    @Nonnull
    @Override
    public String generateJavaConstantName( @Nonnull final String name, @Nullable final String label )
    {
        String constantName = convertToJavaConstantName(name, label);
        constantName = finishJavaConstantNameGeneration(constantName);

        throwIfConversionResultIsNullOrEmpty(name, label, constantName, "Java constant name");
        throwIfConversionResultIsReservedKeyword(name, label, constantName, "Java constant name");

        return constantName;
    }

    /**
     * Allows post-processing of the Java identifier generated by {@link #generateJavaConstantName(String, String)}.
     *
     * @param constantName
     *            The Java constant name to post-process.
     * @return The processed Java constant name.
     */
    @Nonnull
    protected String finishJavaConstantNameGeneration( @Nonnull final String constantName )
    {
        return constantName;
    }

    @Nonnull
    @Override
    public String generateJavaNavigationPropertyFieldName( @Nonnull final String name )
    {
        String fieldName = convertToJavaFieldName(name, null);
        fieldName = finishJavaNavigationPropertyFieldNameGeneration(fieldName);

        throwIfConversionResultIsNullOrEmpty(name, null, fieldName, "Java navigation property field name");
        throwIfConversionResultIsReservedKeyword(name, null, fieldName, "Java navigation property field name");

        return fieldName;
    }

    /**
     * Allows post-processing of the Java identifier generated by
     * {@link #generateJavaNavigationPropertyFieldName(String)}.
     *
     * @param fieldName
     *            The Java field name to post-process.
     * @return The processed Java field name.
     */
    @Nonnull
    protected String finishJavaNavigationPropertyFieldNameGeneration( @Nonnull final String fieldName )
    {
        return fieldName;
    }

    @Nonnull
    @Override
    public String generateJavaNavigationPropertyConstantName( @Nonnull final String name )
    {
        String constantName = convertToJavaConstantName(name, null);
        constantName = finishJavaNavigationPropertyConstantNameGeneration(constantName);

        throwIfConversionResultIsNullOrEmpty(name, null, constantName, "Java navigation property constant name");
        throwIfConversionResultIsReservedKeyword(name, null, constantName, "Java navigation property constant name");

        return constantName;
    }

    /**
     * Allows post-processing of the Java identifier generated by
     * {@link #generateJavaNavigationPropertyConstantName(String)}.
     *
     * @param constantName
     *            The Java constant name to post-process.
     * @return The processed Java constant name.
     */
    @Nonnull
    protected String finishJavaNavigationPropertyConstantNameGeneration( @Nonnull final String constantName )
    {
        return constantName;
    }

    @Nonnull
    @Override
    public String generateJavaMethodName( @Nonnull final String name )
    {
        String methodName = convertToJavaMethodName(name, null);
        methodName = finishJavaMethodNameGeneration(methodName);

        throwIfConversionResultIsNullOrEmpty(name, null, methodName, "Java method name");
        throwIfConversionResultIsReservedKeyword(name, null, methodName, "Java method name");

        return methodName;
    }

    /**
     * Allows post-processing of the Java identifier generated by {@link #generateJavaMethodName(String)}.
     *
     * @param methodName
     *            The Java method name to post-process.
     * @return The processed Java method name.
     */
    @Nonnull
    protected String finishJavaMethodNameGeneration( @Nonnull final String methodName )
    {
        return methodName;
    }

    @Nonnull
    @Override
    public String generateJavaBuilderMethodName( @Nonnull final String name )
    {
        String methodName = convertToJavaMethodName(name, null);
        methodName = finishJavaBuilderMethodNameGeneration(methodName);

        throwIfConversionResultIsNullOrEmpty(name, null, methodName, "Java builder method name");
        throwIfConversionResultIsReservedKeyword(name, null, methodName, "Java builder method name");

        return methodName;
    }

    /**
     * Allows post-processing of the Java identifier generated by {@link #generateJavaBuilderMethodName(String)}.
     *
     * @param methodName
     *            The Java method name to post-process.
     * @return The processed Java method name.
     */
    @Nonnull
    protected String finishJavaBuilderMethodNameGeneration( @Nonnull final String methodName )
    {
        return methodName;
    }

    @Nonnull
    @Override
    public String generateJavaOperationMethodName( @Nonnull final String name, @Nullable final String label )
    {
        String methodName = convertToJavaMethodName(name, label);
        methodName = finishJavaOperationMethodNameGeneration(methodName);

        throwIfConversionResultIsNullOrEmpty(name, label, methodName, "Java function import method name");
        throwIfConversionResultIsReservedKeyword(name, label, methodName, "Java function import method name");

        return methodName;
    }

    /**
     * Allows post-processing of the Java identifier generated by
     * {@link #generateJavaOperationMethodName(String, String)}.
     *
     * @param methodName
     *            The Java method name to post-process.
     * @return The processed Java method name.
     */
    @Nonnull
    protected String finishJavaOperationMethodNameGeneration( @Nonnull final String methodName )
    {
        return methodName;
    }

    @Nonnull
    @Override
    public String generateJavaMethodParameterName( @Nonnull final String name, @Nullable final String label )
    {
        String parameterName = convertToJavaFieldName(name, label);
        parameterName = finishJavaMethodParameterNameGeneration(parameterName);

        throwIfConversionResultIsNullOrEmpty(name, label, parameterName, "Java method parameter name");
        throwIfConversionResultIsReservedKeyword(name, label, parameterName, "Java method parameter name");

        return parameterName;
    }

    /**
     * Allows post-processing of the Java identifier generated by
     * {@link #generateJavaMethodParameterName(String, String)}.
     *
     * @param parameterName
     *            The Java method parameter name to post-process.
     * @return The processed Java method parameter name.
     */
    @Nonnull
    protected String finishJavaMethodParameterNameGeneration( @Nonnull final String parameterName )
    {
        return parameterName;
    }

    @Nonnull
    @Override
    public String generateJavaFluentHelperClassName( @Nonnull final String name, @Nullable final String label )
    {
        String className = convertToJavaClassName(name, label);
        className = finishJavaFluentHelperClassNameGeneration(className);

        throwIfConversionResultIsNullOrEmpty(name, label, className, "Java fluent helper class name");
        throwIfConversionResultIsReservedKeyword(name, label, className, "Java fluent helper class name");

        return className;
    }

    /**
     * Allows post-processing of the Java identifier generated by
     * {@link #generateJavaFluentHelperClassName(String, String)}.
     *
     * @param className
     *            The Java class name to post-process.
     * @return The processed Java class name.
     */
    @Nonnull
    protected String finishJavaFluentHelperClassNameGeneration( @Nonnull final String className )
    {
        return className;
    }

    /**
     * Chooses between the given {@code name} and {@code label} based on the used {@link NameSource}. The chosen string
     * will then be converted into a syntactically valid Java class name.
     *
     * @param name
     *            The name property of an OData tag (e.g. of an <b>Entity</b>).
     * @param label
     *            The <b>sap:label</b> property of an OData tag (e.g. of an <b>Entity</b>), if any.
     * @return A syntactically correct Java class name.
     */
    @Nonnull
    protected String convertToJavaClassName( @Nonnull final String name, @Nullable final String label )
    {
        String className = chooseBetweenNameAndLabel(name, label);
        className = removeWhiteSpaces(className);
        className = removeInvalidJavaCharacters(className);
        className = uncapitalizeLeadingAcronym(className);
        className = NamingUtils.capitalize(className);

        return className;
    }

    /**
     * Chooses between the given {@code name} and {@code label} based on the used {@link NameSource}. The chosen string
     * will then be converted into a syntactically valid Java field name.
     *
     * @param name
     *            The name property of an OData tag (e.g. of an <b>Entity</b>).
     * @param label
     *            The <b>sap:label</b> property of an OData tag (e.g. of an <b>Entity</b>), if any.
     * @return A syntactically correct Java field name.
     */
    @Nonnull
    protected String convertToJavaFieldName( @Nonnull final String name, @Nullable final String label )
    {
        String fieldName = chooseBetweenNameAndLabel(name, label);
        fieldName = removeWhiteSpaces(fieldName);
        fieldName = removeInvalidJavaCharacters(fieldName);
        fieldName = uncapitalizeLeadingAcronym(fieldName);
        fieldName = NamingUtils.uncapitalize(fieldName);

        return fieldName;
    }

    /**
     * Chooses between the given {@code name} and {@code label} based on the used {@link NameSource}. The chosen string
     * will then be converted into a syntactically valid Java constant name.
     *
     * @param name
     *            The name property of an OData tag (e.g. of an <b>Entity</b>).
     * @param label
     *            The <b>sap:label</b> property of an OData tag (e.g. of an <b>Entity</b>), if any.
     * @return A syntactically correct Java constant name.
     */
    @Nonnull
    protected String convertToJavaConstantName( @Nonnull final String name, @Nullable final String label )
    {
        String constantName = chooseBetweenNameAndLabel(name, label);
        constantName = removeWhiteSpaces(constantName);
        constantName = removeInvalidJavaCharacters(constantName);
        constantName = NamingUtils.capitalize(constantName);
        constantName = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, constantName);
        constantName = fixAcronymsInConstantNames(constantName);
        constantName = removeRepeatedUnderscores(constantName);

        return constantName;
    }

    /**
     * Chooses between the given {@code name} and {@code label} based on the used {@link NameSource}. The chosen string
     * will then be converted into a syntactically valid Java method name.
     *
     * @param name
     *            The name property of an OData tag (e.g. of an <b>Entity</b>).
     * @param label
     *            The <b>sap:label</b> property of an OData tag (e.g. of an <b>Entity</b>), if any.
     * @return A syntactically correct Java method name.
     */
    @Nonnull
    protected String convertToJavaMethodName( @Nonnull final String name, @Nullable final String label )
    {
        String methodName = chooseBetweenNameAndLabel(name, label);
        methodName = removeWhiteSpaces(methodName);
        methodName = removeInvalidJavaCharacters(methodName);
        methodName = uncapitalizeLeadingAcronym(methodName);
        methodName = NamingUtils.uncapitalize(methodName);

        return methodName;
    }

    /**
     * Selects either the given {@code name} or the {@code label} based on the used {@link NameSource}.
     *
     * @param name
     *            The name property of an OData tag (e.g. of an <b>Entity</b>).
     * @param label
     *            The <b>sap:label</b> property of an OData tag (e.g. of an <b>Entity</b>), if any.
     * @return Either the {@code name} or the {@code label}.
     */
    @Nonnull
    protected String chooseBetweenNameAndLabel( @Nonnull final String name, @Nullable final String label )
    {
        if( StringUtils.isBlank(label) || getNameSource() == NameSource.NAME ) {
            return name;
        }

        return label;
    }

    /**
     * Removes all white spaces from the given {@code name}.
     *
     * @param name
     *            The name to modify.
     * @return The modified {@code name}.
     */
    @Nonnull
    protected final String removeWhiteSpaces( @Nonnull final String name )
    {
        final StringBuilder result = new StringBuilder();
        final String[] words = name.trim().split("\\s");
        for( final String word : words ) {
            final String capitalized = NamingUtils.capitalize(word);
            result.append(capitalized);
        }
        return result.toString();
    }

    /**
     * Removes all characters that are not valid Java syntax from the given {@code name}.
     *
     * @param name
     *            The name to modify.
     * @return The modified {@code name}.
     */
    @Nonnull
    protected final String removeInvalidJavaCharacters( @Nonnull final CharSequence name )
    {
        return NamingUtils.replaceInvalidJavaCharacters(name, null);
    }

    /**
     * Converts a leading acronym into lower case format.
     * <p>
     * </p>
     * Example: {@code uncapitalizeLeadingAcronym("URLAddress") -> "urlAddress"}
     *
     * @param name
     *            The name to modify.
     * @return The modified {@code name}.
     */
    @Nonnull
    protected final String uncapitalizeLeadingAcronym( @Nonnull final String name )
    {
        String formattedName = name;
        // make first word lowercase, e.g. URLAddress -> urlAddress
        final Matcher matcher = Pattern.compile("^[A-Z]+(?=[A-Z])").matcher(formattedName);
        if( matcher.find() ) {
            formattedName = formattedName.replaceFirst(matcher.group(), matcher.group().toLowerCase(Locale.ENGLISH));
        } else {
            formattedName = StringUtils.uncapitalize(formattedName);
        }

        return formattedName;
    }

    /**
     * Removes repeated underscores ({@code "_"}) from the given {@code name}.
     *
     * @param name
     *            The name to modify.
     * @return The modified {@code name}.
     */
    @Nonnull
    protected final String removeRepeatedUnderscores( @Nonnull final String name )
    {
        return name.replaceAll("__+", "_");
    }

    /**
     * Removes underscores ({@code "_"}) from in between the letters on acronyms from the given {@code name}.
     * <p>
     * </p>
     * Example: {@code fixAcronymsInConstantNames("U_R_L_ADDRESS") -> "URL_ADDRESS"}
     *
     * @param name
     *            The name to modify.
     * @return The modofied {@code name}.
     */
    @Nonnull
    protected final String fixAcronymsInConstantNames( @Nonnull final String name )
    {
        final String regexp = "(?<=[A-Z][A-Z]_|__|^)" + // positive look behind: string start OR two uppercase with _ OR two consecutive __
            "[A-Z](_[A-Z])+" + // matching group: single uppercase letters separated by _
            "(?=_[A-Z]{2,}|__|$)"; // positive look ahead: string end OR _ with two uppercase OR two consecutive __

        String result = name;
        final Matcher matcher = Pattern.compile(regexp).matcher(name);
        while( matcher.find() ) {
            result = result.replaceFirst(matcher.group(), matcher.group().replace("_", ""));
        }
        return result;
    }

    /**
     * Appends the given {@code suffix} to the given {@code name}, if the {@code name} is a reserved keyword.
     *
     * @param name
     *            The name to modify.
     * @param suffix
     *            The suffix to append, in case the {@code name} is a reserved keyword.
     * @return The modified {@code name}.
     */
    @Nonnull
    protected final
        String
        appendSuffixIfNameIsReservedKeyword( @Nonnull final String name, @Nonnull final String suffix )
    {
        if( isReservedKeyword(name.toLowerCase()) ) {
            return name + suffix;
        }

        return name;
    }

    /**
     * Throws an {@link IllegalStateException} if the given {@code conversionOutput} is {@code null} or empty.
     *
     * @param conversionInputName
     *            The original input name, taken for example from the name tag of an OData <b>Entity</b>, which was used
     *            for the conversion.
     * @param conversionInputLabel
     *            The original input label, taken for example from the <b>sap:label</b> tag of an OData <b>Entity</b>,
     *            which was used for the conversion.
     * @param conversionOutput
     *            The output of the conversion.
     * @param conversionTarget
     *            The target type of the conversion. Example: {@code "Java class name"}.
     */
    protected final void throwIfConversionResultIsNullOrEmpty(
        @Nonnull final String conversionInputName,
        @Nullable final String conversionInputLabel,
        @Nullable final CharSequence conversionOutput,
        @Nonnull final String conversionTarget )
    {
        if( !StringUtils.isBlank(conversionOutput) ) {
            return;
        }

        final String labelExceptionPortion =
            conversionInputLabel == null
                ? ""
                : String
                    .format(
                        " or the label \"%s\" (using %s \"%s\")",
                        conversionInputLabel,
                        NameSource.class.getSimpleName(),
                        getNameSource());

        throw new IllegalStateException(
            String
                .format(
                    "The conversion of the name \"%s\"%s to a %s resulted in an empty string.",
                    conversionInputName,
                    labelExceptionPortion,
                    conversionTarget));
    }

    /**
     * Throws an {@link IllegalStateException} if the given {@code conversionOutput} is a reserved keyword.
     *
     * @param conversionInputName
     *            The original input name, taken for example from the name tag of an OData <b>Entity</b>, which was used
     *            for the conversion.
     * @param conversionInputLabel
     *            The original input label, taken for example from the <b>sap:label</b> tag of an OData <b>Entity</b>,
     *            which was used for the conversion.
     * @param conversionOutput
     *            The output of the conversion.
     * @param conversionTarget
     *            The target type of the conversion. Example: {@code "Java class name"}.
     */
    protected final void throwIfConversionResultIsReservedKeyword(
        @Nonnull final String conversionInputName,
        @Nullable final String conversionInputLabel,
        @Nonnull final String conversionOutput,
        @Nonnull final String conversionTarget )
    {
        if( !isReservedKeyword(conversionOutput) ) {
            return;
        }

        final String labelExceptionPortion =
            conversionInputLabel == null
                ? ""
                : String
                    .format(
                        " or the label \"%s\" (using %s \"%s\")",
                        conversionInputLabel,
                        NameSource.class.getSimpleName(),
                        getNameSource());

        throw new IllegalStateException(
            String
                .format(
                    "The conversion of the name \"%s\"%s to a %s resulted in \"%s\", which is a reserved keyword.",
                    conversionInputName,
                    labelExceptionPortion,
                    conversionTarget,
                    conversionOutput));
    }
}
