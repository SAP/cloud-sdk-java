# Release the SAP Cloud SDK for Java to Maven Central

- In GitHub, go to the [Prepare Release](https://github.com/SAP/cloud-sdk-java/actions/workflows/prepare_release.yml) Workflow.
- Click **Run workflow** drop-down.
- Optional: Enter the desired version in the **Release version** field. If not provided, the current snapshot version will be used.
- Optional: Enter the branch name to release from. By default, this is our `main` branch.

When the workflow runs successfully, it will have created a `RELEASE-X.Y.Z` branch.
Additionally, the workflow will create a few PRs:

1. One to update our JavaDocs in [the Documentation Repository](https://github.com/sap/cloud-sdk)
2. Another one to update our release notes, also in [the Documentation Repository](https://github.com/sap/cloud-sdk)
    * **Note** As we are splitting our release notes every 15 minor versions, some manual adjustment might be needed
3. A third one for the actual changes in [the Code Repository](https://github.com/sap/cloud-sdk-java)

Lastly, the workflow also creates a new draft release and an according tag.
All of these things will be linked in the Code PR, so that they can be found easily.

**Follow the steps outlined in the PR description _in order_**.

As the second to last step, you should approve the Code PR (after you have reviewed, adjusted, and approved the other PRs).
This will trigger the [Perform Release](https://github.com/SAP/cloud-sdk-java/actions/workflows/perform-release.yml) workflow.

This workflow will take care of merging the PRs and creates a **staging release** in [Sonatype](https://oss.sonatype.org/).
As the very last step, once the _Perform Release_ workflow has finished successfully, you need to log into _Sonatype_ and publish the staging release.

That's it!
The new version should be available for consumers within a few hours.
