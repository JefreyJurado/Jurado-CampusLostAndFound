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

- **Feed** (start screen): filter by All / Lost / Found, tap an item to open it, or tap **Report Item**.
- **Report Item**: Lost/Found toggle, Name, Category, Location, and an optional photo from the system
  photo picker. Submitting with blank fields outlines them in red. A complete report is added to the
  top of the Feed, which shows a "Report Posted" banner. Leaving with unsaved changes (back arrow or
  system Back) asks "Discard this report?" first.
- **Item Detail**: the item's photo and details, a heart to save it, and **Message finder** /
  **Message owner** depending on whether the item was found or lost.
- **Conversation**: the messages for that item. Send is enabled only when there is text.

## Where things are

| Path | What it holds |
| --- | --- |
| `ui/navigation/Routes.kt` | Every route string and the helpers that build routes with an item id |
| `ui/navigation/AppNavHost.kt` | The navigation graph, all `navigate` / `popBackStack` calls, and app-level state |
| `ui/screens/` | One file per screen: a stateful `XScreen` and, where it has local state, a stateless `XContent` |
| `ui/components/` | Shared pieces: header, gray button, status badge, item photo, discard dialog, posted banner |
| `ui/theme/` | Colors, typography, and spacing tokens (`Dimens`) |
| `data/SampleData.kt` | Item and message models plus the sample data |

## Navigation and state rules

- Navigation happens only inside click handlers, never while a screen is being drawn.
- Each navigation call runs only if its screen is the settled, visible one, so rapid taps open one
  screen and one tap on back pops one screen.
- Only the item id (an `Int`) is passed between screens; each screen looks the item up from app state.
- Form fields, the Feed filter, and the message draft use `rememberSaveable`, so they survive
  leaving and returning to a screen and rotating the device.
