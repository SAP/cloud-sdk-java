package testcomparison.namespaces.nameclash.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.nameclash.TestEntityV2;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2}. This interface is used by {@link testcomparison.namespaces.nameclash.field.TestEntityV2Field TestEntityV2Field} and {@link testcomparison.namespaces.nameclash.link.TestEntityV2Link TestEntityV2Link}.
 *
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.nameclash.TestEntityV2#KEY_PROPERTY_GUID KEY_PROPERTY_GUID}</li>
 * <li>{@link testcomparison.namespaces.nameclash.TestEntityV2#MULTI_LINK MULTI_LINK}</li>
 * <li>{@link testcomparison.namespaces.nameclash.TestEntityV2#TO_MULTI_LINK TO_MULTI_LINK}</li>
 * <li>{@link testcomparison.namespaces.nameclash.TestEntityV2#TO_MULTI_LINK_2 TO_MULTI_LINK_2}</li>
 * </ul>
 *
 */
public interface TestEntityV2Selectable
    extends EntitySelectable<TestEntityV2>
{


}
