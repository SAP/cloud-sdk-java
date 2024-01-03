/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;

/**
 * Represents a strategy for generating various types of Java names from OData entity/property names and labels.
 */
@Beta
public interface NamingStrategy
{
    /**
     * Returns the {@link NameSource} used by this {@link NamingStrategy}.
     *
     * @return The used {@link NameSource}.
     */
    @Nonnull
    NameSource getNameSource();

    /**
     * Sets the {@link NameSource} that should be used by this {@link NamingStrategy}.
     *
     * @param nameSource
     *            The {@link NameSource} to use.
     */
    void setNameSource( @Nonnull final NameSource nameSource );

    /**
     * Determines whether the {@code proposedName} is a language reserved keyword.
     *
     * @param name
     *            String to check
     * @return {@code true} if {@code proposedName} is a reserved keyword, {@code false} otherwise.
     */
    boolean isReservedKeyword( @Nonnull final String name );

    /**
     * Called by the VDM generator to convert the name and the label of an OData entity type into a suitable Java class
     * name. The resulting name is used to generate the following:
     * <ul>
     * <li>Class that represents the entity type</li>
     * <li>Entity retrieval fluent helpers ({@code ...FluentHelper} and {@code ...ByKeyFluentHelper})</li>
     * <li>Entity modification fluent helpers ({@code ...CreateFluentHelper}, {@code ...UpdateFluentHelper},
     * {@code ...DeleteFluentHelper})</li>
     * </ul>
     *
     * @param name
     *            Provided by the VDM generator. It reads the <b>Name</b> attribute value of an <b>EntityType</b> tag in
     *            the metadata file.
     * @param label
     *            Provided by the VDM generator. I reads the <b>sap:label</b> attribute of an <b>EntityType</b> tag in
     *            the metadata file.
     * @return A suitable Java class name that the VDM generator will use to create new VDM classes.
     */
    @Nonnull
    String generateJavaClassName( @Nonnull final String name, @Nullable final String label );

    /**
     * Called by the VDM generator to convert the name and the label of a property within an OData entity type into a
     * suitable Java member variable name. These are the members of VDM entity classes that hold the values of entity
     * properties.
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of a
     *            <b>Property</b> tag, which is within an <b>EntityType</b> tag.
     * @param label
     *            Provided by the VDM generator. In the metadata file, it reads the <b>sap:label</b> attribute value of
     *            a <b>Property</b> tag, which is within an <b>EntityType</b> tag.
     * @return A suitable Java member variable name that the VDM generator will use to create a member variable in the
     *         newly generated VDM entity class.
     */
    @Nonnull
    String generateJavaFieldName( @Nonnull final String name, @Nullable final String label );

    /**
     * Called by the VDM generator to convert the property name and the label of an OData entity type into a suitable
     * Java constant ({@code public static final}) name. These constants are the fluent helper fields and they end up in
     * the newly generated VDM entity classes.
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of a
     *            <b>Property</b> tag, which is within an <b>EntityType</b> tag.
     * @param label
     *            Provided by the VDM generator. In the metadata file, it reads the <b>sap:label</b> attribute value of
     *            a <b>Property</b> tag, which is within an <b>EntityType</b> tag.
     * @return A suitable Java constant name that the VDM generator will use to create a constant in the newly generated
     *         VDM entity class.
     */
    @Nonnull
    String generateJavaConstantName( @Nonnull final String name, @Nullable final String label );

    /**
     * Called by the VDM generator to convert the name and the label of an OData navigation property into a suitable
     * Java member variable name. These are the members of VDM entity classes that hold references to other VDM entities
     * (as defined by navigation properties in the metadata file).
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of a
     *            <b>NavigationProperty</b> tag, which is within an <b>EntityType</b> tag.
     * @return A suitable Java member variable name that the VDM generator will use to create a member variable in the
     *         newly generated VDM entity class.
     */
    @Nonnull
    String generateJavaNavigationPropertyFieldName( @Nonnull final String name );

    /**
     * Called by the VDM generator to convert the name and the label of an OData navigation property into a suitable
     * Java constant ({@code public static final}) name. These constants are the fluent helper fields and they end up in
     * the newly generated VDM entity classes.
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of a
     *            <b>NavigationProperty</b> tag, which is within an <b>EntityType</b> tag.
     * @return A suitable Java constant name that the VDM generator will use to create a constant in the newly generated
     *         VDM entity class.
     */
    @Nonnull
    String generateJavaNavigationPropertyConstantName( @Nonnull final String name );

    /**
     * Called by the VDM generator to convert the name and the label of an OData navigation property into a partial Java
     * method name. The generator uses the result to create the following methods in the VDM entity class for accessing
     * an OData navigation property:
     * <ul>
     * <li>Retrieval methods - {@code fetch...()}, {@code get...OrNull()}, {@code get...OrFetch()}</li>
     * <li>Modification methods - {@code set...()}, {@code add...()}</li>
     * </ul>
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of a
     *            <b>NavigationProperty</b> tag, which is within an <b>EntityType</b> tag.
     * @return A suitable Java method name that the VDM generator will use to create methods in the newly generated VDM
     *         entity class.
     */
    @Nonnull
    String generateJavaMethodName( @Nonnull final String name );

    /**
     * Called by the VDM generator to convert the name and the label of an OData navigation property into a Lombok
     * builder method name. The generator uses the result to augment the Lombok builder of a VDM entity class with
     * methods to populate references to other VDM entities (navigation properties). For builder methods, a different
     * naming convention should be used that is consistent with the Lombok builder pattern.
     *
     * @see <a href="https://projectlombok.org/features/Builder">Lombok Builder</a>
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of a
     *            <b>NavigationProperty</b> tag, which is within an <b>EntityType</b> tag.
     * @return A suitable Java method name that the VDM generator will use to create a method in the builder inner class
     *         of newly generated VDM entity classes.
     */
    @Nonnull
    String generateJavaBuilderMethodName( @Nonnull final String name );

    /**
     * Called by the VDM generator to convert the name and the label of an OData operation (e.g. (un-)bound actions or
     * functions) into a suitable Java method name. The generator uses the result to create a method in the associated
     * service class which calls the OData operation.
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of an
     *            operation (e.g. <b>FunctionImport</b> inside the <b>EntityContainer</b> tag).
     * @param label
     *            Provided by the VDM generator. In the metadata file, it reads the <b>sap:label</b> attribute value of
     *            an operation (e.g. <b>FunctionImport</b> inside the <b>EntityContainer</b> tag).
     * @return A suitable Java method name that the VDM generator will use to create a method in the newly generated VDM
     *         service class.
     */
    @Nonnull
    String generateJavaOperationMethodName( @Nonnull final String name, @Nullable final String label );

    /**
     * Called by the VDM generator to convert the name and the label of an OData operation (e.g. (un-)bound actions or
     * functions) parameter into a suitable Java variable (method parameter) name. The generator uses the result to
     * populate the method it created for the function import with the parameter.
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of a
     *            <b>Parameter</b> tag, which is within the operation tag (e.g. <b>FunctionImport</b> inside the
     *            <b>EntityContainer</b> tag).
     * @param label
     *            Provided by the VDM generator. In the metadata file, it reads the <b>sap:label</b> attribute value of
     *            a <b>Parameter</b> tag, which is within the operation tag (e.g. <b>FunctionImport</b> inside the
     *            <b>EntityContainer</b> tag).
     * @return A suitable Java variable name that the VDM generator will use to populate the function import method with
     *         parameters.
     */
    @Nonnull
    String generateJavaMethodParameterName( @Nonnull final String name, @Nullable final String label );

    /**
     * Called by the VDM generator to convert the name and the label of an OData operation (e.g. (un-)bound actions or
     * functions) into a suitable Java class name. The generator uses the result to create a fluent helper class for
     * calling an OData function import. This fluent helper is returned by the function import method in the associated
     * VDM service class.
     *
     * @param name
     *            Provided by the VDM generator. In the metadata file, it reads the <b>Name</b> attribute value of an
     *            operation (e.g. <b>FunctionImport</b> inside the <b>EntityContainer</b> tag).
     * @param label
     *            Provided by the VDM generator. In the metadata file, it reads the <b>sap:label</b> attribute value of
     *            an operation (e.g. <b>FunctionImport</b> inside the <b>EntityContainer</b> tag).
     * @return A suitable Java class name that the VDM generator will use to create a function import fluent helper
     *         class.
     */
    @Nonnull
    String generateJavaFluentHelperClassName( @Nonnull final String name, @Nullable final String label );
}
