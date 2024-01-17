import os
import re


def get_data_from_file(file_name):
    with open(file_name, 'r', encoding="utf8") as file:
        return file.read()


def write_data_to_file(file_name, data):
    with open(file_name, 'w', encoding="utf8") as file:
        file.write(data)


def remove_unchanged_sections(file_name, unchanged_sections):
    data = get_data_from_file(file_name)
    for unchanged_section in unchanged_sections:
        # if data contains unchanged_section, remove it
        data = re.sub(unchanged_section, "", data)
        # print if unchanged_section was found
        print("Searching for unchanged section: " + unchanged_section)
        if re.search(unchanged_section, data):
            print("Found unchanged section: " + unchanged_section)
    write_data_to_file(file_name, data)


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

- 

""",
"""### 🐛 Fixed Issues

- 

"""]
file_name = "release_notes.md"
if os.path.exists(file_name):
    remove_unchanged_sections(file_name, unchanged_sections)
