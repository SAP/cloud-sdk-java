import xml.etree.ElementTree as XmlTree
from pathlib import Path
from typing import Optional, List

from common._maven_module.maven_module_reader_configuration import MavenModuleReaderConfiguration, IgnoredMavenModule, \
    IgnoredPom
from common.maven_module import MavenModule
from common.release_audience import ReleaseAudience
from common.release_maturity import ReleaseMaturity


class ModuleAttributesMissingException(Exception):
    def __init__(self, pom_file: Path, missing_attributes: List[str]):
        self._pom_file: Path = pom_file
        self._missing_attributes: List[str] = missing_attributes

    def __repr__(self) -> str:
        return str(self)

    def __str__(self) -> str:
        joined: str = ", ".join(self._missing_attributes)
        return f"Following attributes are missing in \"{self._pom_file.resolve()}\": {joined}"


class ExceptionCollection(Exception):
    def __init__(self):
        self.exceptions: List[Exception] = []

    def append(self, exception: Exception) -> "ExceptionCollection":
        self.exceptions.append(exception)
        return self

    def __repr__(self) -> str:
        return str(self)

    def __str__(self) -> str:
        return "\n".join(str(exception) for exception in self.exceptions)


class MavenModuleReader:
    @staticmethod
    def read(pom_file: Path,
             reader_config: Optional[MavenModuleReaderConfiguration] = None,
             verbose: bool = False) -> Optional[MavenModule]:
        return MavenModuleReader(pom_file, reader_config, verbose).get_module()

    @staticmethod
    def read_recursively(root: Path,
                         reader_config: Optional[MavenModuleReaderConfiguration] = None,
                         verbose: bool = False) -> List[MavenModule]:
        return MavenModuleReader._read_recursively(root, root, reader_config, verbose)

    @staticmethod
    def _read_recursively(root: Path,
                          current_path: Path,
                          reader_config: Optional[MavenModuleReaderConfiguration] = None,
                          verbose: bool = False) -> List[MavenModule]:
        if current_path.is_file():
            if current_path.name == "pom.xml":
                module: Optional[MavenModule] = MavenModuleReader.read(current_path, reader_config, verbose)
                if module is not None:
                    module._pom_file = module.pom_file.relative_to(root)
                    return [module]

            return []

        modules: List[MavenModule] = []
        exception_collection: ExceptionCollection = ExceptionCollection()
        for path in current_path.iterdir():
            try:
                modules.extend(MavenModuleReader._read_recursively(root, path, reader_config, verbose))

            except ExceptionCollection as e:
                exception_collection.exceptions.extend(e.exceptions)

            except ModuleAttributesMissingException as e:
                exception_collection.exceptions.append(e)

        if len(exception_collection.exceptions) > 0:
            raise exception_collection

        return modules

    def __init__(self,
                 pom_file: Path,
                 reader_config: Optional[MavenModuleReaderConfiguration] = None,
                 verbose: bool = False):
        self._group_id: Optional[str] = None
        self._artifact_id: Optional[str] = None
        self._packaging: Optional[str] = None
        self._release_audience: Optional[ReleaseAudience] = None
        self._release_maturity: Optional[ReleaseMaturity] = None
        self._pom_file: Path = pom_file
        self._exclude_from_blackduck_scan: Optional[bool] = None
        self._parent_group_id: Optional[str] = None
        self._parent_artifact_id: Optional[str] = None

        self._reader_config: MavenModuleReaderConfiguration = reader_config if reader_config is not None else MavenModuleReaderConfiguration()
        self._root_node: XmlTree = XmlTree.parse(pom_file.resolve()).getroot()
        self._verbose: bool = verbose

    def get_module(self) -> Optional[MavenModule]:
        maybe_ignored_pom: Optional[IgnoredPom] = self._reader_config.try_find_ignored_pom(self._pom_file)
        if maybe_ignored_pom is not None:
            self._log(f"Skipping ignored pom \"{self._pom_file}\": {maybe_ignored_pom.reason}")
            return None

        try:
            self._read_module_properties()

            if self._artifact_id is None:
                self._log(f"Skipping module defined in \"{self._pom_file.resolve()}\": No artifactId defined")
                return None

            self._read_parent_properties()
            self._inherit_group_id_if_necessary()
            self._read_additional_properties()

        except:
            pass

        maybe_ignored_module: Optional[IgnoredMavenModule] = self._reader_config.try_find_ignored_module(self._group_id,
                                                                                                         self._artifact_id)
        if maybe_ignored_module is not None:
            self._log(
                f"Skipping ignored module \"{self._group_id}:{self._artifact_id}\": {maybe_ignored_module.reason}")
            return None

        self._verify()

        self._log(
            f"Extracted module \"{self._group_id}:{self._artifact_id}\" from \"{self._pom_file.resolve()}\"")
        return MavenModule(self._group_id,
                           self._artifact_id,
                           self._packaging,
                           self._release_audience,
                           self._release_maturity,
                           self._pom_file,
                           self._exclude_from_blackduck_scan,
                           self._parent_group_id,
                           self._parent_artifact_id)

    def _read_module_properties(self) -> None:
        artifact_id_element: XmlTree = self._find(self._root_node, "artifactId")

        if artifact_id_element is not None:
            self._artifact_id = artifact_id_element.text

        group_id_element: XmlTree = self._find(self._root_node, "groupId")

        if group_id_element is not None:
            self._group_id = group_id_element.text

        packaging_type_element: XmlTree = self._find(self._root_node, "packaging")
        if packaging_type_element is not None:
            self._packaging = packaging_type_element.text
        else:
            self._packaging = "jar"

    def _read_parent_properties(self) -> None:
        parent_root: XmlTree = self._find(self._root_node, "parent")
        if parent_root is None:
            return

        self._parent_group_id = self._find_or_raise(parent_root, "groupId").text
        self._parent_artifact_id = self._find_or_raise(parent_root, "artifactId").text

    def _inherit_group_id_if_necessary(self) -> None:
        if self._group_id is None and self._parent_group_id is not None:
            self._group_id = self._parent_group_id

    def _read_additional_properties(self) -> None:
        properties_root: XmlTree = self._find_or_raise(self._root_node, "properties")

        self._release_audience = ReleaseAudience(self._find_or_raise(properties_root, "x-sap-release-audience").text)
        self._release_maturity = ReleaseMaturity(self._find_or_raise(properties_root, "x-sap-release-maturity").text)

        exclude_from_blackduck_scan_element: XmlTree = self._find(properties_root,
                                                                    "x-sap-exclude-from-blackduck-scan")
        if exclude_from_blackduck_scan_element is not None:
            self._exclude_from_blackduck_scan = bool(exclude_from_blackduck_scan_element.text)

    def _verify(self) -> None:
        missing_properties: List[str] = []

        if self._artifact_id is None:
            missing_properties.append("artifactId")

        if self._group_id is None:
            missing_properties.append("groupId")

        if self._packaging is None:
            missing_properties.append("packaging")

        if self._release_audience is None:
            missing_properties.append("releaseAudience")

        if self._release_maturity is None:
            missing_properties.append("releaseMaturity")

        if self._exclude_from_blackduck_scan is None:
            self._exclude_from_blackduck_scan = False

        if self._parent_group_id is not None and self._parent_artifact_id is None:
            missing_properties.append("parentArtifactId")
        elif self._parent_group_id is None and self._parent_artifact_id is not None:
            missing_properties.append("parentGroupId")

        if len(missing_properties) > 0:
            ex: ModuleAttributesMissingException = ModuleAttributesMissingException(self._pom_file, missing_properties)
            self._log(str(ex))
            raise ex

    def _log(self, message: str) -> None:
        if self._verbose:
            print(message)

    def _find_or_raise(self, parent: XmlTree, tag: str) -> XmlTree:
        found_node: XmlTree = MavenModuleReader._find(parent, tag)

        if found_node is None:
            raise Exception(
                f"Unable to find xml tag \"{parent.tag}/{tag}\" in \"{self._pom_file.resolve()}\".")

        return found_node

    @staticmethod
    def _find(parent: XmlTree, tag: str) -> XmlTree:
        return parent.find(f"{{http://maven.apache.org/POM/4.0.0}}{tag}")
