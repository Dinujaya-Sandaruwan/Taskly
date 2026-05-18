# Taskly - Your Personal Productivity Partner

Taskly is a comprehensive Android-based task management application designed to help users organize their daily lives, track productivity, and never miss a deadline. Built with a focus on simplicity and efficiency, Taskly offers a robust set of features for both casual users and power organizers.

## 🚀 Features

### 🔐 Secure Authentication
- **User Registration & Login:** Create a personalized account to keep your tasks private.
- **SHA-256 Hashing:** Your passwords are never stored in plain text; we use industry-standard SHA-256 hashing for maximum security.
- **Session Management:** Stay logged in across app restarts for a seamless experience.

### 📝 Task Management
- **Create & Edit:** Quickly add tasks with titles, detailed descriptions, due dates, and alert times.
- **Priority Levels:** Categorize tasks as Low, Medium, or High priority to focus on what matters most.
- **Completion Tracking:** Easily toggle task status and watch your productivity grow.
- **Delete Tasks:** Remove completed or redundant tasks with a single swipe or click.

### 🔍 Advanced Search & Filtering
- **Dynamic Search:** Find any task instantly by searching through titles and descriptions.
- **Smart Filters:** Narrow down your list by:
  - **Status:** Pending or Completed.
  - **Priority:** Low, Medium, or High.
  - **Due Date:** Today, Tomorrow, or This Week.

### 🔔 Smart Reminders
- **Timely Alerts:** Set specific alert times for your tasks.
- **System Notifications:** Receive push notifications even when the app is closed, ensuring you stay on top of your schedule.

### 📊 Dashboard & Insights
- **Productivity Overview:** See your task completion progress at a glance.
- **Task Segregation:** View today's tasks and upcoming deadlines in separate, easy-to-read sections.

### ⚙️ Personalization
- **Profile Management:** Update your name and email address.
- **Security Settings:** Change your password anytime within the app.
- **Help & Support:** Access guidance and support directly from the settings.

---

## 📸 App Screenshots

| Splash Screen | Welcome Screen | Login Screen |
|:---:|:---:|:---:|
| <img src="screens/Taskly Splash Screen.png" width="200"> | <img src="screens/Welcome Screen.png" width="200"> | <img src="screens/Login Screen.png" width="200"> |

| Sign-up Screen | Dashboard | Add Task |
|:---:|:---:|:---:|
| <img src="screens/Sign-up Screen.png" width="200"> | <img src="screens/Taskly Dashboard.png" width="200"> | <img src="screens/Add Task Screen.png" width="200"> |

| Search & Filters | Edit Task | Profile & Settings |
|:---:|:---:|:---:|
| <img src="screens/Search and Filters.png" width="200"> | <img src="screens/Edit Task Screen.png" width="200"> | <img src="screens/Profile & Settings.png" width="200"> |

---

## 🛠️ Tech Stack

- **Platform:** Android
- **Language:** Java (JDK 1.8)
- **Database:** SQLite (Local storage)
- **Minimum SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 34 (Android 14)
- **UI Framework:** Material Design Components
- **Architecture:** Activity-based with dedicated Helper classes (Database, Session, Notifications)

## 📦 Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/[your-username]/tasklyproduction2.git
   ```
2. **Open in Android Studio:**
   - Go to `File > Open` and select the cloned directory.
3. **Build the project:**
   - Wait for Gradle to sync and build the project.
4. **Run the app:**
   - Connect an Android device or start an emulator and click the **Run** button.

---

## 🛡️ Security Note

Taskly prioritizes your data security. User passwords are encrypted using **SHA-256** before being stored in the local SQLite database. All database operations involving sensitive information are handled through a secure `DatabaseHelper` class.

---

## 📄 License

This project is for educational/portfolio purposes.
