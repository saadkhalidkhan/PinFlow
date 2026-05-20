# Screenshots & GIFs

The README references preview assets in this folder. **Replace the SVG placeholders** with real captures for a polished GitHub profile.

## Recommended captures

| File | Content |
|------|---------|
| `hero.gif` | 10–15s: typing OTP, paste fill, success state (`5678`) |
| `preview-boxes.png` | Default box style |
| `preview-secure.png` | Secure PIN + reveal last digit |
| `preview-success.png` | Success + slide animation |
| `preview-single-field.png` | Single-field mode |

## How to record

1. Run `./gradlew :sample:installDebug` on a device or emulator.
2. Use **Android Studio → Logcat → Screen record**, or a tool like ScreenToGif / OBS.
3. Crop to ~360–400px width for README (keeps repo size reasonable).
4. Optimize GIFs (≤ 5 MB): [ezgif.com](https://ezgif.com/optimize) or `gifsicle -O3`.

## After adding files

Update `README.md` image paths if filenames differ, then commit:

```bash
git add docs/images/hero.gif docs/images/preview-*.png
git commit -m "docs: add README demo media"
```
