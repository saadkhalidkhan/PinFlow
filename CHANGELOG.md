# Changelog

All notable changes to PinFlow are documented here.

## [1.1.1] — 2026-06-12

### Fixed

- GitHub Actions Release workflow: in-memory GPG signing for Maven Central CI publish
- Document armored-key export for `SIGNING_IN_MEMORY_KEY` secret

No API changes — same library as 1.1.0.

## [1.1.0] — 2026-05-20

### Added

- **Gradient borders** — `borderBrush` on `PinFlow` (boxes, underline, single-field)
- **Cursor customization** — `cursorColor`, `cursorWidth`, blinking caret on the active cell
- **Custom cell content** — `cellContent = { digit, state -> … }`; `PinFlowCellState` is now public
- **Haptic feedback** — `hapticEnabled = true` on each new digit
- **Preset themes** — `PinFlowThemes.Default()`, `Glass()`, `Neon()`, `Minimal()` (light/dark adaptive)
- **`style` parameter** — pass `PinFlowStyle` from themes in one argument
- Sample app demos for all MVP 2 features

### Marketing

Create beautiful OTP experiences without building custom components.

## [1.0.0] — 2026-05-20

### Added

- Initial release: `PinFlow` composable with Boxes, Underline, Circle, SingleField, SecurePin modes
- Animations: Bounce, Glow, ShakeOnError, Slide
- `PinFlowValidator`, `onComplete`, `isSuccess`
- Maven Central and JitPack publishing
