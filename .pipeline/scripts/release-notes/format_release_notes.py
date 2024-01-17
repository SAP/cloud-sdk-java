import argparse
import os
import re
from datetime import datetime


def read_file(file_name):
    with open(file_name, 'r', encoding="utf8") as file:
        return file.read()


def write_file(file_name, data):
    with open(file_name, 'w', encoding="utf8") as file:
        file.write(data)

unchanged_sections = [
"""### 🚧 Known Issues

- 

""",
"""### 🔧 Compatibility Notes

- 

""",
"""### ✨ New Functionality

- 

""",
"""### 📈 Improvements

- Dependency Updates:
  - SAP dependency updates:
    - Update [thing](https://link-to-thing) from `a.b.c` to `x.z.y`
  - Other dependency updates:
    - Major version updates:
      - Update [thing](https://link-to-thing) from `a.b.c` to `x.z.y`
    - Minor version updates:
      - Update [thing](https://link-to-thing) from `a.b.c` to `x.z.y`

""",
"""### 🐛 Fixed Issues

- 

"""]

def remove_unchanged_sections(file, unchanged_sections):
    for unchanged_section in unchanged_sections:
        # if file contains unchanged_section, remove it
        file = re.sub(unchanged_section, "", file)
    return file

def set_header(file, version):
    date = datetime.today().strftime('%B %d, %Y')
    # Replace the first line with: ## 5.2.0 - January 17, 2024
    file = re.sub("^.*", "## " + version + " - " + date, file)
    return file

def link_github_release(file, version):
    old_github_release_link = "\[All Release Changes\]\(https://github.com/SAP/cloud-sdk-java/releases\)"
    new_github_release_link = "[All Release Changes](https://github.com/SAP/cloud-sdk-java/releases/tag/rel%2F"+version+")"
    file = re.sub(old_github_release_link, new_github_release_link, file)
    return file


def clean_up_dependency_updates(file):
    empty_dependency_update = "- Update \[thing\]\(https://link-to-thing\) from `a.b.c` to `x.z.y`\n"
    sap_dependency_update = "  - SAP dependency updates:\n    "+empty_dependency_update
    major_dependency_update = "    - Major version updates:\n      "+empty_dependency_update
    minor_dependency_update = "    - Minor version updates:\n      "+empty_dependency_update
    other_dependency_update = "  - Other dependency updates:\n    "+major_dependency_update+"\n"+minor_dependency_update

    # Dependency Updates cannot be ALL empty since we already removed the entire empty section in remove_unchanged_sections()
    # First we remove the biggest strings, then the smaller ones

    # 2 biggest strings
    file = re.sub(sap_dependency_update, "", file)
    file = re.sub(other_dependency_update, "", file)
    # smaller strings
    file = re.sub(major_dependency_update, "", file)
    file = re.sub(minor_dependency_update, "", file)
    # smallest leftovers
    file = re.sub(empty_dependency_update, "", file)
    return file

file_name = "release_notes.md"

if __name__ == '__main__':
    try:
        parser = argparse.ArgumentParser(description='SAP Cloud SDK - Release Notes formatting script.')

        parser.add_argument('--version',
                            metavar='VERSION',
                            help='The version to be released.', required=True)
        args = parser.parse_args()

        if os.path.exists(file_name):
            file = read_file(file_name)
            file = remove_unchanged_sections(file, unchanged_sections)
            file = set_header(file, args.version)
            file = link_github_release(file, args.version)
            file = clean_up_dependency_updates(file)
            write_file(file_name, file)

    except KeyboardInterrupt:
        sys.exit(1)
