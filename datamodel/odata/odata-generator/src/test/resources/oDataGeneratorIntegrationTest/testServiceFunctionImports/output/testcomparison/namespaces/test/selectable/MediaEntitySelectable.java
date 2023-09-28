package testcomparison.namespaces.test.selectable;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import testcomparison.namespaces.test.MediaEntity;


/**
 * Interface to enable OData entity selectors for {@link testcomparison.namespaces.test.MediaEntity MediaEntity}. This interface is used by {@link testcomparison.namespaces.test.field.MediaEntityField MediaEntityField} and {@link testcomparison.namespaces.test.link.MediaEntityLink MediaEntityLink}.
 *
 * <p>Available instances:
 * <ul>
 * <li>{@link testcomparison.namespaces.test.MediaEntity#KEY_PROPERTY KEY_PROPERTY}</li>
 * </ul>
 *
 */
public interface MediaEntitySelectable
    extends EntitySelectable<MediaEntity>
{


}
