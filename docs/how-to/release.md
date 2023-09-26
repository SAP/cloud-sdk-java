# Release the SAP Cloud SDK for Java to Maven Central

- In GitHub, go to the [Prepare Release](https://github.com/SAP/cloud-sdk-java/actions/workflows/prepare_release.yml) Workflow.
- Click **Run workflow** drop-down.
- Optional: Enter the desired version in the **Release version** field. If not provided, the current snapshot version will be used.

When the workflow runs successfully, it will have created a `RELEASE-X.Y.Z` branch which contains two commits.
One is the version change, and the other is the next snapshot version change.
A PR will have been opened for the release branch.

**Follow the steps outlined in the PR description to complete the release**.

The workflow also creates a GitHub Release Draft.
As outlined in the PR description, publishing the Draft Release triggers the final workflow `Perform Release`.
This grabs the assets created during `Prepare Release` and attached to the Draft Release and publishes it on Maven Central.
Note that the release PR needs to be approved (after checking the diff), but there is no need to merge it.
