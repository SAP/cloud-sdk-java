#!/usr/bin/env python3

import argparse

def print_pr_body(commit_url, release_url):
    print(f"# ToDos\n\n"
          f"Perform these steps in order:\n\n"
          f"- [ ] Check diff of [version update commit]({commit_url})\n"
          f"- [ ] Approve this PR if appropriate\n"
          f"- [ ] Check release notes, **edit** the [release]({release_url}) and **Publish** it (triggers workflow)\n\n"
          f"The `perform_release` workflow will automatically merge this PR once the release is published."
          )

def main():
    parser: argparse.ArgumentParser = argparse.ArgumentParser(
        description="Prints release ToDos as Github Actions step summary.")
    parser.add_argument("--commit-url",
                        help="Commit URL of the release commit.",
                        required=True)
    parser.add_argument("--release-url",
                        help="URL to the release notes.",
                        required=True)
    args = parser.parse_args()

    print_pr_body(args.commit_url, args.release_url)

if __name__ == '__main__':
    main()
