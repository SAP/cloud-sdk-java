import os
import json
import argparse
import shutil

pom_beginning = """
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.sap.cloud.sdk</groupId>
    <artifactId>sdkdist</artifactId>
    <version>${sdkVersion}</version>
    <properties>
        <maven.deploy.skip>true</maven.deploy.skip>
    </properties>
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
                <version>3.1.1</version>
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


def get_module_source_path(module):
    return module["pomFile"].replace("pom.xml", "target")


def get_module_dest_path(module):
    return os.path.join("artifacts", module["pomFile"].replace("pom.xml", ""))


def copy_artifacts(path_prefix, sdk_version):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        for module in module_inventory:
            if module["releaseAudience"] != "Public":
                continue

            dst_path = os.path.join(path_prefix, get_module_dest_path(module))
            os.makedirs(dst_path, exist_ok=True)

            shutil.copyfile(module["pomFile"], os.path.join(dst_path, "pom.xml"))

            if module["packaging"] != "pom":
                src_path = get_module_source_path(module)

                src_artifact = os.path.join(src_path, module["artifactId"] + "-" + sdk_version + ".jar")
                dst_artifact = os.path.join(dst_path, module["artifactId"] + "-" + sdk_version + ".jar")
                shutil.copyfile(src_artifact, dst_artifact)


def generate_pom(path_prefix, sdk_version):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        with open(os.path.join(path_prefix, "pom.xml"), "w") as f:
            f.write(pom_beginning.replace("${sdkVersion}", sdk_version))
            f.write(pom_begin_install_plugin)

            for module in module_inventory:
                if module["releaseAudience"] != "Public":
                    continue

                artifact_path = get_module_dest_path(module) \
                                + "/" + module["artifactId"] + "-" + sdk_version
                file = artifact_path + "." + module["packaging"]
                pom_path = "artifacts/" + module["pomFile"]
                if module["packaging"] == "pom":
                    file = pom_path
                elif module["packaging"] == "maven-archetype" or module["packaging"] == "maven-plugin":
                    file = artifact_path + ".jar"
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

                artifact_path = get_module_dest_path(module) \
                                + "/" + module["artifactId"] + "-" + sdk_version
                file = artifact_path + "." + module["packaging"]
                pom_path = "artifacts/" + module["pomFile"]
                if module["packaging"] == "pom":
                    file = pom_path
                elif module["packaging"] == "maven-archetype" or module["packaging"] == "maven-plugin":
                    file = artifact_path + ".jar"
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
    parser.add_argument("--path-prefix",
                        help="Path in which to generate the release folder structure.",
                        required=True)

    args = parser.parse_args()

    if os.path.exists(args.path_prefix):
        shutil.rmtree(args.path_prefix)

    copy_artifacts(args.path_prefix, args.version)

    generate_pom(args.path_prefix, args.version)


if __name__ == '__main__':
    main()
