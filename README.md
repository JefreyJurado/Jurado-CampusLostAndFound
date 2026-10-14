# Campus Lost and Found

A Jetpack Compose app for reporting and finding lost items on campus (MO-IT114, Milestone 2).
UI layer only: there is no backend, so sample items are hardcoded and new reports, saved items,
and messages live in app state until the app is closed.

## Running it

1. Open the project folder in a recent Android Studio.
2. Let Gradle sync. The build uses Gradle 8.13, AGP 8.13, Kotlin 2.2.20, and a Java 21 toolchain
   (downloaded automatically if missing).
3. Run the `app` configuration on an emulator or phone with Android 7.0 (API 24) or newer.

## Screens and flow

The approved prototype is in [`docs/prototype/`](docs/prototype).

```
Feed ─┬─> Report Item      back / Discard / Submit → Feed
      └─> Item Detail ─> Conversation
```

- **Feed** (start screen): filter chips for All / Lost / Found / Saved, tap an item to open it,
  or tap **Report Item**.
- **Report Item**: Lost/Found toggle, Name, Category, Location, and an optional photo.
  A complete report is added to the top of the Feed, which shows a "Report Posted" banner.
- **Item Detail**: the item's photo and details, a heart to save it, and **Message finder** /
  **Message owner** depending on whether the item was found or lost.
- **Conversation**: the messages for that item only.

## Features beyond the prototype

| Feature | Why |
| --- | --- |
| Saved filter chip | Items saved with the heart can be found again from the Feed |
| Photo picker with remove (X) | Uses the system photo picker, so no storage permission is needed |
| Per-item conversations | Each item has its own thread; new ones start with a "Say hi" prompt |
| Spelled-out form errors | Empty required fields say what is missing ("Enter a category"), not just turn red, and the error clears as soon as the field is filled |
| Discard confirmation on back | Both the back arrow and the system Back button ask before throwing away a filled-in form |
| Keyboard actions | Next / Done move through the report form; the keyboard's Send key sends a message |
| Screen reader support | Screen titles are headings; rows, the back arrow, and the photo box announce what tapping them does; field errors are read out |
| State that survives | Filters, form text, saved items, and messages survive leaving a screen, returning, and rotating |

## Design approach

- **Unidirectional data flow / state hoisting.** Each screen has a stateful `XScreen` that owns its
  local UI state and a stateless `XContent` that only draws what it is given and reports events
  through callbacks. Screens without local state are stateless directly.
- **Single source of truth.** Data shared between screens lives in
  [`CampusAppState`](app/src/main/java/com/example/campuslostandfound/ui/state/CampusAppState.kt), a
  plain state holder. Screens never change it directly; they call its functions (`addReport`,
  `toggleSaved`, `sendMessage`), and Compose re-draws whatever reads the changed state (the
  Observer pattern built into Compose state).
- **Centralized navigation.** Every route is defined in `Routes.kt`; every `navigate` /
  `popBackStack` is in `AppNavHost.kt`, inside a click handler, never during composition.
  Only the item id (`Int`) is passed between screens.
- **Stable under rapid input.** A navigation tap runs only if its screen is the settled, visible one
  (lifecycle `RESUMED`), so repeated taps open one screen, one back tap pops one screen, and a
  double tap on Submit posts one report.
- **Reusable components.** `ScreenHeader`, `GrayButton`, `FormField`, `ItemPhoto`, `StatusBadge`,
  `EmptyState`, and the dialog/banner are shared across screens; spacing and corner shapes come
  from the theme (`Dimens`, `MaterialTheme.shapes`).

## Where things are

| Path | What it holds |
| --- | --- |
| `ui/navigation/Routes.kt` | Every route string and the helpers that build routes with an item id |
| `ui/navigation/AppNavHost.kt` | The navigation graph and the rapid-tap guard |
| `ui/state/CampusAppState.kt` | App-level state shared by several screens, and the functions that change it |
| `ui/screens/` | One file per screen, plus `FeedFilter` (the Feed's chip options) |
| `ui/components/` | Shared UI pieces used across screens |
| `ui/theme/` | Colors, typography, shapes, and spacing tokens |
| `data/` | Models (`Models.kt`) and the sample data |
| `app/src/test/` | Automated tests (below) |

## Testing

Run all tests from the project folder (no emulator needed):

```
./gradlew test
```

- **`NavigationStabilityTest`** runs the Week 9 stability protocol against the real app with
  Robolectric: rapid taps on rows, back arrows, Submit, and Send; back-stack order three screens
  deep; the discard dialog; filters, saved items, and messages surviving re-entry; rotation; and
  the full flow twice in a row.
- **`CampusAppStateTest`** checks the app-state logic: new report ids, the posted banner flag,
  saving and unsaving, and sending messages (including ignoring blank ones).
- **`FeedFilterTest`** checks which items each Feed chip keeps.

Manual walkthrough on a device or emulator follows the same protocol: tap quickly, go three
screens deep and back, leave and re-enter screens, and run the full flow twice.
