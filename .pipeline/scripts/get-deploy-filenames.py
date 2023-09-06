import argparse
import glob
import os



def write_file_paths_to_output(path):
    all_jar_files = glob.glob(path + "/**/*.jar", recursive=True)
    print(all_jar_files[0])

    # github_output = os.environ.get("GITHUB_OUTPUT")
    # with open(github_output, "a") as f:
    #     f.write(f"EXCLUDES={excludes}\n")
    #     f.write(f"PREFIXED_EXCLUDES={prefixed_excludes}\n")

def main():
    parser: argparse.ArgumentParser = argparse.ArgumentParser(
        description="Returns the first filepath of files to deploy and the rest as a comma separated list of filepaths.")

    parser.add_argument("--path",
                        help="Path where to look for files to deploy.",
                        required=True)

    args = parser.parse_args()

    write_file_paths_to_output(args.path)

if __name__ == '__main__':
    main()
