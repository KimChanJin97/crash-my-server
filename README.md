# Crush My Server

<p align="center">
 <strong>Stress test and monitor your server in real-time!</strong>
</p>

## 🎯 Introduction

This is an interactive platform for server stress testing and real-time monitoring. Users can put load on the server and observe the results in real-time.

## ✨ Features

- 🖱 **Request Button**: Put load on the server with a single click.
- 💬 **Real-time Chat**: Communicate with other users in real-time.
- 🏆 **Ranking System**: Check who has put the most load on the server.

## 🚀 Getting Started

### Prerequisites

- Node.js (v18 이상)
- Yarn

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/KimChanJin97/crashMyServer-be.git
   cd crush-my-server
   ```

2. Write application.yml

   ```bash
   spring:
     jpa:
       properties: ...
     datasource:
       url: ...
     kafka:
       consumer: ...
       producer: ...
     data:
       redis: ...
       mongodb: ...
   ```

4. Write docker-compose.yml

   ```bash
   version: '3'

   services:
     zookeeper: ...
     kafka: ...
     mysql: ...
     mongodb: ...
     redis: ...
   ```

4. Run Modules

   ```bash
   docker-compose -f docker-compose.yml up -d
   ```

5. Run the server

🎉 Now you can check the APIs at http://localhost:8080/swagger-ui.html

## 💻 Usage

1. Click the 'Request' button on the main page to put load on the server.
2. Use the chat window to communicate with other users in real-time.
3. Check the ranking board to see the top records!

## 📸 Screenshot
<table>
  <tr>
    <td align="center">
        <img src="https://github.com/user-attachments/assets/2c05e23f-1442-435e-8496-86a9b0da46a5" width="300px;" alt="login"/>
    </td>
   <td align="center">
        <img src="https://github.com/user-attachments/assets/97d00edc-ce13-46ff-bb2f-4ab8423eed07" width="300px;" alt="home"/>
    </td>
  </tr>
</table>

## 🛠 Tech Stack

- Frontend: React, Vite, Styled-Components
- Backend: Spring Boot, MySQL, MongoDB, Redis, Kafka, Nginx
- Real-time Communication: WebSocket
- Deployment: Vercel (Frontend), AWS EC2 (Backend)
- Monitor: Spring Actuator, Prometheus, Grafana (Backend)

## 👥 Contributors

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/hotbreakb">
        <img src="https://github.com/hotbreakb.png" width="100px;" alt="hotbreakb"/><br />
        <sub><b>hotbreakb</b></sub>
      </a><br />
      Frontend Developer
    </td>
    <td align="center">
      <a href="https://github.com/KimChanJin97">
        <img src="https://github.com/KimChanJin97.png" width="100px;" alt="Kim Chan Jin"/><br />
        <sub><b>Kim Chan Jin</b></sub>
      </a><br />
      Backend Developer
    </td>
  </tr>
</table>

## 🔗 Related Projects

- [Frontend Repository](https://github.com/KimChanJin97/crush-my-server-fe)

## 📄 License

This project is under the MIT License.

---

<p align="center">
  Made with ❤️ by the Crush My Server Team
</p>
