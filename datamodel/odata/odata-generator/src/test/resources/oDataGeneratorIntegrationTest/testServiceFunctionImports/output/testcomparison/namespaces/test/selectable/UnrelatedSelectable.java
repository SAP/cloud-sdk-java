/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.test.Unrelated;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.test.Unrelated Unrelated}. This interface is used by {@link testcomparison.namespaces.test.field.UnrelatedField UnrelatedField} and {@link testcomparison.namespaces.test.link.UnrelatedLink UnrelatedLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.test.Unrelated#KEY_PROPERTY KEY_PROPERTY}</li>
 * </ul>
 * 
 */
public interface UnrelatedSelectable
    extends EntitySelectable<Unrelated>
{


}
