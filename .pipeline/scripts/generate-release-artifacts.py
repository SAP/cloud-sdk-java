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
        <maven.install.skip>true</maven.install.skip>
    </properties>
    <build>
        <plugins>
"""

pom_begin_install_plugin = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-install-plugin</artifactId>
                <version>3.1.1</version>
                <executions>
"""

pom_begin_deploy_plugin = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <version>3.1.1</version>
                <executions>
"""


pom_begin_gpg_plugin = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-gpg-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
"""

pom_end_plugin = """
                </executions>
            </plugin>
"""

pom_end = """
        </plugins>
    </build>
</project>
"""


def sanitize_path(*args):
    return os.path.normpath(os.path.join(*args))


def to_maven_path(*args):
    return sanitize_path(*args).replace("\\", "/")


def get_module_source_path(module):
    return module["pomFile"].replace("pom.xml", "target")


def get_module_dest_path(module):
    return sanitize_path("artifacts", module["pomFile"].replace("pom.xml", ""))


def copy_artifact(source_path, target_path):
    shutil.copyfile(source_path, target_path)


def copy_artifacts(path_prefix, sdk_version):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        for module in module_inventory:
            if module["releaseAudience"] != "Public":
                continue

            dst_path = sanitize_path(path_prefix, get_module_dest_path(module))
            artifact_base_name = module["artifactId"] + "-" + sdk_version
            os.makedirs(dst_path, exist_ok=True)

            shutil.copyfile(module["pomFile"], sanitize_path(dst_path, "pom.xml"))

            if module["packaging"] != "pom":
                src_path = get_module_source_path(module)

                src_artifact = sanitize_path(src_path, artifact_base_name + ".jar")
                dst_artifact = sanitize_path(dst_path, artifact_base_name + ".jar")
                copy_artifact(src_artifact, dst_artifact)

                src_docs_artifact = sanitize_path(src_path, artifact_base_name + "-javadoc.jar")
                if os.path.exists(src_docs_artifact):
                    dst_docs_artifact = sanitize_path(dst_path, artifact_base_name + "-javadoc.jar")
                    copy_artifact(src_docs_artifact, dst_docs_artifact)

                src_sources_artifact = sanitize_path(src_path, artifact_base_name + "-sources.jar")
                if os.path.exists(src_sources_artifact):
                    dst_sources_artifact = sanitize_path(dst_path, artifact_base_name + "-sources.jar")
                    copy_artifact(src_sources_artifact, dst_sources_artifact)


def generate_execution(path_prefix, phase, goal, module, sdk_version):
    artifact_path = to_maven_path(get_module_dest_path(module), module["artifactId"] + "-" + sdk_version)
    file = artifact_path + "." + module["packaging"]
    pom_path = to_maven_path("artifacts", module["pomFile"])
    packaging = module["packaging"]
    sources = artifact_path + "-sources.jar"
    if not os.path.exists(sanitize_path(path_prefix, sources)):
        sources = ""
    javadoc = artifact_path + "-javadoc.jar"
    if not os.path.exists(sanitize_path(path_prefix, javadoc)):
        javadoc = ""
    if module["packaging"] == "pom":
        file = pom_path
    elif module["packaging"] == "maven-archetype" or module["packaging"] == "maven-plugin":
        file = artifact_path + ".jar"
        if phase == "install":
            packaging = "jar"
    return f"""
                  <execution>
                      <id>{phase}-{module["artifactId"]}</id>
                      <phase>{phase}</phase>
                      <goals>
                          <goal>{goal}</goal>
                      </goals>
                      <configuration>
                          <file>{file}</file>
                          <sources>{sources}</sources>
                          <javadoc>{javadoc}</javadoc>
                          <pomFile>{pom_path}</pomFile>
                          <groupId>{module["groupId"]}</groupId>
                          <artifactId>{module["artifactId"]}</artifactId>
                          <version>{sdk_version}</version>
                          <packaging>{packaging}</packaging>
                      </configuration>
                  </execution>
                """


def generate_pom(path_prefix, sdk_version, with_signing):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        with open(sanitize_path(path_prefix, "pom.xml"), "w") as f:
            f.write(pom_beginning.replace("${sdkVersion}", sdk_version))

            f.write(pom_begin_install_plugin)
            for module in module_inventory:
                if module["releaseAudience"] == "Public":
                    f.write(generate_execution(path_prefix, "install", "install-file", module, sdk_version))
            f.write(pom_end_plugin)

            if with_signing is True:
                f.write(pom_begin_gpg_plugin)
                for module in module_inventory:
                    if module["releaseAudience"] == "Public":
                        f.write(generate_execution(path_prefix, "deploy", "sign-and-deploy-file", module, sdk_version))
            else:
                f.write(pom_begin_deploy_plugin)
                for module in module_inventory:
                    if module["releaseAudience"] == "Public":
                        f.write(generate_execution(path_prefix, "deploy", "deploy-file", module, sdk_version))
            f.write(pom_end_plugin)

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
    parser.add_argument("--with-signing",
                        action="store_true",
                        help="Specify whether to sign all artifacts during deployment.")

    args = parser.parse_args()

    print(args)

    if os.path.exists(args.path_prefix):
        shutil.rmtree(args.path_prefix)

    copy_artifacts(args.path_prefix, args.version)

    generate_pom(args.path_prefix, args.version, args.with_signing)


if __name__ == '__main__':
    main()
