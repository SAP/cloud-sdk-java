/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmEnumDeserializer;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmEnumSerializer;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;


/**
 * <p>Original enum type name from the Odata EDM: <b>A_TestEnumType</b></p>
 * 
 */
@JsonAdapter(GsonVdmAdapterFactory.class)
@JsonSerialize(using = JacksonVdmEnumSerializer.class)
@JsonDeserialize(using = JacksonVdmEnumDeserializer.class)
public enum A_TestEnumType
    implements VdmEnum
{


    /**
     * Member1
     * 
     */
    MEMBER1("Member1", 1L),

    /**
     * Member2
     * 
     */
    MEMBER2("Member2", 2L);
    private final String name;
    private final Long value;

    private A_TestEnumType(final String enumName, final Long enumValue) {
        name = enumName;
        value = enumValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getValue() {
        return value;
    }

}
