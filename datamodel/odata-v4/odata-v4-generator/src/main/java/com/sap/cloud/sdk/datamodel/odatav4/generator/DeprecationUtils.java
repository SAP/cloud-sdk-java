package com.sap.cloud.sdk.datamodel.odatav4.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

/**
 * Helper class containing methods related to deprecated services that are used at multiple places.
 */
final class DeprecationUtils
{
    static final String INDENT = "    ";

    private DeprecationUtils()
    {
        // no instances of this utils class need to be constructed
    }

    static void addGetDefaultServicePathBody(
        final JMethod createdMethod,
        final JDefinedClass serviceInterface,
        final Service service )
    {
        if( service.isDeprecated() ) {
            // in case of a deprecated API we need to reference the service classes (annotated as deprecate) via the
            // full path to prevent an import of a deprecated class which would result in a warning
            createdMethod
                .body()
                .directStatement(
                    "@SuppressWarnings( \"deprecation\" )\n"
                        + INDENT
                        + INDENT
                        + "final String defaultServicePath = "
                        + serviceInterface.fullName()
                        + "."
                        + ServiceClassGenerator.DEFAULT_SERVICE_PATH_FIELD_NAMING
                        + ";\n"
                        + INDENT
                        + INDENT
                        + "return defaultServicePath;");
        } else {
            createdMethod
                .body()
                ._return(serviceInterface.staticRef(ServiceClassGenerator.DEFAULT_SERVICE_PATH_FIELD_NAMING));
        }
    }

    static void createBasicServiceInterfaceField(
        final JDefinedClass classToAddTheFieldTo,
        final JDefinedClass serviceInterface,
        final String fieldName,
        final Service service )
    {
        if( service.isDeprecated() ) {
            classToAddTheFieldTo
                .direct(
                    "\n"
                        + INDENT
                        + "@SuppressWarnings( \"deprecation\" )\n"
                        + INDENT
                        + "@Nonnull\n"
                        + INDENT
                        + "private final "
                        + serviceInterface.fullName()
                        + " "
                        + fieldName
                        + ";\n");
        } else {
            final JFieldVar serviceField =
                classToAddTheFieldTo.field(JMod.PRIVATE | JMod.FINAL, serviceInterface, fieldName);
            serviceField.annotate(Nonnull.class);
        }
    }

    /**
     * Adds javadoc and annotations regarding states such as deprecation if necessary.
     *
     * @param affectedClass
     *            The class to add information to.
     * @param service
     *            The service to take the status information from.
     * @param customDeprecationNotice
     *            The custom deprecation notice that is added as a comment
     */
    static void addStatusInformation(
        @Nonnull final JDefinedClass affectedClass,
        @Nonnull final Service service,
        @Nullable final String customDeprecationNotice )
    {
        if( service.isDeprecated() ) {
            addDeprecationInformation(
                affectedClass,
                service.getDeprecationInfo().getOrElse(EdmService.DefaultDeprecationInfo.EMPTY),
                customDeprecationNotice);
        }
    }

    private static void addDeprecationInformation(
        @Nonnull final JDefinedClass affectedClass,
        @Nonnull final Service.DeprecationInfo deprecationInfo,
        @Nullable final String customDeprecationNotice )
    {
        final String release = deprecationInfo.getDeprecationRelease().map(r -> " as of release " + r).getOrElse("");
        final String date = deprecationInfo.getDeprecationDate().map(d -> " (" + d + ")").getOrElse("");
        final String defaultDeprecationNotice = "Please use the odata generator to generate the VDM. ";
        final String deprecationNotice =
            " "
                + (customDeprecationNotice != null && !customDeprecationNotice.isEmpty()
                    ? customDeprecationNotice
                    : defaultDeprecationNotice);

        final String successor =
            deprecationInfo
                .getSuccessorApi()
                .map(s -> " Please use the <a href=\"" + s + "\"> successor API</a> instead.")
                .getOrElse(deprecationNotice);

        affectedClass
            .javadoc()
            .addDeprecated()
            .add("The service and all its related classes are deprecated" + release + date + "." + successor);
        affectedClass.annotate(Deprecated.class);
    }
}
