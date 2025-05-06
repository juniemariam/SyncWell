# SyncWell Android App

**Empowering women to monitor and manage their menstrual health with ease.**

![image](https://github.com/user-attachments/assets/2c096a03-bb34-460f-9fbc-aff913bb7979)

---

## 📱 Overview

**SyncWell** is an Android application designed to assist women in tracking their menstrual cycles, monitoring health metrics, and accessing personalized insights. By leveraging intuitive interfaces and reliable data storage, SyncWell aims to promote proactive health management and awareness.

---

## ✨ Features

- **User Profile Management**: Input and update personal details such as name, age, weight, and height.
- **BMI Calculation**: Automatic computation of Body Mass Index based on user inputs.
- **Menstrual Cycle Logging**: Record start and end dates of menstrual periods.
- **Cycle History**: View a comprehensive history of logged cycles with durations.
- **Data Persistence**: Secure storage of user data using Room database.
- **User-Friendly Interface**: Clean and intuitive UI for seamless navigation.

---

## 🛠️ Tech Stack

- **Language**: Java  
- **Framework**: Android SDK  
- **Database**: Room (SQLite abstraction)  
- **UI Components**: Material Design, ConstraintLayout  
- **Date Handling**: Java Calendar, DatePickerDialog  
- **Data Storage**: SharedPreferences for lightweight data  

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later  
- Android SDK 30 or higher  
- Gradle 7.0 or higher  

### Installation

```bash
git clone https://github.com/yourusername/syncwell-android.git
``` 
2. **Open in Android Studio**  
   Navigate to `File > Open` and select the cloned project directory.

3. **Build the project**  
   Allow Gradle to sync and build the project without errors.

4. **Run the app**  
   - Connect a physical Android device or start an emulator.
   - Click the **Run** button in Android Studio or press `Shift + F10`.

---

## 🧪 Testing

- **Unit Tests**: Located in the `src/test/java` directory.
- **Instrumented Tests**: Located in the `src/androidTest/java` directory.

### To run tests:

Using Android Studio:
- Right-click on the test class or method and select **Run**.

Using terminal:

```bash
./gradlew test
./gradlew connectedAndroidTest
``` 

## 📈 Future Enhancements

- 🔔 **Notifications**: Reminders for upcoming menstrual cycles.
- ☁️ **Data Backup**: Cloud synchronization for user data.
- 📊 **Analytics**: Insights and predictions based on logged data.
- 🌐 **Localization**: Support for multiple languages.

