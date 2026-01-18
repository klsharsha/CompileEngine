# CompileEngine  (Online Compiler System) (Backend + Frontend)

A secure, asynchronous **online compiler system** that executes untrusted user code inside **isolated Docker containers**, enforcing **time and memory limits**, and supporting **multiple programming languages**.

This project focuses on **backend system design, sandboxing, and async processing**, rather than deployment or UI polish.

---

## 🚀 Features

* ✅ Multi-language support

  * Java
  * Python
  * C
  * C++
  * JavaScript

* 🔄 Asynchronous execution using RabbitMQ

* 🐳 Secure Docker-based sandbox per execution

* ⏱️ Time Limit Enforcement (TLE)

* 💾 Memory Limit Enforcement (MLE)

* 📥 STDIN / STDOUT support

* ❌ Compile-time & runtime error classification

* 🗄️ Persistent submission storage using MySQL

---

## 🏗️ High-Level Architecture

Client (React)
→ Spring Boot REST API
→ MySQL (store submissions)
→ RabbitMQ (execution queue)
→ Execution Consumer
→ Docker Sandbox (language-specific runner)

Execution is **fully asynchronous** to ensure API responsiveness and system stability.

---

## 🔐 Security & Sandboxing

Each code execution runs inside a **short-lived Docker container** with strict isolation:

* `--network none` → no internet access
* `--memory` & `--memory-swap` → memory limits
* `--pids-limit` → process limit
* Temporary filesystem (auto-cleaned)
* No host access except mounted working directory

This design prevents:

* Infinite loops
* Fork bombs
* Network abuse
* Host filesystem access

---

## ⚙️ Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Data JPA
* RabbitMQ
* MySQL
* Docker

### Frontend

* React
* Fetch API (polling-based async updates)

---
## 📁 Repository Structure

```text
online-compiler/
├── backend/
├── frontend/
├── dockercompilers/
│   ├── java-runner/
│   ├── python-runner/
│   ├── c-runner/
│   ├── cpp-runner/
│   ├── javascript-runner/
│   └── rabbitmq/
├── docker-compose.yml
├── docs/
│   ├── execution-flow.md
│   └── security.md
└── README.md

```
## 🧪 How It Works (Execution Flow)

1. Client submits code via REST API
2. Submission is saved to MySQL with status `PENDING`
3. Job ID is pushed to RabbitMQ
4. Consumer picks job and runs code in Docker
5. Status transitions:

PENDING → RUNNING → SUCCESS / ERROR / TLE / MLE

6. Client polls submission status asynchronously

---

## ▶️ Running the Project Locally

### Prerequisites

* Java 17
* Maven
* Node.js
* Docker
* Local MySQL installed and running

---

### 1️⃣ Start Docker Services (RabbitMQ + Runners)

```bash
docker-compose up -d
```

RabbitMQ UI:
[http://localhost:15672](http://localhost:15672)
username: guest
password: guest

---

### 2️⃣ Configure MySQL (Local)

```sql
CREATE DATABASE compiler;
CREATE USER 'compiler'@'localhost' IDENTIFIED BY 'compiler';
GRANT ALL PRIVILEGES ON compiler.* TO 'compiler'@'localhost';
FLUSH PRIVILEGES;
```

---

### 3️⃣ Run Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs at:
[http://localhost:8080](http://localhost:8080)

---

### 4️⃣ Run Frontend

```bash
cd frontend
npm install
npm start
```

Frontend runs at:
[http://localhost:3000](http://localhost:3000)

---

## 🧪 Sample API Request

```json
POST /api/submit
{
  "language": "JAVA",
  "code": "public class Main { public static void main(String[] args){ System.out.println(\"Hello World\"); }}",
  "input": ""
}
```

---

## 🎥 Demo Video

A full walkthrough video demonstrating:

* Architecture
* Async execution
* Input handling
* Time & memory limits
* Error classification

📺 Demo Video Link:
[[Youtube Link](https://youtu.be/3-FFXjSXU_I)]

---

## ❓ Why No Public Deployment?

This system executes **untrusted user code** and requires **Docker-level sandboxing**, which is not supported on most free PaaS platforms.

Instead, the project is designed to be:

* Fully reproducible locally
* Secure by default
* Easy to evaluate via source code and demo video

This is a **conscious architectural decision**, not a limitation.

---

## 📌 Future Improvements

* WebSocket-based live output streaming
* Per-language configurable limits
* Authentication & rate limiting
* Kubernetes-based runner scaling

---

## 🏁 Conclusion

This project demonstrates:

* Real-world backend system design
* Secure sandboxed execution
* Asynchronous processing
* Docker & message queue integration

It is intended as a **learning-focused** rather than a hosted product.

---

### 👨‍💻 Author

KLSHarsha
