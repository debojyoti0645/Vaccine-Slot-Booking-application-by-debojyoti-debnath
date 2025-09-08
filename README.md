# 💉 Vaccine Slot Booking App
An **Android mobile application** built with Java that enables users to book vaccination appointments and allows administrators to manage vaccine slots efficiently.  
Streamline the vaccination process with real-time slot management, certificate generation, and comprehensive user tracking.

---

## 🚀 Features

### 👤 User Features
- 🔐 **Secure Authentication**: Firebase-based login and registration system
- 📅 **Slot Booking**: Browse and book available vaccination slots
- 📱 **Real-time Updates**: Live slot availability with automatic refresh
- 📋 **Vaccination History**: Track all taken vaccines with detailed records
- 🏥 **Certificate Download**: Generate and download vaccination certificates as PDF
- 👨‍💼 **Profile Management**: Edit personal information and upload profile pictures
- 📧 **Email Integration**: Account verification and notifications

### 🔧 Admin Features
- ➕ **Slot Management**: Add new vaccination slots with date, time, and location
- 👀 **Slot Monitoring**: View and edit existing slots with status control
- 👥 **User Management**: Search and view user vaccination data
- 📊 **Real-time Dashboard**: Monitor slot availability and user bookings
- 🚫 **Slot Control**: Cancel or modify slots as needed

---

## 🛠️ Tech Stack

- **Java & Android SDK**
- **Firebase Authentication** for secure user management
- **Cloud Firestore** for real-time database operations
- **Firebase Storage** for profile picture uploads
- **PDF Generation** with Android Canvas for certificates
- **Glide** for efficient image loading
- **Picasso** for profile picture management
- **MediaStore API** for file downloads
- **Material Design** components for modern UI

---

## 📱 App Architecture

### Core Components
- **Authentication System**: Secure login/register with role-based access
- **Real-time Database**: Firestore collections for users, slots, and bookings
- **Certificate System**: Dynamic PDF generation with user data overlay
- **Profile Management**: Complete user profile with image upload
- **Admin Panel**: Comprehensive management tools for administrators

### Database Structure
```
users/
├── {userId}/
│   ├── name, email, phone, location
│   ├── doses_taken, vaccinated status
│   ├── profileImageUrl
│   └── bookedVaccines/
│       └── {bookingId}/
│           ├── vaccineName, slotDate
│           └── slotLocation, slotTime

slots/
├── {slotId}/
│   ├── date, time, location
│   ├── vaccineName, dosageNumber
│   └── status (Available/Cancelled)
```

---

## 🎮 App Flow

### User Journey
- **Registration**: Create account with personal details and role selection
- **Authentication**: Secure login with Firebase authentication
- **Slot Discovery**: Browse available vaccination slots by date and location  
- **Booking Process**: Select and confirm vaccination appointments
- **Certificate Access**: Download personalized vaccination certificates
- **Profile Management**: Update personal information and track vaccination history

### Admin Workflow
- **Dashboard Access**: Dedicated admin panel with management tools
- **Slot Creation**: Add new vaccination slots with all required details
- **User Monitoring**: Search and view user vaccination data
- **Slot Management**: Edit, cancel, or modify existing slots
- **Real-time Updates**: Monitor bookings and slot availability

---

## 📱 Key Activities

### 🏠 Splash Screen
- Network connectivity verification
- Automatic user authentication check
- Role-based navigation to appropriate dashboard
- Offline handling with retry mechanism

### 🔐 Authentication Flow
- **Login Activity**: Email/password authentication with role detection
- **Register Activity**: Complete user registration with admin key support
- Firebase integration with real-time validation
- Error handling with device vibration feedback

### 👤 User Panel
- Dashboard with quick access to all user features
- Slot booking with confirmation dialogs
- Vaccination history with detailed records
- Certificate download with custom filename support

### 🔧 Admin Panel  
- Comprehensive slot management interface
- User data search and verification system
- Real-time slot monitoring with edit capabilities
- Bulk operations for efficient management

### 📋 Profile Management
- Complete user profile with editable fields
- Profile picture upload with Firebase Storage
- Vaccination status tracking
- Logout functionality with session management

---

## 💾 Data Management

### Real-time Synchronization
- **Firestore Integration**: Live data updates across all users
- **Transaction Support**: Atomic booking operations to prevent conflicts
- **Offline Capability**: Local data caching for improved performance
- **Error Handling**: Comprehensive error management with user feedback

### Security Features
- **Role-based Access**: Admin and user role separation
- **Data Validation**: Input validation with error feedback
- **Secure Storage**: Firebase security rules implementation
- **Authentication States**: Persistent login sessions

---

## 📄 Certificate System

### PDF Generation
- **Template-based Design**: Professional certificate template
- **Dynamic Text Overlay**: User data integration with custom positioning
- **High Resolution**: Quality output suitable for official use
- **Custom Naming**: User-defined filenames for easy organization

### Storage Integration
- **MediaStore API**: Seamless download to device storage
- **Version Compatibility**: Support for Android 10+ scoped storage
- **File Management**: Organized storage in Downloads directory

---

## 🎨 UI/UX Design

### Design Principles
- **Material Design**: Modern Android UI guidelines compliance
- **Responsive Layouts**: Adaptive design for different screen sizes
- **User Feedback**: Visual and haptic feedback for all interactions
- **Accessibility**: Support for accessibility services and features

### Visual Elements
- **Color Scheme**: Professional medical theme with intuitive navigation
- **Typography**: Clear, readable fonts with appropriate sizing
- **Icons**: Consistent iconography throughout the application
- **Loading States**: Progress indicators for all async operations

---

## 📂 Project Structure

```
app/src/main/java/com/example/vaccineslotbooking/
├── activities/
│   ├── SplashActivity.java         # App entry point
│   ├── LoginActivity.java          # User authentication
│   ├── RegisterActivity.java       # User registration
│   ├── AdminPanelActivity.java     # Admin dashboard
│   ├── UserPanelActivity.java      # User dashboard
│   └── ProfileActivity.java        # Profile management
├── models/
│   ├── Slot.java                   # Slot data model
│   ├── UserSlot.java              # User booking model
│   ├── Vaccine.java               # Vaccine record model
│   └── Certificate.java           # Certificate data model
├── adapters/
│   ├── UserSlotAdapter.java       # Slot list adapter
│   ├── VaccineAdapter.java        # Vaccine history adapter
│   └── CertificateAdapter.java    # Certificate list adapter
└── utils/
    ├── AddSlotActivity.java       # Slot creation
    ├── ViewSlotsActivity.java     # Slot management
    ├── CheckUserDataActivity.java # User data search
    └── DownloadCertificateActivity.java # Certificate generation
```

---

## 🔧 Key Features Implementation

### Slot Booking System
- **Real-time Availability**: Live updates of slot capacity
- **Conflict Prevention**: Transaction-based booking to avoid overbooking
- **Confirmation System**: Multi-step booking confirmation process
- **Automatic Updates**: User vaccination count increment on successful booking

### Certificate Generation
- **Template System**: Overlay user data on professional certificate template
- **PDF Creation**: High-quality PDF generation with Canvas API
- **Custom Download**: User-defined filenames with validation
- **Storage Integration**: Seamless integration with device file system

### Admin Management
- **Comprehensive Dashboard**: Complete slot and user management tools
- **Search Functionality**: Quick user lookup by email address  
- **Slot Control**: Full CRUD operations on vaccination slots
- **Real-time Monitoring**: Live updates of all system activity

---

## 📌 Summary

The **Vaccine Slot Booking App** is a comprehensive solution for managing vaccination appointments with separate interfaces for users and administrators. Built with modern Android development practices and Firebase backend services, the app provides real-time slot management, secure user authentication, and professional certificate generation.

The application features a robust architecture with **Firebase integration**, efficient data management, and a user-friendly interface that streamlines the vaccination booking process for both users and healthcare administrators.

---
