#!/usr/bin/env bash
set -e

python -m venv .venv
source ./.venv/bin/activate
pip install jacoco-badge-generator

python3 -m jacoco_badge_generator --jacoco-csv-file $(find . -name jacoco.csv | tr "\n" " ")
