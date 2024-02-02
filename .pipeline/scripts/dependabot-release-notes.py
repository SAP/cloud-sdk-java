import argparse
import re
from dataclasses import dataclass
from pathlib import Path
from typing import ClassVar, Optional
from unittest import TestCase

_MARKDOWN_LINK_PATTERN: re.Pattern = re.compile(r"^\[([^]]+)]\(([^)]+)\)$")


def parse_markdown_link(value: str) -> tuple[str, str]:
    """
    Extracts the text and the hyperlink from the provided markdown link.
    :param value: The raw markdown link in the form of "`[text](link)`".
    :return: The text and the link from the markdown link.
    """

    match: re.Match = _MARKDOWN_LINK_PATTERN.match(value.strip())
    if not match:
        raise Exception(f"'{value}' is not a valid markdown link.")

    return match.group(1), match.group(2)


@dataclass
class DependencyUpdate:
    group_id: str
    artifact_id: str
    old_version: str
    new_version: str


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

        group_and_artifact_id: Optional[tuple[str, str]] = PrBodyParser._extract_dependency_group_and_artifact(items[1].strip())
        if group_and_artifact_id is None:
            return None

        old_version: str = PrBodyParser._extract_version(items[2].strip())
        new_version: str = PrBodyParser._extract_version(items[3].strip())

        return DependencyUpdate(group_and_artifact_id[0], group_and_artifact_id[1], old_version, new_version)

    @staticmethod
    def _extract_dependency_group_and_artifact(raw: str) -> Optional[tuple[str, str]]:
        link_match: re.Match = _MARKDOWN_LINK_PATTERN.match(raw.strip())
        if link_match:
            full_name: str = link_match.group(1)
        else:
            full_name: str = raw

        items: list[str] = full_name.split(":")
        if len(items) != 2:
            return None

        return items[0], items[1]

    @staticmethod
    def _extract_version(raw: str) -> str:
        if raw.startswith("`"):
            raw = raw[1:]

        if raw.endswith("`"):
            raw = raw[:-1]

        return raw


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

        artifact_id, _ = parse_markdown_link(items[0].strip())

        group_id_match: re.Match = ReleaseNotesParser._GROUP_ID_PATTERN.match(items[1].strip())
        if not group_id_match:
            raise Exception(f"Unable to extract the group id from '{cell}'.")

        return artifact_id, group_id_match.group(1)

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
            search_link: str = f"https://search.maven.org/search?q=g:{update.group_id}%20AND%20a:%20{update.artifact_id}"
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

        new_lines: list[str] = old_lines[0:start_and_end[1]] + [""] + updated_lines + old_lines[start_and_end[1]:]
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
        new_lines: list[str] = old_lines + ["", "### 📈 Improvements", ""] + updated_lines
        return "\n".join(new_lines)


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
    parser.add_argument("--release-notes",
                        help="Path to our 'release_notes.md' file.",
                        default="release_notes.md",
                        required=False)

    args = parser.parse_args()

    pr_body_file: Path = Path(args.pr_body)
    if not pr_body_file.is_file():
        raise Exception(f"'{args.pr_body}' does not exist.")

    pr_body: str = pr_body_file.read_text()

    release_notes_file: Path = Path(args.release_notes)
    if not release_notes_file.is_file():
        raise Exception(f"'{args.release_notes}' does not exist.")

    old_release_notes: str = release_notes_file.read_text()

    new_updates: list[DependencyUpdate] = PrBodyParser.parse(pr_body)
    if len(new_updates) < 1:
        print(f"There seem to be no dependency updates.")
        exit(0)

    old_updates: list[DependencyUpdate] = ReleaseNotesParser.parse(old_release_notes)
    merged_updates: list[DependencyUpdate] = merge_updates(old_updates, new_updates)

    new_release_notes: str = ReleaseNotesUpdater.update(old_release_notes, merged_updates)
    release_notes_file.write_text(new_release_notes)


if __name__ == "__main__":
    main()


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
| [actions/setup-java](https://github.com/actions/setup-java) | `3` | `4` |
| [actions/upload-artifact](https://github.com/actions/upload-artifact) | `3` | `4` |
| [actions/cache](https://github.com/actions/cache) | `3` | `4` |
| [actions/download-artifact](https://github.com/actions/download-artifact) | `3` | `4` |
| [github/codeql-action](https://github.com/github/codeql-action) | `2` | `3` |
| [fsfe/reuse-action](https://github.com/fsfe/reuse-action) | `1.2.0` | `2.0.0` |"""

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

</details>
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
| [new-artifact-id](https://search.maven.org/search?q=g:new.group.id%20AND%20a:%20new-artifact-id) (`new.group.id`) | `new-from` | `new-to` |

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
| [new-artifact-id](https://search.maven.org/search?q=g:new.group.id%20AND%20a:%20new-artifact-id) (`new.group.id`) | `new-from` | `new-to` |

</details>""")

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
| [new-artifact-id](https://search.maven.org/search?q=g:new.group.id%20AND%20a:%20new-artifact-id) (`new.group.id`) | `new-from` | `new-to` |

</details>""")
