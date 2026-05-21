# Publishing PinFlow

PinFlow **1.0.0** is live on Maven Central. This guide covers ongoing releases, **JitPack**, **Maven Central**, **GitHub Actions**, and verification.

## Coordinates (current)

| Channel | Dependency |
|---------|------------|
| **Maven Central** | `io.github.saadkhalidkhan:pinflow-compose:1.0.0` |
| **JitPack** | `com.github.saadkhalidkhan:PinFlow:1.0.0` |

For new versions: bump `PINFLOW_VERSION_NAME` in `gradle.properties` and tag `vX.Y.Z`. See [RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md).

---

## Quick links

| Resource | URL |
|----------|-----|
| Maven Central artifact | https://central.sonatype.com/artifact/io.github.saadkhalidkhan/pinflow-compose |
| Maven repo browser | https://repo1.maven.org/maven2/io/github/saadkhalidkhan/pinflow-compose/ |
| JitPack builds | https://jitpack.io/#saadkhalidkhan/PinFlow |
| GitHub Releases | https://github.com/saadkhalidkhan/PinFlow/releases |
| API docs (Pages) | https://saadkhalidkhan.github.io/PinFlow/ |

---

## Consumers — installation

### Maven Central

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.saadkhalidkhan:pinflow-compose:1.0.0")
}
```

Sync Gradle after changing the version. First-time availability on Central can take **10–30 minutes** after publish.

### JitPack

**v1.0.0** — [green build](https://jitpack.io/#saadkhalidkhan/PinFlow/v1.0.0)

**Step 1.** Add the JitPack repository in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2.** Add the dependency:

```kotlin
dependencies {
    implementation("com.github.saadkhalidkhan:PinFlow:1.0.0")
}
```

**Important:** Replace `1.0.0` with your tag (e.g. `1.0.1`). JitPack only builds **tagged** commits.

---

## JitPack — first time & each release

1. Ensure the repo is public: [saadkhalidkhan/PinFlow](https://github.com/saadkhalidkhan/PinFlow).
2. Create and push a tag (version must match `PINFLOW_VERSION_NAME`):
   ```bash
   git tag v1.0.1
   git push origin v1.0.1
   ```
3. Open **https://jitpack.io/#saadkhalidkhan/PinFlow/v1.0.1** and wait for a green build.
4. Click **Get it** to see the Gradle line.

`jitpack.yml` configures JDK 17, `assembleRelease`, and publish to Maven Local (signing disabled for JitPack).

### JitPack troubleshooting

| Problem | Fix |
|---------|-----|
| Build failed | Open **Log** on JitPack; run the same command locally: `./gradlew :pinflow:assembleRelease :pinflow:publishMavenPublicationToMavenLocal -Psigning.required=false` |
| Old version cached | Use **Look up** with the exact tag, or `1.0.1` not `v1.0.1` in the dependency string |
| Dependency not found | Add `maven { url = uri("https://jitpack.io") }` in **settings**, not project `build.gradle` |

---

## Maven Central — Sonatype setup (one-time)

### 1. Register namespace

1. Account at [Central Portal](https://central.sonatype.com/).
2. Claim namespace **`io.github.saadkhalidkhan`** (GitHub verification).
3. Wait for approval before the first upload.

### 2. GPG signing key

```bash
gpg --full-generate-key
gpg --keyid-format short --list-secret-keys
```

Publish the public key to a keyserver (Central requirement):

```bash
gpg --keyid-format short --export YOUR_KEY_ID | gpg --import
# Upload via https://keys.openpgp.org/ or keyserver.ubuntu.com
```

### 3. Local credentials (recommended location)

Copy [gradle.properties.example](gradle.properties.example) and set values in **`~/.gradle/gradle.properties`** only:

```properties
mavenCentralUsername=YOUR_TOKEN_USER
mavenCentralPassword=YOUR_TOKEN_PASSWORD
signing.keyId=YOUR_KEY_ID
signing.password=YOUR_KEY_PASSPHRASE
```

Project `gradle.properties` already sets `signing.useGpgCmd=true` for local GPG.

### 4. Publish locally

```bash
./gradlew :pinflow:publishToMavenCentral
```

Success looks like: `Deployment is being published to Maven Central` → **BUILD SUCCESSFUL**.

Dry run (no upload):

```bash
./gradlew :pinflow:publishToMavenLocal -Psigning.required=false
```

Artifacts: `~/.m2/repository/io/github/saadkhalidkhan/pinflow-compose/`

---

## GitHub Actions — optional CI releases

Automate Maven Central uploads on every `v*` tag.

### Step 1 — Add secrets

Follow [.github/SETUP_SECRETS.md](.github/SETUP_SECRETS.md) (4 secrets).

### Step 2 — Enable workflows

- **Actions** tab must be enabled for the repo.
- Workflow file: [.github/workflows/release.yml](.github/workflows/release.yml)

### Step 3 — Release via tag

```bash
# After bumping PINFLOW_VERSION_NAME and committing:
git tag v1.0.1
git push origin master
git push origin v1.0.1
```

The **Release** workflow runs:

```bash
./gradlew :pinflow:testDebugUnitTest :pinflow:publishToMavenCentral
```

You can also run it manually: **Actions → Release → Run workflow** (if enabled).

### Step 4 — GitHub Pages (API docs, optional)

Requires **Dokka 2.2.0+** (AGP 9 compatibility) — already configured in this repo.

1. **Settings → Pages → Build and deployment → Source:** **GitHub Actions**  
   (The Docs workflow also sets `enablement: true` to request Pages automatically.)
2. Push to `master` — **Docs** workflow runs `:pinflow:dokkaGeneratePublicationHtml` and deploys.

Public URL: **https://saadkhalidkhan.github.io/PinFlow/**

If you see `Get Pages site failed` / `Not Found`, enable Pages manually in repo **Settings → Pages** (needs admin access).

### Other workflows

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| [ci.yml](.github/workflows/ci.yml) | Push / PR | Tests, sample APK, Dokka artifact |
| [docs.yml](.github/workflows/docs.yml) | Push to `master` | Deploy Dokka to Pages |
| [release.yml](.github/workflows/release.yml) | Tag `v*` / manual | Maven Central publish |

---

## GitHub Release (optional)

Creates a visible release on GitHub (good for changelog + JitPack discovery).

```bash
gh release create v1.0.1 --title "PinFlow 1.0.1" --notes "Bug fixes and ..."
```

Or: **Releases → New release** → select tag `v1.0.1` → publish.

---

## Verify a release

### Maven Central

```bash
# Browser
https://repo1.maven.org/maven2/io/github/saadkhalidkhan/pinflow-compose/1.0.0/
```

In a test app:

```kotlin
implementation("io.github.saadkhalidkhan:pinflow-compose:1.0.0")
```

Refresh Gradle; confirm `PinFlow` imports from `com.pinflow.compose`.

### JitPack

1. Green build at https://jitpack.io/#saadkhalidkhan/PinFlow/v1.0.0  
2. Test dependency resolves in a clean project.

---

## Security

- Never commit `mavenCentralPassword`, GPG keys, or `signing.properties`.
- `.gitignore` excludes `central.properties`, `release.properties`, `signing.properties`.
- Rotate Central tokens if exposed.
- CI secrets: only in GitHub **Actions secrets**, not in the repo.

---

## Full release flow (summary)

See **[RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md)** for a printable checklist.

1. Bump `PINFLOW_VERSION_NAME` + README version lines  
2. Merge to `master`  
3. `./gradlew :pinflow:testDebugUnitTest :sample:assembleDebug`  
4. Publish Maven Central (local **or** push tag for CI)  
5. Push same tag for JitPack → verify green on jitpack.io  
6. Optional: GitHub Release + confirm Pages docs  
