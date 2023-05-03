# 2. Re-start numbering of ADRs

Date: 2023-05-02

## Status

Accepted

## Context

This project started not as an open source project.
The existing internal repo already made use of ADR.

## Decision

The open source repo starts numbering ADR with `0001`.

## Consequences

- ADR IDs are not unique across the internal and the public repo
- ADR numbering in the public repo is simpler compared to starting numbering with an offset
  - Starting at an offset of for example `100` might lead to confusion and out of order numbering when new ADRs are created
