# View CodeQL Scan Results

CodeQL is added as a check to each pull request and new findings lead to a failure of this check.

Note, this check is added automatically by Github if CodeQL has been executed to analyze the CodeQL scan results.
The CodeQL step in the Github actions workflow remains marked as a success even if there were any findings.

![PR Check](../images/code-scanning-results-pr.png)

The CodeQL scan results for PRs are added as PR comments.
CodeQL highlights the lines of code that triggered the result and offers the possibility to dismiss the findings right away, e.g., by marking them as a false positive.

![PR Comment](../images/code-ql-pr-comment.png)

To view the CodeQL scan results for all branches where CodeQL has been executed check the [`Code Scanning`](https://github.com/SAP/cloud-sdk-java/security/code-scanning) section of the `Security` tab of the repository.

![Code Scanning Tab](../images/code-scanning-tab.png)

More information can be found in the [CodeQL documentation](https://docs.github.com/en/code-security/code-scanning/managing-code-scanning-alerts/about-code-scanning-alerts).
