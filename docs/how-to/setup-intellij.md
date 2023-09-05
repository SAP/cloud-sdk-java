# Setup of IntelliJ IDEA

1. Download the latest version of IntelliJ.
1. Navigate to "Preferences" (`[Cmd] + [,]` on Mac OS, `[Ctrl] + [Alt] + [S]` on Win).
1. Navigate to "Preferences" -> "Editor" -> "General" -> "Auto Import" and disable "Optimize imports on the fly" and "Add unambiguous imports on the fly".
1. Navigate to "Preferences" -> "Editor" -> "Code Style" -> "Java".
    - Import and activate our Java code style from `codestyle/intellij_java_style.xml`.
    - In "Imports" tab set `class count` and `names count to use import with '*'` to `100`.
1. Navigate to "Preferences" -> "Editor" -> "Inspections".
  Import and activate our Inspection rules from `codestyle/intellij_inspections.xml`.
1. Navigate to "Preferences" -> "Plugins".
  If you cannot find a plugin in the list of available plugins, search for non-bundled plugins by clicking the corresponding link in the search results list.
    - Install and activate the "Adapter for Eclipse Code Formatter" plugin in the settings dialog.
    Search for the installed plugin and configure it to use the `codestyle/java_formatter.xml` configuration file.
    - Install the "CheckStyle-IDEA" plugin and in the plugin configuration, import and activate the file `codestyle/checkstyle.xml`.
      Make sure that the Checkstyle version specified in the plugin is compatible with the Maven plugin version.
      Currently, version 8.41 works. Compare the checkstyle version referenced in our [pom.xml](../../pom.xml).
1. In the main window of IntelliJ, navigate to the combobox in the upper right corner and select "Edit configurations".
  Under "Defaults" -> "JUnit", add the following VM options:
  ```-Xmx1024m -Dorg.slf4j.simpleLogger.defaultLogLevel=warn```.