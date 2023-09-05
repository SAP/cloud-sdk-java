import requests
import os

payload = {"text":f"Github Action Workflow {os.getenv('WORKFLOW')} failed on branch {os.getenv('BRANCH_NAME')}: {os.getenv('WORKFLOW_RUN_URL')}"}
res = requests.post(os.getenv('SLACK_WEBHOOK'), json=payload)
