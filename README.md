# 🔗 Short URL Service
 
Web: https://shorturl-chi-tan.vercel.app/

Hệ thống rút gọn URL hiện đại với tính năng tracking và analytics, được xây dựng bằng Spring Boot 4.0.6.

## ✨ Tính năng

- 🔐 **Xác thực người dùng** - Đăng ký, đăng nhập với BCrypt
- 🔗 **Rút gọn URL** - Tạo link ngắn gọn với mã 6 ký tự ngẫu nhiên
- 👤 **Quản lý link** - Xem danh sách link của bạn
- 📊 **Analytics** - Theo dõi clicks với IP, country, timestamp
- ⚡ **Redis Cache** - Token session và tracking real-time
- 🌍 **GeoIP** - Tự động phát hiện quốc gia từ IP
- 🔄 **Auto Sync** - Scheduled job đồng bộ analytics mỗi 5 phút

## 🛠️ Tech Stack

- **Backend:** Spring Boot 4.0.6, Java 17
- **Database:** MySQL 8.0
- **Cache:** Redis Cloud (Render)
- **Security:** BCrypt, Token-based Authentication
- **Build Tool:** Maven
- **Libraries:** Lombok, Jackson, Spring Data JPA

## 📋 Yêu cầu

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis (local hoặc cloud)

## 🚀 Cài đặt

### 1. Clone repository

```bash
git clone https://github.com/dargits/shorturl.git
cd shorturl
```

### 2. Cấu hình database

Tạo database MySQL:

```sql
CREATE DATABASE roadtosemv;
```

Cập nhật `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/roadtosemv
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Cấu hình Redis

**Option 1: Redis Local**
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

**Option 2: Redis Cloud (Render)**
```properties
spring.data.redis.url=rediss://your-redis-url
```

### 4. Build và chạy

```bash
mvn clean install
mvn spring-boot:run
```

Server sẽ chạy tại: `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

---

## 🔐 Authentication APIs

### 1. Đăng ký

**Endpoint:** `POST /api/v1/users/register`

**Request Body:**
```json
{
  "account": "user123",
  "password": "password123",
  "userName": "John Doe",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**Validation:**
- `account`: 3-50 ký tự, bắt buộc
- `password`: tối thiểu 6 ký tự, bắt buộc
- `userName`: tối đa 100 ký tự
- `avatarUrl`: tối đa 255 ký tự

**Response:**
```json
{
  "code": 201,
  "message": "Register success, please sign in.",
  "data": null
}
```

---

### 2. Đăng nhập

**Endpoint:** `POST /api/v1/users/login`

**Request Body:**
```json
{
  "account": "user123",
  "password": "password123"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Login succes.",
  "token": "a3f5c8d2-4b7e-4a1c-9d3f-8e2b1c4a5d6e"
}
```

**Note:** Token có hiệu lực 7 ngày

---

## 🔗 Short Link APIs

### 3. Tạo link rút gọn

**Endpoint:** `POST /api/v1/links/`

**Headers:**
```
Authorization: {token}  (optional)
```

**Request Body:**
```json
{
  "original_link": "https://example.com/very-long-url",
  "custom_link": "mylink"
}
```

**Validation:**
- `original_link`: URL hợp lệ (http/https), bắt buộc
- `custom_link`: 6-10 ký tự (tùy chọn, chưa implement)

**Response:**
```json
{
  "code": 201,
  "message": "Short link created successfully.",
  "data": "localhost:8080/abc123"
}
```

**Note:** 
- Nếu có token → link được gán cho user
- Nếu không có token → link ẩn danh

---

### 4. Xem danh sách links của tôi

**Endpoint:** `GET /api/v1/links/my-links`

**Headers:**
```
Authorization: {token}  (required)
```

**Response:**
```json
{
  "code": 200,
  "message": "Get links successfully.",
  "data": [
    {
      "id": 1,
      "originalUrl": "https://example.com/very-long-url",
      "shortKey": "abc123",
      "shortUrl": "localhost:8080/abc123",
      "createdAt": "2026-05-20T10:30:45"
    },
    {
      "id": 2,
      "originalUrl": "https://google.com",
      "shortKey": "xyz789",
      "shortUrl": "localhost:8080/xyz789",
      "createdAt": "2026-05-20T11:15:30"
    }
  ]
}
```

---

### 5. Redirect (Sử dụng link rút gọn)

**Endpoint:** `GET /{shortKey}`

**Example:** `GET /abc123`

**Response:** HTTP 302 Redirect đến URL gốc

**Note:** Mỗi lần truy cập sẽ được tracking (IP, country, timestamp)

---

## 📊 Analytics APIs

### 6. Xem analytics của link

**Endpoint:** `GET /api/v1/links/analytics/{linkId}`

**Headers:**
```
Authorization: {token}  (required)
```

**Example:** `GET /api/v1/links/analytics/1`

**Response:**
```json
{
  "code": 200,
  "message": "Get analytics successfully.",
  "data": {
    "linkId": 1,
    "shortKey": "abc123",
    "originalUrl": "https://example.com/very-long-url",
    "totalClicks": 150,
    "recentClicks": [
      {
        "ipAddress": "192.168.1.1",
        "country": "Vietnam",
        "status": "SUCCESS",
        "clickedAt": "2026-05-20T10:30:45"
      },
      {
        "ipAddress": "103.45.67.89",
        "country": "United States",
        "status": "SUCCESS",
        "clickedAt": "2026-05-20T10:31:12"
      }
    ]
  }
}
```

**Note:** Chỉ owner của link mới xem được analytics

---

## 🧪 Testing APIs

### 7. Test Redis Connection

**Endpoint:** `GET /api/redis-test/run`

**Response:** HTML page hiển thị kết quả test Redis

---

## 🔒 Error Responses

### 400 Bad Request
```json
{
  "code": 400,
  "message": "Original URL cannot be empty.",
  "data": null
}
```

### 401 Unauthorized
```json
{
  "code": 401,
  "message": "Unauthorized. Please login.",
  "data": null
}
```

### 403 Forbidden
```json
{
  "code": 403,
  "message": "You don't have permission to view this analytics.",
  "data": null
}
```

### 404 Not Found
```json
{
  "code": 404,
  "message": "Link not found.",
  "data": null
}
```

---

## 📁 Cấu trúc dự án

```
src/main/java/semv/shorturl/
├── controller/          # REST Controllers
│   ├── UserController.java
│   ├── ShortLinkController.java
│   ├── GetLinkController.java
│   └── RedisTestController.java
├── service/            # Business Logic
│   ├── UserService.java
│   ├── ShortLinkService.java
│   ├── RedisService.java
│   └── GeoIpService.java
├── repository/         # Data Access Layer
│   ├── UserRepository.java
│   ├── ShortLinkRepository.java
│   └── LinkAnalysticsRepository.java
├── entity/            # JPA Entities
│   ├── User.java
│   ├── ShortLink.java
│   └── LinkAnalytics.java
├── dto/               # Data Transfer Objects
│   ├── request/
│   └── response/
├── exception/         # Custom Exceptions
│   ├── ExistsException.java
│   ├── NotFoundException.java
│   ├── OverloadException.java
│   └── handel/
│       └── GlobalHandelException.java
└── scheduler/         # Scheduled Jobs
    └── AnalyticsScheduler.java
```

---

## ⚙️ Cấu hình nâng cao

### Thay đổi thời gian sync analytics

Mặc định: 5 phút (300,000ms)

File: `src/main/java/semv/shorturl/scheduler/AnalyticsScheduler.java`

```java
@Scheduled(fixedRate = 300000) // Đổi thành 600000 cho 10 phút
```

### Thay đổi độ dài short key

Mặc định: 6 ký tự

File: `src/main/java/semv/shorturl/service/impl/ShortLinkServiceImpl.java`

```java
for (int i = 0; i < 6; i++) { // Đổi thành 8 cho mã dài hơn
```

### Thay đổi TTL token

Mặc định: 7 ngày

File: `src/main/java/semv/shorturl/service/RedisService.java`

```java
redis.opsForValue().set("token:" + token, userId + ":" + role, 7, TimeUnit.DAYS);
```

---

## 🌐 GeoIP Service

Dự án sử dụng API miễn phí từ [ipwho.is](https://ipwho.is) để phát hiện quốc gia.

**Giới hạn:** 10,000 requests/tháng

**Cải tiến:** Có thể tích hợp MaxMind GeoIP2 database để tra cứu offline.

---

## 🐛 Troubleshooting

### Redis connection timeout

**Vấn đề:** Redis ở Singapore, latency cao

**Giải pháp:**
- Development: Dùng Redis local
- Production: Deploy backend cùng region với Redis

### Database connection error

**Kiểm tra:**
```bash
mysql -u root -p
USE roadtosemv;
SHOW TABLES;
```

### Port 8080 đã được sử dụng

Đổi port trong `application.properties`:
```properties
server.port=8081
```

---

## 📈 Performance Tips

1. **Redis Connection Pool** - Đã cấu hình trong `application.properties`
2. **Batch Insert** - Analytics sử dụng `saveAll()` thay vì `save()`
3. **Async Tracking** - Tracking không block redirect
4. **Index Database** - Đảm bảo index trên `shortKey`, `userId`

---

## 🤝 Contributing

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

---

## 📝 License

Dự án này được phát hành dưới MIT License.

---

## 👨‍💻 Author

**SEMV Team**

- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Redis](https://redis.io/)
- [ipwho.is](https://ipwho.is)
- [Lombok](https://projectlombok.org/)

---

**⭐ Nếu dự án hữu ích, hãy cho một star nhé!**
