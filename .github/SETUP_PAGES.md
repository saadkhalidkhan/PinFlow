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

## Why PinFlow needed this (vs [ComposeGlassKitTheme](https://github.com/saadkhalidkhan/ComposeGlassKitTheme))

| ComposeGlassKitTheme | PinFlow (this repo) |
|----------------------|---------------------|
| Docs are **static** Markdown in `docs/` (README links only) | Optional **Dokka HTML** on GitHub Pages |
| CI = tests + build only (`android-ci.yml`) | CI also tried Dokka + Pages deploy |
| No `deploy-pages` / Pages API | First-time Pages setup was required |

ComposeGlassKit did not hit `404` / `deploy-pages` errors because it never automated hosted API docs. PinFlow’s `gh-pages` workflow matches that simpler model once Pages points at the branch.

## Optional

- **CI** and **Maven Central / JitPack** do not depend on Pages.
- To skip docs deploys, disable or ignore the **Docs** workflow.
