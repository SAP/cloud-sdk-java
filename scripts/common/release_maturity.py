from enum import Enum


class ReleaseMaturity(Enum):
    BETA = "Beta"
    STABLE = "Stable"
    DEPRECATED = "Deprecated"

    def __repr__(self) -> str:
        return str(self)

    def __str__(self) -> str:
        return self.value
