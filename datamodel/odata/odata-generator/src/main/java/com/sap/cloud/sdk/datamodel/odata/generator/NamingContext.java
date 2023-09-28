package com.sap.cloud.sdk.datamodel.odata.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.google.common.collect.Lists;

class NamingContext
{
    private static final int GETTER_AND_SETTER_PREFIX_LENGTH = 3;

    /**
     * <p>
     * Determines whether checking two names of equality considers the case. When two names are considered equal, one of
     * them gets renamed (e.g., by adding _2 as prefix). Otherwise, no renaming takes place.
     * </p>
     *
     * <p>
     * <b>Example:</b>
     * <ul>
     * <li>Given: Two names ABC and abc</li>
     * <li>Using NameEqualityStrategy {@link NameEqualityStrategy#CASE_SENSITIVE}: ABC and abc are not equal and no
     * renaming happens.</li>
     * <li>Using NameEqualityStrategy {@link NameEqualityStrategy#CASE_INSENSITIVE}: ABC and abc are equal and one of
     * them gets renamed.</li>
     * </p>
     */
    enum NameEqualityStrategy
    {
        /**
         * <p>
         * The name quality check considers the case of the two given names.
         * </p>
         * <p>
         * <b>Example:</b>
         * </p>
         * <p>
         * Given two names ABC and abc, both are considered not equal, as the case differs.
         * </p>
         */
        CASE_SENSITIVE(true),

        /**
         * <p>
         * The name quality check does not consider the case of the two given names.
         * </p>
         * <p>
         * <b>Example:</b>
         * </p>
         * <p>
         * Given two names ABC and abc, both are considered equal, as their (different) case is not considered.
         * </p>
         */
        CASE_INSENSITIVE(false);

        private final boolean caseSensitive;

        NameEqualityStrategy( final boolean caseSensitive )
        {
            this.caseSensitive = caseSensitive;
        }

        Map<String, Integer> getOccurrencesMap()
        {
            return caseSensitive ? new HashMap<>() : new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
    }

    private static final Logger logger = MessageCollector.getLogger(NamingContext.class);

    /**
     * All names of SDK provided fields that might clash some way or another with fields and method given by the user.
     * <ul>
     * <li>allFields: If a given field is called "allFields" the corresponding static Field reference would clash with
     * our "ALL_FIELDS" variable.</li>
     * <li>destinationForFetch: A field with this name would clash with the lombok generated getter for our
     * destinationForFetch.</li>
     * </ul>
     *
     */
    private static final List<String> knownGeneratedFields = Lists.newArrayList("allFields", "destinationForFetch");

    private final Map<String, Integer> occurrences;
    private final NameFormattingStrategy nameFormattingStrategy;

    NamingContext()
    {
        this(new DefaultNameFormattingStrategy(), NameEqualityStrategy.CASE_SENSITIVE);
    }

    NamingContext( final NameFormattingStrategy nameFormattingStrategy )
    {
        this(nameFormattingStrategy, NameEqualityStrategy.CASE_SENSITIVE);
    }

    NamingContext( final NameEqualityStrategy nameEqualityStrategy )
    {
        this(new DefaultNameFormattingStrategy(), nameEqualityStrategy);
    }

    NamingContext(
        final NameFormattingStrategy nameFormattingStrategy,
        final NameEqualityStrategy nameEqualityStrategy )
    {
        this.nameFormattingStrategy = nameFormattingStrategy;
        occurrences = nameEqualityStrategy.getOccurrencesMap();
    }

    /**
     * Ensures that the given name will not create clashes in the scope this context was created for.
     * <p>
     * <b>Note:</b> All method or field names (depending on the use case of the context) need to be given to this method
     * so that we can keep track of them.
     *
     * @param proposedName
     *            The name you want to use.
     * @return The name you should use.
     */
    String ensureUniqueName( final String proposedName )
    {
        final String comparisonName = nameFormattingStrategy.applyFormat(proposedName);
        String uniqueName = proposedName;

        if( occurrences.containsKey(comparisonName) ) {
            uniqueName = addFollowingOccurrence(proposedName, comparisonName);

            logger
                .info(
                    String
                        .format(
                            "Found more than one occurrence of the Java identifier %s. The new identifier has been renamed to %s",
                            proposedName,
                            uniqueName));
        } else {
            addFirstOccurrence(comparisonName);
        }

        return uniqueName;
    }

    /**
     * Checks whether a given name already exists in this context.
     * <p>
     * This method is side-effect-free, so it does not add the name to this context!
     * <p>
     * Primarily used for testing. If this would be used in the actual code we would call the strategy too often.
     *
     * @param proposedName
     *            The name you want to use.
     * @return True, if this name is already in use; false, else
     */
    boolean alreadyUses( final String proposedName )
    {
        final String comparisonName = nameFormattingStrategy.applyFormat(proposedName);
        return occurrences.containsKey(comparisonName);
    }

    private void addFirstOccurrence( final String comparisonName )
    {
        occurrences.put(comparisonName, 1);
    }

    private String addFollowingOccurrence( final String proposedName, final String comparisonName )
    {
        final int newCount = occurrences.get(comparisonName) + 1;
        occurrences.put(comparisonName, newCount);
        return formatNextName(proposedName, newCount);
    }

    private String formatNextName( final String proposedName, final int occurrence )
    {
        return String.format("%s_%d", proposedName, occurrence);
    }

    /**
     * Adds all setter and getter methods (as variable names) visible in the given class to this context to prevent
     * method name clashing.
     *
     * @param methodsProvidingClass
     *            The class you want to take all visible (not private) getter and setter method names from.
     */
    void loadGettersAndSettersOfClassAsAlreadyPresentFields( final Class<?> methodsProvidingClass )
    {
        final Method[] methods = methodsProvidingClass.getDeclaredMethods();
        Arrays
            .stream(methods)
            .filter(NamingContext::isVisibleInSubclasses)
            .map(Method::getName)
            .filter(NamingContext::hasGetterOrSetterPrefix)
            .map(NamingContext::removeGetterOrSetterPrefix)
            .map(StringUtils::uncapitalize)
            .map(nameFormattingStrategy::applyFormat)
            .forEach(this::addFirstOccurrence);

        // as getDeclaredMethods only lists methods on the given class we need to recursively go up the corresponding
        // inheritance hierarchy
        final Class<?> superclass = methodsProvidingClass.getSuperclass();
        if( superclass != null ) {
            loadGettersAndSettersOfClassAsAlreadyPresentFields(superclass);
        }
    }

    private static boolean hasGetterOrSetterPrefix( final String methodName )
    {
        return (methodName.startsWith("get") || methodName.startsWith("set"))
            && Character.isUpperCase(methodName.charAt(GETTER_AND_SETTER_PREFIX_LENGTH));
    }

    private static String removeGetterOrSetterPrefix( final String methodName )
    {
        return methodName.substring(GETTER_AND_SETTER_PREFIX_LENGTH);
    }

    private static boolean isVisibleInSubclasses( final Method method )
    {
        return !Modifier.isPrivate(method.getModifiers());
    }

    void loadKnownGeneratedFields()
    {
        knownGeneratedFields.stream().map(nameFormattingStrategy::applyFormat).forEach(this::addFirstOccurrence);
    }
}
