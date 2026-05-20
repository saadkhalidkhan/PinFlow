# Publishing PinFlow

This guide covers **JitPack** (quick) and **Maven Central** (production), plus the GitHub Actions release workflow.

## Coordinates

| Channel | Dependency |
|---------|------------|
| **Maven Central** | `io.github.saadkhalidkhan:pinflow-compose:1.0.0` |
| **JitPack** | `com.github.saadkhalidkhan:PinFlow:1.0.0` |

Bump `PINFLOW_VERSION_NAME` in `gradle.properties` and create a Git tag `v1.0.0` for each release.

---

## JitPack (fastest)

1. Push the repo to GitHub (already at [saadkhalidkhan/PinFlow](https://github.com/saadkhalidkhan/PinFlow)).
2. Create a release tag, e.g. `v1.0.0`:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
3. Open [jitpack.io](https://jitpack.io/#saadkhalidkhan/PinFlow) and look up the tag.
4. Add to `settings.gradle.kts`:
   ```kotlin
   maven { url = uri("https://jitpack.io") }
   ```
5. In `build.gradle.kts`:
   ```kotlin
   implementation("com.github.saadkhalidkhan:PinFlow:1.0.0")
   ```

`jitpack.yml` in the repo root configures JDK 17 and publishes the `:pinflow` module to Maven Local for JitPack.

---

## Maven Central (Sonatype)

### 1. Register namespace

1. Create an account at [Central Portal](https://central.sonatype.com/).
2. Claim the namespace **`io.github.saadkhalidkhan`** (GitHub verification).
3. Wait for approval before the first upload.

### 2. GPG signing key

Generate a key (if you do not have one):

```bash
gpg --full-generate-key
gpg --keyid-format short --export-secret-keys YOUR_KEY_ID > secring.gpg
```

Base64-encode the private key for GitHub Actions:

```bash
gpg --armor --export-secret-keys YOUR_KEY_ID | base64 -w0
```

### 3. GitHub repository secrets

Add these under **Settings → Secrets and variables → Actions**:

| Secret | Description |
|--------|-------------|
| `MAVEN_CENTRAL_USERNAME` | Central Portal user token |
| `MAVEN_CENTRAL_PASSWORD` | Central Portal password / token |
| `SIGNING_IN_MEMORY_KEY` | Base64-encoded GPG private key |
| `SIGNING_IN_MEMORY_KEY_PASSWORD` | GPG key passphrase |

### 4. Release

```bash
# Update version in gradle.properties, then:
git add -A
git commit -m "Release 1.0.0"
git tag v1.0.0
git push origin master --tags
```

The **Release** workflow (`.github/workflows/release.yml`) runs:

```bash
./gradlew :pinflow:publishToMavenCentral
```

### 5. Enable GitHub Pages (API docs)

1. Repo **Settings → Pages → Build and deployment → Source**: **GitHub Actions**.
2. Push to `master` / `main` — the **Docs** workflow deploys Dokka HTML from `:pinflow:dokkaHtml`.

Docs URL: `https://saadkhalidkhan.github.io/PinFlow/`

---

## Local publish (dry run)

```bash
./gradlew :pinflow:publishToMavenLocal
```

Artifacts appear under `~/.m2/repository/io/github/saadkhalidkhan/pinflow-compose/`.

---

## Checklist before `v1.0.0`

- [ ] Replace placeholder images in `docs/images/` with real screenshots or GIFs
- [ ] Sonatype namespace `io.github.saadkhalidkhan` verified
- [ ] GitHub Actions secrets configured
- [ ] GitHub Pages enabled for Dokka
- [ ] Tag pushed and JitPack build green
