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
 * <p>Original complex type name from the Odata EDM: <b>PurchaseHistoryItem</b></p>
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
public class PurchaseHistoryItem
    extends VdmComplex<PurchaseHistoryItem>
{

    @Getter
    private final String odataType = "com.sap.cloud.sdk.store.grocery.PurchaseHistoryItem";
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ReceiptId</b></p>
     *
     * @return
     *     The receiptId contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("ReceiptId")
    private Integer receiptId;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<PurchaseHistoryItem> RECEIPT_ID = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<PurchaseHistoryItem>(PurchaseHistoryItem.class, "ReceiptId");
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ProductCount</b></p>
     *
     * @return
     *     The productCount contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("ProductCount")
    private ProductCount productCount;
    /**
     * Use with available request builders to apply the <b>ProductCount</b> complex property to query operations.
     *
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<PurchaseHistoryItem, ProductCount> PRODUCT_COUNT = new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<PurchaseHistoryItem, ProductCount>(PurchaseHistoryItem.class, "ProductCount", ProductCount.class);

    @Nonnull
    @Override
    public Class<PurchaseHistoryItem> getType() {
        return PurchaseHistoryItem.class;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("ReceiptId", getReceiptId());
        values.put("ProductCount", getProductCount());
        return values;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("ReceiptId")) {
                final Object value = values.remove("ReceiptId");
                if ((value == null)||(!value.equals(getReceiptId()))) {
                    setReceiptId(((Integer) value));
                }
            }
        }
        // structured properties
        {
            if (values.containsKey("ProductCount")) {
                final Object value = values.remove("ProductCount");
                if (value instanceof Map) {
                    if (getProductCount() == null) {
                        setProductCount(new ProductCount());
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> inputMap = ((Map<String, Object> ) value);
                    getProductCount().fromMap(inputMap);
                }
                if ((value == null)&&(getProductCount()!= null)) {
                    setProductCount(null);
                }
            }
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
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ReceiptId</b></p>
     *
     * @param receiptId
     *     The receiptId to set.
     */
    public void setReceiptId(
        @Nullable
        final Integer receiptId) {
        rememberChangedField("ReceiptId", this.receiptId);
        this.receiptId = receiptId;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>ProductCount</b></p>
     *
     * @param productCount
     *     The productCount to set.
     */
    public void setProductCount(
        @Nullable
        final ProductCount productCount) {
        rememberChangedField("ProductCount", this.productCount);
        this.productCount = productCount;
    }

}
