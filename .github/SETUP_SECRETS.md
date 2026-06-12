# GitHub Actions secrets (optional — for CI releases)

Configure these so the **Release** workflow can publish to Maven Central when you push a `v*` tag, without running Gradle on your machine.

**Path:** Repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

| Secret name | What to put | How to get it |
|-------------|-------------|----------------|
| `MAVEN_CENTRAL_USERNAME` | Central Portal username / token user | [Central Portal](https://central.sonatype.com/) → Account → Generate user token |
| `MAVEN_CENTRAL_PASSWORD` | Token password | Shown once when you create the token |
| `SIGNING_IN_MEMORY_KEY` | Base64-encoded GPG **private** key | See below |
| `SIGNING_IN_MEMORY_KEY_PASSWORD` | GPG key passphrase | The password you set when creating the key |

## Export signing key for CI (one-time)

Replace `YOUR_KEY_ID` with your short key id (same as `signing.keyId` in `~/.gradle/gradle.properties`).

### Option A — Armored key directly (recommended)

Gradle accepts the raw armored private key. Fewer encoding mistakes than base64.

**PowerShell:**

```powershell
gpg --armor --export-secret-keys YOUR_KEY_ID
```

Copy **everything** printed, including:

```
-----BEGIN PGP PRIVATE KEY BLOCK-----
...
-----END PGP PRIVATE KEY BLOCK-----
```

Paste into `SIGNING_IN_MEMORY_KEY` (GitHub allows multiline secrets).

**Git Bash / Linux:**

```bash
gpg --armor --export-secret-keys YOUR_KEY_ID
```

### Option B — Base64-encoded armored key

**Git Bash / Linux:**

```bash
gpg --armor --export-secret-keys YOUR_KEY_ID | base64 -w0
```

**PowerShell:**

```powershell
$keyId = "YOUR_KEY_ID"
$armored = (gpg --armor --export-secret-keys $keyId 2>$null) -join "`n"
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($armored.Trim()))
```

Paste the **single-line** base64 output into `SIGNING_IN_MEMORY_KEY`.

Do **not** paste `signing.keyId` or `signing.password` into `SIGNING_IN_MEMORY_KEY`.

## Verify secrets (after saving)

1. Bump `PINFLOW_VERSION_NAME` in `gradle.properties` (e.g. `1.0.1`).
2. Commit and push, then create and push a tag:
   ```bash
   git tag v1.0.1
   git push origin v1.0.1
   ```
3. Open **Actions** → **Release** and confirm the job succeeds.
4. Check [Central Portal deployments](https://central.sonatype.com/publishing/deployments).

## Local vs CI signing

| Where you publish | Signing method |
|-------------------|----------------|
| **Your PC** | `signing.keyId` + `signing.password` + `signing.useGpgCmd=true` in `~/.gradle/gradle.properties` |
| **GitHub Actions** | All four repository secrets above → in-memory PGP keys (Release workflow sets `signing.useGpgCmd=false`) |

Do **not** commit real credentials. Use [gradle.properties.example](../gradle.properties.example) as a template.

## Troubleshooting CI: `gpg: no default secret key`

This means the **Release** workflow fell back to `gpg` instead of in-memory signing. Common causes:

1. **Secrets not configured** — `gh secret list` is empty. Add all four secrets from the table above.
2. **`signing.useGpgCmd=true` in project `gradle.properties`** — removed from the repo; keep it only in local `~/.gradle/gradle.properties`.
3. **Wrong key format** — CI error `Could not read PGP secret key` / `tag 0xffffffff` means `SIGNING_IN_MEMORY_KEY` is malformed. Re-export with **Option A** (armored key) above and update the secret. Do not paste `signing.keyId` or the passphrase here.

After adding secrets, re-run the failed workflow: **Actions → Release → Re-run all jobs**, or push a new tag.

## Re-run publish without a new release

```bash
gh workflow run release --ref v1.1.0
```

Or from GitHub: **Actions → Release → Run workflow** and select the tag branch.
