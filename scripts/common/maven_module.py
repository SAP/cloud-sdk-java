from pathlib import Path
from typing import Optional, Dict

from common.release_audience import ReleaseAudience
from common.release_maturity import ReleaseMaturity


class MavenModule:
    def __init__(self,
                 group_id: str,
                 artifact_id: str,
                 packaging: str,
                 release_audience: ReleaseAudience,
                 release_maturity: ReleaseMaturity,
                 pom_file: Path,
                 exclude_from_blackduck_scan: bool = False,
                 parent_group_id: Optional[str] = None,
                 parent_artifact_id: Optional[str] = None):
        self._group_id: str = group_id
        self._artifact_id: str = artifact_id
        self._packaging: str = packaging
        self._release_audience: ReleaseAudience = release_audience
        self._release_maturity: ReleaseMaturity = release_maturity
        self._exclude_from_blackduck_scan: bool = exclude_from_blackduck_scan
        self._pom_file: Path = pom_file
        self._parent_group_id: str = "" if parent_group_id is None else parent_group_id
        self._parent_artifact_id: str = "" if parent_artifact_id is None else parent_artifact_id

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
    def packaging(self) -> str:
        return self._packaging

    @property
    def release_audience(self) -> ReleaseAudience:
        return self._release_audience

    @property
    def release_maturity(self) -> ReleaseMaturity:
        return self._release_maturity

    @property
    def is_excluded_from_blackduck_scan(self) -> bool:
        return self._exclude_from_blackduck_scan

    @property
    def pom_file(self) -> Path:
        return self._pom_file

    @property
    def parent_group_id(self) -> str:
        return self._parent_group_id

    @property
    def parent_artifact_id(self) -> str:
        return self._parent_artifact_id

    @property
    def is_excluded_from_release(self) -> bool:
        return self.release_audience == ReleaseAudience.NONE

    def __repr__(self) -> str:
        return str(self)

    def __str__(self) -> str:
        return self.identifier

    def to_dictionary(self) -> Dict[str, str]:
        return {
            "groupId": self.group_id,
            "artifactId": self.artifact_id,
            "packaging": self.packaging,
            "releaseAudience": str(self.release_audience),
            "releaseMaturity": str(self.release_maturity),
            "pomFile": str(self.pom_file).replace("\\", "/"),
            "parentGroupId": self.parent_group_id,
            "parentArtifactId": self.parent_artifact_id,
            "excludeFromBlackDuckScan": self.is_excluded_from_blackduck_scan,
        }

    @staticmethod
    def from_dictionary(dictionary: Dict[str, str]) -> "MavenModule":
        return MavenModule(str(dictionary["groupId"]),
                           str(dictionary["artifactId"]),
                           str(dictionary["packaging"]),
                           ReleaseAudience(str(dictionary["releaseAudience"])),
                           ReleaseMaturity(str(dictionary["releaseMaturity"])),
                           Path(str(dictionary["pomFile"])),
                           bool(dictionary["excludeFromBlackDuckScan"]),
                           str(dictionary["parentGroupId"]),
                           str(dictionary["parentArtifactId"]))
