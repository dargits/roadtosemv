# Hướng dẫn Deploy lên Render

## ✅ Database đã được cấu hình

Database PostgreSQL từ Render đã được tích hợp vào dự án:
- **Host:** dpg-d8c1ba77f7va7df7d7hg-a
- **Port:** 5432
- **Database:** roadtosemv_db
- **Username:** roadtosemv_db_user

## Bước 1: Push code lên GitHub

```bash
git add .
git commit -m "Add PostgreSQL config and Docker support"
git push origin main
```

## Bước 2: Deploy trên Render

### Cách 1: Sử dụng render.yaml (Tự động)

1. Vào Render Dashboard: https://dashboard.render.com
2. Click "New" → "Blueprint"
3. Kết nối GitHub repository
4. Render sẽ tự động đọc file `render.yaml` và deploy

### Cách 2: Manual Deploy

1. Vào Render Dashboard
2. Chọn "New" → "Web Service"
3. Kết nối GitHub repository
4. Cấu hình:
   - **Name**: shorturl-api
   - **Runtime**: Docker
   - **Region**: Singapore
   - **Branch**: main
   - **Dockerfile Path**: ./Dockerfile
   - **Instance Type**: Free

5. Thêm Environment Variables:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d8c1ba77f7va7df7d7hg-a:5432/roadtosemv_db
SPRING_DATASOURCE_USERNAME=roadtosemv_db_user
SPRING_DATASOURCE_PASSWORD=BVbwazavbLJhUmUzlH54OEwm5e6Pp8j
SPRING_DATA_REDIS_URL=rediss://red-d860bl8js32c73ar6gqg:jSqcimbrK3Ik8zzkWHXpsLuqCFHJVWop@singapore-keyvalue.render.com:6379
SYSTEM_URL=https://your-app.onrender.com/
```

6. Click "Create Web Service"

## Bước 3: Kiểm tra

Sau khi deploy xong (khoảng 5-10 phút), truy cập:

- **Health check:** `https://your-app.onrender.com/actuator/health`
- **Test Register:** `https://your-app.onrender.com/api/v1/users/register`
- **Test Redis:** `https://your-app.onrender.com/api/redis-test/run`

## Bước 4: Cập nhật SYSTEM_URL

Sau khi có URL từ Render, cập nhật biến môi trường:

1. Vào Web Service Settings
2. Environment → Edit
3. Thay `SYSTEM_URL` thành URL thực tế (ví dụ: `https://shorturl-api-abc123.onrender.com/`)
4. Save Changes (service sẽ tự động restart)

## 📝 Lưu ý quan trọng

### Database
- ✅ PostgreSQL đã được cấu hình sẵn
- ✅ Hibernate sẽ tự động tạo bảng khi khởi động lần đầu
- ✅ Dữ liệu sẽ được lưu trữ vĩnh viễn trên Render

### Redis
- ✅ Redis đã được cấu hình sẵn
- ✅ Dùng để lưu token session (7 ngày)

### Free Tier Limitations
- Service sẽ sleep sau 15 phút không hoạt động
- Lần đầu truy cập sau khi sleep mất ~30-50s để wake up
- Database free tier có giới hạn 1GB storage

### Build Time
- Lần build đầu tiên mất ~5-10 phút (download dependencies)
- Các lần build sau nhanh hơn nhờ Docker cache

## 🔧 Troubleshooting

### Lỗi kết nối Database
```
Kiểm tra:
1. Database service đang chạy trên Render
2. Environment variables đúng
3. Firewall/Network settings
```

### Lỗi Redis
```
Kiểm tra:
1. Redis service đang chạy
2. SPRING_DATA_REDIS_URL đúng format
3. SSL certificate (dùng rediss:// không phải redis://)
```

### Service không start
```
Xem logs:
1. Vào Web Service Dashboard
2. Click "Logs" tab
3. Tìm error message
```

## 🚀 Sau khi Deploy thành công

1. **Test API:** Dùng Postman hoặc cURL test các endpoint
2. **Update Frontend:** Thay đổi API_BASE_URL trong frontend
3. **Monitor:** Theo dõi logs và metrics trên Render Dashboard

## 📊 Monitoring

Render cung cấp:
- **Logs:** Real-time logs
- **Metrics:** CPU, Memory, Request count
- **Health checks:** Tự động ping `/actuator/health` mỗi 5 phút

---

**Thời gian deploy:** ~5-10 phút  
**Database:** PostgreSQL (Render)  
**Cache:** Redis (Render)  
**Region:** Singapore
