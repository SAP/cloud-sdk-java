package testcomparison.namespaces.sdkgrocerystore;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmComplex;
import com.sap.cloud.sdk.result.ElementName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>Original complex type name from the Odata EDM: <b>ProductCount</b></p>
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
public class ProductCount
    extends VdmComplex<ProductCount>
{

    @Getter
    private final String odataType = "com.sap.cloud.sdk.store.grocery.ProductCount";
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ProductId</b></p>
     *
     * @return
     *     The productId contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("ProductId")
    private Integer productId;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<ProductCount> PRODUCT_ID = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<ProductCount>(ProductCount.class, "ProductId");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Quantity</b></p>
     *
     * @return
     *     The quantity contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Quantity")
    private Integer quantity;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<ProductCount> QUANTITY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<ProductCount>(ProductCount.class, "Quantity");

    @Nonnull
    @Override
    public Class<ProductCount> getType() {
        return ProductCount.class;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("ProductId", getProductId());
        values.put("Quantity", getQuantity());
        return values;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("ProductId")) {
                final Object value = values.remove("ProductId");
                if ((value == null)||(!value.equals(getProductId()))) {
                    setProductId(((Integer) value));
                }
            }
            if (values.containsKey("Quantity")) {
                final Object value = values.remove("Quantity");
                if ((value == null)||(!value.equals(getQuantity()))) {
                    setQuantity(((Integer) value));
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

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        return entityKey;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ProductId</b></p>
     *
     * @param productId
     *     The productId to set.
     */
    public void setProductId(
        @Nullable
        final Integer productId) {
        rememberChangedField("ProductId", this.productId);
        this.productId = productId;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>Quantity</b></p>
     *
     * @param quantity
     *     The quantity to set.
     */
    public void setQuantity(
        @Nullable
        final Integer quantity) {
        rememberChangedField("Quantity", this.quantity);
        this.quantity = quantity;
    }

}
