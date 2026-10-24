# HƯỚNG DẪN BẬT / TẮT DEMO — SpringBook (Nhóm 2)

## Cách bật (cách 1 — dễ nhất)
Nhấp đúp file **`chay_demo.bat`** trong thư mục dự án.
Chờ tới khi log hiện dòng `Tomcat started on port 8080` (lần đầu ~1 phút, các lần sau ~5 giây), rồi mở trình duyệt:

- **Ứng dụng:** http://localhost:8080
- **Xem CSDL:** http://localhost:8080/h2-console
- **Tài khoản mẫu:** `an` / `binh` / `chi` — mật khẩu `123456`

## Cách bật (cách 2 — bằng lệnh)
Mở terminal (CMD/PowerShell/Git Bash) trong thư mục dự án:

.\mvnw.cmd spring-boot:run

```

*(Máy chưa có JDK ở lab-tools: cài JDK 21 bất kỳ rồi chỉ JAVA_HOME tới thư mục cài.)*

## Cách TẮT
- **Tắt an toàn (khuyên dùng):** bấm **Ctrl + C** trong cửa sổ đang chạy → gõ `Y` (hoặc bấm Ctrl+C lần nữa) → app dừng, cổng 8080 tự do.
- Hoặc đơn giản **đóng cửa sổ terminal** đang chạy app.
- Kiểm tra đã tắt chưa: mở http://localhost:8080 — nếu trình duyệt báo "không kết nối được" là đã tắt.

## Lưu ý khi demo
- **Dữ liệu luôn sạch như mới**: CSDL H2 chạy trong RAM — mỗi lần bật lại, dữ liệu mẫu (3 users, 6 posts, 10 comments) tự nạp lại; bài/bình luận vừa thử sẽ mất → không sợ "bẩn" trước giờ trình bày.
- Vào **/h2-console**: JDBC URL điền `jdbc:h2:mem:socialdb;MODE=MySQL;DB_CLOSE_DELAY=-1`, User Name `sa`, Password trống → Connect → gõ SQL (ví dụ `SELECT * FROM posts;`) → Ctrl+Enter... (bấm nút Run).
- **Lỗi "Port 8080 was already in use"**: đang có một bản chạy trước — tắt bản cũ (Ctrl+C ở cửa sổ cũ) rồi bật lại.
- App cần JDK 21; **không cần** internet (Bootstrap đã nhúng sẵn) và **không cần** cài CSDL.
