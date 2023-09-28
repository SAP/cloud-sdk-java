package testcomparison.namespaces.minimalmetadata;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntitySet;
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
import testcomparison.services.MinimalMetadataService;


/**
 * <p>Original entity name from the Odata EDM: <b>A_SimplePerson</b></p>
 *
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class)
@JsonSerialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class)
@JsonDeserialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class)
public class SimplePerson
    extends VdmEntity<SimplePerson>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_MINIMAL_TEST_CASE.A_SimplePerson";
    /**
     * Selector for all available fields of SimplePerson.
     *
     */
    public final static SimpleProperty<SimplePerson> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Person</b></p>
     *
     * @return
     *     The person contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Person")
    private java.lang.String person;
    public final static SimpleProperty.String<SimplePerson> PERSON = new SimpleProperty.String<SimplePerson>(SimplePerson.class, "Person");
    /**
     * Constraints: Nullable, Maximum length: 241 <p>Original property name from the Odata EDM: <b>ToFriend</b></p>
     *
     * @return
     *     The toFriend contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("ToFriend")
    private java.lang.String toFriend;
    public final static SimpleProperty.String<SimplePerson> TO_FRIEND = new SimpleProperty.String<SimplePerson>(SimplePerson.class, "ToFriend");
    /**
     * Navigation property <b>Friend</b> for <b>SimplePerson</b> to single <b>Friend</b>.
     *
     */
    @ElementName("Friend")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Friend toFriend_2;
    /**
     * Use with available request builders to apply the <b>Friend</b> navigation property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<SimplePerson, Friend> TO_FRIEND_2 = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<SimplePerson, Friend>(SimplePerson.class, "Friend", Friend.class);

    @Nonnull
    @Override
    public Class<SimplePerson> getType() {
        return SimplePerson.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Person</b></p>
     *
     * @param person
     *     The person to set.
     */
    public void setPerson(
        @Nullable
        final java.lang.String person) {
        rememberChangedField("Person", this.person);
        this.person = person;
    }

    /**
     * Constraints: Nullable, Maximum length: 241 <p>Original property name from the Odata EDM: <b>ToFriend</b></p>
     *
     * @param toFriend
     *     The toFriend to set.
     */
    public void setToFriend(
        @Nullable
        final java.lang.String toFriend) {
        rememberChangedField("ToFriend", this.toFriend);
        this.toFriend = toFriend;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "A_SimplePersons";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("Person", getPerson());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> values = super.toMapOfFields();
        values.put("Person", getPerson());
        values.put("ToFriend", getToFriend());
        return values;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("Person")) {
                final Object value = values.remove("Person");
                if ((value == null)||(!value.equals(getPerson()))) {
                    setPerson(((java.lang.String) value));
                }
            }
            if (values.containsKey("ToFriend")) {
                final Object value = values.remove("ToFriend");
                if ((value == null)||(!value.equals(getToFriend()))) {
                    setToFriend(((java.lang.String) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
            if ((values).containsKey("Friend")) {
                final Object value = (values).remove("Friend");
                if (value instanceof Map) {
                    if (toFriend_2 == null) {
                        toFriend_2 = new Friend();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) value);
                    toFriend_2 .fromMap(inputMap);
                }
            }
        }
        super.fromMap(values);
    }

    @Override
    protected java.lang.String getDefaultServicePath() {
        return MinimalMetadataService.DEFAULT_SERVICE_PATH;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties() {
        final Map<java.lang.String, Object> values = super.toMapOfNavigationProperties();
        if (toFriend_2 != null) {
            (values).put("Friend", toFriend_2);
        }
        return values;
    }

    /**
     * Retrieval of associated <b>Friend</b> entity (one to one). This corresponds to the OData navigation property <b>Friend</b>.
     * <p>
     * If the navigation property for an entity <b>SimplePerson</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     *
     * @return
     *     If the information for navigation property <b>Friend</b> is already loaded, the result will contain the <b>Friend</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<Friend> getFriendIfPresent() {
        return Option.of(toFriend_2);
    }

    /**
     * Overwrites the associated <b>Friend</b> entity for the loaded navigation property <b>Friend</b>.
     *
     * @param value
     *     New <b>Friend</b> entity.
     */
    public void setFriend(final Friend value) {
        toFriend_2 = value;
    }


    /**
     * Helper class to allow for fluent creation of SimplePerson instances.
     *
     */
    public final static class SimplePersonBuilder {

        private Friend toFriend_2;

        private SimplePerson.SimplePersonBuilder toFriend_2(final Friend value) {
            toFriend_2 = value;
            return this;
        }

        /**
         * Navigation property <b>Friend</b> for <b>SimplePerson</b> to single <b>Friend</b>.
         *
         * @param value
         *     The Friend to build this SimplePerson with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public SimplePerson.SimplePersonBuilder friend(final Friend value) {
            return toFriend_2(value);
        }

    }

}
