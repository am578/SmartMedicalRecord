# 🚀 Smart Medical Record App

## 📱 Description

Smart Medical Record is an Android application designed to manage medical workflows efficiently.  
It connects patients, doctors, receptionists, and admins in one system to handle appointments, patient records, staff accounts, and medical requests.

---

## ✨ Features

- 🔐 Login & Register system
- 👥 Role-based dashboards:
    - 👨‍⚕️ Doctor
    - 🧾 Receptionist
    - 👤 Patient
    - 👑 Admin
- 📋 Patient management system
- 🔎 Patients list
- 📅 Appointment request system
- ✅ Appointment acceptance / rejection / suggestion
- 🏥 Medical records tracking
- 💊 Diagnosis, medical notes, and prescription management
- 🔥 Firebase integration
- 📁 Supabase storage for medical files
- 🐳 Docker build setup

---

## 👤 Patient Features

- 📝 Create patient account
- 📅 Request appointments
- ✍️ Submit symptoms / notes
- 👀 View appointment status
- 💳 Confirm appointment payment
- 📜 View medical record

---

## 👨‍⚕️ Doctor Features

- 👥 View patients list
- 📁 Access patient details
- 🩺 Add diagnosis
- 📝 Add medical notes
- 💊 Add prescription / treatment
- 💾 Save medical records

---

## 🧾 Receptionist Features

- 👤 Register new patients
- 👥 View patients list
- 📅 Manage appointments
- 📥 View appointment requests
- ✅ Accept appointment requests
- ❌ Reject appointment requests
- 🕒 Suggest another appointment time

---

## 👑 Admin Features

- ⚙️ Create staff accounts
- 👥 Manage users
- 🧑‍⚕️ Manage doctors
- 🧾 Manage receptionists
- 🔐 Manage user roles

---

## 🛠 Tech Stack

- Kotlin 🟣
- Jetpack Compose 🎨
- MVVM Architecture 🧠
- Firebase Authentication 🔥
- Cloud Firestore ☁️
- Supabase Storage 📁
- Docker 🐳
- GitHub 🌐

---

## 🔥 Firebase

Firebase is used as the main backend service.

It handles:

- User authentication
- User roles
- Patients data
- Appointments
- Medical records
- Real-time data storage using Cloud Firestore

---

## 📁 Supabase

Supabase is used mainly for medical file storage.

It is used to upload medical files and store their URLs, while Firebase remains the main backend for authentication and database management.

---

## 🐳 Docker

Docker was added to provide a consistent build environment for the Android project.

Build the Docker image:

```bash
docker build -t smart-medical-record .