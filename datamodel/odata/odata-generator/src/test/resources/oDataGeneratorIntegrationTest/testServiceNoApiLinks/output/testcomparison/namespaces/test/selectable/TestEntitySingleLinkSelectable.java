package testcomparison.namespaces.test.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.test.TestEntitySingleLink;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.test.TestEntitySingleLink TestEntitySingleLink}. This interface is used by {@link testcomparison.namespaces.test.field.TestEntitySingleLinkField TestEntitySingleLinkField} and {@link testcomparison.namespaces.test.link.TestEntitySingleLinkLink TestEntitySingleLinkLink}.
 * 
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.test.TestEntitySingleLink#KEY_PROPERTY KEY_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntitySingleLink#STRING_PROPERTY STRING_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntitySingleLink#BOOLEAN_PROPERTY BOOLEAN_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntitySingleLink#GUID_PROPERTY GUID_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntitySingleLink#INT16_PROPERTY INT16_PROPERTY}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntitySingleLink#TO_MULTI_LINK TO_MULTI_LINK}</li>
 * <li>{@link testcomparison.namespaces.test.TestEntitySingleLink#TO_SINGLE_LINK TO_SINGLE_LINK}</li>
 * </ul>
 * 
 */
public interface TestEntitySingleLinkSelectable
    extends EntitySelectable<TestEntitySingleLink>
{


}
