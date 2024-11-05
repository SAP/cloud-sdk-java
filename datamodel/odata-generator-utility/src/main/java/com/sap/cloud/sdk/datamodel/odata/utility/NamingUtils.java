/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

/**
 * Utility class, that bundles various naming related operations. <b>This class is meant for internal usage only.</b>
 */
public final class NamingUtils
{
    private static final String FLUENT_HELPER_SUFFIX = "FluentHelper";
    private static final String FLUENT_HELPER_BY_KEY_SUFFIX = "ByKeyFluentHelper";
    private static final String FLUENT_HELPER_CREATE_SUFFIX = "CreateFluentHelper";
    private static final String FLUENT_HELPER_UPDATE_SUFFIX = "UpdateFluentHelper";
    private static final String FLUENT_HELPER_DELETE_SUFFIX = "DeleteFluentHelper";

    private NamingUtils()
    {
        throw new AssertionError("This static utility class must not be instantiated.");
    }

    /**
     * Converts the given {@code apiName} into the title of a service.
     *
     * @param apiName
     *            The name to convert.
     * @return The converted service title.
     */
    @Nonnull
    public static String apiNameToServiceTitle( @Nonnull final String apiName )
    {
        String formattedName = apiName;
        formattedName = formattedName.replaceAll("_", " ");
        return formattedName;
    }

    /**
     * Converts the given {@code serviceName} into a Java package name.
     *
     * @param serviceName
     *            The name to convert.
     * @return The converted Java package name.
     */
    @Nonnull
    public static String serviceNameToJavaPackageName( @Nonnull final String serviceName )
    {
        return serviceNameToBaseJavaClassName(serviceName).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Converts the given {@code serviceName} into a base name for a Java class.
     *
     * @param serviceName
     *            The name to convert.
     * @return The converted base name for a Java class.
     */
    @Nonnull
    public static String serviceNameToBaseJavaClassName( @Nonnull final String serviceName )
    {
        String formattedName =
            serviceName.trim().replaceAll("/", "Or").replaceAll("&", "And").replaceAll("\\(.*\\)", "");

        formattedName = prependValidCharacterIfNecessary(formattedName, 'A');
        formattedName = replaceInvalidJavaCharacters(formattedName, "_");
        formattedName = formattedName.replaceAll("_+", "_");

        // To keep the CamelCase portions of the name as is we need to format them as snake_case first ...
        formattedName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, formattedName);
        // ... and then format this together with the replacements made above back to CamelCase
        formattedName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, formattedName);

        formattedName =
            formattedName
                .replace("ODataServiceFor", "")
                .replace("RemoteApiFor", "")
                .replace("ApiFor", "")
                .replace("Api", "")
                .replaceAll("Service$", "");

        return formattedName;
    }

    private static String prependValidCharacterIfNecessary( final String stringToModify, final Character charToPrepend )
    {
        final StringBuilder result = new StringBuilder();
        if( !Character.isJavaIdentifierStart(stringToModify.charAt(0)) ) {
            result.append(charToPrepend);

        }
        result.append(stringToModify);
        return result.toString();
    }

    /**
     * Replaces all characters that are not part of a valid Java name within the given {@code stringToClean} with the
     * given {@code partReplacement}.
     *
     * @param stringToClean
     *            The string that may contain invalid characters.
     * @param partReplacement
     *            The string to replace invalid characters with.
     * @return The cleaned {@code stringToClean}.
     */
    @Nonnull
    public static String replaceInvalidJavaCharacters(
        @Nonnull final CharSequence stringToClean,
        @Nullable final String partReplacement )
    {
        final StringBuilder result = new StringBuilder();
        int inputIndex = 0;

        while( result.length() == 0 && inputIndex < stringToClean.length() ) {
            final char currChar = stringToClean.charAt(inputIndex);
            if( Character.isJavaIdentifierStart(currChar) ) {
                result.append(currChar);
            }
            inputIndex++;
        }

        for( int i = inputIndex; i < stringToClean.length(); i++ ) {
            final char currChar = stringToClean.charAt(i);
            if( Character.isJavaIdentifierPart(currChar) ) {
                result.append(currChar);
            } else if( partReplacement != null ) {
                result.append(partReplacement);
            }
        }

        return result.toString();
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>getByKey</b> service method name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveGetEntityServiceMethodName( @Nonnull final String javaEntityClassName )
    {
        return formatServiceMethodName(javaEntityClassName, "get", "ByKey");
    }

    /**
     * Converts the given {javaEntityClassName} into a Java <b>getAll</b> service method name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveGetAllEntitiesServiceMethodName( @Nonnull final String javaEntityClassName )
    {
        return formatServiceMethodName(javaEntityClassName, "getAll", "");
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>count</b> service method name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveCountEntitiesServiceMethodName( @Nonnull final String javaEntityClassName )
    {
        return formatServiceMethodName(javaEntityClassName, "count", "");
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>create</b> service method name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveCreateEntityServiceMethodName( @Nonnull final String javaEntityClassName )
    {
        return formatServiceMethodName(javaEntityClassName, "create", "");
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>update</b> service method name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveUpdateEntityServiceMethodName( @Nonnull final String javaEntityClassName )
    {
        return formatServiceMethodName(javaEntityClassName, "update", "");
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>delete</b> service method name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveDeleteEntityServiceMethodName( @Nonnull final String javaEntityClassName )
    {
        return formatServiceMethodName(javaEntityClassName, "delete", "");
    }

    private static String formatServiceMethodName(
        final String javaEntityClassName,
        final String methodPrefix,
        final String methodSuffix )
    {
        return methodPrefix + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, javaEntityClassName) + methodSuffix;
    }

    /*
     * Entities
     */

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>FluentHelper</b> class name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java class name.
     */
    @Nonnull
    public static String deriveJavaEntityFluentHelperClassName( @Nonnull final String javaEntityClassName )
    {
        return javaEntityClassName + FLUENT_HELPER_SUFFIX;
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>FluentHelperByKey</b> class name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java class name.
     */
    @Nonnull
    public static String deriveJavaEntityByKeyFluentHelperClassName( @Nonnull final String javaEntityClassName )
    {
        return javaEntityClassName + FLUENT_HELPER_BY_KEY_SUFFIX;
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>FluentHelperCreate</b> class name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java class name.
     */
    @Nonnull
    public static String deriveJavaCreateFluentHelperClassName( @Nonnull final String javaEntityClassName )
    {
        return javaEntityClassName + FLUENT_HELPER_CREATE_SUFFIX;
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>FluentHelperUpdate</b> class name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java class name.
     */
    @Nonnull
    public static String deriveJavaUpdateFluentHelperClassName( @Nonnull final String javaEntityClassName )
    {
        return javaEntityClassName + FLUENT_HELPER_UPDATE_SUFFIX;
    }

    /**
     * Converts the given {@code javaEntityClassName} into a Java <b>FluentHelperDelete</b> class name.
     *
     * @param javaEntityClassName
     *            The name to convert.
     * @return The converted Java class name.
     */
    @Nonnull
    public static String deriveJavaDeleteFluentHelperClassName( @Nonnull final String javaEntityClassName )
    {
        return javaEntityClassName + FLUENT_HELPER_DELETE_SUFFIX;
    }

    /*
     * Navigation properties
     */

    /**
     * Converts the given {@code navigationPropertyJavaMethodName} into a Java <b>fetch</b> method name.
     *
     * @param navigationPropertyJavaMethodName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveJavaFetchMethodName( @Nonnull final String navigationPropertyJavaMethodName )
    {
        return "fetch" + StringUtils.capitalize(navigationPropertyJavaMethodName);
    }

    /**
     * Converts the given {@code navigationPropertyJavaMethodName} into a Java <b>getIfPresent</b> method name.
     *
     * @param navigationPropertyJavaMethodName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveJavaGetIfPresentMethodName( @Nonnull final String navigationPropertyJavaMethodName )
    {
        return "get" + StringUtils.capitalize(navigationPropertyJavaMethodName) + "IfPresent";
    }

    /**
     * Converts the given {@code navigationPropertyJavaMethodName} into a Java <b>getOrFetch</b> method name.
     *
     * @param navigationPropertyJavaMethodName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveJavaGetOrFetchMethodName( @Nonnull final String navigationPropertyJavaMethodName )
    {
        return "get" + StringUtils.capitalize(navigationPropertyJavaMethodName) + "OrFetch";
    }

    /**
     * Converts the given {@code navigationPropertyJavaMethodName} into a Java <b>set</b> method name.
     *
     * @param navigationPropertyJavaMethodName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveJavaSetMethodName( @Nonnull final String navigationPropertyJavaMethodName )
    {
        return "set" + StringUtils.capitalize(navigationPropertyJavaMethodName);
    }

    /**
     * Converts the given {@code navigationPropertyJavaMethodName} into a Java <b>add</b> method name.
     *
     * @param navigationPropertyJavaMethodName
     *            The name to convert.
     * @return The converted Java method name.
     */
    @Nonnull
    public static String deriveJavaAddMethodName( @Nonnull final String navigationPropertyJavaMethodName )
    {
        return "add" + StringUtils.capitalize(navigationPropertyJavaMethodName);
    }

    /*
     * Function imports
     */

    /**
     * Converts the given {@code httpMethod} into a Java constant name for the appropriate HttpClient instance.
     *
     * @param httpMethod
     *            The name to convert.
     * @return The converted Java constant name.
     */
    @Nonnull
    public static String httpMethodToApacheClientClassName( @Nonnull final String httpMethod )
    {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, httpMethod);
    }

    @Nonnull
    static String capitalize( @Nonnull final String words )
    {
        final Pattern matchPattern = Pattern.compile("^\\s*|\\s+");
        return modifyFirstCharacterAfterMatch(words, matchPattern, String::toUpperCase);
    }

    @Nonnull
    static String uncapitalize( @Nonnull final String words )
    {
        final Pattern matchPattern = Pattern.compile("^\\s*|\\s+");
        return modifyFirstCharacterAfterMatch(words, matchPattern, String::toLowerCase);
    }

    private static String modifyFirstCharacterAfterMatch(
        @Nonnull final String words,
        @Nonnull final Pattern pattern,
        @Nonnull final Function<String, String> modify )
    {
        if( words.isEmpty() ) {
            return words;
        }

        final Matcher matcher = pattern.matcher(words);

        String result = words;
        while( matcher.find() ) {
            final int nextEnd = matcher.end();
            if( nextEnd >= words.length() ) {
                // we reached the end of the string
                break;
            }

            result =
                result.substring(0, nextEnd)
                    + modify.apply(result.substring(nextEnd, nextEnd + 1))
                    + result.substring(nextEnd + 1);
        }

        return result;
    }
}
