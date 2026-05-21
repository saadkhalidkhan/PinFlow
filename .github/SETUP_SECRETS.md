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

Replace `YOUR_KEY_ID` with your short key id (e.g. from `gpg --list-secret-keys --keyid-format short`).

**Linux / macOS / Git Bash:**

```bash
gpg --armor --export-secret-keys YOUR_KEY_ID | base64 -w0
```

**PowerShell:**

```powershell
$bytes = gpg --armor --export-secret-keys YOUR_KEY_ID | Out-String
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($bytes))
```

Paste the **entire** base64 string into the `SIGNING_IN_MEMORY_KEY` secret.

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
| **Your PC** | `signing.keyId` + `signing.password` in `~/.gradle/gradle.properties` and `signing.useGpgCmd=true` in project `gradle.properties` |
| **GitHub Actions** | `SIGNING_IN_MEMORY_*` secrets → `signingInMemoryKey` Gradle properties (set by the Release workflow) |

Do **not** commit real credentials. Use [gradle.properties.example](../gradle.properties.example) as a template.
