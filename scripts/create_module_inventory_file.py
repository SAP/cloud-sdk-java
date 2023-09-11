import argparse
import json
from pathlib import Path
from typing import List, Dict, Optional, Any

from common._maven_module.maven_module_reader import MavenModuleReader, ExceptionCollection
from common._maven_module.maven_module_reader_configuration import MavenModuleReaderConfiguration
from common.maven_module import MavenModule


def create_module_inventory_file(sdk_root: Path,
                                 output_file: Path,
                                 reader_config_file: Optional[Path] = None,
                                 verbose: bool = False) -> List[MavenModule]:
    reader_config: Optional[MavenModuleReaderConfiguration] = None
    if reader_config_file is not None:
        reader_config = MavenModuleReaderConfiguration.from_dictionary(json.load(reader_config_file.open("r")))

        if verbose and len(reader_config.ignored_modules) > 0:
            ignored_modules: str = "\n".join(m.identifier for m in reader_config.ignored_modules)
            print(f"Following modules will be ignored:\n{ignored_modules}")

        if verbose and len(reader_config.ignored_poms) > 0:
            ignored_poms: str = "\n".join(p.path for p in reader_config.ignored_poms)
            print(f"Following pom files will be ignored:\n{ignored_poms}")

    try:
        modules: List[MavenModule] = MavenModuleReader.read_recursively(sdk_root, reader_config, verbose)

    except ExceptionCollection as exception_collection:
        if verbose:
            print("===================================================================================================")
        print("Unable to generate the latest module inventory due to following errors:")
        print(exception_collection)
        exit(1)
        return []

    sorted_modules: List[MavenModule] = sorted(modules, key=lambda m: m.identifier)
    serializable_modules: List[Dict[str, str]] = [module.to_dictionary() for module in sorted_modules]
    json.dump(serializable_modules, output_file.open("w+"), indent=2)

    return sorted_modules


def main() -> None:
    parser: argparse.ArgumentParser = argparse.ArgumentParser(
        description="Generates a file containing the module configuration.")
    parser.add_argument("--sdk-root-directory",
                        type=Path,
                        help="Path of the SDK root directory.",
                        required=True)

    parser.add_argument("--output-file",
                        type=Path,
                        help="Path to where to store the module inventory file.",
                        required=True)

    parser.add_argument("--script-config",
                        type=Path,
                        help="Path to the configuration file of this script.",
                        required=False,
                        default=None)

    parser.add_argument("--verbose",
                        action="store_true",
                        help="Enable verbose output.",
                        required=False,
                        default=False)
    args: Any = parser.parse_args()

    create_module_inventory_file(args.sdk_root_directory, args.output_file, args.script_config, args.verbose)


if __name__ == "__main__":
    main()