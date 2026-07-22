<div align="center">

# 🚀 Taskify – Minimal To-Do Android Application

**A sleek, dark-themed, and secure Android To-Do app with User Authentication and SQLite storage built for Rakamanda Maheswara Rao.**

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![SQLite](https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org)
[![Material Design](https://img.shields.io/badge/UI-Material%20Components-757575?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![Gradle](https://img.shields.io/badge/Build-Gradle%209.3-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org)

</div>

---

## 📌 Overview

**Taskify** is a native Android application engineered as part of the **Oasis Infobyte Internship (Task 2 – Android App Development)**.

The application combines secure user authentication (Sign-Up / Login with SHA-256 password hashing) with persistent task management powered by an embedded **SQLite database**. Built using **Java** and **XML layouts**, Taskify features a modern dark theme, real-time daily task completion progress tracking, and system window insets handling to ensure content never overlaps status or navigation bars.

---

## ✨ Key Features & Capabilities

- 🔑 **User Authentication:** Complete Login and Registration screens supporting Email or Username authentication.
- 🔒 **SHA-256 Password Hashing:** Passwords are encrypted using SHA-256 before database insertion and credential matching.
- 💾 **SQLite Database Storage:** Two structured tables (`users` and `tasks`) linked via relational foreign key constraints.
- 🔄 **Persistent Session Management:** Automatic login state persistence via `SharedPreferences` so logged-in users bypass the login screen on app restart.
- 📝 **Task Management (CRUD):**
  - **Create Task:** Add new tasks via a clean modal dialog.
  - **Complete Task:** Interactive checkboxes update SQLite database state and apply strikethrough styling to completed tasks.
  - **Delete Task:** Remove tasks permanently from database with a confirmation prompt.
- 📊 **Daily Progress Tracker:** Dynamic linear progress bar card showing real-time task completion statistics (`X of Y tasks completed`).
- 🎨 **Friendly Empty State:** Clean clipboard vector illustration and helpful prompt displayed when no tasks exist.
- 🌙 **Modern Dark UI Design:** Tailored dark color palette (`#0F172A` Slate background, `#1E293B` card surfaces, `#6366F1` Indigo accents, `#10B981` Emerald progress).
- 📱 **System Window Insets Handling:** Dynamic status bar and navigation bar inset padding (`ViewCompat.setOnApplyWindowInsetsListener`) ensuring zero layout clipping under device status/nav bars.

---

## 🛠️ Tech Stack & Architecture

| Component | Technology / Library | Description |
| :--- | :--- | :--- |
| **Language** | Java (JDK 11) | Core application logic, database operations, and event handling |
| **UI Framework** | Android XML & Material Components | `ConstraintLayout`, `RecyclerView`, `CardView`, `FloatingActionButton`, Dialogs |
| **Database** | SQLite (`SQLiteOpenHelper`) | Embedded relational database (`taskify.db`) for user accounts and tasks |
| **Security** | `java.security.MessageDigest` | SHA-256 password hashing utility |
| **Session** | `android.content.SharedPreferences` | Persistent login session management |
| **Theme & Style** | Dark Mode Palette | Custom tokenized color system (`colors.xml`, `themes.xml`) |
| **Window Insets** | `androidx.core.view.ViewCompat` | Dynamic status bar & navigation bar inset padding handling |
| **Minification** | ProGuard / R8 | Rules for preserving models, database helpers, layout inflation & Activity entry points |
| **Build System** | Gradle 9.3 (AGP 9.3.0) | Android Application Gradle Plugin with Version Catalog (`libs.versions.toml`) |

---

## 📂 Project Structure

```text
OIBSIP/
 └── Android-Task2-ToDoApp/
     ├── assets/
     │   ├── signin.png                       # Sign In screen screenshot
     │   ├── create_account.png               # Sign Up screen screenshot
     │   ├── home.png                         # Main task list screen screenshot
     │   ├── new_task.png                     # Add task dialog screenshot
     │   └── task_progress.png                # Completed tasks & progress tracker screenshot
     ├── app/
     │   ├── proguard-rules.pro               # ProGuard / R8 optimization & keep rules
     │   └── src/main/
     │       ├── AndroidManifest.xml          # Application manifest file
     │       ├── java/com/maheswara660/taskify/
     │       │   ├── LoginActivity.java       # User authentication & session check
     │       │   ├── SignUpActivity.java      # Registration logic & auto-login
     │       │   ├── TaskListActivity.java    # Main task list screen, progress card & CRUD
     │       │   ├── adapter/
     │       │   │   └── TaskAdapter.java     # RecyclerView adapter for tasks with strikethrough logic
     │       │   ├── database/
     │       │   │   └── DatabaseHelper.java  # SQLite database creation & CRUD methods
     │       │   ├── model/
     │       │   │   ├── User.java            # User data model
     │       │   │   └── Task.java            # Task data model
     │       │   └── utils/
     │       │       ├── PasswordUtils.java   # SHA-256 hashing utility
     │       │       └── SessionManager.java  # SharedPreferences session manager
     │       └── res/
     │           ├── drawable/                # Custom icons, buttons, cards & vector assets
     │           │   ├── ic_lock.xml
     │           │   ├── ic_add.xml
     │           │   ├── ic_delete.xml
     │           │   ├── ic_logout.xml
     │           │   └── ic_task_empty.xml
     │           ├── layout/                  # XML layout files for screens & items
     │           │   ├── activity_login.xml
     │           │   ├── activity_sign_up.xml
     │           │   ├── activity_task_list.xml
     │           │   ├── item_task.xml
     │           │   └── dialog_add_task.xml
     │           └── values/                  # Strings, colors, and dark theme definitions
     │               ├── colors.xml
     │               ├── strings.xml
     │               └── themes.xml
     ├── build.gradle.kts                     # Root build configuration
     ├── gradle/libs.versions.toml            # Gradle Version Catalog
     └── README.md                            # Comprehensive project documentation
```

---

## 📸 Screenshots & Demonstration

| 🔐 1. Sign In | 📝 2. Create Account | 📋 3. Task List (Home) |
| :---: | :---: | :---: |
| ![Sign In Screen](assets/signin.png) | ![Create Account Screen](assets/create_account.png) | ![Task List Screen](assets/home.png) |
| *Email / Username login with validation* | *Registration with match validation* | *Header greeting, logout & empty state* |

<br>

| ➕ 4. Add New Task Dialog | 📊 5. Task Progress & Completion |
| :---: | :---: |
| ![Add New Task Dialog](assets/new_task.png) | ![Task Progress Screen](assets/task_progress.png) |
| *Modal dialog for entering task description* | *Interactive checkbox strikethrough & progress card* |

---

## 📲 Local Installation & Setup

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Maheswara660/OIBSIP.git
   cd OIBSIP/Android-Task2-ToDoApp
   ```

2. **Build Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   The compiled APK will be located at:  
   `app/build/outputs/apk/debug/app-debug.apk`

3. **Install on Connected Device / Emulator:**
   ```bash
   ./gradlew installDebug
   ```

---

## 🛡️ ProGuard / R8 Configuration

The application includes dedicated optimization rules in [`app/proguard-rules.pro`](app/proguard-rules.pro):
- Preserves `LoginActivity`, `SignUpActivity`, and `TaskListActivity` entry points and manifest-bound class definitions.
- Keeps data models (`User`, `Task`), database helpers (`DatabaseHelper`), utilities (`PasswordUtils`, `SessionManager`), and adapter ViewHolders.
- Keeps AndroidX AppCompat, Material Component, RecyclerView, and ConstraintLayout widget constructors for smooth XML inflation.
- Preserves line numbers (`LineNumberTable`) and source files for diagnostic stack trace reporting.

---

## 📜 Internship Task Compliance

This project satisfies all requirements for **Task 2 – To-Do App with Login (Taskify)** under the **Oasis Infobyte Internship Program**:
- ✅ Built strictly in **Java** with **XML layouts**.
- ✅ Login screen with email/username and password fields + Login button.
- ✅ Sign-Up screen with name, email, password fields + Register button.
- ✅ Embedded **SQLite database** with `users` and `tasks` tables.
- ✅ Passwords stored as **hashed values (SHA-256)**.
- ✅ Task List screen with **Add Task** dialog, **Mark Complete** (strikethrough styling), and **Delete Task** (SQLite removal).
- ✅ **Logout button** that clears session and returns to login screen.
- ✅ Full input validation with friendly Toast messages.
- ✅ Display tasks in a **RecyclerView** with a friendly empty state when no tasks exist.

---

## 📌 Author

**Rakamanda Maheswara Rao**  
Final-year Computer Science & Engineering Student  
Visakhapatnam, India  
GitHub: [@Maheswara660](https://github.com/Maheswara660)
