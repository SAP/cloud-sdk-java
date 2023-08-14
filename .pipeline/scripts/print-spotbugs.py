#!/usr/bin/env python3

import os
import sys
import glob
import yaml
import xml.etree.ElementTree as ET

findings = {
    '1': 0,
    '2': 0,
    '3': 0,
}

config = None
with open(".pipeline/config.yml", "r") as stream:
    try:
        config = yaml.safe_load(stream)
    except yaml.YAMLError as exc:
        print(exc)

threshold_low = int(config['stages']['codeCheck']['findbugs']['low'])
threshold_normal = int(config['stages']['codeCheck']['findbugs']['normal'])
threshold_high = int(config['stages']['codeCheck']['findbugs']['high'])


all_spotbugs_report_files = glob.glob('**/spotbugsXml.xml', recursive=True)
for spotbugs_report_file in all_spotbugs_report_files:
    if os.path.isfile(spotbugs_report_file):

        parsed_spotbugs_report = ET.parse(spotbugs_report_file)
        spotbugs_report = parsed_spotbugs_report.getroot()

        for bugInstance in spotbugs_report.findall('.//BugInstance'):
            findings[str(bugInstance.get('priority'))] += 1
            if bugInstance.get('priority') == '1':
                print('  - Bug Type:', bugInstance.get('type'))
                print('    Bug Category:', bugInstance.get('category'))
                print('    Bug Priority:', bugInstance.get('priority'))
                print('    Bug Message:', bugInstance.find('LongMessage').text)
                print('    Source File:', bugInstance.find('SourceLine').get('sourcefile'))
                print('    Source Line Number:', bugInstance.find('SourceLine').get('start'))
                print()

if 'GITHUB_STEP_SUMMARY' in os.environ:
    with open(os.environ["GITHUB_STEP_SUMMARY"], "a") as f:
        print('## Spotbugs result', file=f)
        print('| Category | Actual Findings | Allowed Findings |', file=f)
        print('| -------- | --------------- | ---------------- |', file=f)
        print(f"| Low    | {findings['3']} | {threshold_low} |", file=f)
        print(f"| Normal | {findings['2']} | {threshold_normal} |", file=f)
        print(f"| High   | {findings['1']} | {threshold_high} |", file=f)

print('Spotbugs result:')
print(f"warnings low:    {findings['3']}, allowed is {threshold_low}")
print(f"warnings normal: {findings['2']}, allowed is {threshold_normal}")
print(f"warnings high:   {findings['1']}, allowed is {threshold_high}")

if findings['1'] > threshold_high: # or findings['2'] > threshold_normal or findings['3'] > threshold_low:
    sys.exit('Spotbugs exceeded thresholds')
