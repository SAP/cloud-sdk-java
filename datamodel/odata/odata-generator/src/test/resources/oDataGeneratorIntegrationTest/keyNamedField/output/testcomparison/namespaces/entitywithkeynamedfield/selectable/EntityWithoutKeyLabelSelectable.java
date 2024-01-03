/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel}. This interface is used by {@link testcomparison.namespaces.entitywithkeynamedfield.field.EntityWithoutKeyLabelField EntityWithoutKeyLabelField} and {@link testcomparison.namespaces.entitywithkeynamedfield.link.EntityWithoutKeyLabelLink EntityWithoutKeyLabelLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel#SOME_FIELD SOME_FIELD}</li>
 * </ul>
 * 
 */
public interface EntityWithoutKeyLabelSelectable
    extends EntitySelectable<EntityWithoutKeyLabel>
{


}
