package com.sap.cloud.sdk.datamodel.odata.utility;

/**
 * An enum representing the logic to be used by the {@code NamingStrategy} for determining the source for the java
 * namings.
 */
public enum NameSource
{
    /**
     * Specifies that the name of an entity/property should be used to determine the java namings.
     */
    NAME,

    /**
     * Specifies that the sap:label of an entity/property should be used to determine the java namings. If the label is
     * not provided (null, empty, or blank) the name will be used.
     */
    LABEL
}
