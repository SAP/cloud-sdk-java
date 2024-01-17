import os
import re
from datetime import datetime


def read_file(file_name):
    with open(file_name, 'r', encoding="utf8") as file:
        return file.read()


def write_file(file_name, data):
    with open(file_name, 'w', encoding="utf8") as file:
        file.write(data)


def remove_unchanged_sections(file_name, unchanged_sections):
    file = read_file(file_name)
    for unchanged_section in unchanged_sections:
        # if file contains unchanged_section, remove it
        file = re.sub(unchanged_section, "", file)
    write_file(file_name, file)


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

def set_date(file_name):
    file = read_file(file_name)
    # get the current date
    date = datetime.today().strftime('%B %d, %Y')
    # add the date at the end of the first line in the format Month XX, 20XX
    file = re.sub(r"^(.*)(\n)", r"\1 - " + date + r"\2", file)
    write_file(file_name, file)

file_name = "release_notes.md"
if os.path.exists(file_name):
    remove_unchanged_sections(file_name, unchanged_sections)
    set_date(file_name)
