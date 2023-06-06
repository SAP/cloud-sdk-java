import os
import re
import subprocess

def determine_versions():
    current_snapshot = subprocess.check_output(
        "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
        shell=True).decode("utf-8").strip()

    input_version = os.environ.get("INPUT_VERSION")
    if not input_version:
        input_version = re.sub(r'-SNAPSHOT', '', current_snapshot)

    version_parts = [int(x) for x in input_version.split('.')]
    new_snapshot_version = f"{version_parts[0]}.{version_parts[1]}.{version_parts[2] + 1}-SNAPSHOT"

    github_output = os.environ.get("GITHUB_OUTPUT")
    with open(github_output, "a") as f:
        f.write(f"RELEASE_VERSION={input_version}\n")
        f.write(f"CURRENT_SNAPSHOT={current_snapshot}\n")
        f.write(f"NEW_SNAPSHOT={new_snapshot_version}\n")

if __name__ == '__main__':
    determine_versions()
