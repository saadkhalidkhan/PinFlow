# GitHub Pages setup (one-time)

The **Docs** workflow publishes Dokka HTML to the **`gh-pages`** branch. You only need to point GitHub Pages at that branch once.

## Step 1 — Run the Docs workflow

1. Push to `master`, **or**
2. **Actions → Docs → Run workflow**

Wait until the run is **green**. That creates/updates the `gh-pages` branch.

## Step 2 — Enable Pages (repo admin)

1. Open **https://github.com/saadkhalidkhan/PinFlow/settings/pages**
2. **Build and deployment → Source:** **Deploy from a branch**
3. **Branch:** `gh-pages` → folder **`/ (root)`**
4. Click **Save**

After 1–2 minutes, API docs should be live at:

**https://saadkhalidkhan.github.io/PinFlow/**

## Workflow permissions

If the workflow fails with permission errors:

**Settings → Actions → General → Workflow permissions** → select **Read and write permissions** → Save.

## Why not “GitHub Actions” as the Pages source?

The native `deploy-pages` action returned `404 Not Found` because Pages was not fully registered for this repo. Publishing to **`gh-pages`** avoids that API and is a common pattern for library docs.

## Optional

- **CI** and **Maven Central / JitPack** do not depend on Pages.
- To skip docs deploys, disable or ignore the **Docs** workflow.
