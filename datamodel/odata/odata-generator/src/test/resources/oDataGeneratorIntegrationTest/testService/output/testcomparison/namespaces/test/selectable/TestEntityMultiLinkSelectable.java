/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.test.TestEntityMultiLink;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink}. This interface is used by {@link testcomparison.namespaces.test.field.TestEntityMultiLinkField TestEntityMultiLinkField} and {@link testcomparison.namespaces.test.link.TestEntityMultiLinkLink TestEntityMultiLinkLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.test.TestEntityMultiLink#KEY_PROPERTY KEY_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityMultiLink#STRING_PROPERTY STRING_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityMultiLink#BOOLEAN_PROPERTY BOOLEAN_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityMultiLink#GUID_PROPERTY GUID_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityMultiLink#INT16_PROPERTY INT16_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityMultiLink#TO_MULTI_LINK TO_MULTI_LINK}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntityMultiLink#TO_SINGLE_LINK TO_SINGLE_LINK}</li>
 * </ul>
 * 
 */
public interface TestEntityMultiLinkSelectable
    extends EntitySelectable<TestEntityMultiLink>
{


}
