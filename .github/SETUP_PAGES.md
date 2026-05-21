# GitHub Pages setup (one-time, manual)

The **Docs** workflow cannot enable Pages for you (`Resource not accessible by integration`). A repo **admin** must turn it on once.

## Steps

1. Open **https://github.com/saadkhalidkhan/PinFlow/settings/pages**
2. Under **Build and deployment** → **Source**, choose **GitHub Actions** (not “Deploy from a branch”).
3. Click **Save**.
4. Re-run the workflow: **Actions → Docs → Run workflow** (or push to `master`).

After a green run, API docs are at:

**https://saadkhalidkhan.github.io/PinFlow/**

## If it still fails

- Confirm you have **Admin** on the repository.
- **Settings → Actions → General → Workflow permissions** → **Read and write permissions** (recommended for Pages deploy).
- Check **Settings → Environments → github-pages** exists (created automatically after first successful deploy).

## Optional

You can disable the Docs workflow until Pages is configured; **CI** and publishing to Maven Central / JitPack do not depend on it.
