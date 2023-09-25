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
    <distributionManagement>
        <repository>
            <id>ossrh</id>
            <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>
        <snapshotRepository>
            <id>ossrh</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        </snapshotRepository>
    </distributionManagement>
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


def get_module_repo_path(module, sdk_version):
    return sanitize_path(module["groupId"].replace(".", "/"), module["artifactId"], sdk_version)


def copy_artifact_and_signature(source_path, target_path):
    shutil.copyfile(source_path, target_path)
    if os.path.exists(source_path + ".asc"):
        shutil.copyfile(source_path + ".asc", target_path + ".asc")


def copy_artifacts(path_prefix, sdk_version):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        for module in module_inventory:
            if module["releaseAudience"] != "Public":
                continue

            src_path = get_module_source_path(module)
            dst_path = sanitize_path(path_prefix, get_module_dest_path(module))
            artifact_base_name = module["artifactId"] + "-" + sdk_version
            os.makedirs(dst_path, exist_ok=True)

            shutil.copyfile(module["pomFile"], sanitize_path(dst_path, "pom.xml"))

            src_pom_signature = sanitize_path(src_path, artifact_base_name + ".pom.asc")
            if os.path.exists(src_pom_signature):
                dst_pom_signature = sanitize_path(dst_path, artifact_base_name + ".pom.asc")
                shutil.copyfile(src_pom_signature, dst_pom_signature)

            if module["packaging"] != "pom":
                src_artifact = sanitize_path(src_path, artifact_base_name + ".jar")
                dst_artifact = sanitize_path(dst_path, artifact_base_name + ".jar")
                copy_artifact_and_signature(src_artifact, dst_artifact)

                src_docs_artifact = sanitize_path(src_path, artifact_base_name + "-javadoc.jar")
                if os.path.exists(src_docs_artifact):
                    dst_docs_artifact = sanitize_path(dst_path, artifact_base_name + "-javadoc.jar")
                    copy_artifact_and_signature(src_docs_artifact, dst_docs_artifact)

                src_sources_artifact = sanitize_path(src_path, artifact_base_name + "-sources.jar")
                if os.path.exists(src_sources_artifact):
                    dst_sources_artifact = sanitize_path(dst_path, artifact_base_name + "-sources.jar")
                    copy_artifact_and_signature(src_sources_artifact, dst_sources_artifact)


def copy_signatures(path_prefix, sdk_version, local_repo):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        for module in module_inventory:
            if module["releaseAudience"] != "Public":
                continue

            src_path = sanitize_path(path_prefix, get_module_dest_path(module))
            dst_path = sanitize_path(local_repo, get_module_repo_path(module, sdk_version))
            artifact_base_name = module["artifactId"] + "-" + sdk_version

            shutil.copyfile(
                sanitize_path(src_path, artifact_base_name + ".pom.asc"),
                sanitize_path(dst_path, artifact_base_name + ".pom.asc"))

            if module["packaging"] != "pom":
                src_artifact = sanitize_path(src_path, artifact_base_name + ".jar")
                dst_artifact = sanitize_path(dst_path, artifact_base_name + ".jar")
                shutil.copyfile(src_artifact + ".asc", dst_artifact + ".asc")

                src_docs_artifact = sanitize_path(src_path, artifact_base_name + "-javadoc.jar")
                if os.path.exists(src_docs_artifact):
                    dst_docs_artifact = sanitize_path(dst_path, artifact_base_name + "-javadoc.jar")
                    shutil.copyfile(src_docs_artifact + ".asc", dst_docs_artifact + ".asc")

                src_sources_artifact = sanitize_path(src_path, artifact_base_name + "-sources.jar")
                if os.path.exists(src_sources_artifact):
                    dst_sources_artifact = sanitize_path(dst_path, artifact_base_name + "-sources.jar")
                    shutil.copyfile(src_sources_artifact + ".asc", dst_sources_artifact + ".asc")


def generate_execution(path_prefix, phase, module, sdk_version):
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
                          <goal>{phase}-file</goal>
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


def generate_pom(path_prefix, sdk_version):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        with open(sanitize_path(path_prefix, "pom.xml"), "w") as f:
            f.write(pom_beginning.replace("${sdkVersion}", sdk_version))

            f.write(pom_begin_install_plugin)
            for module in module_inventory:
                if module["releaseAudience"] == "Public":
                    f.write(generate_execution(path_prefix, "install", module, sdk_version))
            f.write(pom_end_plugin)

            f.write(pom_begin_deploy_plugin)
            for module in module_inventory:
                if module["releaseAudience"] == "Public":
                    f.write(generate_execution(path_prefix, "deploy", module, sdk_version))
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
    parser.add_argument("--copy-signatures",
                        help="Copy the signature files (.asc) for all artifacts.",
                        required=False)
    parser.add_argument("--local-repository",
                        help="Copy the signature files (.asc) for all artifacts.",
                        required=False)

    args = parser.parse_args()

    if args.copy_signatures:
        copy_signatures(args.path_prefix, args.version, args.local_repository)
    else:
        if os.path.exists(args.path_prefix):
            shutil.rmtree(args.path_prefix)

        copy_artifacts(args.path_prefix, args.version)

        generate_pom(args.path_prefix, args.version)


if __name__ == '__main__':
    main()
