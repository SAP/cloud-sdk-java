package testcomparison.namespaces.minimalmetadata.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.minimalmetadata.SimplePerson;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson}. This interface is used by {@link testcomparison.namespaces.minimalmetadata.field.SimplePersonField SimplePersonField} and {@link testcomparison.namespaces.minimalmetadata.link.SimplePersonLink SimplePersonLink}.
 *
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.minimalmetadata.SimplePerson#PERSON PERSON}</li>
 * <li>{@link testcomparison.namespaces.minimalmetadata.SimplePerson#EMAIL_ADDRESS EMAIL_ADDRESS}</li>
 * </ul>
 *
 */
public interface SimplePersonSelectable
    extends EntitySelectable<SimplePerson>
{


}
