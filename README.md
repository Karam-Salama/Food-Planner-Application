<div align="center">

# 🍲 Food Planner

**Android Meal Planning & Management Mobile App**

</div>

## 📖 Overview

**Food Planner** is a modern Android application that helps users plan their meals easily and efficiently. Built using **MVVM Architecture**, **Retrofit**, **Room Database**, and **Coroutines**, the app allows users to browse meals, search for specific dishes, save favorites, and manage their meal plans with a smooth and intuitive experience, supporting both online API data and offline local caching.

<br>

<img width="1376" height="768" alt="project thumb" src="https://github.com/user-attachments/assets/089a0150-17fc-4579-bf95-74b36617e550" />

</div>

## ✨ Core Features

| # | Screen / Feature | Description |
|---|------------------|-------------|
| 1 | Splash & Onboarding | Introduction screens introducing meal planning and discovery |
| 2 | Authentication | Secure sign-in and sign-up using the app's auth flow |
| 3 | Home / Discover | Browse meals, categories, and recommendations powered by TheMealDB API |
| 4 | Meal Details | Comprehensive recipe info including ingredients, instructions, and measurements |
| 5 | Search & Filter | Search meals quickly by name or criteria |
| 6 | Personal Planner | Organize and add meals to your personal calendar or meal plan |
| 7 | Local Favorites | Save and manage favorite meals locally using Room Database for offline access |
| 8 | Offline Support | Seamlessly works online with API and offline with local caching |

## 🧱 Tech Stack & Architecture

- **Language:** Kotlin
- **Architecture Pattern:** MVVM (Model-View-ViewModel) with Clean Separation of Concerns
- **Asynchronous Programming:** Kotlin Coroutines & Flows
- **UI State Management:** LiveData / StateFlow & ViewModel
- **Networking:** Retrofit for REST API communication (TheMealDB API)
- **Local Database:** Room Database / SQLite for local data persistence and offline caching
- **Image Loading:** Glide for efficient image loading

## 📸 Screenshots

| Splash | Onboarding | Login | Sign Up | Home | Details |
|:---:|:---:|:---:|:---:|:---:|:---:|
| ![Splash](https://github.com/user-attachments/assets/ee058465-3abe-456c-93fe-77c7710e5ba6) | ![onboarding](https://github.com/user-attachments/assets/558b0747-1d77-4f2b-bff3-ef1fc6f8607a) | ![Login](https://github.com/user-attachments/assets/98f9400c-7c59-4a4b-98d2-1934feeb9aa8) | ![signUp](https://github.com/user-attachments/assets/65632682-010f-49b9-9863-c5dc76efb6b2) | ![home](https://github.com/user-attachments/assets/285071a6-1ded-4ee9-9992-e4b15866b721) | ![details1](https://github.com/user-attachments/assets/89a8df25-a306-4c9f-af68-3f79a62f9b2d) |

## 📁 Project Structure

```text
food-planner
├── core/                  # Shared app-wide core components
│   ├── constants/          # API endpoints, keys, and global constants
│   ├── theme/              # Colors, typography, and styling tokens
│   ├── network/            # Retrofit client and network helper configurations
│   ├── utils/              # Extensions, formatters, and validators
│   └── errors/             # Global exception and error handling
│
└── modules/              
    ├── splash/
    │   ├── view/           # Activities / Fragments for Splash screen
    │   ├── model/          # Data classes or states
    │   └── viewmodel/      # Splash ViewModel
    │
    ├── onboarding/
    │   ├── view/           # Onboarding UI components
    │   ├── model/          # Onboarding data models
    │   └── viewmodel/      # Onboarding ViewModel
    │
    ├── auth/
    │   ├── view/           # Login & Register Activities/Fragments
    │   ├── model/          # User and authentication data models
    │   └── viewmodel/      # Auth ViewModel (handling sign-in/up logic)
    │
    ├── home/
    │   ├── view/           # Discover screen, categories, and search UI
    │   ├── model/          # Meal categories & response data models
    │   └── viewmodel/      # Home ViewModel
    │
    ├── details/
    │   ├── view/           # Meal details screen, ingredients, and instructions UI
    │   ├── model/          # Detailed meal models
    │   └── viewmodel/      # Details ViewModel
    │
    ├── planner/
    │   ├── view/           # Meal planning calendar and scheduled lists UI
    │   ├── model/          # Planned meals data models
    │   └── viewmodel/      # Planner ViewModel
    │
    ├── favorites/
    │   ├── view/           # Favorite meals screen and adapters
    │   ├── model/          # Local Room entity / favorite model
    │   └── viewmodel/      # Favorites ViewModel
    │
    └── profile/
        ├── view/           # User profile and settings UI
        ├── model/          # Profile data models
        └── viewmodel/      # Profile ViewModel
