# Release checklist

Use this for every new version (`1.0.1`, `1.1.0`, …).

## Before release

- [ ] All changes merged to `master`
- [ ] `./gradlew :pinflow:testDebugUnitTest :sample:assembleDebug` passes
- [ ] README / Dokka still accurate if API changed
- [ ] Bump version in `gradle.properties`:
  ```properties
  PINFLOW_VERSION_NAME=1.0.1
  ```
- [ ] Update README install lines to the new version (Maven Central + JitPack)

## Publish to Maven Central

### Option A — Local (GPG on your machine)

1. Ensure `~/.gradle/gradle.properties` has `mavenCentralUsername`, `mavenCentralPassword`, `signing.keyId`, `signing.password` (see [gradle.properties.example](gradle.properties.example)).
2. Run:
   ```bash
   ./gradlew :pinflow:publishToMavenCentral
   ```
3. Wait for **BUILD SUCCESSFUL** and deployment validation in the log.

### Option B — GitHub Actions (recommended after secrets are set)

1. Complete [.github/SETUP_SECRETS.md](.github/SETUP_SECRETS.md).
2. Commit version bump, then:
   ```bash
   git tag v1.0.1
   git push origin master
   git push origin v1.0.1
   ```
3. Watch **Actions → Release** until green.

## Publish on JitPack

JitPack builds from the **Git tag** (not only `master`).

1. Push the tag (same as above): `git push origin v1.0.1`
2. Open **https://jitpack.io/#saadkhalidkhan/PinFlow/v1.0.1**
3. Wait until status is **green** (Get it).
4. If build fails, open **Log** and fix `jitpack.yml` or Gradle, then push a new tag (e.g. `v1.0.1-fix`).

## GitHub Release (optional but professional)

```bash
gh release create v1.0.1 --title "PinFlow 1.0.1" --notes-file RELEASE_NOTES.md
```

Or create manually: **Releases → Draft a new release** → choose tag → publish.

## After release

- [ ] Maven artifact visible (may take 10–30 min):  
  https://repo1.maven.org/maven2/io/github/saadkhalidkhan/pinflow-compose/
- [ ] JitPack badge / lookup works for the new tag
- [ ] Test in a fresh app with Maven Central dependency
- [ ] Test in a fresh app with JitPack dependency (if you ship both)
- [ ] GitHub Pages docs updated (automatic on push to `master` if Pages is enabled)

## Version tag rule

| `gradle.properties` | Git tag   |
|---------------------|-----------|
| `PINFLOW_VERSION_NAME=1.0.1` | `v1.0.1` |

Tag must start with `v` + match the version string.
