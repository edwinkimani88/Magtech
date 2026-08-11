# MagTech Investments - Mobile Admin & Pawnshop Management App

A native Android application built with **Kotlin** and **Jetpack Compose** for **MagTech Investments** (Nairobi, Kenya). The app streamlines inventory management, pawn loans, direct buy-ins, automated market valuations, and online marketplace publishing across two physical branches: **Shop 1 (Chairman Road)** and **Shop 2 (Deliverance Road)**.

---

## 📲 How to Download & Install the APK from GitHub

Whenever code is pushed to this GitHub repository, **GitHub Actions** automatically compiles the Android APK binary.

### Option 1: Download latest build from GitHub Actions (Artifacts)
1. Go to the **Actions** tab at the top of this GitHub repository.
2. Click on the latest workflow run named **"Build & Release Android APK"**.
3. Scroll down to the **Artifacts** section at the bottom of the page.
4. Click on **`MagTech-Investments-Debug-APK`** to download the ZIP package containing `app-debug.apk`.
5. Unzip the file, transfer `app-debug.apk` to your Android phone, and install it!

### Option 2: Download from GitHub Releases
1. Navigate to the **Releases** section on the right sidebar of the repository (`/releases`).
2. Download `app-debug.apk` under the Assets section of the latest release version.

---

## 🚀 Pushing Code Updates from Google AI Studio to GitHub

If you are editing or updating this application inside **Google AI Studio**:

1. Click the **Export** button in the top toolbar or navigation menu.
2. Select **Push to GitHub**.
3. Authorize your GitHub account or select your target repository (`username/repository-name`).
4. Once pushed, GitHub Actions will automatically trigger a new build and produce a downloadable APK file!

---

## 🛠️ Building the APK Locally (Android Studio)

If you clone this repository to build on your machine:

1. Open the project folder in **Android Studio (Ladybug or newer)**.
2. Let Gradle sync dependencies automatically.
3. Build the APK via terminal:
   ```bash
   ./gradlew assembleDebug
   ```
4. Find the generated APK at:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## ✨ Key Features

- **Dual Branch Management**: Switch seamlessly between **Shop 1 (Chairman Road)** and **Shop 2 (Deliverance Road)** with shop-filtered sales data, active loan balances, and audit feeds.
- **In-App Camera Photography**: Capture 2–4 physical photos per collateral item directly using native camera intents.
- **AI Valuations & Sheng Draft SMS**: Powered by OpenRouter (`gpt-4o-mini`) to evaluate market pricing for electronics and draft customer loan payment reminders in authentic Nairobi Sheng.
- **Room Database Persistence**: Fully offline-capable local SQLite database tracking customers, loan agreements, forfeitures, and direct buy-ins.
- **Web Marketplace Catalog Sync**: Built-in marketplace publishing flag and location badges (`📍 Shop 1` / `📍 Shop 2`) to sync inventory with the central web store.
