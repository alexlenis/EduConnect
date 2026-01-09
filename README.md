# EduConnect 📚

**EduConnect** is an Android application designed to help students organize their academic life in one place. It brings together subjects, assignments, notes, calendar, map locations, and settings with a clean and modern Material Design UI.

---

## ✨ Features

* 📘 **Subjects**

  * Add / edit / delete subjects
  * Professor name, semester, schedule
  * Optional location on map

* 📝 **Assignments**

  * Title, description, deadline
  * Completion status tracking

* 🗒️ **Notes**

  * Simple text notes
  * Attach photos (camera or gallery)

* 📅 **Calendar & Events**

  * Monthly calendar view
  * Events displayed per selected day

* 🗺️ **Map**

  * Save important locations (office, secretariat, subject)

* ⚙️ **Settings**

  * Light / Dark / Auto (Sunset) theme
  * Notifications toggle

* 📊 **Grades**

  * Grades overview
  * Empty state when no grades are available
 
📸 Screenshots

All application screenshots are available in the screenshots/ folder of the repository.



## 🏗 Architecture

EduConnect follows a clean **MVVM architecture** with a local persistence layer:

* **UI Layer**: Activities / Fragments
* **ViewModels**: Handle UI state and business logic
* **Repository Layer**: Acts as a single source of truth between ViewModels and data sources
* **Persistence Layer**: Implemented using Room

  * `@Entity` classes for data models
  * `@Dao` interfaces for database operations
  * `RoomDatabase` for database configuration and access

This structure ensures clear separation of concerns, better testability, and scalability.

---

## 🛠️ Technologies

* **Kotlin**
* **Android SDK**
* **MVVM Architecture**
* **Room (Entity, DAO, RoomDatabase)**
* **Repository Pattern**
* **Material Design**
* **Google Maps API**

---

## ▶️ Run the Project

```bash
git clone https://github.com/alexlenis/EduConnect.git
```

1. Open the project in **Android Studio**
2. Sync Gradle files
3. Run on an emulator or physical device

---

## 🚧 Project Status

This project is under **active development**.

Planned improvements:

* Cloud backup / sync
* User authentication
* Push notifications for deadlines
* Grade statistics and insights

---

## 👤 Author

* **alexlenis**

---

## 📄 License

Educational / personal project. You may add a license (e.g. MIT) if desired.

---

⭐ If you find this project useful, consider giving it a star!
