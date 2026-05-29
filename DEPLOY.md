# Hướng dẫn Deploy lên Render

## Bước 1: Chuẩn bị Database MySQL trên Render

1. Tạo MySQL database trên Render (hoặc dùng service khác như PlanetScale, Railway)
2. Lấy connection string

## Bước 2: Cấu hình Environment Variables trên Render

Khi tạo Web Service trên Render, thêm các biến môi trường sau:

```
SPRING_DATASOURCE_URL=jdbc:mysql://[host]:[port]/[database]?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_DATA_REDIS_URL=rediss://[redis-host]:[port]
SYSTEM_URL=https://your-app.onrender.com/
```

## Bước 3: Deploy

### Cách 1: Sử dụng render.yaml (Tự động)

1. Push code lên GitHub
2. Kết nối repository với Render
3. Render sẽ tự động đọc file `render.yaml` và deploy

### Cách 2: Manual Deploy

1. Vào Render Dashboard
2. Chọn "New" → "Web Service"
3. Kết nối GitHub repository
4. Chọn:
   - **Runtime**: Docker
   - **Region**: Singapore (hoặc gần bạn nhất)
   - **Branch**: main
   - **Dockerfile Path**: ./Dockerfile
5. Thêm Environment Variables (như bước 2)
6. Click "Create Web Service"

## Bước 4: Kiểm tra

Sau khi deploy xong, truy cập:
- Health check: `https://your-app.onrender.com/actuator/health`
- Test API: `https://your-app.onrender.com/api/v1/users/register`

## Lưu ý

- Free tier của Render sẽ sleep sau 15 phút không hoạt động
- Lần đầu truy cập sau khi sleep sẽ mất ~30s để wake up
- Nên dùng `spring.jpa.hibernate.ddl-auto=update` để tự động tạo bảng
