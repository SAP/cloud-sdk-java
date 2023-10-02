/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.multipleentitysets.SimplePerson;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson}. This interface is used by {@link testcomparison.namespaces.multipleentitysets.field.SimplePersonField SimplePersonField} and {@link testcomparison.namespaces.multipleentitysets.link.SimplePersonLink SimplePersonLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.multipleentitysets.SimplePerson#PERSON PERSON}</li>
 * <li>{@link testcomparison.namespaces.multipleentitysets.SimplePerson#EMAIL_ADDRESS EMAIL_ADDRESS}</li>
 * </ul>
 * 
 */
public interface SimplePersonSelectable
    extends EntitySelectable<SimplePerson>
{


}
