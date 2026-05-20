# Contributing to PinFlow

Thanks for your interest in improving PinFlow.

## Development setup

1. Clone the repository.
2. Open the project in Android Studio (JDK 17+).
3. Run the sample app: `./gradlew :sample:installDebug`
4. Run library tests: `./gradlew :pinflow:testDebugUnitTest`

## Pull requests

- Target the `master` branch.
- Keep changes focused; one feature or fix per PR.
- Ensure CI passes (`CI` workflow: tests, assemble, Dokka).
- Update `README.md` if you change public API or behavior.
- Add or update unit tests in `:pinflow` when changing logic.

## Code style

- Kotlin official style (`kotlin.code.style=official` in `gradle.properties`).
- Match existing Compose and Material 3 patterns in `pinflow/`.
- Avoid drive-by refactors unrelated to your change.

## Reporting issues

Use GitHub Issues and include:

- Library version or commit SHA
- Android / Compose versions
- Minimal reproduction steps
- Expected vs actual behavior

## Releases

Maintainers only. See [PUBLISHING.md](PUBLISHING.md).
