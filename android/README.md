# Pixelmarks — Android Native (Kotlin + Jetpack Compose)

A native Android bookmark manager built with **Kotlin**, **Jetpack Compose**, and **Material 3 Expressive**. Inspired by Pixel Bookmarks with native dynamic theming, expressive morphing shapes, and automatic link metadata & thumbnail detection.

## Architecture & Tech Stack

- **UI Framework**: 100% Jetpack Compose with Material 3 Expressive (`androidx.compose.material3`).
- **Dynamic Theming**: Material You dynamic color extraction (Monet engine) for Android 12+, with automatic Day/Night mode.
- **Expressive Shapes & Motion**: Procedural M3 Expressive shapes (`CookieShape`, `SunnyShape`, `CloverShape`) and physics-based spring animations (`ExpressiveMotion.fastSpatial`, `ExpressiveMotion.bouncy`).
- **Local Persistence**: Offline-first Room Database (`@Entity`, `@Dao`, Coroutines `Flow`).
- **Asynchronous Image Loading**: Coil 3 with crossfade animation, caching, and website thumbnail rendering with live fallback (clean thumbnail covers without middle logos).
- **Metadata Detector**: Native Ktor HTTP client detecting YouTube oEmbed, Wikipedia REST API, GitHub repo cards, OpenGraph, Twitter Cards, and live website thumbnail screenshots.
- **Android Share Target**: Registered `android.intent.action.SEND` receiver in `AndroidManifest.xml` to save links directly from YouTube, Chrome, Reddit, Twitter/X, Instagram, or any Android app.

## Project Structure

```
app/src/main/java/com/pixelmarks/app/
├── MainActivity.kt               // Edge-to-edge Compose activity + Share Intent handler
├── PixelmarksApplication.kt      // Application container & database instantiation
├── data/
│   ├── model/                   // Bookmark, BookmarkCollection, WebsiteMetadata
│   ├── local/                   // Room DB, Entities, TypeConverters, DAOs
│   ├── remote/                  // MetadataDetector (YouTube, Wiki, GitHub, OG)
│   └── repository/              // BookmarkRepository
└── ui/
    ├── theme/                   // Color, Type, Shape, Motion, Theme
    ├── components/              // WebsiteThumbnail, MorphShape, SelectionToolbar
    ├── screens/
    │   ├── home/                // HomeScreen, HomeViewModel, BookmarkGridCard, BookmarkListRow
    │   ├── collections/         // CollectionsScreen, CollectionDetailScreen
    │   └── editor/              // BookmarkEditorSheet (ModalBottomSheet)
    └── navigation/              // Compose Navigation & routes
```

## How to Build in Android Studio

1. Open **Android Studio** (Giraffe / Hedgehog / Iguana / Jellyfish / Koala / Ladybug or newer).
2. Select **Open** and choose this `android/` directory.
3. Wait for Gradle sync to complete (JDK 17+ recommended).
4. Run on any Android device or emulator running **Android 8.0 (API 26)** or higher (Pixel running Android 14/15/16 recommended for full Material 3 Expressive dynamic color).
