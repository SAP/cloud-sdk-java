from enum import Enum


class ReleaseAudience(Enum):
    NONE = "None"
    INTERNAL = "Internal"
    PUBLIC = "Public"

    def __repr__(self) -> str:
        return str(self)

    def __str__(self) -> str:
        return self.value
