package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Helper class for representing links (navigation properties) between entities.
 *
 * @param <LinkT>
 *            The type of this link.
 * @param <EntityT>
 *            The type of the entity.
 * @param <SubEntityT>
 *            The type of the subentity.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class EntityLink<LinkT extends EntityLink<LinkT, EntityT, SubEntityT>, EntityT extends VdmObject<?>, SubEntityT extends VdmObject<?>>
    implements
    EntitySelectable<EntityT>
{
    private static final String STAR_SELECTOR = "*";

    private final List<EntityLink<LinkT, ?, ?>> descendants = new ArrayList<>();
    private final List<EntitySelectable<SubEntityT>> selectors = new ArrayList<>();

    @Nonnull
    @Getter
    private final String fieldName;

    private EntityLink(
        @Nonnull final EntityLink<LinkT, ?, SubEntityT> toCopy,
        @Nullable final Iterable<? extends EntityLink<LinkT, ?, ?>> addDescendants,
        @Nullable final Iterable<? extends EntitySelectable<SubEntityT>> addSelectors )
    {
        this(toCopy.fieldName);
        descendants.addAll(toCopy.descendants);
        selectors.addAll(toCopy.selectors);

        if( addDescendants != null ) {
            descendants.addAll(Lists.newArrayList(addDescendants));
        }

        if( addSelectors != null ) {
            selectors.addAll(Lists.newArrayList(addSelectors));
        }
    }

    /**
     * Copy constructor.
     *
     * @param toCopy
     *            The link to copy.
     */
    protected EntityLink( @Nonnull final EntityLink<LinkT, EntityT, SubEntityT> toCopy )
    {
        this(toCopy, null, null);
    }

    /**
     * Used in combination with {@link FluentHelperRead#select(Object[]) FluentHelperRead.select} when expanding a
     * navigation property to specify which fields of that navigation property to select, and which navigation
     * properties of that navigation property to expand.
     *
     * @param selectors
     *            Array of fields to select and/or navigation properties to expand.
     * @return Selector for {@link FluentHelperRead#select(Object[]) FluentHelperRead.select}.
     */
    @SuppressWarnings( { "unchecked", "varargs" } )
    @SafeVarargs
    @Nonnull
    public final LinkT select( @Nonnull final EntitySelectable<SubEntityT>... selectors )
    {
        final List<EntityLink<LinkT, ?, ?>> additionalDescendants = new ArrayList<>();
        final List<EntitySelectable<SubEntityT>> additionalSelectors = new ArrayList<>();

        for( final EntitySelectable<SubEntityT> select : selectors ) {
            if( select instanceof EntityLink ) {
                additionalDescendants.add((EntityLink<LinkT, ?, ?>) select);
            } else {
                additionalSelectors.add(select);
            }
        }

        final EntityLink<LinkT, EntityT, SubEntityT> toTranslate =
            new EntityLink<>(this, additionalDescendants, additionalSelectors);

        return translateLinkType(toTranslate);
    }

    /*
     * may not be not required, since getSelections() provide implicit expand definitions.
     * E.g. select(to_BusinessPartnerRole/*) => expand(to_BusinessPartner)
     */

    /**
     * Returns a list of expansions for this link.
     *
     * @return A list of expansions for this link.
     */
    @Nonnull
    public List<String> getExpansions()
    {
        if( descendants.isEmpty() ) {
            return Lists.newArrayList(getFieldName());
        }

        final List<String> result = new ArrayList<>();
        for( final EntityLink<?, ?, ?> descendant : descendants ) {
            for( final String name : descendant.getExpansions() ) {
                result.add(getFieldName() + "/" + name);
            }
        }
        return result;
    }

    @Nonnull
    @Override
    public List<String> getSelections()
    {
        final List<String> result = new ArrayList<>();
        if( selectors.isEmpty() && descendants.isEmpty() ) {
            result.add(getFieldName() + "/" + STAR_SELECTOR);
        }
        for( final EntitySelectable<SubEntityT> field : selectors ) {
            result.add(getFieldName() + "/" + field.getFieldName());
        }

        for( final EntityLink<?, ?, ?> descendant : descendants ) {
            for( final String name : descendant.getSelections() ) {
                result.add(getFieldName() + "/" + name);
            }
        }
        return result;
    }

    /**
     * Returns the given {@code link} in a type-safe manner.
     *
     * @param link
     *            The link to cast.
     * @return The given {@code link} in a type-safe manner.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    protected LinkT translateLinkType( final EntityLink<LinkT, EntityT, SubEntityT> link )
    {
        return (LinkT) link;
    }

    /**
     * Add a filter expression on a single navigation property.
     *
     * @param filterExpression
     *            The filter to apply.
     * @return A new expression builder that includes the given filter.
     */
    @Nonnull
    protected ExpressionFluentHelper<EntityT> filterOnOneToOneLink(
        @Nonnull final ExpressionFluentHelper<SubEntityT> filterExpression )
    {
        final ValueBoolean exp =
            ( protocol, prefixes ) -> getFieldName()
                + "/"
                + filterExpression.getDelegateExpressionWithoutOuterParentheses().getExpression(protocol, prefixes);
        return new ExpressionFluentHelper<>(exp);
    }
}
