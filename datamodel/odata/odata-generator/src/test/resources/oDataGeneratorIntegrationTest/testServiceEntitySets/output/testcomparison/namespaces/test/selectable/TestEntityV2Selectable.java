/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.test.TestEntityV2;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2}. This interface is used by {@link testcomparison.namespaces.test.field.TestEntityV2Field TestEntityV2Field} and {@link testcomparison.namespaces.test.link.TestEntityV2Link TestEntityV2Link}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#KEY_PROPERTY_GUID KEY_PROPERTY_GUID}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#KEY_PROPERTY_STRING KEY_PROPERTY_STRING}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#STRING_PROPERTY STRING_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#BOOLEAN_PROPERTY BOOLEAN_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#GUID_PROPERTY GUID_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#INT16_PROPERTY INT16_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#INT32_PROPERTY INT32_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#INT64_PROPERTY INT64_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#DECIMAL_PROPERTY DECIMAL_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#SINGLE_PROPERTY SINGLE_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#DOUBLE_PROPERTY DOUBLE_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#TIME_PROPERTY TIME_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#DATE_TIME_PROPERTY DATE_TIME_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#DATE_TIME_OFF_SET_PROPERTY DATE_TIME_OFF_SET_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#BYTE_PROPERTY BYTE_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#S_BYTE_PROPERTY S_BYTE_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityV2#BINARY_PROPERTY BINARY_PROPERTY}</li>
 * </ul>
 * 
 */
public interface TestEntityV2Selectable
    extends EntitySelectable<TestEntityV2>
{


}
