# Family Tasks

A desktop task-management application built with **JavaFX 21**, implementing a parent-child reward system where a parent assigns tasks to children, and children earn a virtual coin balance upon task completion and approval.

---

## How to Run

### Prerequisites

- **Java 25** (or later) — [download from Oracle](https://www.oracle.com/java/technologies/downloads/) or use a manager like SDKMAN
- **JavaFX 21** — bundled via Maven dependencies (no separate install needed)

### Build & Launch

```bash
# Clone the repository
git clone https://github.com/your-repo/familytasks.git
cd familytasks

# Build the project (Maven Wrapper included — no global Maven required)
mvn clean install

# Run the application by running HelloApplication.java
```


### Default Credentials

On first launch (when no `data.json` exists), a seed account is created automatically:

| Role | Login | Password |
|------|-------|----------|
| Parent | `Parent` | `123` |

---

## Technology Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 25 |
| UI Framework | JavaFX | 21.0.6 |
| Build Tool | Apache Maven | Wrapper (mvnw) |
| Module System | Java Platform Module System (JPMS) | — |
| Theming | AtlantaFX (`PrimerDark`) + BootstrapFX | 2.0.1 / 0.4.0 |
| UI Components | ControlsFX, FormsFX, ValidatorFX, Ikonli | — |
| Serialization | Google Gson | 2.10.1 |
| Password Hashing | jBCrypt | 0.4 |
| Testing | JUnit Jupiter | 5.12.1 |

---

## Architecture

The project follows a classic **MVC (Model-View-Controller)** pattern, enforced by the Java Platform Module System.

```
src/main/
├── java/
│   ├── module-info.java                         # JPMS module descriptor
│   └── com/example/familytasks/
│       ├── HelloApplication.java                # Application entry point (JavaFX Application)
│       ├── Launcher.java                        # Main class wrapper (bypasses module classpath issues)
│       ├── models/
│       │   ├── User.java                        # User entity (id, name, password, role, balance, sessionToken)
│       │   ├── Task.java                        # Task entity (id, title, reward, childId, status)
│       │   └── Role.java                        # Enum: PARENT | CHILD
│       ├── services/
│       │   ├── DataService.java                 # Data persistence layer (Gson → data.json)
│       │   └── SessionService.java              # Session management (Gson → session.json)
│       └── controllers/
│           ├── LoginController.java
│           ├── ParentController.java
│           ├── ChildController.java
│           ├── AddChildController.java
│           ├── AddTaskController.java
│           ├── HistoryController.java
│           └── ProfileController.java
└── resources/
    └── com/example/familytasks/
        ├── login.fxml
        ├── parent_main.fxml
        ├── child_main.fxml
        ├── add_child_dialog.fxml
        ├── add_task_dialog.fxml
        ├── history_dialog.fxml
        └── profile_dialog.fxml
```

---

## Domain Model

### `User`
| Field | Type | Description |
|---|---|---|
| `id` | `int` | Auto-incremented unique identifier |
| `name` | `String` | Display name and login name |
| `password` | `String` | BCrypt-hashed password |
| `role` | `Role` | `PARENT` or `CHILD` |
| `balance` | `int` | Accumulated coin balance (children only) |
| `sessionToken` | `String` | UUID token for session validation |

### `Task`
| Field | Type | Description |
|---|---|---|
| `id` | `int` | Auto-incremented unique identifier |
| `title` | `String` | Task description |
| `reward` | `int` | Coin reward amount |
| `childId` | `int` | Foreign-key reference to assigned `User` |
| `status` | `String` | `NEW` → `PENDING` → `COMPLETED` |

---

## Task Lifecycle

```
[NEW] ──(child marks done)──▶ [PENDING] ──(parent approves)──▶ [COMPLETED]
                                              │
                                    (parent rejects)
                                              │
                                           [NEW]
```

- **NEW** — Task has been created by the parent and is awaiting action from the child.
- **PENDING** — Child has submitted the task for review.
- **COMPLETED** — Parent approved the task; the child's balance is incremented by the reward amount.
- Rejection resets the status back to **NEW** without any balance change.

---

## Service Layer

### `DataService`
- Manages a single in-memory `AppData` object containing all `User` and `Task` lists.
- Persists state to **`data.json`** in the working directory using Gson with pretty-printing.
- On first launch (no `data.json`), seeds a default `PARENT` account (`Parent` / `123`) with a BCrypt-hashed password.
- `authenticate(name, password)` — validates credentials via `BCrypt.checkpw`, then generates and stores a UUID session token.
- `getUserByToken(id, token)` — validates an in-memory session by matching both user ID and token; used to guard against stale sessions.

### `SessionService`
- Persists the active session (user ID + token) to **`session.json`**.
- On application startup, `getSavedSessionUser()` reads this file and cross-validates the token against `DataService`, providing automatic re-login ("Remember Me" functionality).
- `clearSession()` deletes `session.json` on explicit logout.

---

## Views & Controllers

| FXML View | Controller | Role Access | Responsibility |
|---|---|---|---|
| `login.fxml` | `LoginController` | All | Credential input, BCrypt validation, role-based routing |
| `parent_main.fxml` | `ParentController` | PARENT only | Children list, tasks table, full CRUD on tasks, approve/reject |
| `child_main.fxml` | `ChildController` | CHILD only | Personal task list filtered by `childId`, submit for review, balance display |
| `add_task_dialog.fxml` | `AddTaskController` | PARENT | Create or edit a task; reuses the same controller via `setTaskData(task)` |
| `add_child_dialog.fxml` | `AddChildController` | PARENT | Register a new child account with BCrypt-hashed password |
| `history_dialog.fxml` | `HistoryController` | Both | Completed tasks; PARENT sees all, CHILD sees only their own |
| `profile_dialog.fxml` | `ProfileController` | Both | Update display name and/or password (re-hashed with BCrypt on save) |

### Scene Routing
`HelloApplication.changeScene(String fxml)` performs in-place scene replacement on the existing `Stage` without opening a new window. Modal dialogs (`add_task_dialog`, `add_child_dialog`, `history_dialog`, `profile_dialog`) are opened as separate `Stage` instances with `Modality.APPLICATION_MODAL` and block the parent window until closed.

---

## Security

- All passwords are hashed using **BCrypt** (via `jbcrypt 0.4`) with a random salt. Plain-text passwords are never stored.
- Sessions are validated on every protected action by matching both the persisted `userId` and the UUID `sessionToken` against the in-memory data store. A stale or tampered `session.json` file will fail validation and fall back to the login screen.
- The default seed account (`Parent` / `123`) is only created on the very first launch when no `data.json` exists.

---

## Data Persistence

All application state is stored in two JSON files created in the **current working directory** at runtime:

| File | Content |
|---|---|
| `data.json` | Full application state — all users and all tasks |
| `session.json` | Active session — `{ activeUserId, token }` |

These files are not bundled with the application and are generated at runtime.

---

## Module System (JPMS)

The application is fully modularized under `module com.example.familytasks`. Key declarations in `module-info.java`:

- `opens com.example.familytasks.models to com.google.gson` — required for Gson reflective serialization/deserialization.
- `opens com.example.familytasks.controllers to javafx.fxml` — required for FXML's reflective controller injection via `@FXML`.
- `opens com.example.familytasks.services to com.google.gson` — required for Gson to access inner `AppData` and `SessionData` classes.
