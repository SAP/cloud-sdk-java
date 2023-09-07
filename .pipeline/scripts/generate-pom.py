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


def get_module_target_path(module, sdk_version):
    # return module["groupId"].replace(".", "/") + "/" + module["artifactId"] + "/" + sdk_version \
    #    + "/" + module["artifactId"] + "-" + sdk_version
    return module["pomFile"].replace("/pom.xml", "/artifacts/target")


def generate_pom(sdk_version, target_pom_path):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        with open(target_pom_path, "w") as f:
            f.write(pom_beginning.replace("${sdkVersion}", sdk_version))
            f.write(pom_begin_install_plugin)

            for module in module_inventory:
                artifact_path = get_module_target_path(module, sdk_version) \
                                + "/" + module["artifactId"] + "-" + sdk_version
                file = artifact_path + "." + module["packaging"]
                pom_path = "artifacts/" + module["pomFile"]
                if module["packaging"] == "pom":
                    file = pom_path
                f.write(f"""
                  <execution>
                      <id>install-{module["artifactId"]}</id>
                      <phase>install</phase>
                      <goals>
                          <goal>install-file</goal>
                      </goals>
                      <configuration>
                          <file>{file}</file>
                          <pomFile>{pom_path}</pomFile>
                          <groupId>{module["groupId"]}</groupId>
                          <artifactId>{module["artifactId"]}</artifactId>
                          <version>{sdk_version}</version>
                          <packaging>{module["packaging"]}</packaging>
                      </configuration>
                  </execution>
                """)
            f.write(pom_end_plugin)
            f.write(pom_begin_deploy_plugin)
            for module in module_inventory:
                if module["releaseAudience"] != "Public":
                    continue

                artifact_path = get_module_target_path(module, sdk_version) \
                                + "/" + module["artifactId"] + "-" + sdk_version
                file = artifact_path + "." + module["packaging"]
                pom_path = "artifacts/" + module["pomFile"]
                if module["packaging"] == "pom":
                    file = pom_path
                f.write(f"""
                  <execution>
                      <id>deploy-{module["artifactId"]}</id>
                      <phase>deploy</phase>
                      <goals>
                          <goal>deploy-file</goal>
                      </goals>
                      <configuration>
                          <file>{file}</file>
                          <pomFile>{pom_path}</pomFile>
                          <groupId>{module["groupId"]}</groupId>
                          <artifactId>{module["artifactId"]}</artifactId>
                          <version>{sdk_version}</version>
                          <packaging>{module["packaging"]}</packaging>
                      </configuration>
                  </execution>
                """)
            f.write(pom_end)


def main():
    parser: argparse.ArgumentParser = argparse.ArgumentParser(
        description="Iterates the module inventory and generates a pom.xml file for installing or deploying all "
                    "module artifacts.")
    parser.add_argument("--version",
                        help="Version of the artifacts.",
                        required=True)
    parser.add_argument("--pomFile",
                        help="Path to the pom.xml file that is to be generated.",
                        required=True)

    args = parser.parse_args()

    generate_pom(args.version, args.pomFile)


if __name__ == '__main__':
    main()
