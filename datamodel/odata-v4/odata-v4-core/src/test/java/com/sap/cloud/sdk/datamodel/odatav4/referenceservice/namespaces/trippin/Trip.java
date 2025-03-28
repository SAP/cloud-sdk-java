/*
 * Generated by OData VDM code generator of SAP Cloud SDK in version 4.21.0
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * Original entity name from the Odata EDM: <b>Trip</b>
 * </p>
 *
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString( doNotUseGetters = true, callSuper = true )
@EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
@JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
@JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
@JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
public class Trip extends VdmEntity<Trip>
{

    @Getter
    private final java.lang.String odataType = "Trippin.Trip";
    /**
     * Selector for all available fields of Trip.
     *
     */
    public final static SimpleProperty<Trip> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>TripId</b>
     * </p>
     *
     * @return The tripId contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "TripId" )
    private Integer tripId;
    public final static SimpleProperty.NumericInteger<Trip> TRIP_ID =
        new SimpleProperty.NumericInteger<Trip>(Trip.class, "TripId");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ShareId</b>
     * </p>
     *
     * @return The shareId contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "ShareId" )
    private UUID shareId;
    public final static SimpleProperty.Guid<Trip> SHARE_ID = new SimpleProperty.Guid<Trip>(Trip.class, "ShareId");
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @return The name contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Name" )
    private java.lang.String name;
    public final static SimpleProperty.String<Trip> NAME = new SimpleProperty.String<Trip>(Trip.class, "Name");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Budget</b>
     * </p>
     *
     * @return The budget contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Budget" )
    private Float budget;
    public final static SimpleProperty.NumericDecimal<Trip> BUDGET =
        new SimpleProperty.NumericDecimal<Trip>(Trip.class, "Budget");
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Description</b>
     * </p>
     *
     * @return The description contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Description" )
    private java.lang.String description;
    public final static SimpleProperty.String<Trip> DESCRIPTION =
        new SimpleProperty.String<Trip>(Trip.class, "Description");
    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Tags</b>
     * </p>
     *
     * @return The tags contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "Tags" )
    private java.util.Collection<java.lang.String> tags;
    public final static SimpleProperty.Collection<Trip, java.lang.String> TAGS =
        new SimpleProperty.Collection<Trip, java.lang.String>(Trip.class, "Tags", java.lang.String.class);
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>StartsAt</b>
     * </p>
     *
     * @return The startsAt contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "StartsAt" )
    private OffsetDateTime startsAt;
    public final static SimpleProperty.DateTime<Trip> STARTS_AT =
        new SimpleProperty.DateTime<Trip>(Trip.class, "StartsAt");
    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>EndsAt</b>
     * </p>
     *
     * @return The endsAt contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName( "EndsAt" )
    private OffsetDateTime endsAt;
    public final static SimpleProperty.DateTime<Trip> ENDS_AT = new SimpleProperty.DateTime<Trip>(Trip.class, "EndsAt");
    /**
     * Navigation property <b>PlanItems</b> for <b>Trip</b> to multiple <b>PlanItem</b>.
     *
     */
    @ElementName( "PlanItems" )
    @Getter( AccessLevel.NONE )
    @Setter( AccessLevel.NONE )
    private List<PlanItem> toPlanItems;
    /**
     * Use with available request builders to apply the <b>PlanItems</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<Trip, PlanItem> TO_PLAN_ITEMS =
        new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<Trip, PlanItem>(
            Trip.class,
            "PlanItems",
            PlanItem.class);

    @Nonnull
    @Override
    public Class<Trip> getType()
    {
        return Trip.class;
    }

    /**
     * (Key Field) Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>TripId</b>
     * </p>
     *
     * @param tripId
     *            The tripId to set.
     */
    public void setTripId( @Nullable final Integer tripId )
    {
        rememberChangedField("TripId", this.tripId);
        this.tripId = tripId;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>ShareId</b>
     * </p>
     *
     * @param shareId
     *            The shareId to set.
     */
    public void setShareId( @Nullable final UUID shareId )
    {
        rememberChangedField("ShareId", this.shareId);
        this.shareId = shareId;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Name</b>
     * </p>
     *
     * @param name
     *            The name to set.
     */
    public void setName( @Nullable final java.lang.String name )
    {
        rememberChangedField("Name", this.name);
        this.name = name;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>Budget</b>
     * </p>
     *
     * @param budget
     *            The budget to set.
     */
    public void setBudget( @Nullable final Float budget )
    {
        rememberChangedField("Budget", this.budget);
        this.budget = budget;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Description</b>
     * </p>
     *
     * @param description
     *            The description to set.
     */
    public void setDescription( @Nullable final java.lang.String description )
    {
        rememberChangedField("Description", this.description);
        this.description = description;
    }

    /**
     * Constraints: Nullable
     * <p>
     * Original property name from the Odata EDM: <b>Tags</b>
     * </p>
     *
     * @param tags
     *            The tags to set.
     */
    public void setTags( @Nullable final java.util.Collection<java.lang.String> tags )
    {
        rememberChangedField("Tags", this.tags);
        this.tags = tags;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>StartsAt</b>
     * </p>
     *
     * @param startsAt
     *            The startsAt to set.
     */
    public void setStartsAt( @Nullable final OffsetDateTime startsAt )
    {
        rememberChangedField("StartsAt", this.startsAt);
        this.startsAt = startsAt;
    }

    /**
     * Constraints: Not nullable
     * <p>
     * Original property name from the Odata EDM: <b>EndsAt</b>
     * </p>
     *
     * @param endsAt
     *            The endsAt to set.
     */
    public void setEndsAt( @Nullable final OffsetDateTime endsAt )
    {
        rememberChangedField("EndsAt", this.endsAt);
        this.endsAt = endsAt;
    }

    @Override
    protected java.lang.String getEntityCollection()
    {
        return "Trips";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey()
    {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("TripId", getTripId());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields()
    {
        final Map<java.lang.String, Object> values = super.toMapOfFields();
        values.put("TripId", getTripId());
        values.put("ShareId", getShareId());
        values.put("Name", getName());
        values.put("Budget", getBudget());
        values.put("Description", getDescription());
        values.put("Tags", getTags());
        values.put("StartsAt", getStartsAt());
        values.put("EndsAt", getEndsAt());
        return values;
    }

    @Override
    protected void fromMap( final Map<java.lang.String, Object> inputValues )
    {
        final Map<java.lang.String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if( values.containsKey("TripId") ) {
                final Object value = values.remove("TripId");
                if( (value == null) || (!value.equals(getTripId())) ) {
                    setTripId(((Integer) value));
                }
            }
            if( values.containsKey("ShareId") ) {
                final Object value = values.remove("ShareId");
                if( (value == null) || (!value.equals(getShareId())) ) {
                    setShareId(((UUID) value));
                }
            }
            if( values.containsKey("Name") ) {
                final Object value = values.remove("Name");
                if( (value == null) || (!value.equals(getName())) ) {
                    setName(((java.lang.String) value));
                }
            }
            if( values.containsKey("Budget") ) {
                final Object value = values.remove("Budget");
                if( (value == null) || (!value.equals(getBudget())) ) {
                    setBudget(((Float) value));
                }
            }
            if( values.containsKey("Description") ) {
                final Object value = values.remove("Description");
                if( (value == null) || (!value.equals(getDescription())) ) {
                    setDescription(((java.lang.String) value));
                }
            }
            if( values.containsKey("Tags") ) {
                final Object value = values.remove("Tags");
                if( value instanceof Iterable ) {
                    final LinkedList<java.lang.String> tags = new LinkedList<java.lang.String>();
                    for( Object item : ((Iterable<?>) value) ) {
                        tags.add(((java.lang.String) item));
                    }
                    setTags(tags);
                }
            }
            if( values.containsKey("StartsAt") ) {
                final Object value = values.remove("StartsAt");
                if( (value == null) || (!value.equals(getStartsAt())) ) {
                    setStartsAt(((OffsetDateTime) value));
                }
            }
            if( values.containsKey("EndsAt") ) {
                final Object value = values.remove("EndsAt");
                if( (value == null) || (!value.equals(getEndsAt())) ) {
                    setEndsAt(((OffsetDateTime) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if( (values).containsKey("PlanItems") ) {
                final Object value = (values).remove("PlanItems");
                if( value instanceof Iterable ) {
                    if( toPlanItems == null ) {
                        toPlanItems = Lists.newArrayList();
                    } else {
                        toPlanItems = Lists.newArrayList(toPlanItems);
                    }
                    int i = 0;
                    for( Object item : ((Iterable<?>) value) ) {
                        if( !(item instanceof Map) ) {
                            continue;
                        }
                        PlanItem entity;
                        if( toPlanItems.size() > i ) {
                            entity = toPlanItems.get(i);
                        } else {
                            entity = new PlanItem();
                            toPlanItems.add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings( "unchecked" )
                        final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object>) item);
                        entity.fromMap(inputMap);
                    }
                }
            }
        }
        super.fromMap(values);
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties()
    {
        final Map<java.lang.String, Object> values = super.toMapOfNavigationProperties();
        if( toPlanItems != null ) {
            (values).put("PlanItems", toPlanItems);
        }
        return values;
    }

    /**
     * Retrieval of associated <b>PlanItem</b> entities (one to many). This corresponds to the OData navigation property
     * <b>PlanItems</b>.
     * <p>
     * If the navigation property for an entity <b>Trip</b> has not been resolved yet, this method will <b>not query</b>
     * further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return If the information for navigation property <b>PlanItems</b> is already loaded, the result will contain
     *         the <b>PlanItem</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is
     *         returned.
     */
    @Nonnull
    public Option<List<PlanItem>> getPlanItemsIfPresent()
    {
        return Option.of(toPlanItems);
    }

    /**
     * Overwrites the list of associated <b>PlanItem</b> entities for the loaded navigation property <b>PlanItems</b>.
     * <p>
     * If the navigation property <b>PlanItems</b> of a queried <b>Trip</b> is operated lazily, an <b>ODataException</b>
     * can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and
     * persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the
     * first time and it has not yet been loaded, an OData query will be run in order to load the missing information
     * and its result will get cached for future invocations.
     *
     * @param value
     *            List of <b>PlanItem</b> entities.
     */
    public void setPlanItems( @Nonnull final List<PlanItem> value )
    {
        if( toPlanItems == null ) {
            toPlanItems = Lists.newArrayList();
        }
        toPlanItems.clear();
        toPlanItems.addAll(value);
    }

    /**
     * Adds elements to the list of associated <b>PlanItem</b> entities. This corresponds to the OData navigation
     * property <b>PlanItems</b>.
     * <p>
     * If the navigation property <b>PlanItems</b> of a queried <b>Trip</b> is operated lazily, an <b>ODataException</b>
     * can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and
     * persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the
     * first time and it has not yet been loaded, an OData query will be run in order to load the missing information
     * and its result will get cached for future invocations.
     *
     * @param entity
     *            Array of <b>PlanItem</b> entities.
     */
    public void addPlanItems( PlanItem... entity )
    {
        if( toPlanItems == null ) {
            toPlanItems = Lists.newArrayList();
        }
        toPlanItems.addAll(Lists.newArrayList(entity));
    }

    /**
     * Function that can be applied to any entity object of this class.
     * </p>
     *
     * @return Function object prepared with the given parameters to be applied to any entity object of this class.
     *         </p>
     *         To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static
        com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToCollection<Trip, Person>
        getInvolvedPeople()
    {
        final Map<java.lang.String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToCollection<Trip, Person>(
            Trip.class,
            Person.class,
            "Trippin.GetInvolvedPeople",
            parameters);
    }

    /**
     * Helper class to allow for fluent creation of Trip instances.
     *
     */
    public final static class TripBuilder
    {

        private List<PlanItem> toPlanItems = Lists.newArrayList();

        private Trip.TripBuilder toPlanItems( final List<PlanItem> value )
        {
            toPlanItems.addAll(value);
            return this;
        }

        /**
         * Navigation property <b>PlanItems</b> for <b>Trip</b> to multiple <b>PlanItem</b>.
         *
         * @param value
         *            The PlanItems to build this Trip with.
         * @return This Builder to allow for a fluent interface.
         */
        @Nonnull
        public Trip.TripBuilder planItems( PlanItem... value )
        {
            return toPlanItems(Lists.newArrayList(value));
        }

    }

}
