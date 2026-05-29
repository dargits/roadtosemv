# Short URL API Documentation

Base URL: `http://localhost:8080` (local) hoặc `https://your-app.onrender.com` (production)

## 📋 Table of Contents
- [Authentication](#authentication)
- [User Endpoints](#user-endpoints)
- [Link Endpoints](#link-endpoints)
- [Redirect Endpoint](#redirect-endpoint)
- [Error Responses](#error-responses)

---

## 🔐 Authentication

Hầu hết các endpoint yêu cầu authentication token trong header:

```
Authorization: your-token-here
```

Token được trả về sau khi login thành công và có hiệu lực 7 ngày.

---

## 👤 User Endpoints

### 1. Register (Đăng ký)

**Endpoint:** `POST /api/v1/users/register`

**Request Body:**
```json
{
  "account": "username123",
  "password": "password123",
  "userName": "John Doe",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**Validation:**
- `account`: Bắt buộc, 3-50 ký tự
- `password`: Bắt buộc, tối thiểu 6 ký tự
- `userName`: Tùy chọn, tối đa 100 ký tự
- `avatarUrl`: Tùy chọn, tối đa 255 ký tự

**Success Response (201):**
```json
{
  "code": 201,
  "message": "Register success, please sign in.",
  "data": null
}
```

**Error Response (400):**
```json
{
  "code": 400,
  "message": "Account is exists.",
  "data": null
}
```

---

### 2. Login (Đăng nhập)

**Endpoint:** `POST /api/v1/users/login`

**Request Body:**
```json
{
  "account": "username123",
  "password": "password123"
}
```

**Success Response (200):**
```json
{
  "code": 200,
  "message": "Login succes.",
  "token": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Error Response (400):**
```json
{
  "code": 400,
  "message": "Account or password incorrect.",
  "token": null
}
```

---

### 3. Logout (Đăng xuất)

**Endpoint:** `POST /api/v1/users/logout`

**Headers:**
```
Authorization: your-token-here
```

**Success Response (200):**
```json
{
  "code": 200,
  "message": "Logout successfully.",
  "data": null
}
```

**Error Response (401):**
```json
{
  "code": 401,
  "message": "Invalid token.",
  "data": null
}
```

---

### 4. Get Profile (Xem thông tin cá nhân)

**Endpoint:** `GET /api/v1/users/profile`

**Headers:**
```
Authorization: your-token-here
```

**Success Response (200):**
```json
{
  "code": 200,
  "message": "Get profile successfully.",
  "data": {
    "id": 1,
    "account": "username123",
    "userName": "John Doe",
    "avatarUrl": "https://example.com/avatar.jpg",
    "role": "USER",
    "createdAt": "2026-05-29T10:30:00"
  }
}
```

---

## 🔗 Link Endpoints

### 1. Create Short Link (Tạo link rút gọn)

**Endpoint:** `POST /api/v1/links/`

**Headers (Optional):**
```
Authorization: your-token-here
```

**Request Body:**
```json
{
  "original_link": "https://www.example.com/very/long/url",
  "custom_link": "mylink"
}
```

**Validation:**
- `original_link`: Bắt buộc, phải bắt đầu với http:// hoặc https://
- `custom_link`: Tùy chọn, 6-10 ký tự (chỉ dành cho user đã login)

**Success Response (201):**
```json
{
  "code": 201,
  "message": "Short link created successfully.",
  "data": "localhost:8080/abc123"
}
```

**Notes:**
- Nếu không có token: Tạo link ẩn danh (không có custom link)
- Nếu có token: Có thể tạo custom link và quản lý link sau này

---

### 2. Get My Links (Xem danh sách link của tôi)

**Endpoint:** `GET /api/v1/links/my-links`

**Headers:**
```
Authorization: your-token-here
```

**Success Response (200):**
```json
{
  "code": 200,
  "message": "Get links successfully.",
  "data": [
    {
      "id": 1,
      "originalUrl": "https://www.example.com/very/long/url",
      "shortKey": "abc123",
      "shortUrl": "localhost:8080/abc123",
      "createdAt": "2026-05-29T10:30:00"
    },
    {
      "id": 2,
      "originalUrl": "https://www.google.com",
      "shortKey": "mylink",
      "shortUrl": "localhost:8080/mylink",
      "createdAt": "2026-05-28T15:20:00"
    }
  ]
}
```

---

### 3. Get Link Analytics (Xem thống kê link)

**Endpoint:** `GET /api/v1/links/analytics/{linkId}`

**Headers:**
```
Authorization: your-token-here
```

**Path Parameters:**
- `linkId`: ID của link (số nguyên)

**Success Response (200):**
```json
{
  "code": 200,
  "message": "Get analytics successfully.",
  "data": {
    "linkId": 1,
    "shortKey": "abc123",
    "originalUrl": "https://www.example.com/very/long/url",
    "totalClicks": 150,
    "recentClicks": [
      {
        "ipAddress": "192.168.1.1",
        "country": "Vietnam",
        "status": "SUCCESS",
        "clickedAt": "2026-05-29T10:30:00"
      },
      {
        "ipAddress": "103.45.67.89",
        "country": "Singapore",
        "status": "SUCCESS",
        "clickedAt": "2026-05-29T09:15:00"
      }
    ]
  }
}
```

**Error Response (403):**
```json
{
  "code": 403,
  "message": "You don't have permission to view this analytics.",
  "data": null
}
```

---

### 4. Update Link (Cập nhật link)

**Endpoint:** `POST /api/v1/links/update/{linkId}`

**Headers:**
```
Authorization: your-token-here
```

**Path Parameters:**
- `linkId`: ID của link cần cập nhật

**Request Body:**
```json
{
  "original_link": "https://www.new-example.com/updated/url",
  "custom_link": "newlink"
}
```

**Success Response (200):**
```json
{
  "code": 200,
  "message": "Link updated successfully.",
  "data": "localhost:8080/newlink"
}
```

**Error Response (400):**
```json
{
  "code": 400,
  "message": "Custom link already exists.",
  "data": null
}
```

---

### 5. Delete Link (Xóa link)

**Endpoint:** `GET /api/v1/links/delete/{linkId}`

**Headers:**
```
Authorization: your-token-here
```

**Path Parameters:**
- `linkId`: ID của link cần xóa

**Success Response (200):**
```json
{
  "code": 200,
  "message": "Link deleted successfully.",
  "data": null
}
```

**Error Response (403):**
```json
{
  "code": 403,
  "message": "You don't have permission to delete this link.",
  "data": null
}
```

---

## 🔀 Redirect Endpoint

### Redirect to Original URL

**Endpoint:** `GET /{shortKey}`

**Path Parameters:**
- `shortKey`: Mã rút gọn (ví dụ: abc123, mylink)

**Success Response:**
- HTTP Status: `302 Found`
- Redirect đến URL gốc
- Tự động tracking IP và country

**Error Response:**
- HTTP Status: `404 Not Found`
- Không tìm thấy link

**Example:**
```
GET http://localhost:8080/abc123
→ Redirect to https://www.example.com/very/long/url
```

---

## ❌ Error Responses

### Common Error Codes

| Code | Meaning |
|------|---------|
| 400 | Bad Request - Dữ liệu không hợp lệ |
| 401 | Unauthorized - Chưa đăng nhập hoặc token không hợp lệ |
| 403 | Forbidden - Không có quyền truy cập |
| 404 | Not Found - Không tìm thấy resource |
| 500 | Internal Server Error - Lỗi server |

### Validation Error Response

```json
{
  "code": 400,
  "message": "Account must be between 3 and 50 characters",
  "data": null
}
```

---

## 📝 Notes for Frontend Developers

### 1. Token Management
- Lưu token sau khi login thành công (localStorage hoặc cookie)
- Gửi token trong header `Authorization` cho các request cần authentication
- Xóa token khi logout hoặc nhận lỗi 401

### 2. CORS
- API đã cấu hình CORS cho phép tất cả origins
- Có thể gọi từ bất kỳ domain nào

### 3. Anonymous Links
- User không cần đăng nhập vẫn có thể tạo link rút gọn
- Nhưng không thể custom link và không thể quản lý link sau này

### 4. Link Tracking
- Mỗi lần redirect sẽ tự động tracking IP và country
- Chỉ owner của link mới xem được analytics

### 5. Rate Limiting
- Hiện tại chưa có rate limiting
- Nên implement debounce ở frontend khi tạo link

---

## 🧪 Testing Examples

### Using cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "account": "testuser",
    "password": "test123",
    "userName": "Test User"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "account": "testuser",
    "password": "test123"
  }'
```

**Create Link (with token):**
```bash
curl -X POST http://localhost:8080/api/v1/links/ \
  -H "Content-Type: application/json" \
  -H "Authorization: your-token-here" \
  -d '{
    "original_link": "https://www.google.com",
    "custom_link": "google"
  }'
```

### Using JavaScript (Fetch API)

**Login:**
```javascript
const login = async (account, password) => {
  const response = await fetch('http://localhost:8080/api/v1/users/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ account, password }),
  });
  
  const data = await response.json();
  if (data.code === 200) {
    localStorage.setItem('token', data.token);
  }
  return data;
};
```

**Create Link:**
```javascript
const createLink = async (originalLink, customLink = null) => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/v1/links/', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': token }),
    },
    body: JSON.stringify({
      original_link: originalLink,
      ...(customLink && { custom_link: customLink }),
    }),
  });
  
  return await response.json();
};
```

**Get My Links:**
```javascript
const getMyLinks = async () => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/v1/links/my-links', {
    method: 'GET',
    headers: {
      'Authorization': token,
    },
  });
  
  return await response.json();
};
```

---

## 🚀 Quick Start for Frontend

1. **Setup Base URL:**
```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
```

2. **Create API Service:**
```javascript
class ShortUrlAPI {
  constructor(baseURL) {
    this.baseURL = baseURL;
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const token = localStorage.getItem('token');
    
    const config = {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': token }),
        ...options.headers,
      },
    };

    const response = await fetch(url, config);
    return await response.json();
  }

  // User endpoints
  register(data) {
    return this.request('/api/v1/users/register', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  login(data) {
    return this.request('/api/v1/users/login', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  logout() {
    return this.request('/api/v1/users/logout', { method: 'POST' });
  }

  getProfile() {
    return this.request('/api/v1/users/profile');
  }

  // Link endpoints
  createLink(data) {
    return this.request('/api/v1/links/', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  getMyLinks() {
    return this.request('/api/v1/links/my-links');
  }

  getLinkAnalytics(linkId) {
    return this.request(`/api/v1/links/analytics/${linkId}`);
  }

  updateLink(linkId, data) {
    return this.request(`/api/v1/links/update/${linkId}`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  deleteLink(linkId) {
    return this.request(`/api/v1/links/delete/${linkId}`);
  }
}

export default new ShortUrlAPI(API_BASE_URL);
```

3. **Usage in Components:**
```javascript
import api from './services/api';

// Register
const handleRegister = async (formData) => {
  const result = await api.register(formData);
  if (result.code === 201) {
    // Success
  }
};

// Login
const handleLogin = async (credentials) => {
  const result = await api.login(credentials);
  if (result.code === 200) {
    localStorage.setItem('token', result.token);
  }
};

// Create link
const handleCreateLink = async (url, customKey) => {
  const result = await api.createLink({
    original_link: url,
    custom_link: customKey,
  });
  if (result.code === 201) {
    console.log('Short URL:', result.data);
  }
};
```

---

**Version:** 1.0.0  
**Last Updated:** May 29, 2026
