# ParkWatch — Design System

> **SOURCE OF TRUTH**
>
> This file defines the global visual and interaction rules for ParkWatch.
>
> When implementing a specific screen, first check:
>
> `design-system/parkwatch/pages/[screen-name].md`
>
> If a screen-specific file exists, only its exceptions override this document.
>
> If no screen-specific file exists, strictly follow this `MASTER.md`.

---

## 1. Product

**Project:** ParkWatch  
**Platform:** Wear OS  
**UI Framework:** Jetpack Compose for Wear OS  
**Design Foundation:** Wear Compose Material 3 / Material 3 Expressive  
**Minimum SDK:** API 30  
**Primary form factor:** Round smartwatch  
**Reference test device:** Wear OS Small Round — 384×384 px

### Product Purpose

ParkWatch allows users to quickly save and retrieve the location of their parked vehicle directly from their smartwatch, without needing to take out their phone.

The experience must be optimized for short, glanceable wrist interactions.

### UX Principle

> Saving or checking the vehicle location must require the minimum possible amount of attention, reading, and interaction.

ParkWatch must NOT feel like a mobile phone application compressed into a watch screen.

---

# 2. Design Principles

## Glanceable

The most important information must be understandable within a few seconds.

Prioritize:

- Floor
- Zone
- Row
- Parking spot
- Vehicle location status
- Time since parking

Avoid long explanatory text.

---

## One Primary Action

Each screen should have one clearly recognizable primary action.

Examples:

Save location → `Save`

Saved location → `View vehicle`

Confirmation → `Done`

Avoid showing multiple actions with equal visual hierarchy.

---

## Progressive Disclosure

Show only the information required for the current step.

Example:

Floor  
→ Zone  
→ Row  
→ Optional parking spot  
→ Confirmation

Do not show a long form containing every field at once.

---

## Wrist-First Interaction

The interface should be usable:

- quickly;
- while walking;
- with one hand;
- without a keyboard whenever possible;
- with limited touch precision;
- without depending on the phone.

---

# 3. Platform Rules

Use components specifically designed for Wear OS whenever a Wear equivalent exists.

Prefer packages such as:

`androidx.wear.compose.material3`

`androidx.wear.compose.foundation`

`androidx.wear.compose.navigation`

Avoid using mobile Material 3 components for the main smartwatch interface when a Wear OS equivalent exists.

---

# 4. Color System

ParkWatch is a **dark-first application**.

A dark background reduces visual distraction and helps important information stand out clearly on a small watch display.

## Core Colors

| Token | Hex | Usage |
|---|---|---|
| `ParkBackground` | `#000000` | Main background |
| `ParkSurface` | `#15181C` | Surfaces and containers |
| `ParkSurfaceHigh` | `#23272D` | Highlighted surfaces |
| `ParkPrimary` | `#72E38C` | Primary action / saved state |
| `ParkOnPrimary` | `#071C0C` | Content displayed on Primary |
| `ParkPrimaryContainer` | `#153D20` | Active selections |
| `ParkOnPrimaryContainer` | `#B7F5C5` | Content on selected states |
| `ParkSecondary` | `#8CC8FF` | Secondary information / location |
| `ParkOnSecondary` | `#071A29` | Content displayed on Secondary |
| `ParkTextPrimary` | `#F5F7F8` | Main text |
| `ParkTextSecondary` | `#B7BDC5` | Secondary text |
| `ParkOutline` | `#454B53` | Dividers and subtle borders |
| `ParkWarning` | `#FFC857` | Warnings |
| `ParkError` | `#FF6B6B` | Errors and destructive actions |

---

## Color Semantics

### Primary

`ParkPrimary`

Use for:

- Save location
- Confirmations
- Vehicle located
- Active states
- Primary CTA

Do not use it purely as decoration.

### Secondary

`ParkSecondary`

Use for:

- GPS location
- Secondary information
- Alternative actions

### Error

`ParkError`

Use only for:

- delete actions;
- loss of information;
- real errors;
- destructive actions.

Never use red as a normal primary CTA.

---

# 5. Dynamic Color

ParkWatch v1 will use its own color palette to preserve a consistent visual identity.

Do not enable Dynamic Color initially.

Dynamic Color may be evaluated later as an optional personalization feature.

---

# 6. Typography

Do not download Google Fonts from the Internet.

Use the typography provided and optimized by Wear Material 3.

Use:

`MaterialTheme.typography`

## Hierarchy

### Hero Numeral

For critical information such as:

`P2`

Use:

`numeralLarge`

or

`numeralMedium`

depending on available space.

### Screen Title

Example:

`Your vehicle`

Use:

`titleMedium`

### Important Information

Examples:

`Zone B`

`Row 14`

Use:

`titleSmall`

or

`bodyLarge`

### Supporting Text

Example:

`Parked 42 min ago`

Use:

`bodyMedium`

### Small Metadata

Example:

`7:42 PM`

Use:

`bodySmall`

---

# 7. Text Rules

Recommended limits:

- titles: 1–2 lines;
- buttons: 1–2 lines;
- descriptions: 2–3 lines.

Do not use long paragraphs.

Important content must not depend on truncation.

Prefer:

`Floor 2`

instead of:

`The vehicle was parked on the second floor`

---

# 8. Spacing System

All layout values must use `dp`.

| Token | Value | Usage |
|---|---:|---|
| `SpaceXs` | `4.dp` | Minimum spacing |
| `SpaceSm` | `8.dp` | Icon + text spacing |
| `SpaceMd` | `12.dp` | Related components |
| `SpaceLg` | `16.dp` | Group separation |
| `SpaceXl` | `24.dp` | Main sections |

Do not use large web-oriented spacing such as 48–64 px.

---

# 9. Round Screen Rules

ParkWatch must be designed for round screens first.

Do not place important information near the corners of the screen bounding box.

Critical content should remain within a visually safe central area.

Scrollable screens must allow content to move naturally away from the circular edges.

Prefer Wear OS adaptive components and layouts.

---

# 10. Touch Targets

Recommended target size:

`48.dp × 48.dp`

Exceptional minimum:

`40.dp × 40.dp`

An icon may visually appear around 24 dp, but its touch surface must be larger.

Never create tiny buttons only because they fit visually.

---

# 11. Components

## ParkPrimaryButton

Use for:

The main action of each screen.

Characteristics:

- based on Wear Material 3 `Button`;
- appropriate width for the available space;
- strong visual contrast;
- short label;
- preferably only one primary action visible at a time.

Examples:

`Save vehicle`

`Save`

`View vehicle`

---

## ParkSecondaryButton

Use for:

Alternative actions.

Examples:

`Edit`

`Cancel`

It must not compete visually with `ParkPrimaryButton`.

---

## ParkIconButton

Use for actions that can be clearly represented by an icon.

Examples:

- close;
- edit;
- voice input;
- location.

Must include `contentDescription` when appropriate.

Do not use emojis as production icons.

---

## ParkLocationCard

Component used to display a saved parking location.

Hierarchy:

FLOOR  
P2

ZONE B  
ROW 14

Time / elapsed time

The user should understand the location within a few seconds.

---

## ParkSelector

Reusable selector for:

- floor;
- zone;
- row;
- parking spot.

Prefer touch selection over manual keyboard input.

---

## ParkVoiceAction

Secondary action for entering parking information using voice.

Use a microphone icon consistent with Material Symbols / Material Icons.

Do not rely only on color to communicate recording state.

---

# 12. Icons

Use one consistent icon system.

Preferred:

Material Symbols / Material Icons compatible with Compose.

Conceptual examples:

- DirectionsCar
- LocationOn
- Mic
- Edit
- Delete
- Check
- ArrowBack

Do not use emojis as production icons.

---

# 13. Cards and Surfaces

Avoid the web pattern of multiple elevated cards with heavy shadows.

Wear OS has limited space and requires a more direct visual hierarchy.

Prefer:

- surface contrast;
- tonal differences;
- Material 3 shapes;
- spacing;
- visual grouping.

Shadows should be minimal or absent.

---

# 14. Shapes

Use shapes from Wear Material 3.

Prefer:

- rounded shapes;
- pills;
- stadium buttons;
- components that work naturally with circular screens.

Do not apply an arbitrary `8.dp` corner radius to every component.

---

# 15. Motion

Use Wear Material 3 `MotionScheme` when appropriate.

Animations must:

- communicate state changes;
- be fast;
- never block interaction;
- exist for usability rather than decoration.

Avoid excessive animation.

Do not implement web concepts such as `hover`.

---

# 16. Navigation

Navigation must remain shallow.

Target:

Maximum 2–3 levels for primary actions.

Primary flow:

Home  
→ Save location  
→ Confirmation

Vehicle lookup:

Home  
→ Vehicle detail

Editing:

Vehicle detail  
→ Edit  
→ Confirmation

The user must always be able to navigate back predictably.

---

# 17. Scrolling

When a screen requires scrolling, use Compose components designed for Wear OS.

Prefer:

`ScreenScaffold`

and Wear-specific list components.

Do not assume that all content must fit simultaneously inside 384×384 px.

---

# 18. Accessibility

All interactive elements must provide proper semantics.

Maintain:

- high contrast;
- adequate touch targets;
- understandable labels;
- content descriptions when necessary;
- information that does not rely only on color;
- TalkBack compatibility.

Do not reduce text excessively just to make content fit.

---

# 19. Content Style

Language must be:

- direct;
- short;
- actionable;
- easy to scan quickly.

Prefer:

`Save vehicle`

instead of:

`Register a new vehicle parking location`

Prefer:

`Floor`

instead of:

`Select the floor where your vehicle is currently parked`

---

# 20. Primary Screens

ParkWatch v1 includes:

- Home
- Save Parking
- Parking Detail
- Edit Parking
- Confirmation
- Delete Confirmation
- Permissions
- Voice Input

Additional screens must be justified before being created.

---

# 21. Empty State

When there is no saved vehicle location, Home must immediately communicate:

`Where did you park?`

and provide the primary action:

`Save vehicle`

Do not show irrelevant information.

---

# 22. Saved Vehicle State

When a vehicle location exists, the main screen must prioritize:

`P2`

`Zone B`

`Row 14`

`Parked 42 min ago`

and provide an action to view or edit details.

---

# 23. Error States

Errors must:

- explain what happened;
- explain what the user can do next;
- avoid technical language.

Example:

`We couldn't get your location`

Action:

`Try again`

Never show raw exceptions, internal codes, or Android system error messages directly to the user.

---

# 24. Destructive Actions

Deleting the saved parking location requires explicit confirmation.

Example:

`Delete location?`

Actions:

`Cancel`

`Delete`

The destructive CTA must use `ParkError`.

---

# 25. Offline First

Core functionality must work without Internet access:

- save floor;
- save zone;
- save row;
- save parking spot;
- view saved vehicle;
- edit;
- delete.

Network access is not part of the critical ParkWatch v1 flow.

---

# 26. Anti-Patterns

DO NOT use:

- compressed phone interfaces;
- long forms;
- multiple CTAs with equal visual weight;
- long text;
- deep navigation;
- mobile Material components when a Wear equivalent exists;
- CSS;
- `px`;
- `rem`;
- hover;
- cursor pointer;
- web breakpoints;
- desktop modals;
- hero sections;
- unnecessary decorative gradients;
- web-style shadows;
- emojis as production icons;
- colors outside the Design System;
- repeated arbitrary dimensions;
- important content near the extreme edges of the round screen.

---

# 27. Implementation Rules

Before implementing any screen:

1. Read this `MASTER.md`.
2. Check for an override inside `pages/`.
3. Use Wear Material 3 components.
4. Use Design System tokens.
5. Reuse existing components.
6. Design for Wear OS Small Round first.
7. Avoid repeated magic values.
8. Add accessibility semantics.
9. Run the project build.
10. Verify visually on the Wear OS emulator.

---

# 28. Pre-Delivery Checklist

Before considering a screen complete, verify:

- [ ] It displays correctly on Wear OS Small Round.
- [ ] The main information can be understood quickly.
- [ ] No critical content is clipped by the round screen.
- [ ] Touch targets are sufficiently large.
- [ ] Contrast is appropriate.
- [ ] No emojis are used as production icons.
- [ ] Wear Compose components are used.
- [ ] No CSS or web-specific concepts are present.
- [ ] Colors come from the Design System.
- [ ] Typography comes from `MaterialTheme.typography`.
- [ ] Back navigation works correctly.
- [ ] TalkBack can understand the primary actions.
- [ ] Error states provide a clear next action.
- [ ] The screen compiles without errors.

---

# 29. Design Direction

ParkWatch should feel like:

**a fast vehicle-location instrument on the user's wrist.**

It should communicate:

**precision + speed + clarity + confidence**

It must NOT feel like:

- a dashboard;
- a website;
- a medical application;
- a compressed mobile app.