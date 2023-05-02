# 3. Use GitHub Actions for pipeline

Date: 2023-05-02

## Status

Accepted

## Context

The SAP Cloud SDK for Java used Jenkins for CI so far.
The SAP Cloud SDK for JavaScript already uses GitHub Actions for CI.

## Decision

The SAP Cloud SDK for Java will use GitHub Actions for CI in the future.

## Consequences

- We can make use of GitHub's hosted runners, freeing us from the burden of maintaining our own server
- We need to rewrite our CI scripts
  - While going open source, many aspects of the existing pipeline are obsolete, so this is not that bad
  - We can use this to re-think and simplify our CI setup
