import os
import json
import argparse

pom_beginning = """
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.sap.cloud.sdk</groupId>
    <artifactId>sdkdist</artifactId>
    <version>${sdkVersion}</version>
    <build>
        <plugins>
"""

pom_begin_install_plugin = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-install-plugin</artifactId>
                <executions>
"""

pom_begin_deploy_plugin = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <executions>
"""

pom_end_plugin = """
                </executions>
            </plugin>
"""

pom_end = """
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
"""

def generate_pom():
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        with open("pom.xml", "w") as f:
            f.write(pom_beginning)
            f.write(pom_begin_install_plugin)

            for module in module_inventory:
                f.write(f"""
                  <execution>
                      <id>install-{module["artifactId"]}</id>
                      <phase>install</phase>
                      <goals>
                          <goal>install-file</goal>
                      </goals>
                      <configuration>
                          <file>${{project.basedir}}/dist/{module["artifactId"]}-{module["version"]}.jar</file>
                          <groupId>{module["groupId"]}</groupId>
                          <artifactId>{module["artifactId"]}</artifactId>
                          <version>{module["version"]}</version>
                          <packaging>{module["packagingType"]}</packaging>
                      </configuration>
                  </execution>
                """)
            f.write(pom_end_plugin)
            f.write(pom_begin_deploy_plugin)
            for module in module_inventory:
                artifact_path = module["groupId"].replace(".", "/") + "/" + module["artifactId"] + "/" + module["version"] + "/" + module["artifactId"] + "-" + module["version"]
                file = artifact_path + "." + module["packagingType"]
                pom_path = artifact_path + ".pom"
                f.write(f"""
                  <execution>
                      <id>deploy-{module["artifactId"]}</id>
                      <phase>deploy</phase>
                      <goals>
                          <goal>deploy-file</goal>
                      </goals>
                      <configuration>
                          <file>${file}</file>
                          <pomFile>${pom_path}</pomFile>
                          <groupId>{module["groupId"]}</groupId>
                          <artifactId>{module["artifactId"]}</artifactId>
                          <version>{module["version"]}</version>
                          <packaging>{module["packagingType"]}</packaging>
                      </configuration>
                  </execution>
                """)
            f.write(pom_end)


def main():
    parser: argparse.ArgumentParser = argparse.ArgumentParser(
        description="Filters the module inventory and returns Maven excludes.")
    parser.add_argument("--version",
                        help="Version of the artifacts.",
                        required=True)

    args = parser.parse_args(args.version)

    generate_pom()

if __name__ == '__main__':
    main()
