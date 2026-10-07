# ParkWatch — Agent Instructions

## Project Overview
Android Wear OS app (minSdk 30, targetSdk 37) using Jetpack Compose for Wear OS. Single module (`:app`). Kotlin 2.2.10, AGP 9.4.1, Compose BOM 2024.09.00.

## Key Commands
```bash
./gradlew build                    # Full build + test
./gradlew assembleDebug            # Debug APK
./gradlew lint                     # Lint (all variants)
./gradlew testDebugUnitTest        # Unit tests only
./gradlew connectedDebugAndroidTest # Instrumentation tests (device required)
./gradlew clean                    # Clean build dir
```

## Architecture Notes
- **Offline-first**: Core features (save/view/edit/delete parking location) must work without internet
- **Dark-first design**: Custom color tokens in `design-system/parkwatch/MASTER.md` (ParkBackground, ParkPrimary, ParkSecondary, ParkError, etc.)
- **Wear OS components only**: Use `androidx.wear.compose.material3` / `androidx.wear.compose.foundation` — avoid mobile Material 3 components
- **Round screen first**: Design for 384×384 Wear OS Small Round; keep critical content away from screen edges
- **Progressive disclosure**: One primary action per screen; shallow nav (max 2–3 levels)

## Source Structure
```
app/src/main/java/com/david/parkwatch/
├── presentation/
│   ├── MainActivity.kt          # Entry point, WearApp composable
│   └── theme/
│       └── Theme.kt             # ParkWatchTheme wraps MaterialTheme
```

## Build Configuration
- Configuration cache **enabled** (`org.gradle.configuration-cache=true` in gradle.properties)
- Version catalog in `gradle/libs.versions.toml`
- Lint config: `app/lint.xml` (ignores IconLocation for tile_preview images)
- `useLibrary("wear-sdk")` in app build.gradle.kts

## Design System (MUST FOLLOW)
Reference: `design-system/parkwatch/MASTER.md`
- Colors: Use design tokens only (no arbitrary hex values)
- Typography: `MaterialTheme.typography` (numeralLarge, titleMedium, bodyLarge, etc.)
- Spacing: dp tokens (SpaceXs=4, SpaceSm=8, SpaceMd=12, SpaceLg=16, SpaceXl=24)
- Touch targets: 48×48 dp minimum (40×40 exceptional)
- Icons: Material Symbols only — no emojis
- Components: Prefer shared ParkWatch components when they exist. Planned components include ParkPrimaryButton, ParkSecondaryButton, ParkIconButton, ParkLocationCard, ParkSelector, and ParkVoiceAction. Do not assume a planned component already exists; inspect the codebase first.
## Testing
- No unit/instrumentation tests currently exist
- Run `./gradlew testDebugUnitTest` for unit tests
- Run `./gradlew connectedDebugAndroidTest` for device tests

## Common Gotchas
- `.opencode/` is local development tooling, not application source code. Do not modify it unless explicitly requested.
- **Do not use mobile Material 3** — Wear equivalents exist in `androidx.wear.compose.material3`
- **Configuration cache** may need invalidation on build script changes: `./gradlew --no-configuration-cache build`
- **GPS/voice are optional enhancements** — manual entry must always work
- **Delete requires explicit confirmation** using ParkError color
- **Do not use CSS/web concepts** (px, rem, hover, cursor-pointer, breakpoints, shadows)
- ## Git Rules
- The developer controls all Git operations.
- Do not run `git add`, `git commit`, `git push`, `git pull`, branch creation/switching, merges, rebases, or resets unless explicitly requested.
- Do not automatically stage files after making changes.
- `git status` and `git diff` may be used only for inspection when useful.