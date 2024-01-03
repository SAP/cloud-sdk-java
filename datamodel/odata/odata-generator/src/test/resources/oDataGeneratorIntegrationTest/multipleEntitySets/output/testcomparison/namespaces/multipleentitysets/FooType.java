/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import testcomparison.namespaces.multipleentitysets.field.FooTypeField;
import testcomparison.namespaces.multipleentitysets.selectable.FooTypeSelectable;


/**
 * <p>Original entity name from the Odata EDM: <b>A_FooTypeType</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class FooType
    extends VdmEntity<FooType>
{

    /**
     * Selector for all available fields of FooType.
     * 
     */
    public final static FooTypeSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Foo</b></p>
     * 
     * @return
     *     The foo contained in this entity.
     */
    @Key
    @SerializedName("Foo")
    @JsonProperty("Foo")
    @Nullable
    @ODataField(odataName = "Foo")
    private String foo;
    /**
     * Use with available fluent helpers to apply the <b>Foo</b> field to query operations.
     * 
     */
    public final static FooTypeField<String> FOO = new FooTypeField<String>("Foo");
    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Type</b></p>
     * 
     * @return
     *     The type_2 contained in this entity.
     */
    @SerializedName("Type")
    @JsonProperty("Type")
    @Nullable
    @ODataField(odataName = "Type")
    private String type_2;
    /**
     * Use with available fluent helpers to apply the <b>Type</b> field to query operations.
     * 
     */
    public final static FooTypeField<String> TYPE_2 = new FooTypeField<String>("Type");

    @Nonnull
    @Override
    public Class<FooType> getType() {
        return FooType.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Foo</b></p>
     * 
     * @param foo
     *     The foo to set.
     */
    public void setFoo(
        @Nullable
        final String foo) {
        rememberChangedField("Foo", this.foo);
        this.foo = foo;
    }

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>Type</b></p>
     * 
     * @param type_2
     *     The type_2 to set.
     */
    public void setType_2(
        @Nullable
        final String type_2) {
        rememberChangedField("Type", this.type_2);
        this.type_2 = type_2;
    }

    @Override
    protected String getEntityCollection() {
        return "A_FooType";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newHashMap();
        result.put("Foo", getFoo());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("Foo", getFoo());
        values.put("Type", getType_2());
        return values;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("Foo")) {
                final Object value = values.remove("Foo");
                if ((value == null)||(!value.equals(getFoo()))) {
                    setFoo(((String) value));
                }
            }
            if (values.containsKey("Type")) {
                final Object value = values.remove("Type");
                if ((value == null)||(!value.equals(getType_2()))) {
                    setType_2(((String) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
        }
        super.fromMap(values);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     * 
     * @param fieldName
     *     The name of the extension field as returned by the OData service.
     * @param <T>
     *     The type of the extension field when performing value comparisons.
     * @param fieldType
     *     The Java type to use for the extension field when performing value comparisons.
     * @return
     *     A representation of an extension field from this entity.
     */
    @Nonnull
    public static<T >FooTypeField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new FooTypeField<T>(fieldName);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     * 
     * @param typeConverter
     *     A TypeConverter<T, DomainT> instance whose first generic type matches the Java type of the field
     * @param fieldName
     *     The name of the extension field as returned by the OData service.
     * @param <T>
     *     The type of the extension field when performing value comparisons.
     * @param <DomainT>
     *     The type of the extension field as returned by the OData service.
     * @return
     *     A representation of an extension field from this entity, holding a reference to the given TypeConverter.
     */
    @Nonnull
    public static<T,DomainT >FooTypeField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new FooTypeField<T>(fieldName, typeConverter);
    }

    @Override
    @Nullable
    public Destination getDestinationForFetch() {
        return super.getDestinationForFetch();
    }

    @Override
    protected void setServicePathForFetch(
        @Nullable
        final String servicePathForFetch) {
        super.setServicePathForFetch(servicePathForFetch);
    }

    @Override
    public void attachToService(
        @Nullable
        final String servicePath,
        @Nonnull
        final Destination destination) {
        super.attachToService(servicePath, destination);
    }

    @Override
    protected String getDefaultServicePath() {
        return (testcomparison.services.MultipleEntitySetsService.DEFAULT_SERVICE_PATH);
    }

}
