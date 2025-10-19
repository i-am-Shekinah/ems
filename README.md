# Employee Management System (EMS)

**Author:** Michael Olatunji
**Framework:** Play Framework (Java)
**Build Tool:** Maven
**Database:** PostgreSQL

---

## 📘 Overview

The **Employee Management System (EMS)** is a simple attendance and employee management platform that allows employers (admins) to manage staff records and monitor attendance activities. It also provides employees with the ability to log their daily attendance within specified work hours.

---

## 🎯 Features

### 👨‍💼 Admin

* Admin authentication (sign-in)
* Add new employees
* Remove employees
* Retrieve employee lists
* View daily attendance records

### 👷 Employee

* Employee authentication (sign-in)
* Mark daily attendance between work hours (9 a.m. - 5 p.m., Monday–Friday)

---

## 🗂 Project Structure

```
service-gateway/
│
├── app/
│   ├── controllers/        # Handles incoming requests and responses
│   ├── models/             # Contains entity classes (User, Employee, Attendance)
│   ├── repositories/       # Database interaction layer
│   ├── services/           # Business logic layer
│   ├── utils/              # Utility classes (e.g., SecurityUtil.java)
│   ├── views/              # HTML/Twirl templates (if applicable)
│
├── conf/
│   ├── application.conf    # Configuration file (DB credentials, app settings)
│   ├── routes              # Route definitions for endpoints
│
├── public/                 # Public assets (CSS, JS, images)
│
├── target/                 # Auto-generated build files
│
├── pom.xml                 # Maven project configuration
└── README.md               # Project documentation
```

---

## ⚙️ Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/employee-management-system.git
cd employee-management-system
```

### 2. Configure the database

Update your `conf/application.conf` with your PostgreSQL credentials:

```hocon
db.default.driver=org.postgresql.Driver
db.default.url="jdbc:postgresql://localhost:5432/ems"
db.default.username="postgres"
db.default.password="postgres"

play.evolutions.enabled = true
play.evolutions.autoApply = true
```

### 3. Run the application

```bash
mvn play2:run
```

Visit [http://localhost:9000](http://localhost:9000) in your browser.

---

## 🧩 Default Admin Account

In `application.conf`, a default admin is defined for testing:

```hocon
default.admin.email="admin@encentral.com"
default.admin.password="admin"
default.admin.firstName="Michael"
default.admin.lastName="Olatunji"
```

This account is automatically created when the application first runs.

---


## 🧱 Database Schema (Conceptual)

| Table          | Fields                                         |
| -------------- | ---------------------------------------------- |
| **users**      | id, firstName, lastName, email, password, role |
| **employees**  | id, user_id (FK), department, dateJoined       |
| **attendance** | id, employee_id (FK), date, timeIn, timeOut    |

---

## 🔒 Security

* Passwords are encrypted using `SecurityUtil` with `bycrypt`.
* Admin and Employee have role-based access control.
* Only Admin can manage employees and attendance records.

---

## 🚀 Future Enhancements

* JWT-based authentication
* Role-based dashboard (Admin/Employee)
* Attendance analytics and reports
* Email notifications for absent employees
* REST API documentation via Swagger

---

## 🧾 License

This project is for educational and internal demonstration purposes.

---

## 🧑‍💻 Author

[**Michael Olatunji**](https://github.com/i-am-Shekinah)

Software Engineer
