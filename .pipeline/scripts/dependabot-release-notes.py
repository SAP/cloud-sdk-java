import argparse
import re
from dataclasses import dataclass
from enum import Enum
from pathlib import Path
from typing import ClassVar, Optional
from unittest import TestCase

_MARKDOWN_LINK_PATTERN: re.Pattern = re.compile(r"^\[([^]]+)]\(([^)]+)\)$")
_MAVEN_ARTIFACT_PATTERN: re.Pattern = re.compile(r"^([a-zA-Z][a-zA-Z0-9._\-]*):([a-zA-Z][a-zA-Z0-9._\-]*)$")


def parse_markdown_link(value: str) -> Optional[tuple[str, str]]:
    match: re.Match = _MARKDOWN_LINK_PATTERN.match(value.strip())
    if not match:
        return None

    return match.group(1), match.group(2)


def parse_maven_artifact(value: str) -> Optional[tuple[str, str]]:
    match: re.Match = _MAVEN_ARTIFACT_PATTERN.match(value.strip())
    if not match:
        return None

    return match.group(1), match.group(2)


@dataclass(frozen=True, eq=True)
class DependencyUpdate:
    group_id: str
    artifact_id: str
    old_version: str
    new_version: str


class DependencyScope(Enum):
    COMPILE = "compile"
    PROVIDED = "provided"
    RUNTIME = "runtime"
    TEST = "test"
    SYSTEM = "system"
    IMPORT = "import"

    @classmethod
    def of(cls, raw: str) -> "DependencyScope":
        raw = raw.strip().lower()
        for scope in cls:
            if scope.value.lower() == raw:
                return scope

        raise Exception(f"Unknown dependency scope: '{raw}'.")


# Expected PR Body Format:
# Bumps the <dependency-type> group with X updates:
#
# | Package | From | To |
# | --- | --- | --- |
# | <group-id>:<artifact-id> (might be within a markdown link) | `old-version` | `new-version` |
class PrBodyParser:
    _TABLE_HEAD_PATTERN: ClassVar[re.Pattern] = re.compile(r"^\s*\|\s*Package\s*\|\s*From\s*\|\s*To\s*\|\s*$",
                                                           re.IGNORECASE)

    @staticmethod
    def parse(pr_body: str) -> list[DependencyUpdate]:
        lines: list[str] = pr_body.splitlines()
        table_offset: Optional[int] = PrBodyParser._find_table_offset(lines)
        if table_offset is None:
            return []

        rows: list[str] = []
        for i in range(table_offset, len(lines)):
            stripped_line: str = lines[i].strip()
            if stripped_line.startswith("|") and stripped_line.endswith("|"):
                rows.append(stripped_line)

            else:
                break

        updates: list[DependencyUpdate] = []
        for row in rows[2:]:
            update: Optional[DependencyUpdate] = PrBodyParser._parse_table_row(row)
            if update is not None:
                updates.append(update)

        return updates

    @staticmethod
    def _find_table_offset(lines: list[str]) -> Optional[int]:
        for i, line in enumerate(lines):
            head_match: re.Match = PrBodyParser._TABLE_HEAD_PATTERN.match(line)
            if head_match:
                return i

        return None

    @staticmethod
    def _parse_table_row(row: str) -> Optional[DependencyUpdate]:
        items: list[str] = row.split("|")
        if len(items) != 5:
            raise Exception(
                f"Dependabot table row has unexpected format. "
                f"Expected to find 3 columns (separated by '|') in '{row}'.")

        group_and_artifact_id: Optional[tuple[str, str]] = PrBodyParser._extract_dependency_group_and_artifact(
            items[1].strip())
        if group_and_artifact_id is None:
            return None

        old_version: str = PrBodyParser._extract_version(items[2].strip())
        new_version: str = PrBodyParser._extract_version(items[3].strip())

        return DependencyUpdate(group_and_artifact_id[0], group_and_artifact_id[1], old_version, new_version)

    @staticmethod
    def _extract_dependency_group_and_artifact(raw: str) -> Optional[tuple[str, str]]:
        link_match: Optional[tuple[str, str]] = parse_markdown_link(raw)
        full_name: str = raw
        if link_match is not None:
            full_name = link_match[0]

        group_and_artifact: Optional[tuple[str, str]] = parse_maven_artifact(full_name)
        if group_and_artifact is None:
            return None

        return group_and_artifact[0], group_and_artifact[1]

    @staticmethod
    def _extract_version(raw: str) -> str:
        if raw.startswith("`"):
            raw = raw[1:]

        if raw.endswith("`"):
            raw = raw[:-1]

        return raw


class PomFileParser:

    def __init__(self, file_content: str):
        lines: list[str] = file_content.splitlines()
        management_section: Optional[range] = PomFileParser._find_dependency_management(lines)
        dependencies_section: Optional[range] = PomFileParser._find_dependencies(lines, management_section)

        self._management_section: list[str] = []
        if management_section is not None:
            self._management_section = lines[management_section.start:management_section.stop]

        self._dependencies_section: list[str] = []
        if dependencies_section is not None:
            self._dependencies_section = lines[dependencies_section.start:dependencies_section.stop]

    @staticmethod
    def _find_dependency_management(lines: list[str]) -> Optional[range]:
        start_line: str = "<dependencyManagement>".lower()
        end_line: str = "</dependencyManagement>".lower()

        start: int = -1
        for i, line in enumerate(lines):
            if line.strip().lower() == start_line:
                start = i + 1  # we want to exclude the start line
                break

        if start < 0:
            return None

        end: int = -1
        for i in range(start, len(lines)):
            if lines[i].strip().lower() == end_line:
                end = i
                break

        if end < 0:
            raise Exception("Unable to find end of dependency management section.")

        return range(start, end)

    @staticmethod
    def _find_dependencies(lines: list[str], management_section: Optional[range]) -> Optional[range]:
        start_line: str = "<dependencies>".lower()
        end_line: str = "</dependencies>".lower()

        exclude: range = range(-1, -1) if management_section is None else management_section

        start: int = -1
        for i, line in enumerate(lines):
            if i in exclude:
                continue

            if line.strip().lower() == start_line:
                start = i + 1  # we want to exclude the start line
                break

        if start < 0:
            return None

        end: int = -1
        for i in range(start, len(lines)):
            if lines[i].strip().lower() == end_line:
                end = i
                break

        if end < 0:
            raise Exception("Unable to find end of dependencies section.")

        return range(start, end)

    def get_scope(self, update: DependencyUpdate) -> Optional[DependencyScope]:
        dependency_declaration: Optional[list[str]] = self._find_dependency_declaration(self._dependencies_section,
                                                                                        update.group_id,
                                                                                        update.artifact_id)
        dependency_scope: Optional[DependencyScope] = None
        if dependency_declaration is not None:
            dependency_scope = PomFileParser._extract_scope(dependency_declaration)

        if dependency_scope is not None:
            return dependency_scope

        management_declaration: Optional[list[str]] = self._find_dependency_declaration(self._management_section,
                                                                                        update.group_id,
                                                                                        update.artifact_id)
        management_scope: Optional[DependencyScope] = None
        if management_declaration is not None:
            management_scope = PomFileParser._extract_scope(management_declaration)

        if dependency_declaration is None and management_declaration is None:
            return None

        if management_scope is not None:
            return management_scope

        return DependencyScope.COMPILE

    @staticmethod
    def _find_dependency_declaration(lines: list[str], group_id: str, artifact_id: str) -> Optional[list[str]]:
        expected_group_id: str = f"<groupId>{group_id}</groupId>".lower()

        for i, line in enumerate(lines):
            actual: str = line.strip().lower()
            if actual != expected_group_id:
                continue

            dependency_declaration: Optional[list[str]] = PomFileParser._get_dependency_declaration(lines, i)
            if dependency_declaration is None:
                continue

            actual_artifact_id: str = PomFileParser._extract_artifact_id(dependency_declaration)
            if actual_artifact_id != artifact_id:
                continue

            return dependency_declaration

        return None

    @staticmethod
    def _get_dependency_declaration(lines: list[str], group_id_index: int) -> Optional[list[str]]:
        dependency_start_line: str = "<dependency>".lower()
        exclusion_start_line: str = "<exclusion>".lower()
        dependency_end_line: str = "</dependency>".lower()

        start: int = -1
        for i in range(group_id_index, -1, -1):  # the end index is EXCLUDED
            actual: str = lines[i].strip().lower()
            if actual == dependency_start_line:
                start = i + 1  # we want to include the dependency start line
                break

            if actual == exclusion_start_line:
                return None

        if start < 0:
            return None

        end: int = -1
        for i in range(group_id_index, len(lines)):
            actual: str = lines[i].strip().lower()
            if actual == dependency_end_line:
                end = i
                break

        if end <= start:
            raise Exception("Unable to find end of dependency declaration.")

        return lines[start:end]

    @staticmethod
    def _extract_artifact_id(lines: list[str]) -> str:
        pattern: re.Pattern = re.compile(r"^\s*<artifactId>([^<]+)</artifactId>\s*$", re.IGNORECASE)
        for line in lines:
            match: re.Match = pattern.match(line)
            if match:
                return match.group(1)

        raise Exception("Unable to find the artifact id in the dependency declaration.")

    @staticmethod
    def _extract_scope(lines: list[str]) -> Optional[DependencyScope]:
        pattern: re.Pattern = re.compile(r"^\s*<scope>([^<]+)</scope>\s*$", re.IGNORECASE)
        for line in lines:
            match: re.Match = pattern.match(line)
            if match:
                return DependencyScope.of(match.group(1))

        return None


# Expected format of the Release Notes:
# ### 📈 Improvements
# ... (notes)
# <details><summary>Dependency Updates</summary>
#
# | Dependency | From | To |
# | --- | --- | --- | --- |
# | [artifact-id](maven-central-search-link) (`group-id`) | `old-version` | `new-version` |
# </details>
class ReleaseNotesParser:
    _TABLE_HEAD_PATTERN: ClassVar[re.Pattern] = re.compile("^\s*\|\s*Dependency\s*\|\s*From\s*\|\s*To\s*\|\s*$",
                                                           re.IGNORECASE)
    _GROUP_ID_PATTERN: ClassVar[re.Pattern] = re.compile(r"^\(`([^`]+)`\)$")

    @staticmethod
    def parse(release_notes: str) -> list[DependencyUpdate]:
        lines: list[str] = release_notes.splitlines()
        table_offset: Optional[int] = ReleaseNotesParser._find_table_offset(lines)
        if table_offset is None:
            return []

        rows: list[str] = []
        for i in range(table_offset, len(lines)):
            stripped_line: str = lines[i].strip()
            if stripped_line.startswith("|") and stripped_line.endswith("|"):
                rows.append(stripped_line)

            else:
                break

        updates: list[DependencyUpdate] = []
        for row in rows[2:]:
            updates.append(ReleaseNotesParser._parse_table_row(row))

        return updates

    @staticmethod
    def _find_table_offset(lines: list[str]) -> Optional[int]:
        for i, line in enumerate(lines):
            head_match: re.Match = ReleaseNotesParser._TABLE_HEAD_PATTERN.match(line)
            if head_match:
                return i

        return None

    @staticmethod
    def _parse_table_row(row: str) -> DependencyUpdate:
        items: list[str] = row.split("|")
        if len(items) != 5:
            raise Exception(f"Expected to find 5 occurrences of '|' in '{row}', but found {len(items)} instead.")

        artifact_id, group_id = ReleaseNotesParser._extract_group_and_artifact(items[1])
        old_version: str = ReleaseNotesParser._extract_version(items[2])
        new_version: str = ReleaseNotesParser._extract_version(items[3])

        return DependencyUpdate(group_id, artifact_id, old_version, new_version)

    @staticmethod
    def _extract_group_and_artifact(cell: str) -> tuple[str, str]:
        items: list[str] = cell.strip().split(" ")
        if len(items) != 2:
            raise Exception(f"Expected to find occurrences of ' ' in '{cell}', but found {len(items)} instead.")

        link_match: Optional[tuple[str, str]] = parse_markdown_link(items[0].strip())
        if link_match is None:
            raise Exception(f"Release note cell ('{cell}') does not contain the group and artifact id in the expected "
                            f"format.")

        group_id_match: re.Match = ReleaseNotesParser._GROUP_ID_PATTERN.match(items[1].strip())
        if not group_id_match:
            raise Exception(f"Unable to extract the group id from '{cell}'.")

        return link_match[0], group_id_match.group(1)

    @staticmethod
    def _extract_version(cell: str) -> str:
        version: str = cell.strip()
        if version.startswith("`"):
            version = version[1:]

        if version.endswith("`"):
            version = version[:-1]

        return version.strip()


class ReleaseNotesUpdater:
    _DETAILS_BLOCK_HEAD: ClassVar[str] = "<details><summary>Dependency Updates</summary>".lower()
    _DETAILS_BLOCK_TAIL: ClassVar[str] = "</details>".lower()
    _IMPROVEMENTS_HEAD_PATTERN: ClassVar[re.Pattern] = re.compile(r"^#+\s+.+Improvements$", re.IGNORECASE)

    @staticmethod
    def update(release_notes: str, updates: list[DependencyUpdate]) -> str:
        old_lines: list[str] = release_notes.splitlines()
        table: list[str] = ReleaseNotesUpdater._to_markdown_table(updates)
        updated_lines: list[str] = ["<details><summary>Dependency Updates</summary>", ""] + table + ["", "</details>"]

        result: Optional[str] = ReleaseNotesUpdater._replace_existing_details_block(old_lines, updated_lines)
        if result is not None:
            return result

        result = ReleaseNotesUpdater._add_details_block_to_improvements_section(old_lines, updated_lines)
        if result is not None:
            return result

        return ReleaseNotesUpdater._add_improvements_section(old_lines, updated_lines)

    @staticmethod
    def _to_markdown_table(updates: list[DependencyUpdate]) -> list[str]:
        # following format should match the expectation of the `ReleaseNotesParser`!
        head: str = "| Dependency | From | To |"
        separator: str = "| --- | --- | --- |"
        rows: list[str] = []

        for update in updates:
            search_link: str = f"https://search.maven.org/search?q=g%3A{update.group_id}%2Ba%3A{update.artifact_id}"
            rows.append(
                f"| [{update.artifact_id}]({search_link}) (`{update.group_id}`) | `{update.old_version}` | `{update.new_version}` |")

        return [head, separator] + rows

    @staticmethod
    def _replace_existing_details_block(old_lines: list[str], updated_lines: list[str]) -> Optional[str]:
        start_and_end: Optional[tuple[int, int]] = ReleaseNotesUpdater._find_details_block(old_lines)
        if start_and_end is None:
            return None

        new_lines: list[str] = old_lines[0:start_and_end[0]] + updated_lines + old_lines[start_and_end[1]:]
        return "\n".join(new_lines)

    @staticmethod
    def _find_details_block(lines: list[str]) -> Optional[tuple[int, int]]:
        start: int = -1
        for i, line in enumerate(lines):
            if line.strip().lower() == ReleaseNotesUpdater._DETAILS_BLOCK_HEAD:
                start = i
                break

        if start < 0:
            return None

        for i in range(start + 1, len(lines)):
            if lines[i].strip().lower() == ReleaseNotesUpdater._DETAILS_BLOCK_TAIL:
                return start, i + 1  # end index is EXCLUDED; we need to add 1 here to include the `</details>` line

        raise Exception(f"Unable to find '{ReleaseNotesUpdater._DETAILS_BLOCK_TAIL}' in the current release notes.")

    @staticmethod
    def _add_details_block_to_improvements_section(old_lines: list[str], updated_lines: list[str]) -> Optional[str]:
        start_and_end: Optional[tuple[int, int]] = ReleaseNotesUpdater._find_improvements_section(old_lines)
        if start_and_end is None:
            return None

        new_lines: list[str] = old_lines[0:start_and_end[1]] + [""] + updated_lines + [""] + old_lines[
                                                                                             start_and_end[1]:]
        return "\n".join(new_lines)

    @staticmethod
    def _find_improvements_section(lines: list[str]) -> Optional[tuple[int, int]]:
        start: int = -1
        for i, line in enumerate(lines):
            head_match: re.Match = ReleaseNotesUpdater._IMPROVEMENTS_HEAD_PATTERN.match(line.strip())
            if head_match:
                start = i
                break

        if start < 0:
            return None

        for i in range(start + 1, len(lines)):
            if lines[i].strip().startswith("#"):
                return start, i  # end index is EXCLUDED

        return start, len(lines)

    @staticmethod
    def _add_improvements_section(old_lines: list[str], updated_lines: list[str]) -> str:
        new_lines: list[str] = old_lines + ["", "### 📈 Improvements", ""] + updated_lines + [""]
        return "\n".join(new_lines)


def deduplicate_updates(updates: list[DependencyUpdate]) -> list[DependencyUpdate]:
    result: set[DependencyUpdate] = set()
    for update in updates:
        result.add(update)

    return list(result)


def filter_updates(updates: list[DependencyUpdate], pom: PomFileParser) -> list[DependencyUpdate]:
    result: list[DependencyUpdate] = []

    for update in updates:
        scope: Optional[DependencyScope] = pom.get_scope(update)
        if scope is None:
            continue

        if scope == DependencyScope.TEST or scope == DependencyScope.SYSTEM:
            continue

        result.append(update)

    return result


def merge_updates(old_updates: list[DependencyUpdate], new_updates: list[DependencyUpdate]) -> list[DependencyUpdate]:
    result: list[DependencyUpdate] = []
    included_names: set[str] = set()

    for update in new_updates:
        full_name: str = f"{update.group_id}:{update.artifact_id}"
        if full_name in included_names:
            raise Exception(f"Following dependency received multiple updates within the same PR: '{full_name}'.")

        included_names.add(full_name)
        result.append(update)

    for update in old_updates:
        full_name: str = f"{update.group_id}:{update.artifact_id}"
        if full_name in included_names:
            continue

        included_names.add(full_name)
        result.append(update)

    return list(sorted(result, key=lambda x: x.artifact_id))


def main():
    parser: argparse.ArgumentParser = argparse.ArgumentParser(
        description="Extracts dependency updates from a PR body created by Dependabot. "
                    "Then merges the new updates into (potentially) existing ones in our release_notes.md.")
    parser.add_argument("--pr-body",
                        help="File that contains the PR body.",
                        required=True)
    parser.add_argument("--pom",
                        help="Path to the 'pom.xml' file that (might) contain the dependency scopes.",
                        required=True)
    parser.add_argument("--release-notes",
                        help="Path to our 'release_notes.md' file.",
                        default="release_notes.md",
                        required=False)

    args = parser.parse_args()

    pr_body_file: Path = Path(args.pr_body)
    if not pr_body_file.is_file():
        raise Exception(f"'{args.pr_body}' does not exist.")

    pr_body: str = pr_body_file.read_text()

    pom_file: Path = Path(args.pom)
    if not pom_file.is_file():
        raise Exception(f"'{args.pom}' does not exist.")

    pom_content: str = pom_file.read_text()
    pom: PomFileParser = PomFileParser(pom_content)

    release_notes_file: Path = Path(args.release_notes)
    if not release_notes_file.is_file():
        raise Exception(f"'{args.release_notes}' does not exist.")

    old_release_notes: str = release_notes_file.read_text()

    new_updates: list[DependencyUpdate] = PrBodyParser.parse(pr_body)
    new_updates = deduplicate_updates(new_updates)
    new_updates = filter_updates(new_updates, pom)
    if len(new_updates) < 1:
        print(f"There seem to be no dependency updates.")
        exit(0)

    old_updates: list[DependencyUpdate] = ReleaseNotesParser.parse(old_release_notes)
    merged_updates: list[DependencyUpdate] = merge_updates(old_updates, new_updates)

    new_release_notes: str = ReleaseNotesUpdater.update(old_release_notes, merged_updates)
    release_notes_file.write_text(new_release_notes)


if __name__ == "__main__":
    main()


class Test(TestCase):

    def test_deduplicate_updates(self):
        first_update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        second_update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        third_update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "2.3.4", "3.4.5")

        all_updates: list[DependencyUpdate] = [first_update, second_update, third_update]
        actual: list[DependencyUpdate] = deduplicate_updates(all_updates)

        self.assertEqual([first_update, third_update], actual)


class PrBodyParserTest(TestCase):

    def test_parse_but_table_is_missing(self):
        data = """
Bumps the production-minor-patch group with 5 updates:

Updates `com.sap.cloud:neo-java-web-api` from 4.67.12 to 4.68.9

Updates `org.assertj:assertj-core` from 3.25.1 to 3.25.2"""

        updates: list[DependencyUpdate] = PrBodyParser.parse(data)
        self.assertEqual([], updates)

    def test_parse_but_table_is_empty(self):
        data = """
Bumps the production-minor-patch group with 5 updates:

| Package | From | To |
| --- | --- | --- |

Updates `com.sap.cloud:neo-java-web-api` from 4.67.12 to 4.68.9

Updates `org.assertj:assertj-core` from 3.25.1 to 3.25.2"""

        updates: list[DependencyUpdate] = PrBodyParser.parse(data)
        self.assertEqual([], updates)

    def test_parse_github_actions_updates_table(self):
        data = """
Bumps the github-actions group with 7 updates:

| Package | From | To |
| --- | --- | --- |
| [actions/checkout](https://github.com/actions/checkout) | `3` | `4` |
| [actions/that.almost.looks:like-a-maven-module](https://github.com/actions/action) | `3` | `4` |"""

        updates: list[DependencyUpdate] = PrBodyParser.parse(data)
        self.assertEqual([], updates)

    def test_parse(self):
        data = """
Bumps the production-minor-patch group with 5 updates:

| Package | From | To |
| --- | --- | --- |
| plain.group.id:artifact-id | `first-old` | `first-new` |
| [linked.group.id:artifact-id](https://foo.bar) | `second-old` | `second-new` |

Updates `com.sap.cloud:neo-java-web-api` from 4.67.12 to 4.68.9

Updates `org.assertj:assertj-core` from 3.25.1 to 3.25.2"""

        updates: list[DependencyUpdate] = PrBodyParser.parse(data)
        self.assertEqual(2, len(updates))

        self.assertEqual(DependencyUpdate("plain.group.id", "artifact-id", "first-old", "first-new"), updates[0])
        self.assertEqual(DependencyUpdate("linked.group.id", "artifact-id", "second-old", "second-new"), updates[1])


class PomFileParserTest(TestCase):

    def test_get_scope_from_dependency_only_with_explicit_scope(self):
        data = """
<dependencies>
    <dependency>
        <groupId>some.group</groupId>
        <artifactId>some-artifact</artifactId>
        <version>2.3.4</version>
        <scope>test</scope>
    </dependency>
</dependencies>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: DependencyScope = parser.get_scope(update)

        self.assertEqual(DependencyScope.TEST, scope)

    def test_get_scope_from_dependency_only_with_implicit_scope(self):
        data = """
<dependencies>
    <dependency>
        <groupId>some.group</groupId>
        <artifactId>some-artifact</artifactId>
        <version>2.3.4</version>
    </dependency>
</dependencies>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: DependencyScope = parser.get_scope(update)

        self.assertEqual(DependencyScope.COMPILE, scope)

    def test_get_scope_from_dependency_management_only_with_explicit_scope(self):
        data = """
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>some.group</groupId>
            <artifactId>some-artifact</artifactId>
            <version>2.3.4</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</dependencyManagement>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: DependencyScope = parser.get_scope(update)

        self.assertEqual(DependencyScope.TEST, scope)

    def test_get_scope_from_dependency_management_only_with_implicit_scope(self):
        data = """
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>some.group</groupId>
            <artifactId>some-artifact</artifactId>
            <version>2.3.4</version>
        </dependency>
    </dependencies>
</dependencyManagement>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: DependencyScope = parser.get_scope(update)

        self.assertEqual(DependencyScope.COMPILE, scope)

    def test_get_scope_from_dependency_management_and_dependencies_with_explicit_scope_01(self):
        data = """
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>some.group</groupId>
            <artifactId>some-artifact</artifactId>
            <version>2.3.4</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>some.group</groupId>
        <artifactId>some-artifact</artifactId>
    </dependency>
</dependencies>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: DependencyScope = parser.get_scope(update)

        self.assertEqual(DependencyScope.TEST, scope)

    def test_get_scope_from_dependency_management_and_dependencies_with_explicit_scope_02(self):
        data = """
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>some.group</groupId>
            <artifactId>some-artifact</artifactId>
            <version>2.3.4</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>some.group</groupId>
        <artifactId>some-artifact</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: DependencyScope = parser.get_scope(update)

        self.assertEqual(DependencyScope.TEST, scope)

    def test_get_scope_from_dependency_management_and_dependencies_with_implicit_scope(self):
        data = """
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>some.group</groupId>
            <artifactId>some-artifact</artifactId>
            <version>2.3.4</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>some.group</groupId>
        <artifactId>some-artifact</artifactId>
    </dependency>
</dependencies>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: DependencyScope = parser.get_scope(update)

        self.assertEqual(DependencyScope.COMPILE, scope)

    def test_get_scope_but_no_dependency_declaration(self):
        data = """
<dependencyManagement>
    <dependencies>
    </dependencies>
</dependencyManagement>

<dependencies>
</dependencies>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: Optional[DependencyScope] = parser.get_scope(update)

        self.assertIsNone(scope)

    def test_get_scope_with_exclusion_present(self):
        data = """
<dependencies>
    <dependency>
        <groupId>foo.group</groupId>
        <artifactId>foo-artifacts</artifactId>
        <version>foo.bar.baz</version>
        <exclusions>
            <exclusion>
                <groupId>some.group</groupId>
                <artifactId>some-artifact</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>some.group</groupId>
        <artifactId>some-artifact</artifactId>
        <version>2.3.4</version>
    </dependency>
</dependencies>"""

        parser: PomFileParser = PomFileParser(data)
        update: DependencyUpdate = DependencyUpdate("some.group", "some-artifact", "1.2.3", "2.3.4")
        scope: Optional[DependencyScope] = parser.get_scope(update)

        self.assertEqual(DependencyScope.COMPILE, scope)


class ReleaseNotesParserTest(TestCase):

    def test_parse_but_table_is_missing(self):
        data = """
### 📈 Improvements

- First improvement.
- Second improvement.

<details><summary>Dependency Updates</summary>
</details>
"""

        updates: list[DependencyUpdate] = ReleaseNotesParser.parse(data)
        self.assertEqual([], updates)

    def test_parse_but_table_is_empty(self):
        data = """
### 📈 Improvements

- First improvement.
- Second improvement.

<details><summary>Dependency Updates</summary>

| Dependency | From | To |
| --- | --- | --- |

</details>
"""

        updates: list[DependencyUpdate] = ReleaseNotesParser.parse(data)
        self.assertEqual([], updates)

    def test_parse(self):
        data = """
### 📈 Improvements

- First improvement.
- Second improvement.

<details><summary>Dependency Updates</summary>

| Dependency | From | To |
| --- | --- | --- |
| [artifact-id](https://foo.bar) (`group.id`) | `old` | `new` |

</details>
"""

        updates: list[DependencyUpdate] = ReleaseNotesParser.parse(data)
        self.assertEqual(1, len(updates))

        self.assertEqual(DependencyUpdate("group.id", "artifact-id", "old", "new"), updates[0])


class ReleaseNotesUpdaterTest(TestCase):

    def test_update_with_existing_details_block(self):
        data = """
### 📈 Improvements

- First Improvement
- Second Improvement

<details><summary>Dependency Updates</summary>

| Dependency | From | To |
| --- | --- | --- |
| [artifact-id](https://foo.bar) (`group.id`) | `from` | `to` |

</details>"""

        updates: list[DependencyUpdate] = [DependencyUpdate("new.group.id", "new-artifact-id", "new-from", "new-to")]
        result: str = ReleaseNotesUpdater.update(data, updates)

        self.assertEqual(result, """
### 📈 Improvements

- First Improvement
- Second Improvement

<details><summary>Dependency Updates</summary>

| Dependency | From | To |
| --- | --- | --- |
| [new-artifact-id](https://search.maven.org/search?q=g%3Anew.group.id%2Ba%3Anew-artifact-id) (`new.group.id`) | `new-from` | `new-to` |

</details>""")

    def test_update_but_no_details_block(self):
        data = """
### 📈 Improvements

- First Improvement
- Second Improvement
"""

        updates: list[DependencyUpdate] = [DependencyUpdate("new.group.id", "new-artifact-id", "new-from", "new-to")]
        result: str = ReleaseNotesUpdater.update(data, updates)

        self.assertEqual(result, """
### 📈 Improvements

- First Improvement
- Second Improvement

<details><summary>Dependency Updates</summary>

| Dependency | From | To |
| --- | --- | --- |
| [new-artifact-id](https://search.maven.org/search?q=g%3Anew.group.id%2Ba%3Anew-artifact-id) (`new.group.id`) | `new-from` | `new-to` |

</details>
""")

    def test_update_but_no_improvements_section(self):
        data = """
### ✨ New Functionality

- New Feature"""

        updates: list[DependencyUpdate] = [DependencyUpdate("new.group.id", "new-artifact-id", "new-from", "new-to")]
        result: str = ReleaseNotesUpdater.update(data, updates)

        self.assertEqual(result, """
### ✨ New Functionality

- New Feature

### 📈 Improvements

<details><summary>Dependency Updates</summary>

| Dependency | From | To |
| --- | --- | --- |
| [new-artifact-id](https://search.maven.org/search?q=g%3Anew.group.id%2Ba%3Anew-artifact-id) (`new.group.id`) | `new-from` | `new-to` |

</details>
""")
