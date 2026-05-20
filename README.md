# PinFlow

[![CI](https://github.com/saadkhalidkhan/PinFlow/actions/workflows/ci.yml/badge.svg)](https://github.com/saadkhalidkhan/PinFlow/actions/workflows/ci.yml)
[![Docs](https://github.com/saadkhalidkhan/PinFlow/actions/workflows/docs.yml/badge.svg)](https://github.com/saadkhalidkhan/PinFlow/actions/workflows/docs.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.saadkhalidkhan/pinflow-compose?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.saadkhalidkhan/pinflow-compose)
[![JitPack](https://jitpack.io/v/saadkhalidkhan/PinFlow.svg)](https://jitpack.io/#saadkhalidkhan/PinFlow)
[![Release](https://img.shields.io/github/v/release/saadkhalidkhan/PinFlow?display_name=tag)](https://github.com/saadkhalidkhan/PinFlow/releases)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg)](https://android-arsenal.com/api?level=23)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52E3?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=android)](https://developer.android.com/jetpack/compose)

**PinFlow** is a lightweight, animated, and customizable **OTP / PIN input** library for Jetpack Compose. Built with Material 3, smart paste handling, secure PIN mode, and smooth interaction states — add polished verification flows in minutes.

<p align="center">
  <img src="docs/images/hero.svg" alt="PinFlow demo — replace with hero.gif" width="720"/>
</p>

> **Tip:** Record a short GIF from the `:sample` app and save it as `docs/images/hero.gif` for maximum impact. See [docs/images/ADD_MEDIA.md](docs/images/ADD_MEDIA.md).

---

## Features

| Feature | Description |
|---------|-------------|
| **Single hidden field** | One `BasicTextField` — reliable keyboard, paste, and a11y |
| **Smart paste** | Paste `123456` and all slots fill automatically |
| **Modes** | `Boxes`, `Underline`, `Circle`, `SingleField`, `SecurePin` |
| **Motion** | `Bounce`, `Glow`, `ShakeOnError`, `Slide` — pick per screen |
| **Secure PIN** | Masking + optional reveal-last-digit |
| **Validation** | `PinFlowValidator` helpers + `onComplete` callback |
| **Material 3** | `PinFlowDefaults.colors()` / `dimensions()` |

---

## Preview

| Boxes + paste | Secure PIN | Success + slide |
|:---:|:---:|:---:|
| ![Boxes](docs/images/preview-boxes.svg) | ![Secure](docs/images/preview-secure.svg) | ![Success](docs/images/preview-success.svg) |

---

## Installation

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

> Requires a published release and verified Sonatype namespace. See [PUBLISHING.md](PUBLISHING.md).

### JitPack

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

```kotlin
dependencies {
    implementation("com.github.saadkhalidkhan:PinFlow:1.0.0")
}
```

### Local module (development)

```kotlin
dependencies {
    implementation(project(":pinflow"))
}
```

---

## Quick start

```kotlin
import com.pinflow.compose.PinFlow
import com.pinflow.compose.PinFlowMode
import com.pinflow.compose.PinFlowValidator

var code by remember { mutableStateOf("") }

PinFlow(
    value = code,
    onValueChange = { code = it },
    length = 6,
    mode = PinFlowMode.Boxes,
    isSuccess = PinFlowValidator.isComplete(code, 6),
    onComplete = { submitted -> verifyOnServer(submitted) },
)
```

---

## Usage examples

### Secure PIN (app lock / payment)

```kotlin
PinFlow(
    value = pin,
    onValueChange = { pin = it },
    mode = PinFlowMode.SecurePin,
    revealLastDigit = true,
)
```

### Error shake

```kotlin
val isError = code == "1234"

PinFlow(
    value = code,
    onValueChange = { code = it },
    mode = PinFlowMode.Underline,
    isError = isError,
    animations = setOf(PinFlowAnimation.ShakeOnError, PinFlowAnimation.Bounce),
)
```

### Success + slide (demo code `5678`)

```kotlin
PinFlow(
    value = code,
    onValueChange = { code = it },
    isSuccess = PinFlowValidator.isComplete(code, 4) && code == "5678",
    animations = setOf(PinFlowAnimation.Slide, PinFlowAnimation.Glow),
)
```

### Custom theme & size

```kotlin
PinFlow(
    value = otp,
    onValueChange = { otp = it },
    colors = PinFlowDefaults.colors(),
    dimensions = PinFlowDefaults.dimensions(
        cellWidth = 52.dp,
        spacing = 10.dp,
        cornerRadius = 14.dp,
    ),
    animations = setOf(PinFlowAnimation.Slide, PinFlowAnimation.Glow),
)
```

### Single-field layout

```kotlin
PinFlow(
    value = code,
    onValueChange = { code = it },
    length = 6,
    mode = PinFlowMode.SingleField,
)
```

### Validation helpers

```kotlin
PinFlowValidator.isComplete(otp, length = 6)
PinFlowValidator.isNumeric(otp)
PinFlowValidator.hasRepeatedDigits(otp)
```

---

## API documentation (Dokka)

HTML API reference is generated with [Dokka](https://kotl.in/dokka) and published to GitHub Pages:

**https://saadkhalidkhan.github.io/PinFlow/**

Generate locally:

```bash
./gradlew :pinflow:dokkaGeneratePublicationHtml
# open pinflow/build/dokka/html/index.html
```

---

## Sample app

```bash
./gradlew :sample:installDebug
```

The sample demonstrates all modes, secure PIN, success/slide, single-field, and alphanumeric input.

---

## Project structure

| Module | Description |
|--------|-------------|
| `:pinflow` | Android library (`minSdk 23`) |
| `:sample` | Demo application |

---

## Publishing & CI

| Workflow | Purpose |
|----------|---------|
| [CI](.github/workflows/ci.yml) | Tests, assemble, Dokka on every push/PR |
| [Docs](.github/workflows/docs.yml) | Deploy Dokka to GitHub Pages |
| [Release](.github/workflows/release.yml) | Publish to Maven Central on `v*` tags |

See [PUBLISHING.md](PUBLISHING.md) for Sonatype, signing, and JitPack setup.

---

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

```
Copyright 2026 Saad Khalid Khan

Licensed under the Apache License, Version 2.0
```

See [LICENSE](LICENSE) for the full text.
