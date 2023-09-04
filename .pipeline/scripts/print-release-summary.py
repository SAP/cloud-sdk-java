#!/usr/bin/env python3

import argparse
import os

def write_release_summary(commit_url, release_url):
    if "GITHUB_STEP_SUMMARY" in os.environ:
        with open(os.environ["GITHUB_STEP_SUMMARY"], "a") as f:
            print("## Release Summary", file=f)
            print("### ToDos", file=f)
            print(f"- [ ] Check diff of [version commit]({commit_url}).", file=f)
            print(f"- [ ] Check [release notes]({release_url}).", file=f)


    print(f"Commit URL: {commit_url}")
    print(f"Release URL: {release_url}")

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

    write_release_summary(args.commit_url, args.release_url)

if __name__ == '__main__':
    main()
