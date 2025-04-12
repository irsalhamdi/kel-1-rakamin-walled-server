# 💰 Walled – Digital Wallet Backend

Walled adalah aplikasi backend dompet digital berbasis Spring Boot yang terintegrasi dengan database MySQL Aiven dan memiliki fitur seperti registrasi, login, transfer saldo, riwayat transaksi, dan pengiriman email notifikasi.

---

## 🚀 Features

- 🔐 User Registration & Authentication (JWT)
- 💸 Transfer antar pengguna
- 📄 Transaction History
- 📬 Email Notification (Mailtrap)
- 🧩 Modular Controller, Service, Repository
- 📦 Dockerized & Deployable via Render

---

## 🛠️ Tech Stack

| Layer      | Technology    |
| ---------- | ------------- |
| Language   | Java 17       |
| Framework  | Spring Boot   |
| Build Tool | Maven         |
| Database   | MySQL (Aiven) |
| Auth       | JWT           |
| Email      | Mailtrap SMTP |
| Container  | Docker        |
| Deployment | Render        |

---

# 💰 Walled – Digital Wallet Backend

Walled adalah aplikasi backend dompet digital berbasis Spring Boot yang terintegrasi dengan database MySQL Aiven dan memiliki fitur seperti registrasi, login, transfer saldo, riwayat transaksi, dan pengiriman email notifikasi.

---

## 🚀 Features

- 🔐 User Registration & Authentication (JWT)
- 💸 Transfer antar pengguna
- 📄 Transaction History
- 📬 Email Notification (Mailtrap)
- 🧩 Modular Controller, Service, Repository
- 📦 Dockerized & Deployable via Render

---

## 🛠️ Tech Stack

| Layer      | Technology    |
| ---------- | ------------- |
| Language   | Java 17       |
| Framework  | Spring Boot   |
| Build Tool | Maven         |
| Database   | MySQL (Aiven) |
| Auth       | JWT           |
| Email      | Mailtrap SMTP |
| Container  | Docker        |
| Deployment | Render        |

---

## ⚙️ Environment Variables (`.env`)

```env
DB_URL="DB_URL"
DB_USERNAME="DB_USERNAME"
DB_PASSWORD="DB_PASSWORD"

JWT_SECRET=walled
JWT_EXPIRATION=86400000

MAIL_HOST=smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=your_mailtrap_username
MAIL_PASSWORD=your_mailtrap_password
```

---

### Docker, Run, API, Deployment, Author

````markdown
---

🐳 Docker Build & Run

```bash
# Build image
docker build -t walled-app .

# Run container with env file
docker run --env-file .env -p 8080:8080 walled-app
```
````

## Pengembang

```bash
Irsal Hamdi - [GitHub](https://github.com/irsalhamdi)
```
