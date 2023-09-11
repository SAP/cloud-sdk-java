# Create a Maven-Module (manually)

This step-by-step manual show how to create a new maven module.

The creation of a module depends on the answer to the following two questions:

1. Where should the module be released to? (Release Target)
   1. Maven-Central (publicly)
   1. Nowhere
1. What task should the module fulfill?
   1. Contain code (take [this](./module-templates/template-code-pom.xml) template)
   1. Group modules for easier consumption (e.g. openapi-parent) (take [this](./module-templates/template-group-pom.xml) template)
   1. Be a parent for other modules (take [this](./module-templates/template-parent-pom.xml) template)

Also points that you should be aware of are:

1. Under which already existing module should the new module be located?

## Steps for all Targets

1. Create a folder where the new module should be located. E.g., a new framework adaption would be located under _frameworks/&lt;new_folder&gt;_
1. Copy the template for your specific use case, as linked above (see the [_module-templates_](./module-templates) directory) into this new directory, renaming it to _pom.xml_.
1. Adjust the _pom.xml_:
   1. Adapt the `name`, `description` and `artifactId`.
   1. Adapt the `x-sap-release-audience` and `x-sap-release-maturity`.
   1. Clean up the `dependencies` and add your own. (One approach may be: remove all dependencies and run `mvn dependency:analyze -DoutputXML`. This will list all used dependencies along with their xml string to be added as a dependency.)
1. Add the new artifact to the _pom.xml_ of the _modules-bom_ module. There you have to add an entry under `dependencyManagement` > `dependencies`. This is only required if the module is meant to be consumed as a `dependency`
   for e.g. this is not required to be done for the `maven-plugin`.
1. Follow one of the step groups described below, depending on your release target.

## Steps for a publicly released module

1. Add the newly created artifact to the direct parent _pom.xml_ under the `modules` block. E.g., if you add a new maven module under the module `openapi-parent`, then add your new module there and not in the parent pom on the root level.

## Steps for a not-to-be-released module

1. Since the new module is not published in any way, you may also skip the API compatibility check by adding the following property:

   ```
   <properties>
       <!-- skip the API compatibility check since this module is not published -->
       <japicmp.skip>true</japicmp.skip>
   </properties>
   ```

1. If it is an empty module, then add a java package and .gitkeep file to get the empty directory committed to git.

To verify that everything worked, build the project as usual.
There your module should appear in the success table.
