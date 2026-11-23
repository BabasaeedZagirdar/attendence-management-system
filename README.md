# Student & Faculty Attendance Management

Java Full-stack project

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.3, Spring Security (JWT + Google OAuth2), Spring Data JPA + Hibernate, MySQL, Maven |
| Frontend | React 18, Vite, Axios, React Router, Bootstrap 5, npm |
| Testing | Postman |

## Run it

### 1. Prerequisites
Java 21, Maven 3.9+, MySQL 8 running on `localhost:3306`, Node 18+.

### 2. Backend
```bash
cd backend
# Windows PowerShell: $env:DB_PASSWORD="yourpassword"      Linux/macOS: export DB_PASSWORD=yourpassword
mvn spring-boot:run
```
- The database `attendance_db` and all tables are created automatically (`ddl-auto=update`).
- A default admin is created on first start: **admin@college.com / Admin@123** (override with `ADMIN_EMAIL`, `ADMIN_PASSWORD`).
- Settings are in `backend/src/main/resources/application.properties`. Change `app.jwt.secret` for production.
- **Eclipse / STS 5:** import as *Existing Maven Project* (no Lombok, so no IDE plugin is needed). Use a JDK 21.

### 3. Frontend
```bash
cd frontend
npm install
npm run dev          # http://localhost:5173  (proxies /api to :8080)
```

### 4. Google login (optional)
Create an OAuth 2.0 *Web* client in Google Cloud Console with `http://localhost:5173` as an authorised JavaScript origin, then:
- backend: set `GOOGLE_CLIENT_ID`
- frontend: copy `.env.example` to `.env` and set `VITE_GOOGLE_CLIENT_ID`

The frontend gets a Google ID token; `POST /api/auth/google` verifies it with Google. Existing emails log in; unknown Google accounts are registered as students.



## Architecture
```
React (Login/Signup, Home, role dashboards, profile)
   │ Axios + REST (JSON, Bearer JWT)
Spring Boot: controller → service → repository → entity   (+ dto, security, config, exception)
   │ JPA / Hibernate
MySQL
```
Backend packages (`com.college.attendance`): `controller`, `service`, `repository`, `entity`, `dto`, `security`, `config`, `exception`.


## Roles and features
**Admin**: dashboard; create/update/deactivate students and faculty; create/manage courses and academic sessions; assign faculty to a course; manage enrolments; timetable; view all student and faculty attendance (and mark faculty attendance); course-wise reports; profile.
**Student**: register/login (email+password or Google); dashboard; view available courses, enrol, see assigned faculty; own attendance (summary + daily); timetable; profile.
**Faculty**: login; dashboard; assigned courses and enrolled students; mark students' attendance; mark own daily attendance; attendance reports; timetable; profile.

## Attendance business rules (enforced in `AttendanceService`)
1. Saturday and Sunday are automatically holidays.
2. Attendance cannot be marked on holidays (or future dates).
3. A faculty member can mark attendance only for their assigned courses (403 otherwise).
4. A student can have attendance only for a course they are enrolled in.
5. One record per student + course + class date (DB unique constraint; re-marking updates it).
6. Admin can view all attendance reports.
Statuses: `PRESENT`, `ABSENT`, `LEAVE`, `HOLIDAY` (HOLIDAY is automatic and cannot be set manually).
Faculty can only mark their own attendance for today; admin can mark/correct any working day.
