# Mail Management System (Java Swing)

A beginner-friendly desktop mail application built with Core Java and Swing. Registered users can exchange messages inside the application. Messages and accounts are saved locally, so data remains available after the application is closed.

> **Scope:** This is an internal messaging application, not an SMTP client. It does not deliver messages to Gmail or external email providers.

## Features
- User registration and login (passwords are stored as SHA-256 hashes)
- Inbox, Sent, Drafts and Trash folders
- Compose, send, save and edit drafts
- Read messages and automatically mark inbox messages as read
- Search the current folder by sender/recipient email or subject
- Delete messages to Trash and restore them
- Local persistence using Java object file handling
- Swing desktop interface

## Technologies
- Java 17 or later
- Java Swing and AWT
- ArrayList / List collections
- Object-oriented programming
- Exception handling
- Java serialization for local file storage
- SHA-256 via `MessageDigest`

## Project structure
```text
MailManagementSystem/
├── src/mailapp/
│   ├── Main.java
│   ├── User.java
│   ├── Email.java
│   ├── DataStore.java
│   ├── FileManager.java
│   ├── MailService.java
│   ├── LoginFrame.java
│   ├── Dashboard.java
│   └── ComposeMail.java
├── data/                  # Created/used automatically
├── docs/ProjectDocumentation.md
├── README.md
├── .gitignore
└── run.sh
```

## Requirements
- JDK 17 or later installed
- VS Code with Extension Pack for Java, or IntelliJ IDEA
- Desktop environment capable of displaying Swing windows

Check Java installation:
```bash
java -version
javac -version
```

## Run on macOS / Linux
From the project root directory:
```bash
chmod +x run.sh
./run.sh
```

Or compile and run manually:
```bash
mkdir -p out
javac -d out src/mailapp/*.java
java -cp out mailapp.Main
```

## Run on Windows
Open Command Prompt in the project root:
```bat
if not exist out mkdir out
javac -d out src\mailapp\*.java
java -cp out mailapp.Main
```

## First-time usage / demo
1. Start the application.
2. Select **Create account** and register two users with different email addresses.
3. Log in as the first user and compose a message addressed to the second user's registered email.
4. Log out and sign in as the second user to view the message in Inbox.
5. Try reading, searching, deleting and restoring the message. Save a draft and test it too.

The data file is created at `data/mail-data.ser` relative to the directory from which the app is launched. Back it up before deleting or moving it. To reset the demo, close the application and delete that file.

## Screenshots
Add screenshots of your own running application here before submission:

| Login | Dashboard | Compose |
|---|---|---|
| `docs/screenshots/login.png` | `docs/screenshots/dashboard.png` | `docs/screenshots/compose.png` |

## Design notes
- `User` and `Email` model the application's entities.
- `MailService` contains validation and mail operations.
- `FileManager` isolates persistence from the user interface.
- `Dashboard`, `LoginFrame` and `ComposeMail` are Swing presentation classes.
- `DataStore` is serialized to one local file.

## Limitations and security note
This is a college demonstration project. SHA-256 hashing here is educational and is **not** a production password-storage design; real applications should use a salted, deliberately slow password-hashing algorithm such as Argon2id, scrypt or PBKDF2. Java serialization is also intended only for this trusted local demo file. Do not load serialized files from untrusted sources. The application has no network transport, encryption-in-transit, attachments or external email delivery.

## Future scope
- Add SMTP/IMAP integration for real email delivery
- Add file attachments
- Add stronger password hashing and account recovery
- Add pagination, message labels and notifications
- Replace serialized storage with a relational database

## License
For educational use. Add a license if you plan to distribute the project publicly.
