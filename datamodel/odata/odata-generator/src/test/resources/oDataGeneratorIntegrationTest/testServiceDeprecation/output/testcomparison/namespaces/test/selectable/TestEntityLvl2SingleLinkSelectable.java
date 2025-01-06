/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.test.TestEntityLvl2SingleLink;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink}. This interface is used by {@link testcomparison.namespaces.test.field.TestEntityLvl2SingleLinkField TestEntityLvl2SingleLinkField} and {@link testcomparison.namespaces.test.link.TestEntityLvl2SingleLinkLink TestEntityLvl2SingleLinkLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.test.TestEntityLvl2SingleLink#KEY_PROPERTY KEY_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityLvl2SingleLink#STRING_PROPERTY STRING_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityLvl2SingleLink#BOOLEAN_PROPERTY BOOLEAN_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityLvl2SingleLink#GUID_PROPERTY GUID_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityLvl2SingleLink#INT16_PROPERTY INT16_PROPERTY}</li>
 * </ul>
 * 
 */
public interface TestEntityLvl2SingleLinkSelectable
    extends EntitySelectable<TestEntityLvl2SingleLink>
{


}
