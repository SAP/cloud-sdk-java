package testcomparison.namespaces.sdkgrocerystore.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.sdkgrocerystore.OpeningHours;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours}. This interface is used by {@link testcomparison.namespaces.sdkgrocerystore.field.OpeningHoursField OpeningHoursField} and {@link testcomparison.namespaces.sdkgrocerystore.link.OpeningHoursLink OpeningHoursLink}.
 *
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.OpeningHours#ID ID}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.OpeningHours#DAY_OF_WEEK DAY_OF_WEEK}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.OpeningHours#OPEN_TIME OPEN_TIME}</li>
 * <li>{@link testcomparison.namespaces.sdkgrocerystore.OpeningHours#CLOSE_TIME CLOSE_TIME}</li>
 * </ul>
 *
 */
public interface OpeningHoursSelectable
    extends EntitySelectable<OpeningHours>
{


}
