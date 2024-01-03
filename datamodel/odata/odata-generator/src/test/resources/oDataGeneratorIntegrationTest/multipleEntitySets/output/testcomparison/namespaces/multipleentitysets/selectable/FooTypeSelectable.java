/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.multipleentitysets.FooType;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.multipleentitysets.FooType FooType}. This interface is used by {@link testcomparison.namespaces.multipleentitysets.field.FooTypeField FooTypeField} and {@link testcomparison.namespaces.multipleentitysets.link.FooTypeLink FooTypeLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.multipleentitysets.FooType#FOO FOO}</li>
 * <li>{@link testcomparison.namespaces.multipleentitysets.FooType#TYPE_2 TYPE_2}</li>
 * </ul>
 * 
 */
public interface FooTypeSelectable
    extends EntitySelectable<FooType>
{


}
