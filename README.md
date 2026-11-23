# Campus Register: Student & Faculty Attendance Management

Full-stack project built from the requirement screenshots.

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.3, Spring Security (JWT + Google OAuth2), Spring Data JPA + Hibernate, MySQL, Maven |
| Frontend | React 18, Vite, Axios, React Router, Bootstrap 5, npm |
| Testing | Postman (collection of endpoints below) |

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

### 5. Password reset
No mail server is configured. `POST /api/auth/forgot-password` writes the reset link to the **backend console log**. Replace the `log.info` line in `AuthService.forgotPassword` with `JavaMailSender` to send real emails.

## Architecture
```
React (Login/Signup, Home, role dashboards, profile)
   │ Axios + REST (JSON, Bearer JWT)
Spring Boot: controller → service → repository → entity   (+ dto, security, config, exception)
   │ JPA / Hibernate
MySQL
```
Backend packages (`com.college.attendance`): `controller`, `service`, `repository`, `entity`, `dto`, `security`, `config`, `exception`.

## Database (MySQL)
| Table | Purpose |
|---|---|
| `users` | id, name, email, password (BCrypt), phone, role, status |
| `student_profile` | user_id, roll_no, department |
| `faculty_profile` | user_id, employee_id, department |
| `courses` | id, course_name, course_code, credits, active, session_id |
| `student_assigned_courses` | student_id, course_id, status (ENROLLED/DROPPED) |
| `faculty_assigned_courses` | faculty_id, course_id |
| `academic_sessions` | name, start/end date, current |
| `password_reset_token` | token, user_id, expires_at, used |
| `timetable` | course_id, day_of_week, start/end time, room |
| `student_attendance` | student_id, course_id, class_date, status. **Unique (student, course, class_date)** |
| `faculty_attendance` | faculty_id, attendance_date, status. Unique (faculty, date) |

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

## REST API (all JSON, `Authorization: Bearer <token>` except `/api/auth/**`)
```
POST /api/auth/login | register | google | forgot-password | reset-password
GET/PUT /api/profile

ADMIN   /api/admin/dashboard
        /api/admin/students            GET POST | PUT /{id} | PATCH /{id}/status?active=
        /api/admin/faculty             GET POST | PUT /{id} | PATCH /{id}/status?active=
        /api/admin/courses             GET POST | PUT /{id} | PATCH /{id}/status?active=
        PUT /api/admin/courses/{courseId}/faculty/{facultyId} | DELETE /api/admin/courses/{courseId}/faculty
        /api/admin/enrolments          GET POST | DELETE ?studentId=&courseId=
        /api/admin/timetable           GET POST | DELETE /{id}
        /api/admin/sessions            GET POST
        GET /api/admin/attendance/students?courseId&studentId&from&to
        GET/POST /api/admin/attendance/faculty
        GET /api/admin/reports/courses

STUDENT /api/student/dashboard | courses | courses/available | attendance | attendance/summary | timetable
        POST /api/student/courses/{courseId}/enrol

FACULTY /api/faculty/dashboard | courses | courses/{id}/students | timetable | reports?courseId
        GET/POST /api/faculty/attendance        (POST body: {courseId, date, records:[{studentId,status}]})
        GET /api/faculty/attendance/history?courseId&from&to
        GET/POST /api/faculty/my-attendance     (POST body: {status})
```

## Quick walkthrough
1. Log in as admin → add an academic session (one exists by default) → add a **faculty** and a **student** → add a **course** and assign the faculty.
2. Admin → Enrolments: enrol the student. Admin → Timetable: add a Monday–Friday slot.
3. Log in as faculty → Mark attendance (pick course and a weekday) → Save.
4. Log in as student → My attendance shows the record and percentage.

## Notes
- The frontend was built and verified in the sandbox (`vite build`). The backend could not be compiled there (no access to Maven Central), so run `mvn clean package` first and send me any compile or runtime error you see.
- Students who sign up with Google have no roll number or department until an admin sets them.
