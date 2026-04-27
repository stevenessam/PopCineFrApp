# 🎬 PopCineFR

A modern Android movies & series discovery app built with Kotlin and Jetpack Compose.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

---

## Features

- Trending movies and series this week
- Top rated content of all time
- Now playing in cinemas & series currently on air
- Browse by genre with dropdown selection
- Real-time search with debounce
- Full grid view with 80+ results per section
- Detail screen with backdrop, genres, overview and trailer
- Opens trailer directly in YouTube
- Save favorites locally — persists across sessions

---

## Architecture

Simplified MVVM — UI talks to ViewModel, ViewModel talks to Repository, Repository talks to the API or local database.

```
UI (Compose) → ViewModel (StateFlow) → Repository → Retrofit / Room
```

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary language |
| Jetpack Compose | UI framework |
| Navigation Compose | Screen routing |
| ViewModel + StateFlow | State management |
| Coroutines | Async & parallel API calls |
| Retrofit + OkHttp | Networking |
| Gson | JSON parsing |
| Coil | Image loading |
| Room | Local favorites database |
| Material 3 | UI components and theming |

---

## API

Data provided by the [TMDB API](https://www.themoviedb.org/).

> This product uses the TMDB API.

---

## Author

**Steven Essam** — Android development with Kotlin and Jetpack Compose.