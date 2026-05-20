# PinFlow

**PinFlow** is a lightweight, animated OTP and PIN input library for Jetpack Compose. Built with Material 3, smart paste handling, secure PIN mode, and smooth interaction states — add polished verification flows in minutes.

## Why PinFlow?

- **One hidden field** — single `BasicTextField` under the hood for reliable keyboard, paste, and accessibility
- **Motion-first** — pick `Bounce`, `Glow`, `ShakeOnError`, and `Slide` animations per screen
- **Modes** — `Boxes`, `Underline`, `Circle`, `SingleField`, and `SecurePin`
- **Material 3 defaults** — `PinFlowDefaults.colors()` and `dimensions()` match your theme

## Modules

| Module   | Role                          |
|----------|-------------------------------|
| `pinflow` | Publishable Android library  |
| `sample`  | Demo app                     |

## Quick start

```kotlin
implementation(project(":pinflow"))
// or, when published: implementation("com.pinflow:pinflow-compose:1.0.0")
```

```kotlin
var code by remember { mutableStateOf("") }

PinFlow(
    value = code,
    onValueChange = { code = it },
    length = 6,
    mode = PinFlowMode.Boxes,
    isSuccess = PinFlowValidator.isComplete(code, 6),
    onComplete = { verifyOnServer(it) },
)
```

### Secure PIN

```kotlin
PinFlow(
    value = pin,
    onValueChange = { pin = it },
    mode = PinFlowMode.SecurePin,
    revealLastDigit = true,
)
```

### Custom colors & animations

```kotlin
PinFlow(
    value = otp,
    onValueChange = { otp = it },
    colors = PinFlowDefaults.colors(),
    dimensions = PinFlowDefaults.dimensions(cellWidth = 52.dp, spacing = 10.dp),
    animations = setOf(PinFlowAnimation.Slide, PinFlowAnimation.Glow),
)
```

### Validation helpers

```kotlin
PinFlowValidator.isComplete(otp, length = 6)
PinFlowValidator.isNumeric(otp)
PinFlowValidator.hasRepeatedDigits(otp)
```

## Run the sample

```bash
./gradlew :sample:installDebug
```

## Tech stack

- Kotlin · Jetpack Compose · Material 3 · Gradle Kotlin DSL
- minSdk **23** (library) · sample minSdk 26

## License

Add your license before publishing.
