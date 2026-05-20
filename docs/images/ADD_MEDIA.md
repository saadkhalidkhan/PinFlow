# Media assets

| File | Description |
|------|-------------|
| `hero.gif` | Screen recording of the sample app (`demo.webm` → GIF) |
| `preview1.png` | Boxes + smart paste demo |
| `preview2.png` | Underline + shake demo |
| `preview3.png` | Alphanumeric (6-digit) demo |

To refresh assets, replace files here and keep the same filenames so the README links stay valid.

### Regenerate `hero.gif` from a new recording

```bash
ffmpeg -i demo.webm -vf "fps=10,scale=400:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" -loop 0 docs/images/hero.gif
```
