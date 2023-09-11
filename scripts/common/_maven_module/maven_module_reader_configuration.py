import re
from pathlib import Path
from typing import List, Dict, Any, Optional, Union


class IgnoredMavenModule:
    def __init__(self, group_id: str, artifact_id: str, use_regex_validation: bool, reason: str):
        self._group_id: str = group_id
        self._artifact_id: str = artifact_id
        self._use_regex_validation: bool = use_regex_validation
        self._reason: str = reason

        if self._use_regex_validation:
            self._group_id_pattern: re.Pattern = re.compile(group_id)
            self._artifact_id_pattern: re.Pattern = re.compile(artifact_id)

    @property
    def identifier(self) -> str:
        return f"{self.group_id}:{self.artifact_id}"

    @property
    def group_id(self) -> str:
        return self._group_id

    @property
    def artifact_id(self) -> str:
        return self._artifact_id

    @property
    def use_regex_validation(self) -> bool:
        return self._use_regex_validation

    @property
    def reason(self) -> str:
        return self._reason

    def matches(self, group_id: Optional[str], artifact_id: Optional[str]) -> bool:
        if group_id is None:
            group_id = ""

        if artifact_id is None:
            artifact_id = ""

        if not self.use_regex_validation:
            return self._group_id == group_id and self._artifact_id == artifact_id

        return self._group_id_pattern.match(group_id) is not None \
               and self._artifact_id_pattern.match(artifact_id) is not None

    def to_dictionary(self) -> Dict[str, Any]:
        return {
            "groupId": self.group_id,
            "artifactId": self.artifact_id,
            "useRegexValidation": self.use_regex_validation,
            "reason": self.reason,
        }

    @staticmethod
    def from_dictionary(dictionary: Dict[str, Any]) -> "IgnoredMavenModule":
        return IgnoredMavenModule(str(dictionary["groupId"]),
                                  str(dictionary["artifactId"]),
                                  bool(dictionary["useRegexValidation"]),
                                  str(dictionary["reason"]))


class IgnoredPom:
    def __init__(self, path: str, use_regex_validation: bool, reason: str):
        self._path: str = path
        self._use_regex_validation: bool = use_regex_validation
        self._reason: str = reason

        if self._use_regex_validation:
            self._path_pattern: re.Pattern = re.compile(path)

    @property
    def path(self) -> str:
        return self._path

    @property
    def use_regex_validation(self) -> bool:
        return self._use_regex_validation

    @property
    def reason(self) -> str:
        return self._reason

    def matches(self, pom: str):
        if not self.use_regex_validation:
            return pom == self.path

        return self._path_pattern.match(pom) is not None

    def to_dictionary(self) -> Dict[str, Any]:
        return {
            "path": self.path,
            "useRegexValidation": self.use_regex_validation,
            "reason": self.reason,
        }

    @staticmethod
    def from_dictionary(dictionary: Dict[str, Any]) -> "IgnoredPom":
        return IgnoredPom(str(dictionary["path"]),
                          bool(dictionary["useRegexValidation"]),
                          str(dictionary["reason"]))


class MavenModuleReaderConfiguration:
    def __init__(self):
        self._ignored_modules: List[IgnoredMavenModule] = []
        self._ignored_poms: List[IgnoredPom] = []

    @property
    def ignored_modules(self) -> List[IgnoredMavenModule]:
        return self._ignored_modules

    @property
    def ignored_poms(self) -> List[IgnoredPom]:
        return self._ignored_poms

    def try_find_ignored_module(self, group_id: Optional[str],
                                artifact_id: Optional[str]) -> Optional[IgnoredMavenModule]:
        for ignored_module in self.ignored_modules:
            if ignored_module.matches(group_id, artifact_id):
                return ignored_module

        return None

    def try_find_ignored_pom(self, pom: Union[str, Path]) -> Optional[IgnoredPom]:
        pom_as_string: str = pom if isinstance(pom, str) else str(pom)

        for ignored_pom in self.ignored_poms:
            if ignored_pom.matches(pom_as_string):
                return ignored_pom

        return None

    def to_dictionary(self) -> Dict[str, Any]:
        return {
            "ignoredModules": [m.to_dictionary() for m in self.ignored_modules],
            "ignoredPoms": [p.to_dictionary() for p in self.ignored_poms]
        }

    @staticmethod
    def from_dictionary(dictionary: Dict[str, Any]) -> "MavenModuleReaderConfiguration":
        config: MavenModuleReaderConfiguration = MavenModuleReaderConfiguration()

        config.ignored_modules.extend([IgnoredMavenModule.from_dictionary(d) for d in dictionary["ignoredModules"]])
        config.ignored_poms.extend([IgnoredPom.from_dictionary(d) for d in dictionary["ignoredPoms"]])

        return config
