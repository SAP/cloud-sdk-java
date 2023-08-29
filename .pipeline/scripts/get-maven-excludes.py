import os
import json
import argparse

def write_maven_excludes_github_output(filter_key, filter_value):
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        filtered_inventory = filter(lambda x: x[filter_key] == filter_value, module_inventory)
        artifact_ids = list(map(lambda x: x["artifactId"], filtered_inventory))
        artifact_ids_for_maven_excludes = list(map(lambda x: "!:" + x, artifact_ids))

        excludes = ','.join(artifact_ids)
        print("Excludes: ",excludes)
        prefixed_excludes = ','.join(artifact_ids_for_maven_excludes)
        print("Prefixed excludes: ", prefixed_excludes)

        github_output = os.environ.get("GITHUB_OUTPUT")
        with open(github_output, "a") as f:
            f.write(f"EXCLUDES={excludes}\n")
            f.write(f"PREFIXED_EXCLUDES={prefixed_excludes}\n")


def main():
    parser: argparse.ArgumentParser = argparse.ArgumentParser(
        description="Filters the module inventory and returns Maven excludes.")
    parser.add_argument("--filter-key",
                        help="Key to filter the inventory for.",
                        required=True)

    parser.add_argument("--filter-value",
                        help="Value to filter the inventory for.",
                        required=True)
    args = parser.parse_args()

    write_maven_excludes_github_output(args.filter_key, args.filter_value)

if __name__ == '__main__':
    main()
