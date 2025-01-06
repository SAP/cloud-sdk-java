package com.sap.cloud.sdk.datamodel.odatav4.generator.annotation;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Implementations of this interface instruct the VDM generator on which annotations to apply to the generated Java
 * code.
 *
 * At this time, annotations can be controlled in the following places:
 * <ul>
 * <li>Classes representing OData entities, at the class level.</li>
 * <li>Member variables representing OData properties of entities.</li>
 * <li>Member variables representing OData navigation properties to other entity classes.</li>
 * <li>Classes representing OData complex types, at the class level.</li>
 * <li>Member variables representing OData properties of complex types.</li>
 * </ul>
 */
public interface AnnotationStrategy
{
    /**
     * Gets a list of annotations to apply to generated classes representing OData entities, at the class level. The VDM
     * generator calls this method and supplies {@link EntityAnnotationModel} objects with relevant information about
     * the OData entity it is currently processing.
     *
     * @param context
     *            Object representing an OData entity.
     * @return Set of {@link AnnotationDefinition} instances representing which annotations should be applied.
     */
    @Nonnull
    Set<AnnotationDefinition> getAnnotationsForEntity( @Nonnull final EntityAnnotationModel context );

    /**
     * Gets a list of annotations to apply to generated member variables representing OData properties of entities. The
     * VDM generator calls this method and supplies {@link EntityPropertyAnnotationModel} objects with relevant
     * information about the OData property it is currently processing.
     *
     * @param context
     *            Object representing an OData entity property.
     * @return Set of {@link AnnotationDefinition} instances representing which annotations should be applied.
     */
    @Nonnull
    Set<AnnotationDefinition> getAnnotationsForEntityProperty( @Nonnull final EntityPropertyAnnotationModel context );

    /**
     * Gets a list of annotations to apply to generated member variables representing OData navigation properties. The
     * VDM generator calls this method and supplies {@link NavigationPropertyAnnotationModel} objects with relevant
     * information about the OData property it is currently processing.
     *
     * @param context
     *            Object representing an OData navigation property.
     * @return Set of {@link AnnotationDefinition} instances representing which annotations should be applied.
     */
    @Nonnull
    Set<AnnotationDefinition>
        getAnnotationsForAssociatedEntity( @Nonnull final NavigationPropertyAnnotationModel context );

    /**
     * Gets a list of annotations to apply to generated classes representing OData complex types, at the class level.
     * The VDM generator calls this method and supplies {@link EntityAnnotationModel} objects with relevant information
     * about the OData complex type it is currently processing.
     *
     * @param context
     *            Object representing an OData complex type.
     * @return Set of {@link AnnotationDefinition} instances representing which annotations should be applied.
     */
    @Nonnull
    Set<AnnotationDefinition> getAnnotationsForComplexType( @Nonnull final EntityAnnotationModel context );

    /**
     * Gets a list of annotations to apply to generated member variables representing OData properties of complex types.
     * The VDM generator calls this method and supplies {@link EntityPropertyAnnotationModel} objects with relevant
     * information about the OData property it is currently processing.
     *
     * @param context
     *            Object representing an OData complex type property.
     * @return Set of {@link AnnotationDefinition} instances representing which annotations should be applied.
     */
    @Nonnull
    Set<AnnotationDefinition>
        getAnnotationsForComplexTypeProperty( @Nonnull final EntityPropertyAnnotationModel context );
}
